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
    Тип: действие (вызывает действие), des: вызов извне, не знает про структуру чего либо
    Функция по получению пользователя по его логину.
    Является обёрткой к getUserFromStorage()
    Оперирует: идентификатор User
     */
    fun getUser(login: String): User? = getUserFromStorage(login)

    /*!
    Тип: действие, des: действие внутри хранилища, знает структуру хранилища
    Функция по получению пользователя из хранилища по логину
    Оперирует: строение UserStorage
     */
    private fun getUserFromStorage(login: String): User? = getValueFromMap(login, usersMap)

    /*!
    Тип: действие (вызывает действие), des: вызов извне, не знает про структуру чего либо
    Функция для изменения роли пользователя.
    Требует объект класса User
    Оперирует:
     */
    fun changeUserRole(user: User, roleNew: Int) {
        changeUser(user, role = roleNew)
    }

    /*!
    Тип: действие
    Функция по добавлению/изменения объекта User в хранилище
    Оперирует: строение UserStorage
     */
    private fun addUserToStorage(user: User) {
        usersMap = addUserAndGetStorage(user, usersMap)
    }

    /*!
    Тип: вычисление
    Функция по добавлению объекта User в подаваемую коллекцию
    Оперирует: бизнес-логика
     */
    private fun addUserAndGetStorage(user: User, usersCollection: Map<String, User>) =
        addValueToMap(user.login, user, usersCollection)

    /*!
    Тип: действие, des: действие внутри хранилища,
    Функция по получению всех занятых никнеймов у пользователей из хранилища
    Оперирует: структура UserStorage
     */
    private fun getAllNicknames() = getAllNicknamesFromCollection(usersMap)

    /*!
    Тип: вычисление, des: работа с объектами User, знает про строение User
     */
    private fun getAllNicknamesFromCollection(collection: Map<String, User>)
        = collection.values.map { it.nickName }.toSet()

    /*!
    Тип: действие, des: действие внутри хранилища,
    Функция по получению всех занятых логинов у пользователей из хранилища
    Оперирует: структура UserStorage
     */
    private fun getAllLogins() = getAllLoginsFromCollection(usersMap)

    /*!
    Тип: вычисление, des: работа с объектами User, знает про строение User
     */
    private fun getAllLoginsFromCollection(collection: Map<String, User>)
            = getAllKeysFromMap(collection)

    /*!
    Тип: действие (вызывает действие), des: вызов извне, TODO: !!!знает про структуру User (user.password)!!!
    Функция по обновлению данных пользователя в хранилище
    Оперирует: строение User, строение UserStorage (транзитивно)
     */
    fun changeUser(
        user: User,
        nickname: String = user.nickName,
        password: String = "",
        about: String = user.about,
        role: Int = user.role
    ) {
        val passwordNew = passwordHandler.createNewPasswordOrReturnOld(password, user.password)
        val updatedUser = createCopyOfUser(user, nickname, passwordNew, about, role)
        addUserToStorage(updatedUser)
    }

    /*!
    Тип: вычисление
    Функция по созданию копии User с обновлёнными данными
    Оперирует: строение User
     */
    private fun createCopyOfUser(
        user: User,
        nickname: String = user.nickName,
        password: String = user.password,
        about: String = user.about,
        role: Int = user.role
    ): User {
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
    private fun createUser(
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
    private fun createUserWithPassword(
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
     */
    fun registerUser(
        nickname: String,
        login: String,
        password: String,
        about: String,
        date: LocalDateTime,
        role: Int = RolesEnums.AUTHOR.id,
    ) {
        val newUser = createUserWithPassword(nickname, login, password, about, date, role)
        addUserToStorage(newUser)
    }

    /*!
    Тип: действие, des: вызов из вне, не оперирует строением чего либо
    Функция по проверке существования логика среди хранилища.
    Является обёрткой для checkLogin()
    Оперирует: строение UserStorage
     */
    fun checkLogin(login: String?): Boolean {
        return checkLoginInStorage(login, getAllLogins())
    }

    /*!
    Тип: вычисление
    Функция по проверке существования логина среди предложенной коллекции
    Оперирует: бизнес-логика
     */
    private fun checkLoginInStorage(login: String?, logins: Collection<String>): Boolean {
        return checkValueInCollectionsWithIgnore(login, null, logins)
    }

    /*!
    Тип: действие
    Функция по проверке уникальности никнейма пользователя среди хранилища.
    Является обёрткой для checkNickname()
    Оперирует: строение UserStorage
     */
    fun checkNickname(nickname: String?, ignore: String? = null): Boolean {
        return checkNicknameInStorage(nickname, ignore, getAllNicknames())
    }

    /*!
    Тип: вычисление
    Функция по проверке уникальности никнейма пользователя среди предложенной коллекции.
    Является обёрткой для checkValueInCollectionsWithIgnore()
    Оперирует: бизнес-правило
     */
    private fun checkNicknameInStorage(nickname: String?, ignore: String? = null, nicksCollection: Collection<String>): Boolean {
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
    private fun createUserInfo(login: String?, nickname: String?, about: String?): UserInfo {
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
    private fun createUserWithData(user: User): UserWithData {
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
        return getPageAmount(INPAGE_DOP, getAllUsers())
    }

    /*!
    Тип: действие
     */
    private fun getAllUsers(): List<User>
        = getAllValuesFromMap(usersMap)

    /*!
    Тип: действие
    Функция по получению коллекции с User для определённого номера страницы.
    Является обёрткой для usersByPage()
    Оперирует: строение UserStorage
     */
    fun usersByPageNumberFromStorage(
        pageNumber: Int,
    ): List<User> {
        return usersByPage(pageNumber, getAllUsers())
    }

    /*!
    Тип: вычисление
    Функция по сбору коллекции с User для определённого номера страницы из предложенной коллекции User.
    Является оболочкой valuesByPage()
    Оперирует: бизнес-правила, константой INPAGE_DOP
     */
    private fun usersByPage(
        pageNumber: Int,
        userList: List<User>,
    ): List<User> {
        return valuesByPage(pageNumber, userList)
    }
}
