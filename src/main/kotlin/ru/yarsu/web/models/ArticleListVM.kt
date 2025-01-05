package ru.yarsu.web.models

import org.http4k.template.ViewModel
import ru.yarsu.web.domain.Paginator
import ru.yarsu.web.domain.PebbleParams
import ru.yarsu.web.domain.article.AgeRatingBook
import ru.yarsu.web.domain.article.ArticleWithData
import ru.yarsu.web.domain.article.Form

class ArticleListVM(
    val articles: List<ArticleWithData>,
    val params: PebbleParams,
    val paginator: Paginator,
    val errorStrings: List<Int>,
    val ages: List<AgeRatingBook>,
    val forms: List<Form>,
) : ViewModel
