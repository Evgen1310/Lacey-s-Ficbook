package ru.yarsu.web.domain.article

data class Chapter(
    val chapter: Int,
    val name: String,
    val content: String,
    val id: Int,
) {
    fun contentList(): List<String> = this.content.trim().replace("\n\n", "\n").split("\n")
}

fun getDefaultChapter() =
    Chapter(
        0,
        "Пусто!",
        "На данный момент автором не было загружено ни одной главы.",
        -1
    )
