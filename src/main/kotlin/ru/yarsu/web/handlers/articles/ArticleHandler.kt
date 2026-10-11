package ru.yarsu.web.handlers.articles

import org.http4k.core.*
import org.http4k.core.Status.Companion.NOT_FOUND
import org.http4k.core.Status.Companion.OK
import org.http4k.lens.Path
import org.http4k.lens.Query
import org.http4k.lens.RequestContextLens
import org.http4k.lens.int
import ru.ac.uniyar.web.templates.ContextAwareViewRender
import ru.yarsu.web.domain.Paginator
import ru.yarsu.web.domain.article.ArticleWithData
import ru.yarsu.web.domain.storage.AddonStorage
import ru.yarsu.web.domain.storage.ArticleStorage
import ru.yarsu.web.domain.storage.UserStorage
import ru.yarsu.web.funs.lensOrDefault
import ru.yarsu.web.funs.lensOrNull
import ru.yarsu.web.models.ArticleVM
import ru.yarsu.web.users.User

class ArticleHandler(
    private val htmlView: ContextAwareViewRender,
    private val storage: ArticleStorage,
    private val addon: AddonStorage,
    private val userStorage: UserStorage,
    private val userLens: RequestContextLens<User?>,
) : HttpHandler {
    private val pathLens = Path.int().of("id")
    private val pageLens = Query.int().required("page")

    override fun invoke(request: Request): Response {
        val userInfo = userStorage.getUserInfo(userLens(request)?.login ?: "")

        lensOrNull(pathLens, request)
            ?.let { id -> storage.getArticleFromStorage(id) }
            ?.let { article ->
                print(article.id)
                val page = lensOrDefault(pageLens, request, 0).takeIf { it > -1 } ?: 0
                val chapters = addon.getChaptersOrDefault(article.chapters)
                val paginator = Paginator(page, article.chapters.size, request.uri.removeQueries("page"))

                if (page > chapters.size)
                    return Response(NOT_FOUND)

                val articleWithData = addon.getArticleWithData(userInfo, article)
                val tags = addon.getTagsByIds(article.tagsArt)

                return Response(OK).with(
                    htmlView(request) of ArticleVM(
                        articleWithData,
                        tags,
                        chapters[page],
                        chapters,
                        paginator,
                    )
                )
            } ?: return Response(NOT_FOUND)
    }
}
