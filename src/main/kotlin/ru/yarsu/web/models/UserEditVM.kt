package ru.yarsu.web.models

import org.http4k.lens.WebForm
import org.http4k.template.ViewModel
import ru.yarsu.web.domain.RolesEnums
import ru.yarsu.web.users.User

class UserEditVM(
    val form: WebForm,
    val user: User,
    val roles: List<RolesEnums>,
    val roleChosen: RolesEnums,
    val errors: List<String>,
    val errorStr: String,
) : ViewModel
