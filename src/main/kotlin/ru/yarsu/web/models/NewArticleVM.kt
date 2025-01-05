package ru.yarsu.web.models

import org.http4k.lens.WebForm
import org.http4k.template.ViewModel
import ru.yarsu.web.domain.article.AgeRatingBook
import ru.yarsu.web.domain.article.Form
import ru.yarsu.web.domain.article.Genre
import ru.yarsu.web.handlers.articles.IntParams

class NewArticleVM(
    val form: WebForm,
    val ages: List<AgeRatingBook>,
    val forms: List<Form>,
    val genres: List<Genre>,
    val errors: List<String>,
    val params: IntParams,
) : ViewModel
