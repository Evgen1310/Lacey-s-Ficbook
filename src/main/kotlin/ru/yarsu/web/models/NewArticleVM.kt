package ru.yarsu.web.models

import org.http4k.lens.WebForm
import org.http4k.template.ViewModel
import ru.yarsu.web.domain.article.AgeRatingBook
import ru.yarsu.web.domain.article.Form
import ru.yarsu.web.domain.article.Genre
import ru.yarsu.web.handlers.articles.ArticleCreationParams

class NewArticleVM(
    val form: WebForm,
    val ages: List<AgeRatingBook>,
    val forms: List<Form>,
    val genres: List<Genre>,
    val errors: List<String>,
    val params: ArticleCreationParams,
) : ViewModel

fun createArticleCreationVM(
    form: WebForm,
    forms: List<Form>,
    genres: List<Genre>,
    errors: List<String> = emptyList(),
    creationParams: ArticleCreationParams
) = NewArticleVM(
    form,
    AgeRatingBook.entries,
    forms,
    genres,
    errors,
    creationParams
)

fun handleArticleCreationError(
    form: WebForm,
    forms: List<Form>,
    genres: List<Genre>,
    creationParams: ArticleCreationParams
): NewArticleVM {
    val errorsName = form.errors.map { it.meta.name }
    return createArticleCreationVM(
        form,
        forms,
        genres,
        errorsName,
        creationParams
    )
}
