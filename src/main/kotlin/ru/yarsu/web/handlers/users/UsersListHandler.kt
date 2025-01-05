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
import ru.yarsu.web.domain.Paginator
import ru.yarsu.web.domain.storage.UserStorage
import ru.yarsu.web.funs.lensOrDefault
import ru.yarsu.web.models.UsersListVM

class UsersListHandler(private val htmlView: ContextAwareViewRender, private val userStorage: UserStorage) :
    HttpHandler {
    private val pageLens = Query.int().required("page")

    override fun invoke(request: Request): Response {
        val page = lensOrDefault(pageLens, request, 0).takeIf { it > -1 } ?: 0
        val users = userStorage.makeUsersWithData(userStorage.usersByPageNumber(page))
        val paginator = Paginator(page, userStorage.getPageAmount(), request.uri.removeQueries("page"))
        val viewModel = UsersListVM(users, paginator)
        return Response(Status.OK).with(htmlView(request) of viewModel)
    }
}
