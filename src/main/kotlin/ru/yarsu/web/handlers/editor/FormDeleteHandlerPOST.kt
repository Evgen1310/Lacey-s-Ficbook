package ru.yarsu.web.handlers.editor

import org.http4k.core.HttpHandler
import org.http4k.core.Request
import org.http4k.core.Response
import org.http4k.core.Status
import org.http4k.lens.Path
import org.http4k.lens.int
import ru.yarsu.db.DataBaseController
import ru.yarsu.web.funs.lensOrNull

class FormDeleteHandlerPOST(private val dataBaseController: DataBaseController) :
    HttpHandler {
    private val pathLens = Path.int().of("id")

    override fun invoke(request: Request): Response {
        lensOrNull(pathLens, request)
            ?.let { id ->
                dataBaseController.getFormById(id)
                    .let { formArt ->
                        if (formArt.form == "Не определено") {
                            return Response(Status.NOT_FOUND)
                        }
                        dataBaseController.removeForm(id)
                        return Response(Status.FOUND).header("Location", "/redaction/forms")
                    }
            } ?: return Response(Status.NOT_FOUND)
    }
}
