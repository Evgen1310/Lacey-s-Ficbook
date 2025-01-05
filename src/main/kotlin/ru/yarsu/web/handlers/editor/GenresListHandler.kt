package ru.yarsu.web.handlers.editor

import org.http4k.core.HttpHandler
import org.http4k.core.Request
import org.http4k.core.Response
import org.http4k.core.Status
import org.http4k.core.removeQueries
import org.http4k.core.with
import org.http4k.lens.Query
import org.http4k.lens.int
import ru.ac.uniyar.web.templates.ContextAwareViewRender
import ru.yarsu.db.DataBaseController
import ru.yarsu.web.domain.Paginator
import ru.yarsu.web.funs.lensOrDefault
import ru.yarsu.web.models.GenresListVM

class GenresListHandler(private val htmlView: ContextAwareViewRender, private val dataBaseController: DataBaseController) :
    HttpHandler {
    private val pageLens = Query.int().required("page")

    override fun invoke(request: Request): Response {
        val page = lensOrDefault(pageLens, request, 0).takeIf { it > -1 } ?: 0
        val genres = dataBaseController.genresByPageNumber(page)
        val paginator = Paginator(page, dataBaseController.pageAmountFormOrGenre("genre"), request.uri.removeQueries("page"))
        val viewModel = GenresListVM(genres, paginator)
        return Response(Status.OK).with(htmlView(request) of viewModel)
    }
}
