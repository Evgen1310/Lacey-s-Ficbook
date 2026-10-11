package ru.yarsu.web.handlers.articles

import org.http4k.core.Body
import org.http4k.core.HttpHandler
import org.http4k.core.Request
import org.http4k.core.Response
import org.http4k.core.Status
import org.http4k.core.Status.Companion.OK
import org.http4k.core.with
import org.http4k.lens.FormField
import org.http4k.lens.RequestContextLens
import org.http4k.lens.Validator
import org.http4k.lens.WebForm
import org.http4k.lens.int
import org.http4k.lens.nonBlankString
import org.http4k.lens.webForm
import org.http4k.template.ViewModel
import ru.ac.uniyar.web.templates.ContextAwareViewRender
import ru.yarsu.web.domain.Permissions
import ru.yarsu.web.domain.article.AgeRatingBook
import ru.yarsu.web.domain.article.Article
import ru.yarsu.web.domain.article.Form
import ru.yarsu.web.domain.article.Genre
import ru.yarsu.web.domain.article.createArticleCreationParams
import ru.yarsu.web.domain.storage.AddonStorage
import ru.yarsu.web.domain.storage.ArticleStorage
import ru.yarsu.web.funs.checkPermissionForArticleCreation
import ru.yarsu.web.funs.lensOrDefault
import ru.yarsu.web.funs.lensOrNull
import ru.yarsu.web.models.NewArticleVM
import ru.yarsu.web.models.handleArticleCreationError
import ru.yarsu.web.users.User
import java.time.LocalDateTime

class NewArticleHandlerPOST(
    private val htmlView: ContextAwareViewRender,
    private val storage: ArticleStorage,
    private val addon: AddonStorage,
    private val userLens: RequestContextLens<User?>,
    private val permissionsLens: RequestContextLens<Permissions>,
) : HttpHandler {
    private val nameField = FormField.nonBlankString().required("name")
    private val annotationField = FormField.nonBlankString().optional("annotation")
    private val ageField = FormField.int().required("age")
    private val formArtField = FormField.int().required("formArt")
    private val genreField = FormField.int().required("genre")

    private val formLens =
        Body.webForm(
            Validator.Feedback,
            nameField,
            annotationField,
            ageField,
            formArtField,
            genreField,
        ).toLens()

    override fun invoke(request: Request): Response {
        val permissions = permissionsLens(request)
        val permissionsStatus = checkPermissionForArticleCreation(permissions)
        if (permissionsStatus != OK)
            return Response(permissionsStatus)

        val login = lensOrNull(userLens, request)?.login ?: ""
        val form = formLens(request)
        val age = lensOrDefault(ageField, form, -1)
        val formArt = lensOrDefault(formArtField, form, -1)
        val genre = lensOrDefault(genreField, form, -1)
        val annotation = lensOrDefault(annotationField, form, "")

        val creationParams = createArticleCreationParams(age, formArt ,genre)

        if (form.errors.isNotEmpty()) {
            val forms = addon.getAllForms()
            val genres = addon.getAllGenres()
            val viewModel =
                handleArticleCreationError(form, forms, genres, creationParams)
            return Response(OK).with(htmlView(request) of viewModel)
        }

        val id = storage.createAndPushArticle(
                LocalDateTime.now(),
                nameField(form),
                creationParams,
                annotation,
                login,
        )
        return Response(Status.FOUND).header("Location", "/articles/$id")
    }
}
