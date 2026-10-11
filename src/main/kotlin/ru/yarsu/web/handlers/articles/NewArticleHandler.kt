package ru.yarsu.web.handlers.articles

import org.http4k.core.HttpHandler
import org.http4k.core.Request
import org.http4k.core.Response
import org.http4k.core.Status
import org.http4k.core.Status.Companion.OK
import org.http4k.core.with
import org.http4k.lens.RequestContextLens
import org.http4k.lens.WebForm
import ru.ac.uniyar.web.templates.ContextAwareViewRender
import ru.yarsu.web.domain.Permissions
import ru.yarsu.web.domain.article.AgeRatingBook
import ru.yarsu.web.domain.article.createArticleCreationParams
import ru.yarsu.web.domain.storage.AddonStorage
import ru.yarsu.web.funs.checkPermissionForArticleCreation
import ru.yarsu.web.models.NewArticleVM
import ru.yarsu.web.models.createArticleCreationVM

class NewArticleHandler(
    private val addon: AddonStorage,
    private val htmlView: ContextAwareViewRender,
    private val permissionsLens: RequestContextLens<Permissions>,
) : HttpHandler {
    override fun invoke(request: Request): Response {
        val permissions = permissionsLens(request)
        val permissionsStatus = checkPermissionForArticleCreation(permissions)
        if (permissionsStatus != OK)
            return Response(permissionsStatus)

        val forms = addon.getAllForms()
        val genres = addon.getAllGenres()
        val articleParams = createArticleCreationParams()

        val viewModel =
            createArticleCreationVM(
                WebForm(),
                forms,
                genres,
                creationParams = articleParams
            )
        return Response(OK).with(htmlView(request) of viewModel)
    }
}
