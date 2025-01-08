package ru.yarsu.db

import org.junit.jupiter.api.Test

import org.assertj.core.api.Assertions.assertThat
import ru.yarsu.config.readConfigurations
import ru.yarsu.web.domain.Params
import ru.yarsu.web.domain.RolesEnums
import ru.yarsu.web.domain.article.*
import ru.yarsu.web.users.User

class DataBaseTest {
    private val appConfig = readConfigurations()
    private val dataBaseController = DataBaseController(authSalt = appConfig.dbConfig.authSalt)

    /**
     * Проверка корректного получения статей по их идентификатору.
     * Ожидаемый результат: статья получена и её идентификатор совпадает с желаемым или статья не будет получена.
     */
    @Test
    fun getArticleByIdTest() {
        for (i in 1..10) {
            val art = dataBaseController.getArticleById(i)
            art?.let { assertThat(it.id).isEqualTo(i) } ?: assertThat(false).isFalse()
        }
    }

    /**
     * Проверка корректной работы добавления статьи и её последующего удаления по идентификатору.
     * Ожидаемый результат: идентификатор добавленной статьи будет больше -1,
     * после удаления статьи не будет в базе данных.
     */
    @Test
    fun addAndDeleteArticleTest() {
        val art = Article(nameArt = "iamtest")
        val id = dataBaseController.addArticle(art)
        assertThat(id).isGreaterThan(-1)
        dataBaseController.deleteArticle(id)
        assertThat(dataBaseController.getArticleById(id)).isNull()
    }

    /**
     * Проверка корректной работы получения отфильтрованных статей по странице.
     * Ожидаемый результат: длина полученного списка не превосходит количество статей на странице.
     */
    @Test
    fun getArticlesByPageLengthTest() {
        val params = Params(listOf(), listOf(), false)
        val arts = dataBaseController.sortedArticlesByPageNumber(params, 2).articles
        assertThat(arts.size).isLessThanOrEqualTo(INPAGE_ARTICLE)
    }

    /**
     * Проверка корректной работы получения отфильтрованных статей по странице при непустых параметрах.
     * Ожидаемый результат: полученные статьи соответствуют выбранному цензору и форме.
     */
    @Test
    fun getArticlesByPageParamsTest() {
        val censor = listOf(1,2,3)
        val forms = listOf(3,4,5)
        val params = Params(censor, forms, false)
        val arts = dataBaseController.sortedArticlesByPageNumber(params, 2).articles
        for (art in arts) {
            assertThat(art.censorAge in censor).isTrue()
            assertThat(art.formArt in forms).isTrue()
        }
    }

    /**
     * Проверка корректности работы редактирования статьи.
     * Ожидаемый результат: тестовая статья будет добавлена, после редактирования поля статьи поменяются.
     */
    @Test
    fun ediArticleEdit() {
        val art = Article(
            nameArt = "nameOld",
            formArt = 1,
            genre = 1,
            chapters = mutableListOf(1,2,3),
            tagsArt = mutableListOf(1,2,3),
            censorAge = 1,
            annotation = "descOld"
        )
        val new = Article(
            nameArt = "nameNew",
            formArt = 2,
            genre = 2,
            censorAge = 2,
            annotation = "descNew"
        )
        val id = dataBaseController.addArticle(art)
        dataBaseController.prepareArticle(id, new, mutableListOf(4), mutableListOf(4))
        val getArt = dataBaseController.getArticleById(id)
        dataBaseController.deleteArticle(id)
        getArt?.let {
            dataBaseController.deleteArticle(id)
            assertThat(it.nameArt == "nameNew").isTrue()
            assertThat(it.formArt == 2).isTrue()
            assertThat(it.genre == 2).isTrue()
            assertThat(it.censorAge == 2).isTrue()
            assertThat(it.annotation == "descNew").isTrue()
        } ?: assertThat(true).isFalse()

    }

    /**
     * Проверка корректности работы получения формы по идентификатору.
     * Ожидаемый результат: форма будет получена с тем же идентификатором или не будет получена вовсе.
     */
    @Test
    fun getFormByIdTest() {
        for(i in 1..10) {
            val form = dataBaseController.getFormById(i)
            assertThat(form.id in listOf(-1, i)).isTrue()
        }
    }
    /**
     * Проверка корректности работы получения формы при отсутствии таковой в базе данных.
     * Ожидаемое поведение: вернётся форма с названием "Не определено" и идентификатором -1.
     */
    @Test
    fun getNonExistForm() {
        val form = dataBaseController.getFormById(-100)
        assertThat(form.form == "Не определено").isTrue()
        assertThat(form.id == -1).isTrue()
    }

    /**
     * Проверка корректности работы получения жанра при отсутствии такового в базе данных.
     * Ожидаемое поведение: вернётся жанр с названием "Не определено" и идентификатором -1.
     */
    @Test
    fun getNonExistGenre() {
        val genre = dataBaseController.getGenreById(-100)
        assertThat(genre.genre == "Не определено").isTrue()
        assertThat(genre.id == -1).isTrue()
    }

