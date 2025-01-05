package ru.yarsu.web.models

import org.http4k.template.ViewModel
import ru.yarsu.web.domain.Paginator
import ru.yarsu.web.domain.article.ArticleWithData
import ru.yarsu.web.domain.article.Chapter

class ArticleVM(
    val article: ArticleWithData,
    val tags: List<String>,
    val chapter: Chapter,
    val chapters: List<Chapter>,
    val paginator: Paginator,
) : ViewModel
