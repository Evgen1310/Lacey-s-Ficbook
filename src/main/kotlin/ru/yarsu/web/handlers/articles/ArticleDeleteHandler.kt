package ru.yarsu.web.handlers.articles

import org.http4k.core.HttpHandler
import org.http4k.core.Request
import org.http4k.core.Response
import org.http4k.core.Status
import org.http4k.core.Status.Companion.NOT_FOUND
import org.http4k.core.Status.Companion.OK
import org.http4k.core.with
import org.http4k.lens.Path
import org.http4k.lens.RequestContextLens
import org.http4k.lens.int
import ru.ac.uniyar.web.templates.ContextAwareViewRender
import ru.yarsu.web.domain.Permissions
import ru.yarsu.web.domain.storage.AddonStorage
import ru.yarsu.web.domain.storage.ArticleStorage
import ru.yarsu.web.domain.storage.UserStorage
import ru.yarsu.web.funs.*
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
        val userInfo = userStorage.getUserInfo(userLens(request)?.login ?: "")

        lensOrNull(pathLens, request)
            ?.let { id -> storage.getArticleFromStorage(id) }
            ?.let { article ->
                val permissionsStatus = checkPermissionForArticleEditing(permissions, userInfo.login, article.user)
                if (permissionsStatus != OK)
                    return Response(permissionsStatus)

                val articles = addon.getArticleWithData(article, userInfo)
                val tags = addon.getTagsByIds(article.tagsArt)

                val viewModel = ArticleDeleteVM(
                    articles,
                    tags
                )

                return Response(OK).with(htmlView(request) of viewModel)
        } ?: return Response(NOT_FOUND)
    }
}
