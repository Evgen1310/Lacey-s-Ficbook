package ru.yarsu.web.domain.article

import jakarta.persistence.*

@Table(name = "genres")
@Entity
class Genre(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    var id: Int = -1,
    @Column(name = "genre", nullable = false)
    var genre: String = "beda",
)
