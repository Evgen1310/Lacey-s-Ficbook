package ru.yarsu.web.handlers.users

import org.http4k.core.Body
import org.http4k.core.HttpHandler
import org.http4k.core.Request
import org.http4k.core.Response
import org.http4k.core.Status
import org.http4k.core.with
import org.http4k.lens.FormField
import org.http4k.lens.Path
import org.http4k.lens.Validator
import org.http4k.lens.WebForm
import org.http4k.lens.int
import org.http4k.lens.nonBlankString
import org.http4k.lens.webForm
import ru.ac.uniyar.web.templates.ContextAwareViewRender
import ru.yarsu.db.DataBaseController
import ru.yarsu.web.domain.RolesEnums
import ru.yarsu.web.funs.lensOrDefault
import ru.yarsu.web.funs.lensOrNull
import ru.yarsu.web.models.UserEditVM
import ru.yarsu.web.users.User

class UserEditHandlerPOST(
    private val htmlView: ContextAwareViewRender,
    private val dataBaseController: DataBaseController,
) : HttpHandler {
    private val pathLens = Path.nonBlankString().of("id")
    private val nicknameField = FormField.nonBlankString().required("nickname")
    private val passwordField = FormField.nonBlankString().optional("password")
    private val aboutField = FormField.nonBlankString().optional("about")
    private val roleField = FormField.int().required("role")
    private val formLens =
        Body.webForm(
            Validator.Feedback,
            passwordField,
            nicknameField,
            aboutField,
            roleField,
        ).toLens()

    override fun invoke(request: Request): Response {
        lensOrNull(pathLens, request)
            ?.let { login ->
                dataBaseController.getUser(login)
                    ?.let { user ->
                        if (RolesEnums.from(user.role) == RolesEnums.ADMIN) {
                            return Response(Status.UNAUTHORIZED)
                        }
                        if (RolesEnums.from(user.role) == RolesEnums.BLOCKED) {
                            return Response(Status.FOUND).header("Location", "/users/${user.login}/unblock")
                        }
                        val form = formLens(request)
                        val errors = checkErrors(form, user)
                        if (errors.isNotEmpty()) {
                            val viewModel =
                                UserEditVM(
                                    form,
                                    user,
                                    RolesEnums.entries,
                                    RolesEnums.from(lensOrDefault(roleField, form, -1)) ?: RolesEnums.AUTHOR,
                                    errors,
                                    errorString(errors),
                                )
                            return Response(Status.OK).with(htmlView(request) of viewModel)
                        }
                        dataBaseController.changeUser(
                            user,
                            nicknameField(form),
                            lensOrDefault(passwordField, form, ""),
                            lensOrDefault(aboutField, form, ""),
                            roleField(form),
                        )
                        return Response(Status.FOUND).header("Location", "/users")
                    }
            } ?: return Response(Status.NOT_FOUND)
    }

    private fun errorString(errors: List<String>): String {
        var resultStr = "Некорректный ввод. Попробуй ещё раз!"
        if ("nicknameExist" in errors) {
            resultStr = "Такой ник уже есть в системе"
        }
        return resultStr
    }

    private fun checkErrors(
        form: WebForm,
        user: User,
    ): List<String> {
        val errors = form.errors.map { it.meta.name }.toMutableList()
        val nickname = lensOrDefault(nicknameField, form, "")
        if (dataBaseController.checkNickname(nickname, user.nickName)) {
            errors.add("nicknameExist")
        }
        return errors
    }
}
