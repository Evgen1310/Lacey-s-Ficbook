package ru.yarsu.web.handlers.authorization

import org.http4k.core.Body
import org.http4k.core.HttpHandler
import org.http4k.core.Request
import org.http4k.core.Response
import org.http4k.core.Status
import org.http4k.core.cookie.Cookie
import org.http4k.core.cookie.cookie
import org.http4k.core.with
import org.http4k.lens.FormField
import org.http4k.lens.Validator
import org.http4k.lens.WebForm
import org.http4k.lens.nonBlankString
import org.http4k.lens.webForm
import ru.ac.uniyar.web.templates.ContextAwareViewRender
import ru.yarsu.db.DataBaseController
import ru.yarsu.web.funs.lensOrDefault
import ru.yarsu.web.jwt.JwtTools
import ru.yarsu.web.models.LoginVM
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZonedDateTime

class LoginHandlerPOST(
    private val htmlView: ContextAwareViewRender,
    private val dataBaseController: DataBaseController,
    private val jwtTools: JwtTools,
) : HttpHandler {
    private val loginField = FormField.nonBlankString().required("login")
    private val passwordField = FormField.nonBlankString().required("password")
    private val formLens =
        Body.webForm(
            Validator.Feedback,
            loginField,
            passwordField,
        ).toLens()

    override fun invoke(request: Request): Response {
        val form = formLens(request)
        val errors = checkErrors(form)
        if (errors.isNotEmpty()) {
            var errorStr = ""
            if ("incorrect" in errors) {
                errorStr = "Неверный логин или пароль. Попробуй ещё раз!"
            }
            val viewModel =
                LoginVM(
                    form,
                    errors,
                    errorStr,
                )
            return Response(Status.OK).with(htmlView(request) of viewModel)
        }
        val token = jwtTools.buildToken(loginField(form)) ?: ""
        if (token == "") {
            val viewModel =
                LoginVM(
                    form,
                    errors,
                    "Произошла непредвиденная ошибка.",
                )
            return Response(Status.OK).with(htmlView(request) of viewModel)
        }
        val cookie =
            Cookie(
                "auth",
                token,
                httpOnly = true,
                expires =
                    ZonedDateTime.of(
                        LocalDateTime.now()
                            .plusDays(7),
                        ZoneId.of("Europe/Moscow"),
                    ).toInstant(),
            )
        return Response(Status.FOUND).cookie(cookie).header("Location", "/")
    }

    private fun checkErrors(form: WebForm): List<String> {
        val errors = form.errors.map { it.meta.name }.toMutableList()
        val user = dataBaseController.getUserByLogin(lensOrDefault(loginField, form, ""))
        user?.let {
            if (!dataBaseController.checkPassword(lensOrDefault(passwordField, form, ""), user.password)) {
                errors.add("incorrect")
            }
        } ?: errors.add("incorrect")
        return errors
    }
}
