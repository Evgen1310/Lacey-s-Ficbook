package ru.yarsu.web.models

import org.http4k.lens.WebForm
import org.http4k.template.ViewModel
import ru.yarsu.web.domain.article.AgeRatingBook
import ru.yarsu.web.domain.article.ArticleCreationParams
import ru.yarsu.web.domain.article.Genre
import ru.yarsu.web.domain.article.Form

class ArticleEditVM (
    val articleName: String,
    val form: WebForm,
    val ages: List<AgeRatingBook>,
    val forms: List<Form>,
    val genres: List<Genre>,
    val errors: List<String>,
    val params: ArticleCreationParams,
) : ViewModel

fun createArticleEditVM(
    articleName: String,
    form: WebForm,
    forms: List<Form>,
    genres: List<Genre>,
    errors: List<String> = emptyList(),
    creationParams: ArticleCreationParams
) = ArticleEditVM(
    articleName,
    form,
    AgeRatingBook.entries,
    forms,
    genres,
    errors,
    creationParams
)

fun handleArticleEditError(
    articleName: String,
    form: WebForm,
    forms: List<Form>,
    genres: List<Genre>,
    errors: List<String>,
    creationParams: ArticleCreationParams
): ArticleEditVM {
    return createArticleEditVM(
        articleName,
        form,
        forms,
        genres,
        errors,
        creationParams
    )
}
