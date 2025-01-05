package ru.yarsu.web.handlers.authorization

import org.http4k.core.Body
import org.http4k.core.HttpHandler
import org.http4k.core.Request
import org.http4k.core.Response
import org.http4k.core.Status
import org.http4k.core.with
import org.http4k.lens.FormField
import org.http4k.lens.Validator
import org.http4k.lens.WebForm
import org.http4k.lens.nonBlankString
import org.http4k.lens.webForm
import ru.ac.uniyar.web.templates.ContextAwareViewRender
import ru.yarsu.web.domain.storage.UserStorage
import ru.yarsu.web.funs.lensOrDefault
import ru.yarsu.web.funs.lensOrNull
import ru.yarsu.web.models.RegisterVM

class RegisterHandlerPOST(
    private val htmlView: ContextAwareViewRender,
    private val userStorage: UserStorage,
) :
    HttpHandler {
    private val loginField = FormField.nonBlankString().required("login")
    private val passwordInitField = FormField.nonBlankString().required("pwInit")
    private val passwordConfField = FormField.nonBlankString().required("pwConf")
    private val nicknameField = FormField.nonBlankString().required("nickname")
    private val aboutField = FormField.nonBlankString().optional("about")
    private val formLens =
        Body.webForm(
            Validator.Feedback,
            loginField,
            passwordInitField,
            passwordConfField,
            nicknameField,
            aboutField,
        ).toLens()

    override fun invoke(request: Request): Response {
        val form = formLens(request)
        val errors = checkErrors(form)
        if (errors.isNotEmpty()) {
            val errString = errorString(errors)
            val viewModel =
                RegisterVM(
                    form,
                    errors,
                    errString,
                )
            return Response(Status.OK).with(htmlView(request) of viewModel)
        }
        val user =
            userStorage.createUser(
                loginField(form),
                passwordInitField(form),
                nicknameField(form),
                lensOrDefault(aboutField, form, ""),
            )
        userStorage.addUser(user)
        return Response(Status.FOUND).header("Location", "/login")
    }

    private fun checkErrors(form: WebForm): List<String> {
        val errors = form.errors.map { it.meta.name }.toMutableList()
        if (lensOrNull(passwordInitField, form) != lensOrNull(passwordConfField, form)) {
            errors.add("passwordDiff")
        }
        if (userStorage.checkLogin(lensOrNull(loginField, form))) {
            errors.add("loginExist")
        }
        if (userStorage.checkNickname(lensOrNull(nicknameField, form))) {
            errors.add("nicknameExist")
        }
        return errors
    }

    private fun errorString(errors: List<String>): String {
        var resultStr = "Некорректный ввод. Попробуй ещё раз!"
        if ("passwordDiff" in errors) {
            resultStr = "Введённые пароли не совпадают"
        }
        if ("nicknameExist" in errors) {
            resultStr = "Введённый ник уже занят"
        }
        if ("loginExist" in errors) {
            resultStr = "Введённый логин уже занят"
        }
        return resultStr
    }
}