    /**
     * Проверка корректности работы получения жанра по идентификатору.
     * Ожидаемый результат: жанр будет получен с тем же идентификатором или не будет получен вовсе.
     */
    @Test
    fun getGenreByIdTest() {
        for(i in 1..10) {
            val genre = dataBaseController.getGenreById(i)
            assertThat(genre.id in listOf(-1, i)).isTrue()
        }
    }

    /**
     * Проверка корректности работы добавления новой формы и последующего её удаления.
     * Ожидаемый результат: форма будет добавлена и её идентификатор не меньше 0,
     * после удаления формы не будет в базе данных.
     */
    @Test
    fun addAndDeleteFormTest() {
        val form = Form(form = "iamtest")
        val id = dataBaseController.addForm(form.form)
        assertThat(id > -1).isTrue()
        val getForm = dataBaseController.getFormById(id)
        assertThat(getForm.id == id).isTrue()
        dataBaseController.removeForm(id)
        assertThat(dataBaseController.getFormById(id).id == -1).isTrue()
    }

    /**
     * Проверка корректности работы добавления нового жанра и последующего его удаления.
     * Ожидаемый результат: жанр будет добавлен и его идентификатор не меньше 0,
     * после удаления жанра не будет в базе данных.
     */
    @Test
    fun addAndDeleteGenreTest() {
        val form = Form(form = "iamtest")
        val id = dataBaseController.addForm(form.form)
        assertThat(id > -1).isTrue()
        val getForm = dataBaseController.getFormById(id)
        assertThat(getForm.id == id).isTrue()
        dataBaseController.removeForm(id)
        assertThat(dataBaseController.getFormById(id).id == -1).isTrue()
    }
    /**
     * Проверка корректности работы по изменению формы.
     * Ожидаемый результат: тестовая форма будет добавлена, после редактирования поля формы поменяются.
     */
    @Test
    fun editFormTest() {
        val form = Form(form = "iamold")
        val id = dataBaseController.addForm(form.form)
        form.form = "iamold"
        dataBaseController.changeForm(id, "iamold")
        assertThat(dataBaseController.getFormById(id).form == "iamold").isTrue()
        dataBaseController.removeForm(id)
    }

    /**
     * Проверка корректности работы по изменению жанра.
     * Ожидаемый результат: тестовый жанр будет добавлен, после редактирования поля жанра поменяются.
     */
    @Test
    fun editGenreTest() {
        val genre = Genre(genre = "iamold")
        val id = dataBaseController.addGenre(genre.genre)
        genre.genre = "iamold"
        dataBaseController.changeGenre(id, "iamold")
        assertThat(dataBaseController.getGenreById(id).genre == "iamold").isTrue()
        dataBaseController.removeGenre(id)
    }
    /**
     * Проверка корректности работы получения тегов по их идентификаторам.
     * Ожидаемый результат: идентификаторы тегов из полученного списка входят в изначальный список.
     */
    @Test
    fun getTagsByIdsTest() {
        val ids = listOf(1,2,3,4)
        val tags = dataBaseController.getTagsByIds(ids)
        for (tag in tags) {
            assertThat(tag.idTag in ids).isTrue()
        }
    }

    /**
     * Проверка корректности работы при добавлении нового тега и его последующего удаления.
     * Ожидаемый результат: идентификатор нового тега не меньше 0, удалённый тег не находится в базе данных.
     */
    @Test
    fun addAndDeleteTagTest() {
        val tag = Tag(tag = "test")
        val id = dataBaseController.addTag(tag.tag)
        assertThat(id > -1).isTrue()
        dataBaseController.deleteTag(tag.tag)
        assertThat(dataBaseController.getTagsByIds(listOf(id)).isEmpty()).isTrue()
    }

    /**
     * Проверка корректности работы при добавлении уже находящегося в базе данных тега и его последующего удаления.
     * Ожидаемый результат: идентификатор, полученный при повторной попытке занесения, равен первоначальному,
     * удалённый тег не находится в базе данных.
     */
    @Test
    fun addAgainAndDeleteTagTest() {
        val tag = Tag(tag = "test")
        val id = dataBaseController.addTag(tag.tag)
        val idNew = dataBaseController.addTag(tag.tag)
        assertThat(idNew == id).isTrue()
        dataBaseController.deleteTag(tag.tag)
        assertThat(dataBaseController.getTagsByIds(listOf(id)).isEmpty()).isTrue()
    }
    /**
     * Проверка корректности работы получения глав по их идентификаторам.
     * Ожидаемый результат: идентификаторы глав из полученного списка входят в изначальный список.
     */
    @Test
    fun getChaptersByIdsTest() {
        val ids = listOf(1,2,3,4)
        val chapters = dataBaseController.getChaptersByIds(ids)
        for (chapter in chapters) {
            assertThat(chapter.id in ids).isTrue()
        }
    }

