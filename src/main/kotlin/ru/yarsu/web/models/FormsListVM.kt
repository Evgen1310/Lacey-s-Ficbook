package ru.yarsu.web.models

import org.http4k.template.ViewModel
import ru.yarsu.web.domain.Paginator
import ru.yarsu.web.domain.article.Form

class FormsListVM(
    val forms: List<Form>,
    val paginator: Paginator,
) : ViewModel
