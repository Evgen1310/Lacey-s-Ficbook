package ru.yarsu.web

import org.http4k.core.Method
import org.http4k.core.then
import org.http4k.lens.RequestContextLens
import org.http4k.routing.RoutingHttpHandler
import org.http4k.routing.bind
import org.http4k.routing.routes
import ru.ac.uniyar.web.templates.ContextAwareViewRender
import ru.yarsu.web.domain.Permissions
import ru.yarsu.web.domain.storage.AddonStorage
import ru.yarsu.web.domain.storage.ArticleStorage
import ru.yarsu.web.domain.storage.UserStorage
import ru.yarsu.web.filters.blockedFilter
import ru.yarsu.web.filters.onlyAdminFilter
import ru.yarsu.web.filters.onlyEditorFilter
import ru.yarsu.web.handlers.articles.ArticleDeleteHandler
import ru.yarsu.web.handlers.articles.ArticleDeleteHandlerPOST
import ru.yarsu.web.handlers.articles.ArticleEditHandler
import ru.yarsu.web.handlers.articles.ArticleEditHandlerPOST
import ru.yarsu.web.handlers.articles.ArticleHandler
import ru.yarsu.web.handlers.articles.ArticleListHandler
import ru.yarsu.web.handlers.users.HomeHandler
import ru.yarsu.web.handlers.authorization.LoginHandler
import ru.yarsu.web.handlers.authorization.LoginHandlerPOST
import ru.yarsu.web.handlers.authorization.LogoutHandler
import ru.yarsu.web.handlers.articles.NewArticleHandler
import ru.yarsu.web.handlers.articles.NewArticleHandlerPOST
import ru.yarsu.web.handlers.authorization.RegisterHandler
import ru.yarsu.web.handlers.authorization.RegisterHandlerPOST
import ru.yarsu.web.handlers.editor.FormDeleteHandler
import ru.yarsu.web.handlers.editor.FormDeleteHandlerPOST
import ru.yarsu.web.handlers.editor.FormsListHandler
import ru.yarsu.web.handlers.editor.GenresListHandler
import ru.yarsu.web.handlers.editor.FormEditHandler
import ru.yarsu.web.handlers.editor.FormEditHandlerPOST
import ru.yarsu.web.handlers.editor.FormNewHandler
import ru.yarsu.web.handlers.editor.FormNewHandlerPOST
import ru.yarsu.web.handlers.editor.GenreDeleteHandler
import ru.yarsu.web.handlers.editor.GenreDeleteHandlerPOST
import ru.yarsu.web.handlers.editor.GenreEditHandler
import ru.yarsu.web.handlers.editor.GenreEditHandlerPOST
import ru.yarsu.web.handlers.editor.GenreNewHandler
import ru.yarsu.web.handlers.editor.GenreNewHandlerPOST
import ru.yarsu.web.handlers.users.UserBlockHandler
import ru.yarsu.web.handlers.users.UserBlockHandlerPOST
import ru.yarsu.web.handlers.users.UserEditHandler
import ru.yarsu.web.handlers.users.UserEditHandlerPOST
import ru.yarsu.web.handlers.users.UserUnBlockHandler
import ru.yarsu.web.handlers.users.UserUnBlockHandlerPOST
import ru.yarsu.web.handlers.users.UsersListHandler
import ru.yarsu.web.jwt.JwtTools
import ru.yarsu.web.users.User

