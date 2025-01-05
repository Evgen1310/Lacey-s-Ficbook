package ru.yarsu.web.handlers.articles

import org.http4k.core.HttpHandler
import org.http4k.core.Request
import org.http4k.core.Response
import org.http4k.core.Status.Companion.FOUND
import org.http4k.core.Status.Companion.NOT_FOUND
import org.http4k.core.Status.Companion.OK
import org.http4k.lens.Path
import org.http4k.lens.RequestContextLens
import org.http4k.lens.int
import ru.yarsu.db.DataBaseController
import ru.yarsu.web.domain.Permissions
import ru.yarsu.web.funs.checkPermissions
import ru.yarsu.web.funs.lensOrNull
import ru.yarsu.web.users.User

class ArticleDeleteHandlerPOST(
    private val userLens: RequestContextLens<User?>,
    private val permissionsLens: RequestContextLens<Permissions>,
    private val dataBaseController: DataBaseController,
) : HttpHandler {
    private val pathLens = Path.int().of("id")

    override fun invoke(request: Request): Response {
        val permissions = permissionsLens(request)
        val login = userLens(request)?.login
        lensOrNull(pathLens, request)
            ?.let { id -> dataBaseController.getArticleById(id) }
            ?.let { article ->
                val permissionsStatus = checkPermissions(permissions, login, article.user)
                if (permissionsStatus != OK) {
                    return Response(permissionsStatus)
                }
                dataBaseController.removeChapters(article.chapters)
                dataBaseController.deleteArticle(article.id)
                return Response(FOUND).header("Location", "/articles")
            } ?: return Response(NOT_FOUND)
    }
}
