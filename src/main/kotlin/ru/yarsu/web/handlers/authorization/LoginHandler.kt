package ru.yarsu.web.handlers.authorization

import org.http4k.core.HttpHandler
import org.http4k.core.Request
import org.http4k.core.Response
import org.http4k.core.Status
import org.http4k.core.with
import org.http4k.lens.FormField
import org.http4k.lens.RequestContextLens
import org.http4k.lens.WebForm
import org.http4k.lens.nonBlankString
import ru.ac.uniyar.web.templates.ContextAwareViewRender
import ru.yarsu.web.models.LoginVM
import ru.yarsu.web.users.User

class LoginHandler(
    private val htmlView: ContextAwareViewRender,
    private val userLens: RequestContextLens<User?>,
) : HttpHandler {
    private val loginField = FormField.nonBlankString().required("login")
    private val passwordField = FormField.nonBlankString().required("pwInit")

    override fun invoke(request: Request): Response {
        if (userLens(request) != null) {
            return Response(Status.UNAVAILABLE_FOR_LEGAL_REASONS)
        }
        val viewModel =
            LoginVM(
                WebForm().with(
                    loginField of "",
                    passwordField of "",
                ),
                listOf(),
                "",
            )
        return Response(Status.OK).with(htmlView(request) of viewModel)
    }
}
