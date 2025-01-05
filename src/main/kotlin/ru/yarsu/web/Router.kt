package ru.yarsu.web

import org.http4k.core.Method
import org.http4k.core.then
import org.http4k.lens.RequestContextLens
import org.http4k.routing.RoutingHttpHandler
import org.http4k.routing.bind
import org.http4k.routing.routes
import ru.ac.uniyar.web.templates.ContextAwareViewRender
import ru.yarsu.db.DataBaseController
import ru.yarsu.web.domain.Permissions
import ru.yarsu.web.filters.blockedFilter
import ru.yarsu.web.filters.onlyAdminFilter
import ru.yarsu.web.filters.onlyEditorFilter
import ru.yarsu.web.handlers.articles.ArticleDeleteHandler
import ru.yarsu.web.handlers.articles.ArticleDeleteHandlerPOST
import ru.yarsu.web.handlers.articles.ArticleEditHandler
import ru.yarsu.web.handlers.articles.ArticleEditHandlerPOST
import ru.yarsu.web.handlers.articles.ArticleHandler
import ru.yarsu.web.handlers.articles.ArticleListHandler
import ru.yarsu.web.handlers.articles.NewArticleHandler
import ru.yarsu.web.handlers.articles.NewArticleHandlerPOST
import ru.yarsu.web.handlers.authorization.LoginHandler
import ru.yarsu.web.handlers.authorization.LoginHandlerPOST
import ru.yarsu.web.handlers.authorization.LogoutHandler
import ru.yarsu.web.handlers.authorization.RegisterHandler
import ru.yarsu.web.handlers.authorization.RegisterHandlerPOST
import ru.yarsu.web.handlers.editor.FormDeleteHandler
import ru.yarsu.web.handlers.editor.FormDeleteHandlerPOST
import ru.yarsu.web.handlers.editor.FormEditHandler
import ru.yarsu.web.handlers.editor.FormEditHandlerPOST
import ru.yarsu.web.handlers.editor.FormNewHandler
import ru.yarsu.web.handlers.editor.FormNewHandlerPOST
import ru.yarsu.web.handlers.editor.FormsListHandler
import ru.yarsu.web.handlers.editor.GenreDeleteHandler
import ru.yarsu.web.handlers.editor.GenreDeleteHandlerPOST
import ru.yarsu.web.handlers.editor.GenreEditHandler
import ru.yarsu.web.handlers.editor.GenreEditHandlerPOST
import ru.yarsu.web.handlers.editor.GenreNewHandler
import ru.yarsu.web.handlers.editor.GenreNewHandlerPOST
import ru.yarsu.web.handlers.editor.GenresListHandler
import ru.yarsu.web.handlers.users.HomeHandler
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
    dataBaseController: DataBaseController,
    jwtTools: JwtTools,
): RoutingHttpHandler {
    return routes(
        blockedFilter(permissionsLens).then(
            routes(
                "/" bind Method.GET to HomeHandler(htmlView),
                "/register" bind Method.GET to RegisterHandler(htmlView, userLens),
                "/register" bind Method.POST to RegisterHandlerPOST(htmlView, dataBaseController),
                "/login" bind Method.GET to LoginHandler(htmlView, userLens),
                "/login" bind Method.POST to LoginHandlerPOST(htmlView, dataBaseController, jwtTools),
                "/articles" bind Method.GET to ArticleListHandler(htmlView, dataBaseController, userLens),
                "/articles/new" bind Method.GET to NewArticleHandler(htmlView, permissionsLens, dataBaseController),
                "/articles/new" bind Method.POST to
                    NewArticleHandlerPOST(
                        htmlView,
                        userLens,
                        permissionsLens,
                        dataBaseController,
                    ),
                "/articles/{id}" bind Method.GET to ArticleHandler(htmlView, userLens, dataBaseController),
                "/articles/{id}/edit" bind Method.GET to
                    ArticleEditHandler(
                        htmlView,
                        userLens,
                        permissionsLens,
                        dataBaseController,
                    ),
                "/articles/{id}/edit" bind Method.POST to
                    ArticleEditHandlerPOST(
                        htmlView,
                        userLens,
                        permissionsLens,
                        dataBaseController,
                    ),
                "/articles/{id}/delete" bind Method.GET to
                    ArticleDeleteHandler(
                        htmlView,
                        userLens,
                        permissionsLens,
                        dataBaseController,
                    ),
                "/articles/{id}/delete" bind Method.POST to
                    ArticleDeleteHandlerPOST(
                        userLens,
                        permissionsLens,
                        dataBaseController,
                    ),
                onlyAdminFilter(permissionsLens).then(
                    routes(
                        "/users" bind Method.GET to UsersListHandler(htmlView, dataBaseController),
                        "/users/{id}/edit" bind Method.GET to UserEditHandler(htmlView, dataBaseController),
                        "/users/{id}/edit" bind Method.POST to UserEditHandlerPOST(htmlView, dataBaseController), // поменять классы
                        "/users/{id}/unblock" bind Method.GET to UserUnBlockHandler(htmlView, dataBaseController),
                        "/users/{id}/unblock" bind Method.POST to UserUnBlockHandlerPOST(dataBaseController),
                        "/users/{id}/block" bind Method.GET to UserBlockHandler(htmlView, dataBaseController),
                        "/users/{id}/block" bind Method.POST to UserBlockHandlerPOST(dataBaseController),
                    ),
                ),
                onlyEditorFilter(permissionsLens).then(
                    routes(
                        "/redaction/forms" bind Method.GET to FormsListHandler(htmlView, dataBaseController),
                        "/redaction/forms/new" bind Method.GET to FormNewHandler(htmlView),
                        "/redaction/forms/new" bind Method.POST to FormNewHandlerPOST(htmlView, dataBaseController),
                        "/redaction/forms/{id}/edit" bind Method.GET to FormEditHandler(htmlView, dataBaseController),
                        "/redaction/forms/{id}/edit" bind Method.POST to FormEditHandlerPOST(htmlView, dataBaseController),
                        "/redaction/forms/{id}/delete" bind Method.GET to FormDeleteHandler(htmlView, dataBaseController),
                        "/redaction/forms/{id}/delete" bind Method.POST to FormDeleteHandlerPOST(dataBaseController),
                        "/redaction/genres" bind Method.GET to GenresListHandler(htmlView, dataBaseController),
                        "/redaction/genres/new" bind Method.GET to GenreNewHandler(htmlView),
                        "/redaction/genres/new" bind Method.POST to GenreNewHandlerPOST(htmlView, dataBaseController),
                        "/redaction/genres/{id}/edit" bind Method.GET to GenreEditHandler(htmlView, dataBaseController),
                        "/redaction/genres/{id}/edit" bind Method.POST to GenreEditHandlerPOST(htmlView, dataBaseController),
                        "/redaction/genres/{id}/delete" bind Method.GET to GenreDeleteHandler(htmlView, dataBaseController),
                        "/redaction/genres/{id}/delete" bind Method.POST to GenreDeleteHandlerPOST(dataBaseController),
                    ),
                ),
            ),
        ),
        "/logout" bind Method.GET to LogoutHandler(),
    )
}
