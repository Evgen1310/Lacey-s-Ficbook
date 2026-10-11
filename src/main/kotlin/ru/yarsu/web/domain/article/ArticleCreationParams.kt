package ru.yarsu.web.domain.article

data class ArticleCreationParams(
    val age: Int,
    val form: Int,
    val genre: Int,
)

fun createArticleCreationParams(age: Int = -1, form: Int = -1, genre: Int = -1) =
    ArticleCreationParams(age, form, genre)
