package ru.yarsu.web.handlers.users

import org.http4k.core.HttpHandler
import org.http4k.core.Request
import org.http4k.core.Response
import org.http4k.core.Status
import org.http4k.core.with
import ru.ac.uniyar.web.templates.ContextAwareViewRender
import ru.yarsu.web.models.HomePageVM

class HomeHandler(private val htmlView: ContextAwareViewRender) : HttpHandler {
    override fun invoke(request: Request): Response {
        val viewModel = HomePageVM("..")
        return Response(Status.OK).with(htmlView(request) of viewModel)
    }
}
