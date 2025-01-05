package ru.yarsu.web.filters

import org.http4k.core.Filter
import org.http4k.core.HttpHandler
import org.http4k.core.Request
import org.http4k.core.Response
import org.http4k.core.Status
import org.http4k.core.with
import ru.ac.uniyar.web.templates.ContextAwareViewRender
import ru.yarsu.web.models.AlreadyInVM
import ru.yarsu.web.models.ErrorVM

class NotFoundFilter(private val htmlView: ContextAwareViewRender) : Filter {
    override fun invoke(next: HttpHandler): HttpHandler = NotFoundHandler(next, htmlView)
}

class NotFoundHandler(private val next: HttpHandler, val htmlView: ContextAwareViewRender) : HttpHandler {
    override fun invoke(request: Request): Response {
        val response = next(request)
        return when (response.status) {
            Status.NOT_FOUND -> response.with(htmlView(request) of ErrorVM(request.uri.path, -1))
            Status.UNAUTHORIZED -> response.with(htmlView(request) of ErrorVM(request.uri.path, 0))
            Status.UNAVAILABLE_FOR_LEGAL_REASONS -> response.with(htmlView(request) of AlreadyInVM())
            Status.FORBIDDEN -> response.with(htmlView(request) of ErrorVM("", -2))
            else -> response
        }
    }
}
