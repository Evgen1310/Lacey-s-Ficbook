package ru.yarsu.web.handlers.editor

import org.http4k.core.Body
import org.http4k.core.HttpHandler
import org.http4k.core.Request
import org.http4k.core.Response
import org.http4k.core.Status
import org.http4k.core.with
import org.http4k.lens.FormField
import org.http4k.lens.Validator
import org.http4k.lens.WebForm
import org.http4k.lens.nonBlankString
import org.http4k.lens.webForm
import ru.ac.uniyar.web.templates.ContextAwareViewRender
import ru.yarsu.web.domain.storage.AddonStorage
import ru.yarsu.web.funs.lensOrDefault
import ru.yarsu.web.models.GenreNewVM

class GenreNewHandlerPOST(private val htmlView: ContextAwareViewRender, private val addon: AddonStorage) :
    HttpHandler {
    private val genreField = FormField.nonBlankString().required("genre")
    private val formLens =
        Body.webForm(
            Validator.Feedback,
            genreField,
        ).toLens()

    override fun invoke(request: Request): Response {
        val form = formLens(request)
        val errors = checkErrors(form)
        if (errors.isNotEmpty()) {
            val viewModel = GenreNewVM(form, errors, errorString(errors))
            return Response(Status.OK).with(htmlView(request) of viewModel)
        }
        addon.addGenre(genreField(form))
        return Response(Status.FOUND).header("Location", "/redaction/genres")
    }

    private fun errorString(errors: List<String>): String {
        var resultStr = "Некорректный ввод. Попробуй ещё раз!"
        if ("genreExist" in errors) {
            resultStr = "Такой жанр уже заведён"
        }
        return resultStr
    }

    private fun checkErrors(form: WebForm): List<String> {
        val errors = form.errors.map { it.meta.name }.toMutableList()
        val genreNew = lensOrDefault(genreField, form, "")
        if (addon.checkGenre(genreNew))
            errors.add("genreExist")
        return errors
    }
}
