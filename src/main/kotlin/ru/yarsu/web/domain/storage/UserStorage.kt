package ru.yarsu.web.domain.storage

import ru.yarsu.web.domain.RolesEnums
import ru.yarsu.web.users.User
import ru.yarsu.web.users.UserInfo
import ru.yarsu.web.users.UserWithData
import java.time.LocalDateTime
import kotlin.collections.contains
import kotlin.math.ceil

class UserStorage(
    users: List<User>,
    val passwordHandler: PasswordHandler
) {
    private var usersMap: Map<String, User> = users.associateBy({ it.login }, { it })
    private var logins: Set<String> = users.map { it.login }.toSet()
    private var nicknames: Set<String> = users.map { it.nickName }.toSet()

    /*!
    Тип: действие (вызывает действие)
    Функция по получению пользователя по его логину.
    Является обёрткой к getUserFromStorage()
    Оперирует: строение User
     */
    fun getUser(login: String): User? = getUserFromStorage(login)

    /*!
    Тип: действие
    Функция по получению пользователя из хранилища по логину
    Оперирует: строение UserStorage
     */
    private fun getUserFromStorage(login: String): User? = usersMap[login]

    /*!
    Тип: действие (вызывает действие)
    Функция для изменения роли пользователя.
    Требует объект класса User
    Оперирует: строение User, строение UserStorage (транзитивно)
     */
    fun changeUserRole(user: User, roleNew: Int) {
        val updatedUser = user.copy(role = roleNew)
        addUserToStorage(updatedUser)
    }

    /*!
    Тип: действие
    Функция по добавлению/изменения объекта User в хранилище
    Оперирует: строение UserStorage
     */
    fun addUserToStorage(user: User) {
        usersMap = usersMap + (user.login to user)
        logins = logins + user.login
        nicknames = nicknames + user.nickName
    }

    /*!
    Тип: действие (вызывает действие)
    Функция по обновлению данных пользователя в хранилище
    Оперирует: строение User, строение UserStorage (транзитивно)
     */
    fun changeUser(user: User, nickname: String, password: String, about: String, role: Int) {
        val updatedUser = createCopyOfUser(user, nickname, password, about, role)
        addUserToStorage(updatedUser)
    }

    /*!
    Тип: вычисление
    Функция по созданию копии User с обновлёнными данными
    Оперирует: строение User
     */
    fun createCopyOfUser(user: User, nickname: String, password: String, about: String, role: Int): User {
        val passwordNew = passwordHandler.createNewPasswordOrReturnOld(password, user.password)
        return user.copy(
            nickName = nickname,
            password = passwordNew,
            about = about,
            role = role,
        )
    }

    /*!
    Тип: вычисление, конструктор
    Функция для создания объекта User
    Оперирует: строение User
     */
    fun createUser(
        nickname: String,
        login: String,
        password: String,
        about: String,
        date: LocalDateTime,
        role: Int = RolesEnums.AUTHOR.id,
    ): User {
        return User(
            nickname,
            login,
            password,
            about,
            date,
            role,
        )
    }

    /*!
    Тип: вычисление, конструктор
    Функция по созданию объекта User с созданием пароля.
    Является обёрткой для createUser()
    Оперирует: строение UserStorage (passwordHandler)
     */
    fun createUserWithPassword(
        nickname: String,
        login: String,
        password: String,
        about: String,
        date: LocalDateTime,
        role: Int = RolesEnums.AUTHOR.id,
    ): User {
        return createUser(
            nickname,
            login,
            passwordHandler.createPassword(password),
            about,
            date,
            role,
        )
    }

    /*!
    Тип: действие
    Функция по проверке существования логика среди хранилища.
    Является обёрткой для checkLogin()
    Оперирует: строение UserStorage
     */
    fun checkLoginInStorage(login: String?): Boolean {
        return checkLogin(login, logins)
    }

    /*!
    Тип: вычисление
    Функция по проверке существования логина среди предложенной коллекции
    Оперирует: бизнес-логика
     */
    fun checkLogin(login: String?, logins: Collection<String>): Boolean {
        return login in logins
    }

    /*!
    Тип: действие
    Функция по проверке уникальности никнейма пользователя среди хранилища.
    Является обёрткой для checkNickname()
    Оперирует: строение UserStorage
     */
    fun checkNicknameInStorage(nickname: String?, ignore: String? = null): Boolean {
        return checkNickname(nickname, ignore, nicknames)
    }

    /*!
    Тип: вычисление
    Функция по проверке уникальности никнейма пользователя среди предложенной коллекции
    Оперирует: бизнес-правило
     */
    fun checkNickname(nickname: String?, ignore: String? = null, nicksCollection: Collection<String>): Boolean {
        return nickname == ignore || nickname != null && nickname in nicksCollection
    }

    /*!
    Тип: действие
    Функция по созданию объекта UserInfo из User по логину.
    Является оболочкой для createUserInfo()
    Оперирует: строение User, строение UserStorage
     */
    fun getUserInfo(login: String): UserInfo {
        val user = usersMap[login]
        return createUserInfo(user)
    }

    /*!
    Тип: вычисление, конструктор
    Функция по созданию объекта UserInfo из объекта User
    Оперирует: строение User, строение UserInfo
     */
    fun createUserInfo(user: User?): UserInfo {
        return UserInfo(
            user?.nickName ?: "",
            user?.about ?: "Ошибка. Обратитесь к администратору.",
        )
    }

    /*!
    Тип: вычисление
    Функция по созданию коллекции объектов UserWithData из коллекции объектов User
    Оболочка для createUserWithData()
    Оперирует: ?
     */
    fun makeUsersWithData(users: List<User>): List<UserWithData> {
        val usersWithData = mutableListOf<UserWithData>()
        users.forEach { user ->
            usersWithData.add(
                createUserWithData(user)
            )
        }
        return usersWithData
    }

    /*!
    Тип: вычисление, конструктор
    Функция по созданию одного объекта класса UserWithData
    Оперирует: строение UserWithData
     */
    fun createUserWithData(user: User): UserWithData {
        return UserWithData(
            user,
            RolesEnums.from(user.role)?.role ?: "Аноним"
        )
    }

    /*!
    Тип: действие
    Функция для определения количества страниц для коллекции с User.
    Является оболочкой для getPageAmount()
    Оперирует: строение UserStorage
     */
    fun getPageAmountFromStorage(): Int{
        return getPageAmount(usersMap.values)
    }

    /*!
    Тип: вычисление
    Функция для определения количества страниц любой коллекции
    Оперирует: бизнес-правила
     */
    fun getPageAmount(collection: Collection<*>): Int {
        return ceil(collection.size.toDouble() / INPAGE_DOP).toInt()
    }

    /*!
    Тип: действие
    Функция по получению коллекции с User для определённого номера страницы.
    Является обёрткой для usersByPage()
    Оперирует: строение UserStorage
     */
    fun usersByPageNumberFromStorage(
        pageNumber: Int,
    ): List<User> {
        val users = usersMap.map { it.value }
        return usersByPage(pageNumber, users)
    }

    /*!
    Тип: вычисление
    Функция по сбору коллекции с User для определённого номера страницы из предложенной коллекции User.
    Оперирует: бизнес-правила, константой INPAGE_DOP
     */
    fun usersByPage(
        pageNumber: Int,
        userList: List<User>,
    ): List<User> {
        val resultList = mutableListOf<User>()
        for (i in 0..<INPAGE_DOP) {
            if (i + pageNumber * INPAGE_DOP < userList.size) {
                resultList.add(userList[i + pageNumber * INPAGE_DOP])
            }
        }
        return resultList
    }
}
