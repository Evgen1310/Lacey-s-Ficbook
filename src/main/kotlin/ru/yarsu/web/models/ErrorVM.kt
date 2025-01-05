package ru.yarsu.web.models

import org.http4k.template.ViewModel

class ErrorVM(
    val uri: String,
    val mode: Int,
) : ViewModel
