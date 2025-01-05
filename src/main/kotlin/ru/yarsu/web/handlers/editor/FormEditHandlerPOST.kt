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
import ru.yarsu.web.domain.storage.AddonStorage
import ru.yarsu.web.funs.lensOrDefault
import ru.yarsu.web.funs.lensOrNull
import ru.yarsu.web.models.FormEditVM

class FormEditHandlerPOST(
    private val htmlView: ContextAwareViewRender,
    private val addonStorage: AddonStorage,
) : HttpHandler {

    private val pathLens = Path.int().of("id")
    private val newFormField = FormField.nonBlankString().required("newForm")
    private val formLens =
        Body.webForm(
            Validator.Feedback,
            newFormField,
        ).toLens()

    override fun invoke(request: Request): Response {
        lensOrNull(pathLens, request)
            ?.let { id ->
                addonStorage.getFormById(id)
                    .let { formArt ->
                        if (formArt == "Не выбрано")
                            return Response(Status.NOT_FOUND)
                        val form = formLens(request)
                        val errors = checkErrors(form, formArt)
                        if (errors.isNotEmpty()) {
                            val viewModel = FormEditVM(
                                form,
                                formArt,
                                errors,
                                errorString(errors),
                            )
                            return Response(Status.OK).with(htmlView(request) of viewModel)
                        }
                        addonStorage.changeForm(id, newFormField(form))
                        return Response(Status.FOUND).header("Location", "/redaction/forms")
                    }
            } ?: return Response(Status.NOT_FOUND)
    }

    private fun errorString(errors: List<String>): String {
        var resultStr = "Некорректный ввод. Попробуй ещё раз!"
        if ("formExist" in errors) {
            resultStr = "Такая форма уже заведена"
        }
        return resultStr
    }

    private fun checkErrors(form: WebForm, formArtOld: String): List<String> {
        val errors = form.errors.map { it.meta.name }.toMutableList()
        val formArtNew = lensOrDefault(newFormField, form, "")
        if (addonStorage.checkForm(formArtNew, formArtOld))
            errors.add("formExist")
        return errors
    }
}
