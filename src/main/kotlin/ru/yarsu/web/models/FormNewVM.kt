package ru.yarsu.web.models

import org.http4k.lens.WebForm
import org.http4k.template.ViewModel
import ru.yarsu.web.domain.article.Form

class FormNewVM(
    val form: WebForm,
    val errors: List<String>,
    val errorStr: String,
) : ViewModel
