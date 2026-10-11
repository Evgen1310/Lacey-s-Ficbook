package ru.yarsu.web.domain.storage

import ru.yarsu.web.domain.FilterParams
import ru.yarsu.web.domain.article.Article
import ru.yarsu.web.domain.article.ArticleCreationParams
import java.time.LocalDateTime
import kotlin.math.ceil

const val INPAGE = 10

class ArticleStorage(articles: List<Article>) {
    private var articlesMap: Map<Int, Article> = articles.associateBy({ it.id }, { it })

    /*!
    Тип: действие
    Функция по получению статьи из хранилища по её ID
    Оперирует: строение ArticleStorage
     */
    fun getArticleFromStorage(id: Int): Article? = articlesMap[id]

    /*!
    Тип: действие
    Функция по получению всех статей из хранилища
    Оперирует: строение ArticleStorage
     */
    fun getAllArticles() = articlesMap.values.toList()

    /*!
    Тип: действие
    Функция по удалению статьи из хранилища.
    Является обёрткой для removeArticleAndGetStorage()
    Оперирует: строение ArticleStorage
     */
    fun deleteArticle(id: Int) {
        articlesMap = removeArticleAndGetStorage(id, articlesMap)
    }

    /*!
    Функция по удалению статьи из предложенной коллекции.
    Является обёрткой для removeValueFromMap()
    Оперирует: бизнес-логика
     */
    fun removeArticleAndGetStorage(id: Int, storage: Map<Int, Article>) =
        removeValueFromMap(id, storage)

    /*!
    Тип: действие
    Функция по сортировке и фильтрации статей из хранилища.
    Является обёрткой для sortAndFilterArticles()
    Оперирует: строение ArticleStorage
     */
    fun sortAndFilterArticlesFromStorage(filterParams: FilterParams): List<Article> {
        var result = getAllArticles()
        result = sortAndFilterArticles(result, filterParams)
        return result
    }

    /*!
    Тип: вычисление
    Функция по сортировке и фильтрации предложенной коллекции статей
    Оперирует: ?
     */
    fun sortAndFilterArticles(articles: List<Article>, filterParams: FilterParams): List<Article> {
        var result = sortArticlesByDate(articles)
        if (filterParams.error) {
            return result
        }
        result = filterArticlesByAge(result, filterParams.ageParams)
        result = filterArticlesByForm(result, filterParams.formParams)
        return result
    }

    /*!
    Тип: вычисление
    Функция по сортировке предложенной коллекции статей по свежести дат
    Оперирует: строение Article
     */
    fun sortArticlesByDate(articles: List<Article>): List<Article> {
        return articles.sortedWith { o1, o2 -> o1.dateAdd.compareTo(o2.dateAdd) }
    }

    /*!
    Тип: вычисление
    Функция по фильтрации предложенной коллекции статей по ID возрастных ограничений
    Оперирует: строение Article
     */
    fun filterArticlesByAge(articles: List<Article>, ageParams: List<Int>): List<Article> {
        return if (ageParams.isEmpty()) {
            articles
        } else {
            articles.filter { ageParams.contains(it.censorAge) }
        }
    }

    /*!
    Тип: вычисление
    Функция по фильтрации предложенной коллекции статей по ID форм
    Оперирует: строение Article
     */
    fun filterArticlesByForm(articles: List<Article>, formParams: List<Int>): List<Article> {
        return if (formParams.isEmpty()) {
            articles
        } else {
            articles.filter { formParams.contains(it.formArt) }
        }
    }

    /*!
    Тип: действие
    Функция по получению свободного ID среди статей хранилища
    Оперирует: строение ArticleStorage
     */
    fun newIdForStorage(): Int {
        return newIdForCollection(getAllArticles())
    }

    /*!
    Тип: вычисление
    Функция по получению свободного ID среди предложенной коллекции
    Оперирует: бизнес-логика
     */
    fun newIdForCollection(articles: List<Article>): Int {
        val ids = articles.map { it.id }.toSet()
        var id = ids.maxOrNull()?.plus(1) ?: 1
        while (id in ids)
            id += 1
        return id
    }

    /*!
    Тип: действие
    Функция по добавлению объекта Article в хранилище.
    Является обёрткой для addArticleAndGetStorage()
    Оперирует: структура ArticleStorage
     */
    fun addArticleToStorage(article: Article): Int {
        articlesMap = addArticleAndGetStorage(article, articlesMap)
        return article.id
    }

    /*!
    Тип: действие
    Функция по созданию Article из параметров и добавление его в хранилище
    Оперирует: структура Article, структура ArticleStorage
     */
    fun createAndPushArticle(
        dateTime: LocalDateTime,
        name: String,
        creationParams: ArticleCreationParams,
        annotation: String,
        login: String
    ): Int {
        val id = newIdForStorage()
        val article = createArticle(dateTime, name, creationParams, annotation, id, login)
        return addArticleToStorage(article)
    }

    /*!
    Тип: вычисление, конструктор
    Функция по созданию объекта класса Article из параметров
    Оперирует: структура Article
     */
    fun createArticle(
        dateTime: LocalDateTime,
        name: String,
        creationParams: ArticleCreationParams,
        annotation: String,
        id: Int,
        login: String
    ) =
        Article(
            dateTime,
            name,
            creationParams.age,
            creationParams.form,
            emptyList(),
            creationParams.genre,
            annotation,
            emptyList(),
            id,
            login,
        )

    /*!
    Тип: действие (вызывает действие)
    Функция по обновлению существующей статьи и сохранению её в хранилище.
    Оперирует: Article, String, ArticleCreationParams, String, List<Int>, List<Int> -> Int
    */
    fun updateArticleAndPush(
        originalArticle: Article,
        newName: String,
        creationParams: ArticleCreationParams,
        annotation: String,
        login: String,
        tags: List<Int>,
        chapters: List<Int>
    ): Int {
        // 1. Чистое вычисление: создаем новую версию статьи на основе старой
        val updatedArticle = originalArticle.copy(
            nameArt = newName,
            censorAge = creationParams.age,
            formArt = creationParams.form,
            genre = creationParams.genre,
            annotation = annotation,
            user = login,
            tagsArt = tags,
            chapters = chapters
        )
        // 2. Действие: сохраняем (перезаписываем по тому же ID)
        return addArticleToStorage(updatedArticle)
    }

    /*!
    Тип: вычисление
    Функция по добавлению объекта Article в предложенную коллекцию.
    Является обёрткой для addValueToMap()
    Оперирует: ?
     */
    fun addArticleAndGetStorage(article: Article, storage: Map<Int, Article>) =
        addValueToMap(article.id, article, storage)

    /*!
    Тип: вычисление
    Функция по расчёту количества страниц для предложенной коллекции
    Оперирует: бизнес-логика
     */
    fun pageAmount(articles: List<Article>): Int {
        return getPageAmount(INPAGE, articles)
    }

    /*!
    Тип: вычисление
    Функция по получению списка статей по заданному номеру страницы из предложенной коллекции.
    Является оболочкой valuesByPage()
    Оперирует: бизнес-логика
     */
    fun articlesByPageNumber(
        pageNumber: Int,
        articles: List<Article>,
    ): List<Article> {
        return valuesByPage(pageNumber, articles)
    }

    /*!
    Тип: вычисление
    Функция по добавлению списка тэгов и глав в предложенный объект Article
    Оперирует: строение Article
     */
    fun addTagsAndChaptersToArticle(
        articleToDo: Article,
        tags: List<Int>,
        chapters: List<Int>,
    ): Article {
        return articleToDo.copy(
            tagsArt = tags,
            chapters = chapters
        )
    }
}
