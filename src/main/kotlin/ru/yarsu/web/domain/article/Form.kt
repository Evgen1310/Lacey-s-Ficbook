package ru.yarsu.web.domain.article

import jakarta.persistence.*

@Table(name = "forms")
@Entity
class Form(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    var id: Int = -1,
    @Column(name = "form", nullable = false)
    var form: String = "beda",
)
