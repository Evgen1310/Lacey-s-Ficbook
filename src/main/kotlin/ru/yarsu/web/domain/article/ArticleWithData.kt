package ru.yarsu.web.domain.article

data class ArticleWithData(
    var article: Article,
    var formStr: String,
    var genreStr: String,
    var userNick: String,
    var userAbout: String,
    var chaptersOrg: List<Int>,
    var editArticle: Boolean,
)
