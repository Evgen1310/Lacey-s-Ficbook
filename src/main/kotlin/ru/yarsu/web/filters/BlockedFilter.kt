package ru.yarsu.web.filters

import org.http4k.core.Filter
import org.http4k.core.HttpHandler
import org.http4k.core.Response
import org.http4k.core.Status
import org.http4k.lens.RequestContextLens
import ru.yarsu.web.domain.Permissions

fun blockedFilter(
    permissionsLens: RequestContextLens<Permissions>,
): Filter =
    Filter { next: HttpHandler ->
        { request ->
            if (permissionsLens(request).isBlocked)
                Response(Status.FORBIDDEN).header("Location", "/")
            else
                next(request)
        }
    }
