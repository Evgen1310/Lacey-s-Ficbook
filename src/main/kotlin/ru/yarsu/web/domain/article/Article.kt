package ru.yarsu.web.domain.article

import jakarta.persistence.*
import java.time.LocalDateTime

@Table(name = "articles")
@Entity
class Article(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    var id: Int = -1,
    @Column(name = "name", nullable = false)
    var nameArt: String = "beda",
    @Column(name = "annotation", nullable = false)
    var annotation: String = "beda",
    @ElementCollection
    @CollectionTable(name = "article_chapters", joinColumns = [JoinColumn(name = "article_id")])
    @Column(name = "chapter", nullable = false)
    var chapters: MutableList<Int> = mutableListOf(),
    @Column(name = "censorAge", nullable = false)
    var censorAge: Int = -1,
    @Column(name = "formArt", nullable = false)
    var formArt: Int = -1,
    @ElementCollection
    @CollectionTable(name = "article_tags", joinColumns = [JoinColumn(name = "article_id")])
    @Column(name = "tag", nullable = false)
    var tagsArt: MutableList<Int> = mutableListOf(),
    @Column(name = "genre", nullable = false)
    var genre: Int = -1,
    @Column(name = "dateAdd", nullable = false)
    var dateAdd: LocalDateTime = LocalDateTime.now(),
    @Column(name = "user", nullable = false)
    var user: String = "",
) {
    fun gAge(): String {
        return AgeRatingBook.from(this.censorAge)?.value ?: AgeRatingBook.Since18.value
    }
}