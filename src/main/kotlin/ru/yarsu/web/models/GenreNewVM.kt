package ru.yarsu.web.models

import org.http4k.lens.WebForm
import org.http4k.template.ViewModel
import ru.yarsu.web.domain.article.Form

class GenreNewVM(
    val form: WebForm,
    val errors: List<String>,
    val errorStr: String,
) : ViewModel
