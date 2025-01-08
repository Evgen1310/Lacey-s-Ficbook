package ru.yarsu.web.handlers.users

import org.http4k.core.HttpHandler
import org.http4k.core.Request
import org.http4k.core.Response
import org.http4k.core.Status
import org.http4k.lens.Path
import org.http4k.lens.nonBlankString
import ru.yarsu.db.DataBaseController
import ru.yarsu.web.domain.RolesEnums
import ru.yarsu.web.funs.lensOrNull

class UserBlockHandlerPOST(private val dataBaseController: DataBaseController) :
    HttpHandler {
    private val pathLens = Path.nonBlankString().of("id")

    override fun invoke(request: Request): Response {
        lensOrNull(pathLens, request)
            ?.let { login ->
                dataBaseController.getUserByLogin(login)
                    ?.let { user ->
                        if (RolesEnums.from(user.role) == RolesEnums.ADMIN) {
                            return Response(Status.UNAUTHORIZED)
                        }
                        dataBaseController.changeUserRole(user.id, RolesEnums.BLOCKED.id)
                        return Response(Status.FOUND).header("Location", "/users")
                    }
            } ?: return Response(Status.NOT_FOUND)
    }
}
