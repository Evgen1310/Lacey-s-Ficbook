package ru.yarsu.web.models

import org.http4k.lens.WebForm
import org.http4k.template.ViewModel

class GenreEditVM(
    val form: WebForm,
    val genre: String,
    val errors: List<String>,
    val errorStr: String,
) : ViewModel