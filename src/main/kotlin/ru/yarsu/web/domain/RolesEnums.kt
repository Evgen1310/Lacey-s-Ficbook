package ru.yarsu.web.domain

import ru.yarsu.web.domain.article.EnumFinder

enum class RolesEnums(val id: Int, val role: String) {
    ADMIN(0, "Админ"),
    EDITOR(1, "Редактор"),
    AUTHOR(2, "Автор"),
    BLOCKED(-2, "Заблокирован"),
    ANONIMOUS(-1, "Незарегистрирован"),
    ;

    companion object : EnumFinder<Int, RolesEnums>(entries.associateBy { it.id })
}
