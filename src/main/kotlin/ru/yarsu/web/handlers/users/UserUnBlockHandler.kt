package ru.yarsu.web.handlers.users

import org.http4k.core.HttpHandler
import org.http4k.core.Request
import org.http4k.core.Response
import org.http4k.core.Status
import org.http4k.core.with
import org.http4k.lens.Path
import org.http4k.lens.nonBlankString
import ru.ac.uniyar.web.templates.ContextAwareViewRender
import ru.yarsu.web.domain.RolesEnums
import ru.yarsu.web.domain.storage.UserStorage
import ru.yarsu.web.funs.lensOrNull
import ru.yarsu.web.models.UserUnBlockVM

class UserUnBlockHandler(private val htmlView: ContextAwareViewRender, private val userStorage: UserStorage) :
    HttpHandler {
    private val pathLens = Path.nonBlankString().of("id")
    override fun invoke(request: Request): Response {
        lensOrNull(pathLens, request)
            ?.let { login ->
                userStorage.getUser(login)
                    ?.let { user ->
                        if (RolesEnums.from(user.role) == RolesEnums.ADMIN)
                            return Response(Status.UNAUTHORIZED)
                        val viewModel = UserUnBlockVM(user, RolesEnums.from(user.role) ?: RolesEnums.ANONIMOUS)
                        return Response(Status.OK).with(htmlView(request) of viewModel)
                    }
            } ?: return Response(Status.NOT_FOUND)
    }
}
