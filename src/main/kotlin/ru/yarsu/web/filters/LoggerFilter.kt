package ru.yarsu.web.filters

import org.http4k.core.Filter
import org.http4k.core.HttpHandler
import org.slf4j.Logger

fun loggerFilter(logger: Logger): Filter =
    Filter { next: HttpHandler ->
        { request ->
            val timeIn = System.currentTimeMillis()
            val response = next(request)
            val timeOut = System.currentTimeMillis()
            logger.atInfo().setMessage("Request")
                .addKeyValue("METHOD", request.method)
                .addKeyValue("URI", request.uri)
                .addKeyValue("SOURCE", request.source)
                .addKeyValue("STATUS", response.status)
                .addKeyValue("RESPONSE_TIME", timeOut - timeIn)
                .log()
            response
        }
    }
