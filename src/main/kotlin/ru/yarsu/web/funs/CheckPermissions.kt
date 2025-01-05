package ru.yarsu.web.funs

import org.http4k.core.Status
import ru.yarsu.web.domain.Permissions

fun checkPermissions(
    permissions: Permissions,
    login: String? = null,
    articleUser: String = "",
): Status {
    if (articleUser != "") {
        if (!permissions.manageAllArticles) {
            if (!(permissions.manageArticle && articleUser == login)) {
                return Status.UNAUTHORIZED
            }
        }
    }
    if (permissions.isBlocked) {
        return Status.FORBIDDEN
    }
    return Status.OK
}
