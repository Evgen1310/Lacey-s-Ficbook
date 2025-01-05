package ru.yarsu.web.domain.article

import jakarta.persistence.*

@Table(name = "chapters")
@Entity
class Chapter(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    var id: Int = -1,
    @Column(name = "chapter", nullable = false)
    var chapter: Int = -1,
    @Column(name = "name", nullable = false)
    var name: String = "beda",
    @Column(name = "content", length = 60000, nullable = false)
    var content: String = "beda",
) {
    fun contentList(): List<String> = this.content.trim().replace("\n\n", "\n").split("\n")
}
