package ru.yarsu.web.handlers.articles

import org.http4k.core.HttpHandler
import org.http4k.core.Request
import org.http4k.core.Response
import org.http4k.core.Status
import org.http4k.core.Status.Companion.NOT_FOUND
import org.http4k.core.with
import org.http4k.lens.Path
import org.http4k.lens.RequestContextLens
import org.http4k.lens.int
import ru.ac.uniyar.web.templates.ContextAwareViewRender
import ru.yarsu.db.DataBaseController
import ru.yarsu.web.domain.Permissions
import ru.yarsu.web.funs.lensOrNull
import ru.yarsu.web.models.ArticleDeleteVM
import ru.yarsu.web.users.User

class ArticleDeleteHandler(
    private val htmlView: ContextAwareViewRender,
    private val userLens: RequestContextLens<User?>,
    private val permissionsLens: RequestContextLens<Permissions>,
    private val dataBaseController: DataBaseController,
) : HttpHandler {
    private val pathLens = Path.int().of("id")

    override fun invoke(request: Request): Response {
        val permissions = permissionsLens(request)
        val login = userLens(request)?.login ?: ""
        lensOrNull(pathLens, request)
            ?.let { id -> dataBaseController.getArticleById(id) }
            ?.let { entity ->
                if (!permissions.manageAllArticles) {
                    if (!(permissions.manageArticle && entity.user == login)) {
                        return Response(Status.UNAUTHORIZED)
                    }
                }
                val viewModel =
                    ArticleDeleteVM(
                        makeArticlesWithData(login, listOf(entity), dataBaseController)[0],
                        dataBaseController.getTagsByIds(entity.tagsArt),
                    )
                return Response(Status.OK).with(htmlView(request) of viewModel)
            } ?: return Response(NOT_FOUND)
    }
}
