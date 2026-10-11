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

    /*!
    Тип: действие (вызывает действие)
    Функция по получению пользователя по его логину.
    Является обёрткой к getUserFromStorage()
    Оперирует: строение UserStorage
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
        usersMap = addUserAndGetStorage(user, usersMap)
    }

    /*!
    Тип: вычисление
    Функция по добавлению объекта User в подаваемую коллекцию
    Оперирует: бизнес-логика
     */
    fun addUserAndGetStorage(user: User, usersCollection: Map<String, User>) =
        usersCollection + (user.login to user)

    /*!
    Тип: действие
    Функция по получению всех занятых никнеймов у пользователей из хранилища
    Оперирует: структура UserStorage
     */
    fun getAllNicknames() = usersMap.values.map { it.nickName }.toSet()

    /*!
    Тип: действие
    Функция по получению всех занятых логинов у пользователей из хранилища
    Оперирует: структура UserStorage
     */
    fun getAllLogins() = usersMap.keys

    /*!
    Тип: действие (вызывает действие)
    Функция по обновлению данных пользователя в хранилище
    Оперирует: строение User, строение UserStorage (транзитивно)
     */
    fun changeUser(user: User, nickname: String, password: String, about: String, role: Int) {
        val passwordNew = passwordHandler.createNewPasswordOrReturnOld(password, user.password)
        val updatedUser = createCopyOfUser(user, nickname, passwordNew, about, role)
        addUserToStorage(updatedUser)
    }

    /*!
    Тип: вычисление
    Функция по созданию копии User с обновлёнными данными
    Оперирует: строение User
     */
    fun createCopyOfUser(user: User, nickname: String, password: String, about: String, role: Int): User {
        return user.copy(
            nickName = nickname,
            password = password,
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
    Тип: действие, конструктор
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
        return checkLogin(login, getAllLogins())
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
        return checkNickname(nickname, ignore, getAllNicknames())
    }

    /*!
    Тип: вычисление
    Функция по проверке уникальности никнейма пользователя среди предложенной коллекции.
    Является обёрткой для checkValueInCollectionsWithIgnore()
    Оперирует: бизнес-правило
     */
    fun checkNickname(nickname: String?, ignore: String? = null, nicksCollection: Collection<String>): Boolean {
        return checkValueInCollectionsWithIgnore(nickname, ignore, nicksCollection)
    }

    /*!
    Тип: действие
    Функция по созданию объекта UserInfo из User по логину.
    Является оболочкой для createUserInfo()
    Оперирует: строение User, строение UserStorage
     */
    fun getUserInfo(login: String): UserInfo {
        val user = getUserFromStorage(login)
        return createUserInfo(user?.login, user?.nickName, user?.about)
    }

    /*!
    Тип: вычисление, конструктор
    Функция по созданию объекта UserInfo из объекта User
    Оперирует: строение UserInfo
     */
    fun createUserInfo(login: String?, nickname: String?, about: String?): UserInfo {
        return UserInfo(
            login ?: "",
            nickname ?: "",
            about ?: "Ошибка. Обратитесь к администратору.",
        )
    }

    /*!
    Тип: вычисление
    Функция по созданию коллекции объектов UserWithData из коллекции объектов User
    Оболочка для createUserWithData()
    Оперирует: строение UserWithData
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
        return getPageAmount(INPAGE_DOP, usersMap.values)
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
    Является оболочкой valuesByPage()
    Оперирует: бизнес-правила, константой INPAGE_DOP
     */
    fun usersByPage(
        pageNumber: Int,
        userList: List<User>,
    ): List<User> {
        return valuesByPage(pageNumber, userList)
    }
}
