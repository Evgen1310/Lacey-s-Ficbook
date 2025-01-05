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
import ru.yarsu.web.models.FormNewVM

class FormNewHandlerPOST(private val htmlView: ContextAwareViewRender, private val addon: AddonStorage) :
    HttpHandler {
    private val formField = FormField.nonBlankString().required("form")
    private val formLens =
        Body.webForm(
            Validator.Feedback,
            formField,
        ).toLens()

    override fun invoke(request: Request): Response {
        val form = formLens(request)
        val errors = checkErrors(form)
        if (errors.isNotEmpty()) {
            val viewModel = FormNewVM(form, errors, errorString(errors))
            return Response(Status.OK).with(htmlView(request) of viewModel)
        }
        addon.addForm(formField(form))
        return Response(Status.FOUND).header("Location", "/redaction/forms")
    }

    private fun errorString(errors: List<String>): String {
        var resultStr = "Некорректный ввод. Попробуй ещё раз!"
        if ("formExist" in errors) {
            resultStr = "Такая форма уже заведена"
        }
        return resultStr
    }

    private fun checkErrors(form: WebForm): List<String> {
        val errors = form.errors.map { it.meta.name }.toMutableList()
        val formArtNew = lensOrDefault(formField, form, "")
        if (addon.checkForm(formArtNew))
            errors.add("formExist")
        return errors
    }
}
