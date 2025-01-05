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
import ru.yarsu.web.domain.Permissions
import ru.yarsu.web.domain.storage.AddonStorage
import ru.yarsu.web.domain.storage.ArticleStorage
import ru.yarsu.web.domain.storage.UserStorage
import ru.yarsu.web.funs.lensOrNull
import ru.yarsu.web.models.ArticleDeleteVM
import ru.yarsu.web.users.User

class ArticleDeleteHandler(
    private val htmlView: ContextAwareViewRender,
    private val storage: ArticleStorage,
    private val addon: AddonStorage,
    private val userStorage: UserStorage,
    private val userLens: RequestContextLens<User?>,
    private val permissionsLens: RequestContextLens<Permissions>,
) : HttpHandler {
    private val pathLens = Path.int().of("id")

    override fun invoke(request: Request): Response {
        val permissions = permissionsLens(request)
        val login = userLens(request)?.login ?: ""
        lensOrNull(pathLens, request)
            ?.let { id -> storage.getArticleId(id) }
            ?.let { entity ->
                if (!permissions.manageAllArticles) {
                    if (!(permissions.manageArticle && entity.user == login)) {
                        return Response(Status.UNAUTHORIZED)
                    }
                }
                val viewModel = ArticleDeleteVM(
                    makeArticlesWithData(login, listOf(entity), addon, userStorage)[0],
                    addon.getTagsByIds(entity.tagsArt)
                )
                return Response(Status.OK).with(htmlView(request) of viewModel)
            } ?: return Response(NOT_FOUND)
    }
}
