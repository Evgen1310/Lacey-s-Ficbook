package ru.yarsu.web.handlers.users

import org.http4k.core.HttpHandler
import org.http4k.core.Request
import org.http4k.core.Response
import org.http4k.core.Status
import org.http4k.core.with
import org.http4k.lens.FormField
import org.http4k.lens.Path
import org.http4k.lens.WebForm
import org.http4k.lens.int
import org.http4k.lens.nonBlankString
import ru.ac.uniyar.web.templates.ContextAwareViewRender
import ru.yarsu.db.DataBaseController
import ru.yarsu.web.domain.RolesEnums
import ru.yarsu.web.funs.lensOrNull
import ru.yarsu.web.models.UserEditVM

class UserEditHandler(
    private val htmlView: ContextAwareViewRender,
    private val dataBaseController: DataBaseController,
) : HttpHandler {
    private val pathLens = Path.nonBlankString().of("id")
    private val nicknameField = FormField.nonBlankString().required("nickname")
    private val aboutField = FormField.nonBlankString().optional("about")
    private val roleField = FormField.int().required("role")

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
                        val viewModel =
                            UserEditVM(
                                WebForm().with(
                                    nicknameField of user.nickName,
                                    aboutField of user.about,
                                    roleField of user.role,
                                ),
                                user,
                                RolesEnums.entries,
                                RolesEnums.from(user.role) ?: RolesEnums.ANONIMOUS,
                                listOf(),
                                "",
                            )
                        return Response(Status.OK).with(htmlView(request) of viewModel)
                    }
            } ?: return Response(Status.NOT_FOUND)
    }
}