    /**
     * Проверка корректности работы при добавлении новой главы и её последующего удаления.
     * Ожидаемый результат: идентификатор новой главы не меньше 0, удалённая глава не находится в базе данных.
     */
    @Test
    fun addAndDeleteChapterTest() {
        val chapter = Chapter(name = "test")
        val id = dataBaseController.addChapter(chapter.chapter, chapter.name, chapter.content)
        assertThat(id > -1).isTrue()
        dataBaseController.removeChapters(listOf(id))
        assertThat(dataBaseController.getChaptersByIds(listOf(id))).isEmpty()
    }
    /**
     * Проверка корректной работы получения форм по странице.
     * Ожидаемый результат: длина полученного списка не превосходит количество форм на странице.
     */
    @Test
    fun getFormsByPageLengthTest() {
        val forms = dataBaseController.formsByPageNumber(1)
        assertThat(forms.size).isLessThanOrEqualTo(INPAGE_DOP)
    }

    /**
     * Проверка корректной работы получения форм по странице.
     * Ожидаемый результат: длина полученного списка не превосходит количество форм на странице.
     */
    @Test
    fun getGenresByPageLengthTest() {
        val genres = dataBaseController.genresByPageNumber(1)
        assertThat(genres.size).isLessThanOrEqualTo(INPAGE_DOP)
    }

    /**
     * Проверка корректности работы полчения пользователя по его логину.
     * Ожиаемый результат: логин полученного пользователя совпадает с запрашиваемым или не будет получен вовсе.
     */
    @Test
    fun getUserByLoginTest() {
        val login = "admin"
        val user = dataBaseController.getUserByLogin(login)
        user?.let {
            assertThat(it.login == login).isTrue()
        } ?: assertThat(false).isFalse()
    }

    /**
     * Проверка корректности работы по созданию и помещению в базу данных пользователя и последующее его удаление.
     * Ожидаемый результат: запись о пользователе будет занесена в базу данных, а позже удалена.
     */
    @Test
    fun addAndDeleteUserTest() {
        val user = dataBaseController.createUser("iamtest", "testPass", "testNick", "testAbout")
        dataBaseController.addUser(user)
        val userGet = dataBaseController.getUserByLogin(user.login)
        userGet?.let {
            assertThat(user.password == it.password).isTrue()
            assertThat(user.login == it.login).isTrue()
        } ?: assertThat(false).isTrue()
        dataBaseController.deleteUser(user.login)
        assertThat(dataBaseController.getUserByLogin(user.login)).isNull()
    }
    /**
     * Проверка корректности работы изменения данных пользователя.
     * Ожидаемый результат: тестовый пользователь будет добавлен, после редактирования поля пользователя поменяются.
     */
    @Test
    fun editUserTest() {
        val user = User(
            role = RolesEnums.BLOCKED.id,
            nickName = "testNick",
            login = "testLogin",
            password = "testPass",
            about = "testAbout",
        )
        val newUser = User(
            role = RolesEnums.AUTHOR.id,
            nickName = "Nick",
            password = "Pass",
            about = "About",
        )
        val id = dataBaseController.addUser(user)
        dataBaseController.changeUser(id, newUser.nickName, newUser.password, newUser.about, newUser.role)
        val getUser = dataBaseController.getUserById(id)
        getUser?.let {
            assertThat(dataBaseController.checkPassword(newUser.password, it.password)).isTrue()
            assertThat(it.about == newUser.about).isTrue()
            assertThat(it.nickName == newUser.nickName).isTrue()
            assertThat(it.role == newUser.role).isTrue()
        } ?: assertThat(false).isTrue()
        dataBaseController.deleteUser(newUser.login)
    }

    /**
     * Проверка корректности работы изменения роли пользователя.
     * Ожидаемый результат: тестовый пользователь будет добавлен, после редактирования поле роль пользователя поменяется.
     */
    @Test
    fun editUserRoleTest() {
        val user = User(
            role = RolesEnums.BLOCKED.id,
            nickName = "testNick",
            login = "testLogin",
            password = "testPass",
            about = "testAbout",
        )
        val id = dataBaseController.addUser(user)
        dataBaseController.changeUserRole(id, RolesEnums.AUTHOR.id)
        val getUser = dataBaseController.getUserByLogin(user.login)
        getUser?.let {
            assertThat(it.role == RolesEnums.AUTHOR.id).isTrue()
        } ?: assertThat(false).isTrue()
        dataBaseController.deleteUser(user.login)
    }

    /**
     * Проверка корректной проверки на существование логина в базе данных.
     * Ожидаемый результат: true, если логин уже существует, иначе - false
     */
    @Test
    fun checkLoginTest() {
        val login = "admin"
        assertThat(dataBaseController.checkLogin(login)).isTrue()
    }

    /**
     * Проверка корректной проверки на существование никнейма в базе данных.
     * Ожидаемый результат: true, если никнейм уже существует, иначе - false
     */
    @Test
    fun checkNicknameTest() {
        val nickname = "admin"
        assertThat(dataBaseController.checkNickname(nickname)).isTrue()
    }
    /**
     * Проверка корректной работы получения пользователей по странице.
     * Ожидаемый результат: длина полученного списка не превосходит количество форм на странице.
     */
    @Test
    fun getUsersByPageLengthTest() {
        val genres = dataBaseController.usersByPageNumber(1)
        assertThat(genres.size).isLessThanOrEqualTo(INPAGE_DOP)
    }
}
