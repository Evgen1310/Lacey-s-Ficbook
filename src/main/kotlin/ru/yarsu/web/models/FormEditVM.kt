package ru.yarsu.web.models

import org.http4k.lens.WebForm
import org.http4k.template.ViewModel

class FormEditVM(
    val form: WebForm,
    val formArt: String,
    val errors: List<String>,
    val errorStr: String,
) : ViewModel
