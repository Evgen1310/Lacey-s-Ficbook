package ru.yarsu.web.models

import org.http4k.lens.WebForm
import org.http4k.template.ViewModel

class LoginVM(
    val form: WebForm,
    val errors: List<String>,
    val errorStr: String,
) : ViewModel
