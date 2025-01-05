package ru.yarsu.web.domain.article

data class ArticleWithData(
    val article: Article,
    val formStr: String,
    val genreStr: String,
    val userNick: String,
    val userAbout: String,
    val chaptersOrg: List<Int>,
    val editArticle: Boolean,
)