package ru.yarsu.web.filters

import org.http4k.core.Filter
import org.http4k.core.HttpHandler
import org.http4k.core.with
import org.http4k.lens.RequestContextLens
import ru.yarsu.web.domain.Permissions
import ru.yarsu.web.domain.RolesEnums
import ru.yarsu.web.funs.lensOrNull
import ru.yarsu.web.users.User

fun getPermissionsFilter(
    userLens: RequestContextLens<User?>,
    permissionsLens: RequestContextLens<Permissions>,
): Filter =
    Filter { next: HttpHandler ->
        { request ->
            val user = lensOrNull(userLens, request)
            val permissions = Permissions.getPermissions(RolesEnums.from(user?.role ?: -1))
            next(request.with(permissionsLens of permissions))
        }
    }
