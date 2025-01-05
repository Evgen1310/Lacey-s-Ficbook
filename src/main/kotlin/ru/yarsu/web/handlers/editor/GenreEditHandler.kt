package ru.yarsu.web.handlers.editor

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
import ru.yarsu.web.funs.lensOrNull
import ru.yarsu.web.models.GenreEditVM

class GenreEditHandler(
    private val htmlView: ContextAwareViewRender,
    private val dataBaseController: DataBaseController,
) : HttpHandler {
    private val pathLens = Path.int().of("id")
    private val genreField = FormField.nonBlankString().required("genre")

    override fun invoke(request: Request): Response {
        lensOrNull(pathLens, request)
            ?.let { id ->
                dataBaseController.getGenreById(id)
                    .let { genre ->
                        if (genre.genre == "Не определено") {
                            return Response(Status.NOT_FOUND)
                        }
                        val viewModel =
                            GenreEditVM(
                                WebForm().with(
                                    genreField of genre.genre,
                                ),
                                genre.genre,
                                listOf(),
                                "",
                            )
                        return Response(Status.OK).with(htmlView(request) of viewModel)
                    }
            } ?: return Response(Status.NOT_FOUND)
    }
}
