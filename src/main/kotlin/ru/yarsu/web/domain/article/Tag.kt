package ru.yarsu.web.domain.article

import jakarta.persistence.*

@Table(name = "tags")
@Entity
class Tag(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    var idTag: Int = -1,
    @Column(name = "tag", nullable = false)
    var tag: String = "beda",
)
