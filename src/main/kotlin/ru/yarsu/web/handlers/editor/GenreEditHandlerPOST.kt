package ru.yarsu.web.handlers.editor

import org.http4k.core.Body
import org.http4k.core.HttpHandler
import org.http4k.core.Request
import org.http4k.core.Response
import org.http4k.core.Status
import org.http4k.core.with
import org.http4k.lens.FormField
import org.http4k.lens.Path
import org.http4k.lens.Validator
import org.http4k.lens.WebForm
import org.http4k.lens.int
import org.http4k.lens.nonBlankString
import org.http4k.lens.webForm
import ru.ac.uniyar.web.templates.ContextAwareViewRender
import ru.yarsu.db.DataBaseController
import ru.yarsu.web.funs.lensOrDefault
import ru.yarsu.web.funs.lensOrNull
import ru.yarsu.web.models.FormEditVM

class GenreEditHandlerPOST(
    private val htmlView: ContextAwareViewRender,
    private val dataBaseController: DataBaseController,
) : HttpHandler {
    private val pathLens = Path.int().of("id")
    private val genreField = FormField.nonBlankString().required("genre")
    private val formLens =
        Body.webForm(
            Validator.Feedback,
            genreField,
        ).toLens()

    override fun invoke(request: Request): Response {
        lensOrNull(pathLens, request)
            ?.let { id ->
                dataBaseController.getGenreById(id)
                    .let { genre ->
                        if (genre.genre == "Не определено") {
                            return Response(Status.NOT_FOUND)
                        }
                        val form = formLens(request)
                        val errors = checkErrors(form, genre.genre)
                        if (errors.isNotEmpty()) {
                            val viewModel =
                                FormEditVM(
                                    form,
                                    genre.genre,
                                    errors,
                                    errorString(errors),
                                )
                            return Response(Status.OK).with(htmlView(request) of viewModel)
                        }
                        dataBaseController.changeGenre(id, genreField(form))
                        return Response(Status.FOUND).header("Location", "/redaction/genres")
                    }
            } ?: return Response(Status.NOT_FOUND)
    }

    private fun errorString(errors: List<String>): String {
        var resultStr = "Некорректный ввод. Попробуй ещё раз!"
        if ("genreExist" in errors) {
            resultStr = "Такой жанр уже есть"
        }
        return resultStr
    }

    private fun checkErrors(
        form: WebForm,
        formArtOld: String,
    ): List<String> {
        val errors = form.errors.map { it.meta.name }.toMutableList()
        val genreNew = lensOrDefault(genreField, form, "")
        if (dataBaseController.checkGenre(genreNew, formArtOld)) {
            errors.add("genreExist")
        }
        return errors
    }
}