fun router(
    htmlView: ContextAwareViewRender,
    userLens: RequestContextLens<User?>,
    permissionsLens: RequestContextLens<Permissions>,
    storage: ArticleStorage,
    addon: AddonStorage,
    userStorage: UserStorage,
    jwtTools: JwtTools,
): RoutingHttpHandler {
    return routes(
        blockedFilter(permissionsLens).then(
            routes(
                "/" bind Method.GET to HomeHandler(htmlView),
                "/register" bind Method.GET to RegisterHandler(htmlView, userLens),
                "/register" bind Method.POST to RegisterHandlerPOST(htmlView, userStorage),
                "/login" bind Method.GET to LoginHandler(htmlView, userLens),
                "/login" bind Method.POST to LoginHandlerPOST(htmlView, userStorage, jwtTools),
                "/articles" bind Method.GET to ArticleListHandler(htmlView, storage, addon, userStorage, userLens),
                "/articles/new" bind Method.GET to NewArticleHandler(addon, htmlView, permissionsLens),
                "/articles/new" bind Method.POST to NewArticleHandlerPOST(
                    htmlView,
                    storage,
                    addon,
                    userLens,
                    permissionsLens
                ),
                "/articles/{id}" bind Method.GET to ArticleHandler(htmlView, storage, addon, userStorage, userLens),
                "/articles/{id}/edit" bind Method.GET to
                        ArticleEditHandler(
                            htmlView,
                            storage,
                            addon,
                            userLens,
                            permissionsLens,
                        ),
                "/articles/{id}/edit" bind Method.POST to
                        ArticleEditHandlerPOST(
                            htmlView,
                            storage,
                            addon,
                            userLens,
                            permissionsLens,
                        ),
                "/articles/{id}/delete" bind Method.GET to
                        ArticleDeleteHandler(
                            htmlView,
                            storage,
                            addon,
                            userStorage,
                            userLens,
                            permissionsLens,
                        ),
                "/articles/{id}/delete" bind Method.POST to ArticleDeleteHandlerPOST(
                    storage,
                    addon,
                    userLens,
                    permissionsLens
                ),
                onlyAdminFilter(permissionsLens).then(
                    routes(
                        "/users" bind Method.GET to UsersListHandler(htmlView, userStorage),
                        "/users/{id}/edit" bind Method.GET to UserEditHandler(htmlView, userStorage),
                        "/users/{id}/edit" bind Method.POST to UserEditHandlerPOST(htmlView, userStorage),
                        "/users/{id}/unblock" bind Method.GET to UserUnBlockHandler(htmlView, userStorage),
                        "/users/{id}/unblock" bind Method.POST to UserUnBlockHandlerPOST(userStorage),
                        "/users/{id}/block" bind Method.GET to UserBlockHandler(htmlView, userStorage),
                        "/users/{id}/block" bind Method.POST to UserBlockHandlerPOST(userStorage),
                    )
                ),
                onlyEditorFilter(permissionsLens).then(
                    routes(
                        "/redaction/forms" bind Method.GET to FormsListHandler(htmlView, addon),
                        "/redaction/forms/new" bind Method.GET to FormNewHandler(htmlView),
                        "/redaction/forms/new" bind Method.POST to FormNewHandlerPOST(htmlView, addon),
                        "/redaction/forms/{id}/edit" bind Method.GET to FormEditHandler(htmlView, addon),
                        "/redaction/forms/{id}/edit" bind Method.POST to FormEditHandlerPOST(htmlView, addon),
                        "/redaction/forms/{id}/delete" bind Method.GET to FormDeleteHandler(htmlView, addon),
                        "/redaction/forms/{id}/delete" bind Method.POST to FormDeleteHandlerPOST(addon),
                        "/redaction/genres" bind Method.GET to GenresListHandler(htmlView, addon),
                        "/redaction/genres/new" bind Method.GET to GenreNewHandler(htmlView),
                        "/redaction/genres/new" bind Method.POST to GenreNewHandlerPOST(htmlView, addon),
                        "/redaction/genres/{id}/edit" bind Method.GET to GenreEditHandler(htmlView, addon),
                        "/redaction/genres/{id}/edit" bind Method.POST to GenreEditHandlerPOST(htmlView, addon),
                        "/redaction/genres/{id}/delete" bind Method.GET to GenreDeleteHandler(htmlView, addon),
                        "/redaction/genres/{id}/delete" bind Method.POST to GenreDeleteHandlerPOST(addon),
                    )
                )
            ),
        ),
        "/logout" bind Method.GET to LogoutHandler(),
    )
}
