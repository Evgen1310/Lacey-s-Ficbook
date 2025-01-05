package ru.yarsu.web.models

import org.http4k.template.ViewModel
import ru.yarsu.web.domain.article.ArticleWithData

class ArticleDeleteVM(
    val article: ArticleWithData,
    val tags: List<String>,
) : ViewModel
