package ru.yarsu.web.handlers.editor

import org.http4k.core.HttpHandler
import org.http4k.core.Request
import org.http4k.core.Response
import org.http4k.core.Status
import org.http4k.core.with
import org.http4k.lens.WebForm
import ru.ac.uniyar.web.templates.ContextAwareViewRender
import ru.yarsu.web.models.FormNewVM

class FormNewHandler(private val htmlView: ContextAwareViewRender) :
    HttpHandler {
    override fun invoke(request: Request): Response {
        val viewModel = FormNewVM(WebForm(), listOf(), "")
        return Response(Status.OK).with(htmlView(request) of viewModel)
    }
}
