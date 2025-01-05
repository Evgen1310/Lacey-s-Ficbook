package ru.yarsu

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
import ru.yarsu.db.DataBaseController
import ru.yarsu.web.domain.Permissions
import ru.yarsu.web.filters.NotFoundFilter
import ru.yarsu.web.filters.getPermissionsFilter
import ru.yarsu.web.filters.getUserFilter
import ru.yarsu.web.filters.loggerFilter
import ru.yarsu.web.jwt.JwtTools
import ru.yarsu.web.rendererProvider
import ru.yarsu.web.router
import ru.yarsu.web.users.User

fun main() {
    val appConfig = readConfigurations()
    val dbConfig = appConfig.dbConfig

    val dataBaseController = DataBaseController(authSalt = dbConfig.authSalt)

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
            NotFoundFilter(htmlView).then(
                loggerFilter(logger).then(
                    routes(
                        getUserFilter(
                            userLens,
                            dataBaseController,
                            jwtTools,
                        ).then(
                            getPermissionsFilter(userLens, permissionsLens).then(
                                router(
                                    htmlViewWithContext,
                                    userLens,
                                    permissionsLens,
                                    dataBaseController,
                                    jwtTools,
                                ),
                            ),
                        ),
                        static(ResourceLoader.Classpath("/ru/yarsu/public")),
                    ),
                ),
            ),
        )

    val server = appWithStaticResources.asServer(Netty(appConfig.webConfig.webPort)).start()

    println("Server started on http://localhost:" + server.port())
}
