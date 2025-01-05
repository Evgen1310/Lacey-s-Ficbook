package ru.yarsu.web.models

import org.http4k.template.ViewModel
import ru.yarsu.web.domain.Paginator
import ru.yarsu.web.domain.article.Article
import ru.yarsu.web.domain.article.ArticleWithData
import ru.yarsu.web.domain.article.Chapter
import ru.yarsu.web.domain.storage.AddonStorage

class ArticleVM(
    val article: ArticleWithData,
    val tags: List<String>,
    val chapter: Chapter,
    val chapters: List<Chapter>,
    val paginator: Paginator,
) : ViewModel
