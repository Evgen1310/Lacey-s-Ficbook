package ru.yarsu.web.domain.storage

import ru.yarsu.web.domain.Params
import ru.yarsu.web.domain.article.Article
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
    Тип: действие
    Функция по удалению статьи и её глав из хранилищ
    Оперирует: строение ArticleStorage, строение AddonStorage
     */
    fun deleteArticleAndChapters(article: Article, addon: AddonStorage) {
        deleteArticle(article.id)
        addon.handleArticleDeletion(article)
    }

    /*!
    Функция по удалению статьи из предложенной коллекции
    Оперирует: бизнес-логика
     */
    fun removeArticleAndGetStorage(id: Int, storage: Map<Int, Article>) =
        storage - id

    /*!
    Тип: действие
    Функция по сортировке и фильтрации статей из хранилища.
    Является обёрткой для sortAndFilterArticles()
    Оперирует: строение ArticleStorage
     */
    fun sortAndFilterArticlesFromStorage(params: Params): List<Article> {
        var result = getAllArticles()
        result = sortAndFilterArticles(result, params)
        return result
    }

    /*!
    Тип: вычисление
    Функция по сортировке и фильтрации предложенной коллекции статей
    Оперирует: ?
     */
    fun sortAndFilterArticles(articles: List<Article>, params: Params): List<Article> {
        var result = sortArticlesByDate(articles)
        if (params.error) {
            return result
        }
        result = filterArticlesByAge(result, params.ageParams)
        result = filterArticlesByForm(result, params.formParams)
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
    Тип: вычисление
    Функция по добавлению объекта Article в предложенную коллекцию
    Оперирует: ?
     */
    fun addArticleAndGetStorage(article: Article, storage: Map<Int, Article>) =
        storage + (article.id to article)

    /*!
    Тип: вычисление
    Функция по расчёту количества страниц для предложенной коллекции
    Оперирует: бизнес-логика
     */
    fun pageAmount(articles: List<Article>): Int {
        return ceil(articles.size.toDouble() / INPAGE).toInt()
    }

    /*!
    Тип: вычисление
    Функция по получению списка статей по заданному номеру страницы из предложенной коллекции
    Оперирует: бизнес-логика
     */
    fun articlesByPageNumber(
        pageNumber: Int,
        articles: List<Article>,
    ): List<Article> {
        val resultList = mutableListOf<Article>()
        for (i in 0..<INPAGE) {
            if (i + pageNumber * INPAGE < articles.size) {
                resultList.add(articles[i + pageNumber * INPAGE])
            }
        }
        return resultList
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
