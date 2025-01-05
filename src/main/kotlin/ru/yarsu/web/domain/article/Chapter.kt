package ru.yarsu.web.domain.article

data class Chapter(
    val chapter: Int,
    val name: String,
    val content: String,
    val id: Int,
) {
    fun contentList(): List<String> = this.content.trim().replace("\n\n", "\n").split("\n")
}
