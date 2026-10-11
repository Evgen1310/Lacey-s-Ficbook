package ru.yarsu.web.funs

import org.http4k.core.Status
import ru.yarsu.web.domain.Permissions

fun checkPermissionForArticleEditing(permissions: Permissions, login: String? = null, articleUser: String = ""): Status {
    if (permissions.manageAllArticles) {
        if (permissions.manageArticle && articleUser == login) {
            return Status.OK
        }
    }
    if (permissions.isBlocked)
        return Status.FORBIDDEN
    return Status.UNAUTHORIZED
}
