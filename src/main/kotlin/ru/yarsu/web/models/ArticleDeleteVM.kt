package ru.yarsu.web.models

import org.http4k.template.ViewModel
import ru.yarsu.web.domain.article.Article
import ru.yarsu.web.domain.article.ArticleWithData
import ru.yarsu.web.domain.storage.AddonStorage

class ArticleDeleteVM(
    val article: ArticleWithData,
    val tags: List<String>,
) : ViewModel
