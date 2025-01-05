package ru.yarsu.web.users

import java.time.LocalDateTime

data class User(
    val nickName: String,
    val login: String,
    val password: String,
    val about: String,
    val dateAdd: LocalDateTime,
    val role: Int,
)
