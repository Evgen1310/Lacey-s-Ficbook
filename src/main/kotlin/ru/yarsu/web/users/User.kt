package ru.yarsu.web.users

import jakarta.persistence.*
import java.time.LocalDateTime

@Table(name = "users")
@Entity
class User(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    var id: Int = -1,
    @Column(name = "role", nullable = false)
    var role: Int = -1,
    @Column(name = "nickname", nullable = false)
    var nickName: String = "beda",
    @Column(name = "login", nullable = false)
    var login: String = "beda",
    @Column(name = "password", nullable = false)
    var password: String = "beda",
    @Column(name = "about", nullable = false)
    var about: String = "beda",
    @Column(name = "dateAdd", nullable = false)
    var dateAdd: LocalDateTime = LocalDateTime.now(),
)
