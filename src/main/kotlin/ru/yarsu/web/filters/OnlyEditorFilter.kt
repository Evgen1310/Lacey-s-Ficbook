package ru.yarsu.web.filters

import org.http4k.core.Filter
import org.http4k.core.HttpHandler
import org.http4k.core.Response
import org.http4k.core.Status
import org.http4k.core.cookie.cookie
import org.http4k.core.with
import org.http4k.lens.RequestContextLens
import ru.yarsu.web.domain.Permissions
import ru.yarsu.web.domain.storage.UserStorage
import ru.yarsu.web.jwt.JwtTools
import ru.yarsu.web.users.User

fun onlyEditorFilter(
    permissionsLens: RequestContextLens<Permissions>,
): Filter =
    Filter { next: HttpHandler ->
        { request ->
            if (!permissionsLens(request).manageAllArticles)
                Response(Status.UNAUTHORIZED)
            else
                next(request)
        }
    }
