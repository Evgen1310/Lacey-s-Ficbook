package ru.yarsu

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import org.http4k.core.ContentType
import org.http4k.core.RequestContexts
import org.http4k.core.then
import org.http4k.filter.ServerFilters
import org.http4k.lens.RequestContextKey
import org.http4k.lens.RequestContextLens
import org.http4k.routing.ResourceLoader
import org.http4k.routing.routes
import org.http4k.routing.static
import org.http4k.server.Netty
import org.http4k.server.asServer
import org.slf4j.LoggerFactory
import ru.ac.uniyar.web.templates.ContextAwareViewRender
import ru.yarsu.config.readConfigurations
import ru.yarsu.web.domain.Permissions
import ru.yarsu.web.domain.article.Article
import ru.yarsu.web.domain.article.Chapter
import ru.yarsu.web.domain.article.Form
import ru.yarsu.web.domain.article.Genre
import ru.yarsu.web.domain.article.Tag
import ru.yarsu.web.domain.storage.AddonStorage
import ru.yarsu.web.domain.storage.ArticleStorage
import ru.yarsu.web.domain.storage.PasswordHandler
import ru.yarsu.web.domain.storage.UserStorage
import ru.yarsu.web.filters.NotFoundFilter
import ru.yarsu.web.filters.getPermissionsFilter
import ru.yarsu.web.filters.getUserFilter
import ru.yarsu.web.filters.loggerFilter
import ru.yarsu.web.jwt.JwtTools
import ru.yarsu.web.rendererProvider
import ru.yarsu.web.router
import ru.yarsu.web.users.User
import java.io.File
import kotlin.concurrent.thread

fun main() {
    val appConfig = readConfigurations()
    val dbConfig = appConfig.dbConfig
    val objectMapper = jacksonObjectMapper()
    objectMapper.findAndRegisterModules()

    val articles: List<Article> = objectMapper.readValue(File("${dbConfig.databasePath}/articles-data.json"))
    val chapters: List<Chapter> = objectMapper.readValue(File("${dbConfig.databasePath}/chapters-data.json"))
    val tags: List<Tag> = objectMapper.readValue(File("${dbConfig.databasePath}/tags-data.json"))
    val users: List<User> = objectMapper.readValue(File("${dbConfig.databasePath}/users-data.json"))
    val forms: List<Form> = objectMapper.readValue(File("${dbConfig.databasePath}/forms-data.json"))
    val genres: List<Genre> = objectMapper.readValue(File("${dbConfig.databasePath}/genres-data.json"))

    val passwordHandler = PasswordHandler(dbConfig.authSalt)

    val storage = ArticleStorage(articles)
    val storageAddon = AddonStorage(chapters, tags, forms, genres)
    val userStorage = UserStorage(users, passwordHandler)
    val jwtTools = JwtTools(dbConfig.jwtSalt, "ru.yarsu", 7)

    val renderer = rendererProvider(true)
    val logger = LoggerFactory.getLogger("ru.yarsu.WebApplication")

    val htmlView = ContextAwareViewRender.withContentType(renderer, ContentType.TEXT_HTML)
    val contexts = RequestContexts()
    val userLens: RequestContextLens<User?> = RequestContextKey.optional(contexts, "user")
    val permissionsLens: RequestContextLens<Permissions> =
        RequestContextKey.required(contexts, name = "permissions")

    val htmlViewWithContext =
        htmlView
            .associateContextLens("currentUser", userLens)
            .associateContextLens("permissions", permissionsLens)
    val appWithStaticResources =
        ServerFilters.InitialiseRequestContext(contexts).then(
            NotFoundFilter(htmlViewWithContext).then(
                getUserFilter(
                    userLens,
                    userStorage,
                    jwtTools,
                ).then(
                    getPermissionsFilter(userLens, permissionsLens).then(
                        loggerFilter(logger).then(
                            routes(
                                router(
                                    htmlViewWithContext,
                                    userLens,
                                    permissionsLens,
                                    storage,
                                    storageAddon,
                                    userStorage,
                                    jwtTools,
                                ),
                                static(ResourceLoader.Classpath("/ru/yarsu/public")),
                            ),
                        ),
                    ),
                ),
            ),
        )
    val server = appWithStaticResources.asServer(Netty(appConfig.webConfig.webPort)).start()

    println("Server started on http://localhost:" + server.port())
    println("Press enter to exit application.")
    readln()
    server.stop()
}