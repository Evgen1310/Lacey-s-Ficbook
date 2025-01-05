package ru.yarsu.web.models

import org.http4k.template.ViewModel
import ru.yarsu.web.domain.Paginator
import ru.yarsu.web.users.UserWithData

class UsersListVM(
    val users: List<UserWithData>,
    val paginator: Paginator,
) : ViewModel
