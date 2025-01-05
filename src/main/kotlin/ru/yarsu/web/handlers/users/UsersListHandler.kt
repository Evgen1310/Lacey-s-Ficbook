package ru.yarsu.web.handlers.users

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
import ru.yarsu.web.models.UsersListVM

class UsersListHandler(private val htmlView: ContextAwareViewRender, private val dataBaseController: DataBaseController) :
    HttpHandler {
    private val pageLens = Query.int().required("page")

    override fun invoke(request: Request): Response {
        val page = lensOrDefault(pageLens, request, 0).takeIf { it > -1 } ?: 0
        val users = dataBaseController.makeUsersWithData(dataBaseController.usersByPageNumber(page))
        val paginator = Paginator(page, dataBaseController.pageAmountUser(), request.uri.removeQueries("page"))
        val viewModel = UsersListVM(users, paginator)
        return Response(Status.OK).with(htmlView(request) of viewModel)
    }
}
