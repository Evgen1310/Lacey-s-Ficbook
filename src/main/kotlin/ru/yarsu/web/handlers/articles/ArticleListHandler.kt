package ru.yarsu.web.handlers.articles

import org.http4k.core.HttpHandler
import org.http4k.core.Request
import org.http4k.core.Response
import org.http4k.core.Status
import org.http4k.core.Uri
import org.http4k.core.findMultiple
import org.http4k.core.queries
import org.http4k.core.removeQueries
import org.http4k.core.with
import org.http4k.lens.Query
import org.http4k.lens.RequestContextLens
import org.http4k.lens.int
import ru.ac.uniyar.web.templates.ContextAwareViewRender
import ru.yarsu.web.domain.Paginator
import ru.yarsu.web.domain.Params
import ru.yarsu.web.domain.PebbleParams
import ru.yarsu.web.domain.article.AgeRatingBook
import ru.yarsu.web.domain.article.Article
import ru.yarsu.web.domain.article.ArticleWithData
import ru.yarsu.web.domain.storage.AddonStorage
import ru.yarsu.web.domain.storage.ArticleStorage
import ru.yarsu.web.domain.storage.UserStorage
import ru.yarsu.web.funs.lensOrDefault
import ru.yarsu.web.funs.lensOrNull
import ru.yarsu.web.funs.prepareErrorStrings
import ru.yarsu.web.models.ArticleListVM
import ru.yarsu.web.users.User

class ArticleListHandler(
    private val htmlView: ContextAwareViewRender,
    private val articles: ArticleStorage,
    private val addon: AddonStorage,
    private val userStorage: UserStorage,
    private val userLens: RequestContextLens<User?>,
) : HttpHandler {
    private val pageLens = Query.int().required("page")
    private val ageLens = Query.int().multi.optional("age")
    private val formLens = Query.int().multi.optional("formArt")

    override fun invoke(request: Request): Response {
        val login = lensOrNull(userLens, request)?.login ?: ""
        val uri = request.uri
        val page = lensOrDefault(pageLens, request, 0).takeIf { it > -1 } ?: 0
        val ages = lensOrNull(ageLens, request)
        val forms = lensOrNull(formLens, request)
        val params = prepareParamsss(ages, forms, uri)
        val sorted = articles.sortArticles(params)
        val paginator = Paginator(page, articles.pageAmount(sorted), uri.removeQueries("page"))
        val viewModel =
            ArticleListVM(
                makeArticlesWithData(login, articles.articlesByPageNumber(page, sorted), addon, userStorage),
                preparePebble(params),
                paginator,
                prepareErrorStrings(params.error, sorted.size),
                AgeRatingBook.entries,
                addon.getAllForms(),
            )
        return Response(Status.OK).with(htmlView(request) of viewModel)
    }
}

fun makeArticlesWithData(
    login: String,
    articles: List<Article>,
    addonStorage: AddonStorage,
    userStorage: UserStorage
): List<ArticleWithData> {
    val result = mutableListOf<ArticleWithData>()
    articles.forEach {
        var artData = addonStorage.createArticleWithData(it, userStorage, addonStorage)
        if (artData.article.user == login)
            artData = artData.copy(editArticle = true)
        result.add(artData)
    }
    return result
}

fun prepareParamsss(
    ages: List<Int>?,
    forms: List<Int>?,
    uri: Uri,
): Params {
    val ageParams = mutableListOf<Int>()
    val formParams = mutableListOf<Int>()
    var error = false
    if (uri.queries().findMultiple("age").isNotEmpty() && ages == null) {
        error = true
    }
    if (uri.queries().findMultiple("formArt").isNotEmpty() && forms == null) {
        error = true
    }
    ages?.forEach { ageParams.add(it) }
    forms?.forEach { formParams.add(it) }
    return Params(ageParams, formParams, error)
}

fun preparePebble(params: Params): PebbleParams {
    val ages = mutableListOf<AgeRatingBook>()
    params.ageParams.forEach {
        ages.add(AgeRatingBook.from(it) ?: AgeRatingBook.Since18)
    }

    return PebbleParams(ages, params.formParams)
}
