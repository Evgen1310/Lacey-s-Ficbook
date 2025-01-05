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
        val login = lensOrNull(userLens, request)?.login ?: ""
        lensOrNull(pathLens, request)
            ?.let { id -> storage.getArticleId(id) }
            ?.let { entity ->
                val page = lensOrDefault(pageLens, request, 0).takeIf { it > -1 } ?: 0
                val chapter = addon.getChaptersByIds(entity.chapters)[page]
                val paginator = Paginator(page, entity.chapters.size, request.uri.removeQueries("page"))
                return Response(OK).with(
                    htmlView(request) of ArticleVM(
                        makeArticlesWithData(
                            login,
                            listOf(entity),
                            addon,
                            userStorage
                        )[0],
                        addon.getTagsByIds(entity.tagsArt),
                        chapter,
                        addon.getChaptersByIds(entity.chapters),
                        paginator,
                    )
                )
            } ?: return Response(NOT_FOUND)
    }
}
