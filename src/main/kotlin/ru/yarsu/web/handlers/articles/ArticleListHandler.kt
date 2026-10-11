package ru.yarsu.web.handlers.articles

import org.http4k.core.HttpHandler
import org.http4k.core.Request
import org.http4k.core.Response
import org.http4k.core.Status
import org.http4k.core.removeQueries
import org.http4k.core.with
import org.http4k.lens.Query
import org.http4k.lens.RequestContextLens
import org.http4k.lens.int
import ru.ac.uniyar.web.templates.ContextAwareViewRender
import ru.yarsu.web.domain.Paginator
import ru.yarsu.web.domain.article.AgeRatingBook
import ru.yarsu.web.domain.createFilterParams
import ru.yarsu.web.domain.prepareChosenFilters
import ru.yarsu.web.domain.storage.AddonStorage
import ru.yarsu.web.domain.storage.ArticleStorage
import ru.yarsu.web.domain.storage.UserStorage
import ru.yarsu.web.funs.checkForErrorInAge
import ru.yarsu.web.funs.checkForErrorInForm
import ru.yarsu.web.funs.lensOrDefault
import ru.yarsu.web.funs.lensOrNull
import ru.yarsu.web.funs.prepareErrorStrings
import ru.yarsu.web.models.ArticleListVM
import ru.yarsu.web.users.User

class ArticleListHandler(
    private val htmlView: ContextAwareViewRender,
    private val storage: ArticleStorage,
    private val addon: AddonStorage,
    private val userStorage: UserStorage,
    private val userLens: RequestContextLens<User?>,
) : HttpHandler {
    private val pageLens = Query.int().required("page")
    private val ageLens = Query.int().multi.optional("age")
    private val formLens = Query.int().multi.optional("formArt")

    override fun invoke(request: Request): Response {
        val userInfo = userStorage.getUserInfo(userLens(request)?.login ?: "")

        val uri = request.uri
        val page = lensOrDefault(pageLens, request, 0).takeIf { it > -1 } ?: 0
        val ages = lensOrNull(ageLens, request)
        val forms = lensOrNull(formLens, request)

        val hasFilterError = (checkForErrorInAge(ages, uri) || checkForErrorInForm(forms, uri))
        val paramsForFilter = createFilterParams(ages, forms, hasFilterError)

        val articles = storage.sortAndFilterArticlesFromStorage(paramsForFilter)

        val paginator = Paginator(page, storage.pageAmount(articles), uri.removeQueries("page"))

        val articlesWithData = addon.makeArticlesWithData(userInfo, storage.articlesByPageNumber(page, articles))
        val chosenFilters = prepareChosenFilters(paramsForFilter)
        val errorStrings = prepareErrorStrings(paramsForFilter.error, articles.size)

        val viewModel =
            ArticleListVM(
                articlesWithData,
                chosenFilters,
                paginator,
                errorStrings,
                AgeRatingBook.entries,
                addon.getAllForms(),
            )

        return Response(Status.OK).with(htmlView(request) of viewModel)
    }
}
