package ru.yarsu.web.domain.article

import java.time.LocalDateTime

data class Article(
    val dateAdd: LocalDateTime,
    val nameArt: String,
    val censorAge: Int,
    val formArt: Int,
    val tagsArt: List<Int>,
    val genre: Int,
    val annotation: String,
    val chapters: List<Int>,
    val id: Int,
    val user: String,
) {
    fun gAge(): String {
        return AgeRatingBook.from(this.censorAge)?.value ?: AgeRatingBook.Since18.value
    }
}
