package ru.yarsu.web.domain.storage

import org.http4k.lens.FormField
import org.http4k.lens.WebForm
import org.http4k.lens.int
import org.http4k.lens.nonBlankString
import ru.yarsu.web.domain.Params
import ru.yarsu.web.domain.article.Article
import ru.yarsu.web.funs.lensOrDefault
import kotlin.math.ceil

const val INPAGE = 10

class ArticleStorage(private var articles: List<Article>) {
    private val idAndArticle: MutableMap<Int, Article> = articles.associateBy({ it.id }, { it }).toMutableMap()
    private val ids: MutableSet<Int> = articles.map { it.id }.toMutableSet()
    private val articlesSize = idAndArticle.size

    fun getAllArticles(): List<Article> = idAndArticle.map { it.value }

    fun getArticleId(id: Int): Article? = idAndArticle[id]

    fun deleteArticle(id: Int) {
        idAndArticle.remove(id)
        ids.remove(id)
    }

    fun sortArticles(params: Params): List<Article> {
        var sorted = idAndArticle.map { it.value }
        sorted = sorted.sortedWith { o1, o2 -> o1.dateAdd.compareTo(o2.dateAdd) }
        if (params.error) {
            return sorted
        }
        params.ageParams.isNotEmpty()
            .let { if (it) sorted = sorted.filter { art -> params.ageParams.contains(art.censorAge) } }
        params.formParams.isNotEmpty()
            .let { if (it) sorted = sorted.filter { art -> params.formParams.contains(art.formArt) } }
        return sorted
    }

    fun newId(): Int {
        var id = articlesSize
        while (id in ids)
            id += 1
        return id
    }

    fun addArticle(article: Article): Int {
        idAndArticle[article.id] = article
        ids.add(article.id)
        return article.id
    }

    fun pageAmount(sorted: List<Article>): Int {
        return ceil(sorted.size.toDouble() / INPAGE).toInt()
    }

    fun articlesByPageNumber(
        pageNumber: Int,
        sorted: List<Article>,
    ): List<Article> {
        val resultList = mutableListOf<Article>()
        for (i in 0..<INPAGE) {
            if (i + pageNumber * INPAGE < sorted.size) {
                resultList.add(sorted[i + pageNumber * INPAGE])
            }
        }
        return resultList
    }

    fun prepareArticle(
        articleToDo: Article,
        tags: List<Int>,
        chapters: List<Int>,
    ): Int {
        return addArticle(
            articleToDo.copy(
                tagsArt = tags,
                chapters = chapters
            )
        )
    }
}
