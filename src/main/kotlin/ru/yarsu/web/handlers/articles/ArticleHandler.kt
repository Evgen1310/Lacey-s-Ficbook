package ru.yarsu.web.handlers.articles

import org.http4k.core.*
import org.http4k.core.Status.Companion.NOT_FOUND
import org.http4k.core.Status.Companion.OK
import org.http4k.lens.Path
import org.http4k.lens.Query
import org.http4k.lens.RequestContextLens
import org.http4k.lens.int
import ru.ac.uniyar.web.templates.ContextAwareViewRender
import ru.yarsu.db.DataBaseController
import ru.yarsu.web.domain.Paginator
import ru.yarsu.web.funs.lensOrDefault
import ru.yarsu.web.funs.lensOrNull
import ru.yarsu.web.models.ArticleVM
import ru.yarsu.web.users.User

class ArticleHandler(
    private val htmlView: ContextAwareViewRender,
    private val userLens: RequestContextLens<User?>,
    private val dataBaseController: DataBaseController,
) : HttpHandler {
    private val pathLens = Path.int().of("id")
    private val pageLens = Query.int().required("page")

    override fun invoke(request: Request): Response {
        val login = lensOrNull(userLens, request)?.login ?: ""
        lensOrNull(pathLens, request)
            ?.let { id -> dataBaseController.getArticleById(id) }
            ?.let { entity ->
                val page = lensOrDefault(pageLens, request, 0).takeIf { it > -1 } ?: 0
                val chapter = dataBaseController.getChaptersByIds(entity.chapters)[page]
                val paginator = Paginator(page, entity.chapters.size, request.uri.removeQueries("page"))
                return Response(OK).with(
                    htmlView(request) of
                        ArticleVM(
                            makeArticlesWithData(
                                login,
                                listOf(entity),
                                dataBaseController,
                            )[0],
                            dataBaseController.getTagsByIds(entity.tagsArt),
                            chapter,
                            dataBaseController.getChaptersByIds(entity.chapters),
                            paginator,
                        ),
                )
            } ?: return Response(NOT_FOUND)
    }
}
