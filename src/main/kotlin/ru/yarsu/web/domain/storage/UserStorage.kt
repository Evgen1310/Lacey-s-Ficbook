package ru.yarsu.web.domain.storage

import ru.yarsu.web.users.User

class UserStorage(
    private val users: List<User>,
    private val authSalt: String,
) {
//    private val usersMap: MutableMap<String, User> = users.associateBy({ it.login }, { it }).toMutableMap()
//    private val logins: MutableSet<String> = users.map { it.login }.toMutableSet()
//    private val nicknames: MutableSet<String> = users.map { it.nickName }.toMutableSet()
//
//    fun getUser(login: String): User? = usersMap[login]
//
//    fun changeUserRole(
//        user: User,
//        roleNew: Int,
//    ) {
//        usersMap[user.login] = user.copy(role = roleNew)
//    }
//
//    fun changeUser(
//        user: User,
//        nicknameNew: String,
//        passwordNew: String,
//        aboutNew: String,
//        roleNew: Int,
//    ) {
//        nicknames.remove(user.nickName)
//        var password = user.password
//        if (passwordNew != "") {
//            password = createPassword(passwordNew)
//        }
//        usersMap[user.login] =
//            user.copy(
//                nickName = nicknameNew,
//                password = password,
//                about = aboutNew,
//                role = roleNew,
//            )
//        nicknames.add(nicknameNew)
//    }
//
//    fun createUser(
//        login: String,
//        password: String,
//        nickname: String,
//        about: String,
//    ): User {
//        return User(
//            nickname,
//            login,
//            createPassword(password),
//            about,
//            LocalDateTime.now(),
//            RolesEnums.AUTHOR.id,
//        )
//    }
//
//    private fun createPassword(password: String): String {
//        val commaFormat = HexFormat.of()
//        val saltedBytes = (password + authSalt).toByteArray(charset("UTF-8"))
//        val messageDigest = MessageDigest.getInstance("SHA-256")
//        val digest = messageDigest.digest(saltedBytes)
//        return commaFormat.formatHex(digest)
//    }
//
//    fun addUser(user: User) {
//        usersMap[user.login] = user
//        logins.add(user.login)
//        nicknames.add(user.nickName)
//    }
//
//    fun checkLogin(
//        login: String?,
//        ignore: String? = null,
//    ): Boolean {
//        val newLogins = logins.toList().toMutableList()
//        newLogins.remove(ignore)
//        return login in newLogins
//    }
//
//    fun checkNickname(
//        nickname: String?,
//        ignore: String? = null,
//    ): Boolean {
//        val newNicknames = nicknames.toList().toMutableList()
//        newNicknames.remove(ignore)
//        return nickname in newNicknames
//    }
//
//    fun checkPassword(
//        passwordIn: String,
//        passwordOld: String,
//    ) = createPassword(passwordIn) == passwordOld
//
//    fun getAllUsers() = usersMap.map { it.value }
//
//    fun getUserInfo(login: String): UserInfo {
//        val user = usersMap[login]
//        return UserInfo(
//            user?.nickName ?: "",
//            user?.about ?: "Ошибка. Обратитесь к администратору.",
//        )
//    }
//
//    fun makeUsersWithData(users: List<User>): List<UserWithData> {
//        val usersWithData = mutableListOf<UserWithData>()
//        users.forEach { user ->
//            usersWithData.add(
//                UserWithData(
//                    user,
//                    RolesEnums.from(user.role)?.role ?: "Аноним",
//                ),
//            )
//        }
//        return usersWithData
//    }
//
//    fun getPageAmount(): Int {
//        return ceil(usersMap.size.toDouble() / INPAGE_DOP).toInt()
//    }
//
//    fun usersByPageNumber(pageNumber: Int): List<User> {
//        val users = usersMap.map { it.value }
//        val resultList = mutableListOf<User>()
//        for (i in 0..<INPAGE_DOP) {
//            if (i + pageNumber * INPAGE_DOP < users.size) {
//                resultList.add(users[i + pageNumber * INPAGE_DOP])
//            }
//        }
//        return resultList
//    }
}
