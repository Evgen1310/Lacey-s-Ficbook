package ru.yarsu.web.domain

data class Permissions(
    val manageUsers: Boolean = false,
    val manageAllArticles: Boolean = false,
    val manageArticle: Boolean = false,
    val manageForms: Boolean = false,
    val isBlocked: Boolean = false,
) {
    companion object {
        private val ADMIN =
            Permissions(manageUsers = true, manageAllArticles = true, manageArticle = true, manageForms = true)
        private val EDITOR = Permissions(manageAllArticles = true, manageArticle = true, manageForms = true)
        private val AUTHOR = Permissions(manageArticle = true)
        private val BLOCKED = Permissions(isBlocked = true)
        private val ANONIMOUS = Permissions()

        fun getPermissions(role: RolesEnums?): Permissions {
            return when (role) {
                RolesEnums.ADMIN -> ADMIN
                RolesEnums.EDITOR -> EDITOR
                RolesEnums.AUTHOR -> AUTHOR
                RolesEnums.BLOCKED -> BLOCKED
                else -> ANONIMOUS
            }
        }
    }
}
