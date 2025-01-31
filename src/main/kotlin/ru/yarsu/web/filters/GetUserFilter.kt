package ru.yarsu.web.filters

import org.http4k.core.Filter
import org.http4k.core.HttpHandler
import org.http4k.core.cookie.cookie
import org.http4k.core.with
import org.http4k.lens.RequestContextLens
import ru.yarsu.db.DataBaseController
import ru.yarsu.web.jwt.JwtTools
import ru.yarsu.web.users.User

fun getUserFilter(
    userLens: RequestContextLens<User?>,
    dataBaseController: DataBaseController,
    jwtTools: JwtTools,
): Filter =
    Filter { next: HttpHandler ->
        { request ->
            val cookie = request.cookie("auth")
            val userNick = cookie?.let { jwtTools.checkToken(it.value) }
            userNick?.let { nick ->
                dataBaseController.getUserByLogin(nick)?.let { user ->
                    next(request.with(userLens of user))
                }
            } ?: next(request)
        }
    }
