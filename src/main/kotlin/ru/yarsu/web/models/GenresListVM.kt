package ru.yarsu.web.models

import org.http4k.template.ViewModel
import ru.yarsu.web.domain.Paginator
import ru.yarsu.web.domain.article.Genre

class GenresListVM(
    val genres: List<Genre>,
    val paginator: Paginator,
) : ViewModel
