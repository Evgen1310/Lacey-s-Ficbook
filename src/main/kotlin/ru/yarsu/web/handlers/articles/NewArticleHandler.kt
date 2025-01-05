package ru.yarsu.web.handlers.articles

import org.http4k.core.HttpHandler
import org.http4k.core.Request
import org.http4k.core.Response
import org.http4k.core.Status
import org.http4k.core.with
import org.http4k.lens.RequestContextLens
import org.http4k.lens.WebForm
import ru.ac.uniyar.web.templates.ContextAwareViewRender
import ru.yarsu.db.DataBaseController
import ru.yarsu.web.domain.Permissions
import ru.yarsu.web.domain.article.AgeRatingBook
import ru.yarsu.web.models.NewArticleVM

class NewArticleHandler(
    private val htmlView: ContextAwareViewRender,
    private val permissionsLens: RequestContextLens<Permissions>,
    private val dataBaseController: DataBaseController,
) : HttpHandler {
    override fun invoke(request: Request): Response {
        if (!permissionsLens(request).manageArticle) {
            return Response(Status.UNAUTHORIZED)
        }
        val viewModel =
            NewArticleVM(
                WebForm(),
                AgeRatingBook.entries,
                dataBaseController.getAllForms(),
                dataBaseController.getAllGenres(),
                listOf(),
                IntParams(-1, -1, -1),
            )
        return Response(Status.OK).with(htmlView(request) of viewModel)
    }
}
