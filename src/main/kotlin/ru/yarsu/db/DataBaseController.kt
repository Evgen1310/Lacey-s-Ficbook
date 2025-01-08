package ru.yarsu.db

import org.hibernate.Session
import org.hibernate.SessionFactory
import org.hibernate.boot.registry.StandardServiceRegistryBuilder
import org.hibernate.cfg.Configuration
import ru.yarsu.web.domain.Params
import ru.yarsu.web.domain.RolesEnums
import ru.yarsu.web.domain.article.*
import ru.yarsu.web.users.User
import ru.yarsu.web.users.UserWithData
import java.security.MessageDigest
import java.time.LocalDateTime
import java.util.HexFormat
import kotlin.math.ceil

const val INPAGE_ARTICLE = 10
const val INPAGE_DOP = 5

class DataBaseController(
    private var sessionFactory: SessionFactory? = null,
    private val authSalt: String,
) {
    private fun openSession(): Session {
        return sessionFactory?.openSession() ?: sessionFactoryInit()
    }

    private fun sessionFactoryInit(): Session {
        val configuration = Configuration()
        configuration.addAnnotatedClass(User::class.java)
        configuration.addAnnotatedClass(Form::class.java)
        configuration.addAnnotatedClass(Genre::class.java)
        configuration.addAnnotatedClass(Tag::class.java)
        configuration.addAnnotatedClass(Chapter::class.java)
        configuration.addAnnotatedClass(Article::class.java)
        val sessionFactoryTmp = configuration.buildSessionFactory(StandardServiceRegistryBuilder().build())
        sessionFactory = sessionFactoryTmp
        return sessionFactoryTmp.openSession()
    }

    fun getAllArticles(): List<Article> {
        val hql = "from News"
        val session = openSession()
        val query = session.createQuery(hql, Article::class.java)
        return query.list()
    }

    fun getArticleById(id: Int): Article? {
        val hql = "from Article where id = :id order by dateAdd desc"
        val session = openSession()
        val query = session.createQuery(hql, Article::class.java)
        query.setParameter("id", id)
        return query.resultStream.findFirst().orElse(null)
    }

    fun deleteArticle(id: Int) {
        val hql = "delete Article where id = :id"
        val session = openSession()
        session.beginTransaction()
        val query = session.createMutationQuery(hql)
        query.setParameter("id", id)
        query.executeUpdate()
        session.transaction.commit()
    }

    private fun getSortedArticlesSize(
        hql: String,
        params: Params,
    ): Int {
        val countHql = "select count(*) $hql"
        val session = openSession()
        val query = session.createQuery(countHql, Long::class.java)
        if (params.ageParams.isNotEmpty()) {
            query.setParameter("ages", params.ageParams)
        }
        if (params.formParams.isNotEmpty()) {
            query.setParameter("forms", params.formParams)
        }
        return query.singleResult.toInt()
    }

    fun sortedArticlesByPageNumber(
        params: Params,
        page: Int,
    ): ArticlesWithSize {
        var hql = "from Article"
        if (params.ageParams.isNotEmpty() || params.formParams.isNotEmpty()) {
            hql += " where"
        }
        if (params.ageParams.isNotEmpty()) {
            hql += " censorAge in :ages"
        }
        if (params.ageParams.isNotEmpty() && params.formParams.isNotEmpty()) {
            hql += " and"
        }
        if (params.formParams.isNotEmpty()) {
            hql += " formArt in :forms"
        }
        hql += " order by dateAdd desc"

        val session = openSession()
        if (params.error) {
            val articles = session.createQuery(hql, Article::class.java).list()
            return ArticlesWithSize(articles, articles.size)
        }
        val query = session.createQuery(hql, Article::class.java)
        if (params.ageParams.isNotEmpty()) {
            query.setParameter("ages", params.ageParams)
        }
        if (params.formParams.isNotEmpty()) {
            query.setParameter("forms", params.formParams)
        }
        query.setMaxResults(INPAGE_ARTICLE)
        query.setFirstResult(INPAGE_ARTICLE * page)
        return ArticlesWithSize(query.list(), getSortedArticlesSize(hql, params))
    }

    fun addArticle(article: Article): Int {
        val session = openSession()
        session.beginTransaction()
        val newArt = Article()
        newArt.nameArt = article.nameArt
        newArt.annotation = article.annotation
        newArt.chapters = article.chapters
        newArt.censorAge = article.censorAge
        newArt.formArt = article.formArt
        newArt.tagsArt = article.tagsArt
        newArt.genre = article.genre
        newArt.dateAdd = article.dateAdd
        newArt.user = article.user
        session.persist(newArt)
        session.transaction.commit()
        return newArt.id
    }

    private fun editArticle(
        id: Int,
        articleNew: Article,
    ) {
        val session = openSession()
        session.beginTransaction()
        val article = session.get(Article::class.java, id)
        article.genre = articleNew.genre
        article.formArt = articleNew.formArt
        article.chapters = articleNew.chapters
        article.annotation = articleNew.annotation
        article.censorAge = articleNew.censorAge
        article.nameArt = articleNew.nameArt
        article.tagsArt = articleNew.tagsArt
        session.transaction.commit()
    }

    fun pageAmountArticle(size: Int): Int {
        return ceil(size.toDouble() / INPAGE_ARTICLE).toInt()
    }

    fun articlesByPageNumber(page: Int): List<Article> {
        val hql = "from Article order by dateAdd desc"
        val session = openSession()
        val query = session.createQuery(hql, Article::class.java)
        query.setFirstResult(INPAGE_ARTICLE * page)
        query.setMaxResults(INPAGE_ARTICLE)
        return query.list()
    }

    fun createArticleWithData(
        article: Article,
        forms: List<Form>,
        genres: List<Genre>,
        user: User?,
    ): ArticleWithData {
        return ArticleWithData(
            article,
            forms[article.formArt - 1].form,
            genres[article.genre - 1].genre,
            user?.nickName ?: "beda",
            user?.about ?: "beda",
            listOf(),
            false,
        )
    }

    fun prepareArticle(
        id: Int,
        articleToDo: Article,
        tags: MutableList<Int>,
        chapters: MutableList<Int>,
    ) {
        articleToDo.tagsArt = tags
        articleToDo.chapters = chapters
        editArticle(
            id,
            articleToDo,
        )
    }

    fun checkForm(
        form: String,
        ignore: String? = null,
    ): Boolean {
        val hql = "select form from Form"
        val session = openSession()
        val query = session.createQuery(hql, String::class.java)
        val forms = query.list().toMutableList()
        forms.remove(ignore)
        return form in forms
    }

    fun checkGenre(
        genre: String,
        ignore: String? = null,
    ): Boolean {
        val hql = "select genre from Genre"
        val session = openSession()
        val query = session.createQuery(hql, String::class.java)
        val genres = query.list().toMutableList()
        genres.remove(ignore)
        return genre in genres
    }

    fun getFormById(id: Int): Form {
        val hql = "from Form where id = :id"
        val session = openSession()
        val query = session.createQuery(hql, Form::class.java)
        query.setParameter("id", id)
        val form: Form? = query.resultStream.findFirst().orElse(null)
        return form ?: Form(-1, "Не определено")
    }

    fun getGenreById(id: Int): Genre {
        val hql = "from Genre where id = :id"
        val session = openSession()
        val query = session.createQuery(hql, Genre::class.java)
        query.setParameter("id", id)
        val genre: Genre? = query.resultStream.findFirst().orElse(null)
        return genre ?: Genre(-1, "Не определено")
    }

    fun removeForm(id: Int) {
        val hql = "delete Form where id = :id"
        val session = openSession()
        session.beginTransaction()
        val query = session.createMutationQuery(hql)
        query.setParameter("id", id)
        query.executeUpdate()
        session.transaction.commit()
    }

    fun removeGenre(id: Int) {
        val hql = "delete Genre where id = :id"
        val session = openSession()
        session.beginTransaction()
        val query = session.createMutationQuery(hql)
        query.setParameter("id", id)
        query.executeUpdate()
        session.transaction.commit()
    }

    fun getAllForms(): List<Form> {
        val hql = "From Form"
        val session = openSession()
        val query = session.createQuery(hql, Form::class.java)
        return query.list()
    }

    fun getAllGenres(): List<Genre> {
        val hql = "From Genre"
        val session = openSession()
        val query = session.createQuery(hql, Genre::class.java)
        return query.list()
    }

    fun changeForm(
        id: Int,
        newForm: String,
    ) {
        val session = openSession()
        session.beginTransaction()
        val form = session.get(Form::class.java, id)
        form.form = newForm
        session.transaction.commit()
    }

    fun changeGenre(
        id: Int,
        newGenre: String,
    ) {
        val session = openSession()
        session.beginTransaction()
        val genre = session.get(Genre::class.java, id)
        genre.genre = newGenre
        session.transaction.commit()
    }

    fun addForm(form: String): Int {
        val session = openSession()
        session.beginTransaction()
        val newForm = Form(form = form)
        session.persist(newForm)
        session.transaction.commit()
        return newForm.id
    }

    fun addGenre(genre: String): Int {
        val session = openSession()
        session.beginTransaction()
        val newGenre = Genre(genre = genre)
        session.persist(newGenre)
        session.transaction.commit()
        return newGenre.id
    }

    fun getTagsByIds(ids: List<Int>): List<String> {
        val hql = "select tag from Tag where id in :ids"
        val session = openSession()
        val query = session.createQuery(hql, String::class.java)
        query.setParameter("ids", ids)
        return query.list()
    }

    fun addTag(tag: String): Int {
        val session = openSession()
        session.beginTransaction()
        val newTag = Tag(tag = tag)
        session.persist(newTag)
        session.transaction.commit()
        return newTag.idTag
    }

    fun getAllTags(): List<Tag> {
        val hql = "from Tag"
        val session = openSession()
        session.beginTransaction()
        val query = session.createQuery(hql, Tag::class.java)
        return query.list()
    }

    fun prepareTags(
        tagsOldField: List<String>,
        tagsNewField: String,
    ): MutableList<Int> {
        val tags = mutableListOf<Int>()
        for (i in tagsOldField) {
            if (i == "") {
                continue
            }
            tags.add(addTag(i).toInt())
        }
        if (tagsNewField != "") {
            tags.add(addTag(tagsNewField).toInt())
        }
        return tags
    }

    fun getChaptersByIds(ids: List<Int>): List<Chapter> {
        val hql = "from Chapter where id in :ids"
        val session = openSession()
        val query = session.createQuery(hql, Chapter::class.java)
        query.setParameter("ids", ids)
        return query.list().sortedWith { o1, o2 -> o1.chapter.compareTo(o2.chapter) }
    }

    fun removeChapters(ids: List<Int>) {
        val hql = "delete Chapter where id in :ids"
        val session = openSession()
        session.beginTransaction()
        val query = session.createMutationQuery(hql)
        query.setParameter("ids", ids)
        query.executeUpdate()
        session.transaction.commit()
    }

    fun getAllChapters(): List<Chapter> {
        val hql = "from Chapter"
        val session = openSession()
        val query = session.createQuery(hql, Chapter::class.java)
        return query.list()
    }

    fun addChapter(
        chapter: Int,
        name: String,
        content: String,
    ): Int {
        val session = openSession()
        session.beginTransaction()
        val chapterNew = Chapter(chapter = chapter, name = name, content = content)
        session.persist(chapterNew)
        session.transaction.commit()
        return chapterNew.id
    }

    fun prepareChapters(
        entity: Article,
        chaptersOldField: List<Int>,
        delChpField: List<Int>,
        namesOldField: List<String>,
        textsOldField: List<String>,
        chaptersNewField: Int,
        namesNewField: String,
        textsNewField: String,
    ): MutableList<Int> {
        val chapters = mutableListOf<Int>()
        for (i in chaptersOldField.indices) {
            if (i !in delChpField) {
                chapters.add(
                    addChapter(
                        chaptersOldField[i],
                        namesOldField[i],
                        textsOldField[i],
                    ),
                )
            }
        }
        removeChapters(entity.chapters)
        if (chaptersNewField != -1) {
            chapters.add(
                addChapter(
                    chaptersNewField,
                    namesNewField,
                    textsNewField,
                ),
            )
        }
        return chapters
    }

    fun pageAmountFormOrGenre(mode: String): Int {
        return when (mode) {
            "form" -> return ceil(getAllForms().size.toDouble() / INPAGE_DOP).toInt()
            "genre" -> return ceil(getAllGenres().size.toDouble() / INPAGE_DOP).toInt()
            else -> 0
        }
    }

    fun formsByPageNumber(page: Int): List<Form> {
        val hql = "from Form"
        val session = openSession()
        val query = session.createQuery(hql, Form::class.java)
        query.setFirstResult(INPAGE_DOP * page)
        query.setMaxResults(INPAGE_DOP)
        return query.list()
    }

    fun genresByPageNumber(page: Int): List<Genre> {
        val hql = "from Genre"
        val session = openSession()
        val query = session.createQuery(hql, Genre::class.java)
        query.setFirstResult(INPAGE_DOP * page)
        query.setMaxResults(INPAGE_DOP)
        return query.list()
    }

    fun getUser(login: String): User? {
        val hql = "from User where login = :login"
        val session = openSession()
        val query = session.createQuery(hql, User::class.java)
        query.setParameter("login", login)
        return query.resultStream.findFirst().orElse(null)
    }

    fun changeUserRole(
        user: User,
        roleNew: Int,
    ) {
        val session = openSession()
        session.beginTransaction()
        val userChanged = session.get(User::class.java, user.id)
        userChanged.role = roleNew
        session.transaction.commit()
    }

    fun changeUser(
        user: User,
        nicknameNew: String,
        passwordNew: String,
        aboutNew: String,
        roleNew: Int,
    ) {
        val session = openSession()
        session.beginTransaction()
        val userChanged = session.get(User::class.java, user.id)
        userChanged.role = roleNew
        userChanged.nickName = nicknameNew
        userChanged.password = createPassword(passwordNew)
        userChanged.about = aboutNew
        session.transaction.commit()
    }

    fun createUser(
        login: String,
        password: String,
        nickname: String,
        about: String,
    ): User {
        return User(
            nickName = nickname,
            login = login,
            password = createPassword(password),
            about = about,
            dateAdd = LocalDateTime.now(),
            role = RolesEnums.AUTHOR.id,
        )
    }

    private fun createPassword(password: String): String {
        val commaFormat = HexFormat.of()
        val saltedBytes = (password + authSalt).toByteArray(charset("UTF-8"))
        val messageDigest = MessageDigest.getInstance("SHA-256")
        val digest = messageDigest.digest(saltedBytes)
        return commaFormat.formatHex(digest)
    }

    fun addUser(user: User) {
        val session = openSession()
        session.beginTransaction()
        session.merge(user)
        session.transaction.commit()
    }

    fun checkLogin(
        login: String?,
        ignore: String? = null,
    ): Boolean {
        val hql = "select login from User"
        val session = openSession()
        val query = session.createQuery(hql, String::class.java)
        val logins = query.list().toMutableList()
        logins.remove(ignore)
        return login in logins
    }

    fun checkNickname(
        nickname: String?,
        ignore: String? = null,
    ): Boolean {
        val hql = "select nickName from User"
        val session = openSession()
        val query = session.createQuery(hql, String::class.java)
        val nicknames = query.list().toMutableList()
        nicknames.remove(ignore)
        return nickname in nicknames
    }

    fun checkPassword(
        passwordIn: String,
        passwordOld: String,
    ) = createPassword(passwordIn) == passwordOld

    fun getAllUsers(): List<User> {
        val hql = "from User"
        val session = openSession()
        val query = session.createQuery(hql, User::class.java)
        return query.list()
    }

    fun makeUsersWithData(users: List<User>): List<UserWithData> {
        val usersWithData = mutableListOf<UserWithData>()
        users.forEach { user ->
            usersWithData.add(
                UserWithData(
                    user,
                    RolesEnums.from(user.role)?.role ?: "Аноним",
                ),
            )
        }
        return usersWithData
    }

    fun pageAmountUser(): Int {
        return ceil(getAllUsers().size.toDouble() / INPAGE_DOP).toInt()
    }

    fun usersByPageNumber(page: Int): List<User> {
        val hql = "from User"
        val session = openSession()
        val query = session.createQuery(hql, User::class.java)
        query.setFirstResult(INPAGE_DOP * page)
        query.setMaxResults(INPAGE_DOP)
        return query.list()
    }
}
