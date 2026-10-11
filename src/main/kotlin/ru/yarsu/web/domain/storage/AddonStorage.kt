package ru.yarsu.web.domain.storage

import ru.yarsu.web.domain.article.Article
import ru.yarsu.web.domain.article.ArticleWithData
import ru.yarsu.web.domain.article.Chapter
import ru.yarsu.web.domain.article.Form
import ru.yarsu.web.domain.article.Genre
import ru.yarsu.web.domain.article.Tag
import ru.yarsu.web.domain.article.getDefaultChapter
import ru.yarsu.web.users.UserInfo
import kotlin.collections.find

const val INPAGE_DOP = 5

class AddonStorage(
    chapters: List<Chapter>,
    tags: List<Tag>,
    forms: List<Form>,
    genres: List<Genre>,
) {
    private var chaptersMap: Map<Int, Chapter> = chapters.associateBy({ it.id }, { it })
    private var tagsMap: Map<Int, Tag> = tags.associateBy({ it.idTag }, { it })
    private var formsMap: Map<Int, Form> = forms.associateBy({ it.id }, { it })
    private var genresMap: Map<Int, Genre> = genres.associateBy({ it.id }, { it })

    /*!
    Тип: действие
    Функции по получению значений из словаря с Forms//Genres/Tags
    Оперирует: строение AddonStorage
     */
    fun getFormsValues() = formsMap.values.toList()
    fun getGenresValues() = genresMap.values.toList()
    fun getTagValues() = tagsMap.values.toList()

    /*!
    Тип: действие
    Функция по проверке существования описания формы в хранилище.
    Является обёрткой для checkForm()
    Оперирует: строение Form, строение AddonStorage
     */
    fun checkFormInStorage(form: String, ignore: String? = null): Boolean {
        val forms = getFormsValues()
        return checkForm(form, ignore, forms)
    }

    /*!
    Тип: вычисление
    Функция по проверке существования формы в предложенной коллекции.
    Является обёрткой для checkValueInCollectionsWithIgnore()
    Оперирует: строение Form
     */
    fun checkForm(form: String, ignore: String? = null, formCollection: List<Form>) =
        checkValueInCollectionsWithIgnore(form, ignore, formCollection)

    /*!
    Тип: действие
    Функция по проверке существования жанра в хранилище.
    Является обёрткой для checkGenre()
    Оперирует: строение Genre, строение AddonStorage
     */
    fun checkGenreInStorage(genre: String, ignore: String? = null): Boolean {
        val genres = getGenresValues()
        return checkGenre(genre, ignore, genres)
    }

    /*!
    Тип: вычисление
    Функция по проверке существования жанра в предложенной коллекции.
    Является обёрткой для checkValueInCollectionsWithIgnore
    Оперирует: строение Genre
     */
    fun checkGenre(genre: String, ignore: String? = null, genreCollection: List<Genre>) =
        checkValueInCollectionsWithIgnore(genre, ignore, genreCollection)

    /*!
    Тип: действие
    Функция по получению описания формы/жанра по ID из хранилища
    Оперирует: строение AddonStorage
     */
    fun getFormById(id: Int) = getFormFromStorage(id).form
    fun getGenreById(id: Int) = getGenreFromStorage(id).genre

    /*!
    Тип: действие
    Функция по получению объекта формы/жанра по ID из хранилища
    Оперирует: строение AddonStorage
     */
    fun getFormFromStorage(id: Int) =
        formsMap[id] ?: Form(-1, "Не выбрано")
    fun getGenreFromStorage(id: Int) =
        genresMap[id] ?: Genre(-1, "Не выбрано")

    /*!
    Тип: действие
    Функция для получения ID формы/жанра, если существует в коллекции.
    Оперирует: строение AddonStorage
     */
    fun getFormIdOrDefault(id: Int) =
        if (getFormById(id) == "Не выбрано") -1 else id
    fun getGenreIdOrDefault(id: Int) =
        if (getGenreById(id) == "Не выбрано") -1 else id

    /*!
    Тип: действие
    Функция по удалению формы/жанра из хранилища.
    Является обёрткой для removeFormAndGetStorage()/removeGenreAndGetStorage()
    Оперирует: строение AddonStorage
     */
    fun removeFormFromStorage(id: Int) {
        formsMap = removeFormAndGetStorage(id, formsMap)
    }
    fun removeGenreFromStorage(id: Int) {
        genresMap = removeGenreAndGetStorage(id, genresMap)
    }

    /*!
    Тип: вычисление
    Функция по удалению формы/жанра из предложенной коллекции.
    Является обёрткой для removeValueFromMap()
    Оперирует: бизнес-логика
     */
    fun removeFormAndGetStorage(id: Int, storage: Map<Int, Form>) =
        removeValueFromMap(id, storage)
    fun removeGenreAndGetStorage(id: Int, storage: Map<Int, Genre>) =
        removeValueFromMap(id, storage)

    /*!
    Тип: действие
    Функция по добавлению формы/жанра в хранилище.
    Является обёрткой для addFormAndGetStorage()/addGenreAndGetStorage()
    Оперирует: строение AddonStorage
     */
    fun addFormToStorage(form: Form) {
        formsMap = addFormAndGetStorage(form, formsMap)
    }
    fun addGenreToStorage(genre: Genre) {
        genresMap = addGenreAndGetStorage(genre, genresMap)
    }

    /*!
     Тип: вычисление
     Функция по добавлению формы/жанра в предложенную коллекцию.
     Является обёрткой для addValueToMap()
     Оперирует: бизнес-логика
     */
    fun addFormAndGetStorage(form: Form, storage: Map<Int, Form>) =
        addValueToMap(form.id, form, storage)
    fun addGenreAndGetStorage(genre: Genre, storage: Map<Int, Genre>) =
        addValueToMap(genre.id, genre, storage)

    /*!
    Тип: вычисление, конструктор
    Функция по созданию объекта Form/Genre из параметров
    Оперирует: строение Form/Genre
     */
    fun makeForm(id: Int = -1, form: String = "Не выбрано") =
        Form(
            id,
            form
        )
    fun makeGenre(id: Int = -1, genre: String = "Не выбрано") =
        Genre(
            id,
            genre
        )

    /*!
    Тип: действие
    Функция по получению всех форм/жанров из хранилища
    Оперирует: строение AddonStorage
     */
    fun getAllForms() = formsMap.map { it.value }.toList()
    fun getAllGenres() = genresMap.map { it.value }.toList()

    /*!
    Тип: действие
    Функция по изменению Form/Genre в хранилище
    Оперирует: строение Form/Genre, строение AddonStorage
     */
    fun changeFormInStorage(id: Int, newForm: String) {
        val new = getFormFromStorage(id).copy(form = newForm)
        addFormToStorage(new)
    }
    fun changeGenreInStorage(id: Int, newGenre: String) {
        val new = getGenreFromStorage(id).copy(genre = newGenre)
        addGenreToStorage(new)
    }

    /*!
    Тип: действие
    Функция по добавлению формы/жанра по их описанию в хранилище
    Оперирует: строение AddonStorage
     */
    fun addForm(form: String): Int? {
        if (checkFormInStorage(form))
            return null

        val id = getNewIdForms()
        addFormToStorage(makeForm(id, form))
        return id
    }
    fun addGenre(genre: String): Int? {
        if (checkGenreInStorage(genre))
            return null

        val id = getNewIdGenres()
        addGenreToStorage(makeGenre(id, genre))
        return id
    }

    /*!
    Тип: действие
    Функция по получению нового Id для хранилища Forme/Genre.
    Является обёрткой для newIdForMap()
    Оперирует: строение AddonStorage
     */
    fun getNewIdForms(): Int {
        return newIdForMap(formsMap)
    }
    private fun getNewIdGenres(): Int {
        return newIdForMap(genresMap)
    }



    /*!
    Тип: действие
    Функция по получению описаний тэгов из хранилища по их Id.
    Является оболочкой для getValuesByIdsFromMap()
    Оперирует: строение AddonStorage
     */
    fun getTagsByIds(ids: List<Int>): List<String> =
        getValuesByIdsFromMap(ids,tagsMap).map { it.tag }.toList()

    /*!
    Тип: действие
    Функция по получению списка объектов Chapters из хранилища по их Id.
    Является оболочкой для getValuesByIdsFromMap()
    Оперирует: строение AddonStorage
     */
    fun getChaptersByIds(ids: List<Int>): List<Chapter> {
        var result = getValuesByIdsFromMap(ids, chaptersMap).toList()
        result = sortChaptersByChapterNum(result)
        return result
    }

    /*!
    Тип: вычисление
    Функция для сортировки предложенного списка Chapters по номерам глав
    Оперирует: строение Chapter
     */
    fun sortChaptersByChapterNum(chapters: List<Chapter>): List<Chapter> =
        chapters.sortedWith { o1, o2 -> o1.chapter.compareTo(o2.chapter) }

    /*!
    Тип: действие
    Функция для получения списка глав из хранилища по их Id.
    При отсутствии глав будет добавлена заглушка
    Оперирует: строение AddonStorage
     */
    fun getChaptersOrDefault(ids: List<Int>): List<Chapter> {
        var chapters = getChaptersByIds(ids)
        if (chapters.isEmpty())
            chapters += getDefaultChapter()
        return chapters
    }

    /*!
    Тип: действие
    Функция для удаления ряда глав из хранилища по их Id.
    Является обёрткой для removeChaptersAndGetStorage()
    Оперирует: строение AddonStorage
     */
    fun removeChaptersFromStorage(ids: List<Int>) {
        chaptersMap = removeChaptersAndGetStorage(ids, chaptersMap)
    }

    /*!
    Тип: вычисление
    Функция для удаления ряда глав из предложенного хранилища по их Id.
    Оперирует: бизнес-логика
     */
    fun removeChaptersAndGetStorage(ids: List<Int>, collection: Map<Int, Chapter>): Map<Int, Chapter> =
        collection - ids.toSet()

    /*!
    Тип: действие
    Функция для добавления нового Tag по его названию в хранилище.
    При существовании тэга возвращает Id старого.
    Оперирует: строение AddonStorage
     */
    private fun addTag(tagName: String): Int {
        val existingId = getTagIdInCollection(tagName, getTagValues())
        if (existingId != -1) {
            return existingId
        }

        val newTag = createTag(newIdForMap(tagsMap), tagName)
        addTagToStorage(newTag)
        return newTag.idTag
    }

    /*!
    Тип: вычисление
    Функция для получения Id Tag из предложенной коллекции.
    Оперирует: строение Tag
     */
    fun getTagIdInCollection(tagName: String, storage: List<Tag>) =
        storage.find { it.tag == tagName }?.idTag ?: -1

    /*!
    Тип: действие
    Функция для добавления объекта Tag в хранилище.
    Является оболочкой для addTagAndGetStorage()
    Оперирует: строение AddonStorage
     */
    fun addTagToStorage(tag: Tag) {
        tagsMap = addTagAndGetStorage(tag, tagsMap)
    }

    /*!
    Тип: вычисление
    Функция для добавления объекта Tag в предложенное хранилище.
    Оперирует: бизнес-логика
     */
    fun addTagAndGetStorage(tag: Tag, storage: Map<Int, Tag>) =
        addValueToMap(tag.idTag, tag, storage)

    /*!
    Тип: вычисление, конструктор
    Функция для создания объекта класса Tag из параметров
    Оперирует: строение Tag
     */
    fun createTag(id: Int, tagName: String) =
        Tag (
            tagName, id
        )

    /*!
    Тип: вычисление
    Функция по вычислению новых ID тэгов и обновленного хранилища.
     */
    private fun calculateNewTags(
        tagsOldField: List<String>,
        tagsNewField: String,
        currentMap: Map<Int, Tag>
    ): Pair<List<Int>, Map<Int, Tag>> {
        var updatedMap = currentMap
        val ids = mutableListOf<Int>()

        val allTags = filterEmptyTags(tagsOldField) +
                (if (tagsNewField.isNotBlank()) listOf(tagsNewField) else emptyList())

        for (tagName in allTags) {
            val existing = getTagIdInCollection(tagName, updatedMap.values.toList())
            if (existing != -1) {
                ids.add(existing)
            } else {
                val newId = newIdForMap(updatedMap)
                val newTag = createTag(newId, tagName)
                updatedMap = addTagAndGetStorage(newTag, updatedMap)
                ids.add(newId)
            }
        }
        return Pair(ids, updatedMap)
    }

    /*!
    Тип: действие
    Функция по подготовке тэгов и обновлению хранилища.
     */
    fun prepareTags(tagsOldField: List<String>, tagsNewField: String): List<Int> {
        val (ids, updatedMap) = calculateNewTags(tagsOldField, tagsNewField, tagsMap)
        tagsMap = updatedMap
        return ids
    }

    /*!
    Тип: вычисление
    Функция для фильтрации пустых тэгов из предложенного хранилища
    Оперирует: бизнес-правило
     */
    fun filterEmptyTags(tags: List<String>) =
        tags.filter { it.isNotEmpty() }


    /*!
    Тип: вычисление
    Функция по вычислению новых ID глав и обновленного хранилища.
    */
    fun calculateNewChapters(
        article: Article,
        chaptersOldField: List<Int>,
        delChpField: List<Int>,
        namesOldField: List<String>,
        textsOldField: List<String>,
        chaptersNewField: Int,
        namesNewField: String,
        textsNewField: String,
        currentChaptersMap: Map<Int, Chapter>
    ): Pair<List<Int>, Map<Int, Chapter>> {
        val chapterIds = mutableListOf<Int>()
        var updatedMap = currentChaptersMap

        val idsToRemove = article.chapters.filter { it !in chaptersOldField || chaptersOldField.indexOf(it) in delChpField }
        updatedMap = removeChaptersAndGetStorage(idsToRemove, updatedMap)

        for (i in chaptersOldField.indices) {
            if (i !in delChpField) {
                val oldId = article.chapters.getOrNull(i) ?: -1
                val chapterNum = chaptersOldField[i]
                val name = namesOldField[i]
                val content = textsOldField[i]

                if (oldId != -1 && updatedMap.containsKey(oldId)) {
                    updatedMap = addChapterAndGetStorage(createChapter(chapterNum, name, content, oldId), updatedMap)
                    chapterIds.add(oldId)
                } else {
                    val newId = newIdForMap(updatedMap)
                    updatedMap = addChapterAndGetStorage(createChapter(chapterNum, name, content, newId), updatedMap)
                    chapterIds.add(newId)
                }
            }
        }

        if (chaptersNewField != -1) {
            val newId = newIdForMap(updatedMap)
            updatedMap = addChapterAndGetStorage(createChapter(chaptersNewField, namesNewField, textsNewField, newId), updatedMap)
            chapterIds.add(newId)
        }

        return Pair(chapterIds, updatedMap)
    }

    /*!
    Тип: действие
    Функция по добавлению Chapter в хранилище.
    Является обёрткой для addChapterAndGetStorage()
    Оперирует: строение AddonStorage
     */
    fun addChapterToStorage(chapter: Chapter) {
        chaptersMap = addChapterAndGetStorage(chapter, chaptersMap)
    }

    /*!
     Тип: вычисление
     Функция по добавлению Chapter в предложенную коллекцию.
     Является обёрткой для addValueToMap()
     Оперирует: бизнес-логика
     */
    fun addChapterAndGetStorage(chapter: Chapter, storage: Map<Int, Chapter>) =
        addValueToMap(chapter.id, chapter, storage)

    /*!
    Тип: вычисление, конструктор
    Функция по созданию объекта Chapter из параметров
    Оперирует: строение Chapter
     */
    fun createChapter(
        chapter: Int,
        name: String,
        content: String,
        id: Int
    ) = Chapter (
        chapter,
        name,
        content,
        id
    )

    /*!
    Тип: действие
    Функция по подготовке глав и обновлению хранилища.
    */
    fun prepareChapters(
        entity: Article,
        chaptersOldField: List<Int>,
        delChpField: List<Int>,
        namesOldField: List<String>,
        textsOldField: List<String>,
        chaptersNewField: Int,
        namesNewField: String,
        textsNewField: String
    ): List<Int> {
        val (chapterIds, updatedMap) = calculateNewChapters(
            entity, chaptersOldField, delChpField, namesOldField, textsOldField,
            chaptersNewField, namesNewField, textsNewField, chaptersMap
        )
        chaptersMap = updatedMap
        return chapterIds
    }

    /*!
    Тип: действие
    Функция по созданию объекта ArticleWithData из параметров, полученных из хранилища
     */
    fun getArticleWithData(
        userInfo: UserInfo,
        article: Article
    ): ArticleWithData {
        val form = getFormById(article.formArt)
        val genre = getGenreById(article.genre)
        val chapters = getChaptersByIds(article.chapters).map { it.id }
        val editArticle = article.user == userInfo.login
        return createArticleWithData(article, form, genre, chapters, userInfo, editArticle)
    }

    /*!
    Тип: вычисление, конструктор
    Функция по созданию объекта ArticleWithData из параметров
     */
    fun createArticleWithData(
        article: Article,
        form: String,
        genre: String,
        chapters: List<Int>,
        userInfo: UserInfo,
        editArticle: Boolean
    ): ArticleWithData {
        return ArticleWithData(
            article,
            form,
            genre,
            userInfo.userNick,
            userInfo.userAbout,
            chapters,
            editArticle
        )
    }

    /*!
    Тип: действие
    Функция по созданию списка ArticleWithData для нескольких статей.
    Оперирует: UserInfo, List<Article> -> List<ArticleWithData>
    */
    fun makeArticlesWithData(userInfo: UserInfo, articles: List<Article>): List<ArticleWithData> {
        return articles.map { getArticleWithData(userInfo, it) }
    }

    fun pageAmount(mode: String): Int {
        return when (mode) {
            "form" -> getPageAmount(INPAGE_DOP, getFormsValues())
            "genre" -> getPageAmount(INPAGE_DOP, getGenresValues())
            else -> 0
        }
    }

    fun formsByPageNumber(
        pageNumber: Int,
    ): List<Form> {
        return valuesByPage(pageNumber,getFormsValues())
    }

    fun genresByPageNumber(
        pageNumber: Int,
    ): List<Genre> {
        return valuesByPage(pageNumber,getGenresValues())
    }
}
