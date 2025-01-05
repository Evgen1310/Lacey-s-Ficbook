package ru.yarsu.web.domain.storage

import ru.yarsu.web.domain.article.Chapter
import ru.yarsu.web.domain.article.Form
import ru.yarsu.web.domain.article.Genre
import ru.yarsu.web.domain.article.Tag

const val INPAGE_DOP = 5

class AddonStorage(
    private val chapters: List<Chapter>,
    private val tags: List<Tag>,
    private val forms: List<Form>,
    private val genres: List<Genre>,
) {
//    private val chaptersMap: MutableMap<Int, Chapter> = chapters.associateBy({ it.id }, { it }).toMutableMap()
//    private val tagsMap: MutableMap<Int, Tag> = tags.associateBy({ it.idTag }, { it }).toMutableMap()
//    private val formsMap: MutableMap<Int, Form> = forms.associateBy({ it.id }, { it }).toMutableMap()
//    private val genresMap: MutableMap<Int, Genre> = genres.associateBy({ it.id }, { it }).toMutableMap()
//
//    private val chaptersIds: MutableList<Int> = chapters.map { it.id }.toMutableList()
//    private val tagsValues: MutableList<String> = tags.map { it.tag }.toMutableList()
//    private val chaptersSize = chaptersMap.size
//    private val formsIds: MutableList<Int> = forms.map { it.id }.toMutableList()
//    private val genresIds: MutableList<Int> = genres.map { it.id }.toMutableList()
//    private val formsVals = forms.map { it.form }.toMutableList()
//    private val genresVals = genres.map { it.genre }.toMutableList()
//
//    private val tagsOldField = FormField.nonBlankString().multi.optional("tagsOld")
//    private val chaptersOldField = FormField.int().multi.required("chaptersOld")
//    private val namesOldField = FormField.nonBlankString().multi.required("namesOld")
//    private val textsOldField = FormField.nonBlankString().multi.required("textsOld")
//    private val tagsNewField = FormField.nonBlankString().optional("tagsNew")
//    private val chaptersNewField = FormField.int().optional("chaptersNew")
//    private val namesNewField = FormField.nonBlankString().optional("namesNew")
//    private val textsNewField = FormField.nonBlankString().optional("textsNew")
//    private val delChpField = FormField.int().multi.optional("delChp")
//
//    fun checkForm(
//        form: String,
//        ignore: String? = null,
//    ): Boolean {
//        val newForms = formsVals.toList().toMutableList()
//        newForms.remove(ignore)
//        return form in newForms
//    }
//
//    fun checkGenre(
//        genre: String,
//        ignore: String? = null,
//    ): Boolean {
//        val newGenres = genresVals.toList().toMutableList()
//        newGenres.remove(ignore)
//        return genre in newGenres
//    }
//
//    fun getFormById(id: Int) = formsMap[id]?.form ?: "Не выбрано"
//
//    fun getGenreById(id: Int) = genresMap[id]?.genre ?: "Не выбрано"
//
//    fun removeForm(id: Int) = formsMap.remove(id)
//
//    fun removeGenre(id: Int) = genresMap.remove(id)
//
//    fun getAllForms() = formsMap.map { it.value }.toList()
//
//    fun getAllGenres() = genresMap.map { it.value }.toList()
//
//    fun changeForm(
//        id: Int,
//        newForm: String,
//    ) {
//        val oldForm = formsMap[id] ?: Form(-1, "Не выбрано")
//        formsVals.remove(oldForm.form)
//        formsMap[id] =
//            Form(
//                id,
//                newForm,
//            )
//    }
//
//    fun changeGenre(
//        id: Int,
//        newGenre: String,
//    ) {
//        val oldGenre = genresMap[id] ?: Genre(-1, "Не выбрано")
//        formsVals.remove(oldGenre.genre)
//        genresMap[id] =
//            Genre(
//                id,
//                newGenre,
//            )
//    }
//
//    fun addForm(form: String): Int? {
//        if (form in formsVals) {
//            return null
//        }
//        val id = getNewIdForms()
//        formsMap[id] = Form(id, form)
//        formsIds.add(id)
//        formsVals.add(form)
//        return id
//    }
//
//    fun addGenre(genre: String): Int? {
//        if (genre in genresVals) {
//            return null
//        }
//        val id = getNewIdGenres()
//        genresMap[id] = Genre(id, genre)
//        genresIds.add(id)
//        genresVals.add(genre)
//        return id
//    }
//
//    private fun getNewIdForms(): Int {
//        var id = formsMap.size
//        while (id in formsIds)
//            id += 1
//        return id
//    }
//
//    private fun getNewIdGenres(): Int {
//        var id = genresMap.size
//        while (id in genresIds)
//            id += 1
//        return id
//    }
//
//    fun getTagsByIds(ids: List<Int>): List<String> = tagsMap.filter { it.key in ids }.values.map { it.tag }.toList()
//
//    fun getChaptersByIds(ids: List<Int>): List<Chapter> {
//        var result = chaptersMap.filter { it.key in ids }.values.toList()
//        result = result.sortedWith { o1, o2 -> o1.chapter.compareTo(o2.chapter) }
//        return result
//    }
//
//    fun removeChapters(ids: List<Int>) {
//        ids.forEach {
//            chaptersMap.remove(it)
//            chaptersIds.remove(it)
//        }
//    }
//
//    private fun addTag(tag: String): Int {
//        if (tag in tagsValues) {
//            return tagsMap.entries.find { it.value.tag == tag }?.key ?: -1
//        }
//        val id = tagsMap.size
//        tagsMap[id] = Tag(tag, id)
//        tagsValues.add(tag)
//        return id
//    }
//
//    private fun addChapter(
//        chapter: Int,
//        name: String,
//        content: String,
//        idOld: Int = -1,
//    ): Int {
//        if (idOld != -1 && idOld in chaptersIds) {
//            chaptersMap[idOld] = Chapter(chapter, name, content, idOld)
//            return idOld
//        }
//        var id = chaptersSize
//        while (id in chaptersIds)
//            id += 1
//        chaptersMap[id] = Chapter(chapter, name, content, id)
//        chaptersIds.add(id)
//        return id
//    }
//
//    fun getAllTags(): List<Tag> = tagsMap.map { it.value }
//
//    fun getAllChapters(): List<Chapter> = chaptersMap.map { it.value }
//
//    fun prepareTags(
//        tagsOldField: List<String>,
//        tagsNewField: String,
//    ): List<Int> {
//        val tags = mutableListOf<Int>()
//        for (i in tagsOldField) {
//            if (i == "") {
//                continue
//            }
//            tags.add(addTag(i))
//        }
//        if (tagsNewField != "") {
//            tags.add(addTag(tagsNewField))
//        }
//        return tags
//    }
//
//    fun prepareChapters(
//        entity: Article,
//        chaptersOldField: List<Int>,
//        delChpField: List<Int>,
//        namesOldField: List<String>,
//        textsOldField: List<String>,
//        chaptersNewField: Int,
//        namesNewField: String,
//        textsNewField: String,
//    ): List<Int> {
//        val chapters = mutableListOf<Int>()
//        for (i in chaptersOldField.indices) {
//            if (i !in delChpField) {
//                chapters.add(
//                    addChapter(
//                        chaptersOldField[i],
//                        namesOldField[i],
//                        textsOldField[i],
//                    ),
//                )
//            }
//        }
//        removeChapters(entity.chapters)
//        if (chaptersNewField != -1) {
//            chapters.add(
//                addChapter(
//                    chaptersNewField,
//                    namesNewField,
//                    textsNewField,
//                ),
//            )
//        }
//        return chapters
//    }
//
//    fun createArticleWithData(
//        article: Article,
//        userStorage: UserStorage,
//        addonStorage: AddonStorage,
//    ): ArticleWithData {
//        return ArticleWithData(
//            article,
//            getFormById(article.formArt),
//            getGenreById(article.genre),
//            userStorage.getUserInfo(article.user).userNick,
//            userStorage.getUserInfo(article.user).userAbout,
//            addonStorage.getChaptersByIds(article.chapters).map { it.id },
//            false,
//        )
//    }
//
//    fun pageAmount(mode: String): Int {
//        return when (mode) {
//            "form" -> return ceil(formsMap.size.toDouble() / INPAGE_DOP).toInt()
//            "genre" -> return ceil(genresMap.size.toDouble() / INPAGE_DOP).toInt()
//            else -> 0
//        }
//    }
//
//    fun formsByPageNumber(pageNumber: Int): List<Form> {
//        val forms = formsMap.map { it.value }
//        val resultList = mutableListOf<Form>()
//        for (i in 0..<INPAGE_DOP) {
//            if (i + pageNumber * INPAGE_DOP < forms.size) {
//                resultList.add(forms[i + pageNumber * INPAGE_DOP])
//            }
//        }
//        return resultList
//    }
//
//    fun genresByPageNumber(pageNumber: Int): List<Genre> {
//        val genres = genresMap.map { it.value }
//        val resultList = mutableListOf<Genre>()
//        for (i in 0..<INPAGE_DOP) {
//            if (i + pageNumber * INPAGE_DOP < genres.size) {
//                resultList.add(genres[i + pageNumber * INPAGE_DOP])
//            }
//        }
//        return resultList
//    }
}
