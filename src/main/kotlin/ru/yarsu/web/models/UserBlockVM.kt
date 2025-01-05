package ru.yarsu.web.models

import org.http4k.lens.WebForm
import org.http4k.template.ViewModel
import ru.yarsu.web.domain.RolesEnums
import ru.yarsu.web.users.User

class UserBlockVM(
    val user: User,
    val role: RolesEnums,
) : ViewModel
