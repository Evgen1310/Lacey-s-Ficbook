package ru.yarsu.web.handlers.articles

import org.http4k.core.Body
import org.http4k.core.HttpHandler
import org.http4k.core.Request
import org.http4k.core.Response
import org.http4k.core.Status
import org.http4k.core.with
import org.http4k.lens.FormField
import org.http4k.lens.RequestContextLens
import org.http4k.lens.Validator
import org.http4k.lens.int
import org.http4k.lens.nonBlankString
import org.http4k.lens.webForm
import ru.ac.uniyar.web.templates.ContextAwareViewRender
import ru.yarsu.web.domain.Permissions
import ru.yarsu.web.domain.article.AgeRatingBook
import ru.yarsu.web.domain.article.Article
import ru.yarsu.web.domain.storage.AddonStorage
import ru.yarsu.web.domain.storage.ArticleStorage
import ru.yarsu.web.funs.lensOrDefault
import ru.yarsu.web.models.NewArticleVM
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
//    private val authorField = FormField.nonBlankString().required("author")
//    private val aboutField = FormField.nonBlankString().optional("about")
    private val ageField = FormField.int().required("age")
    private val formArtField = FormField.int().required("formArt")
    private val genreField = FormField.int().required("genre")
    private val formLens =
        Body.webForm(
            Validator.Feedback,
            nameField,
            annotationField,
//            authorField,
//            aboutField,
            ageField,
            formArtField,
            genreField,
        ).toLens()

    override fun invoke(request: Request): Response {
        val login = userLens(request)?.login ?: ""
        val form = formLens(request)
        val age = lensOrDefault(ageField, form, -1)
        val formArt = lensOrDefault(formArtField, form, -1)
        val genre = lensOrDefault(genreField, form, -1)
        if (!permissionsLens(request).manageArticle) {
            return Response(Status.UNAUTHORIZED)
        }
        if (form.errors.isNotEmpty()) {
            val errorsName = form.errors.map { it.meta.name }
            val viewModel =
                NewArticleVM(
                    form,
                    AgeRatingBook.entries,
                    addon.getAllForms(),
                    addon.getAllGenres(),
                    errorsName,
                    IntParams(age, formArt, genre),
                )
            return Response(Status.OK).with(htmlView(request) of viewModel)
        }
        val id = storage.newId()
        storage.addArticle(
            Article(
                LocalDateTime.now(),
                nameField(form),
//                addon.addAuthor(authorField(form), lensOrDefault(aboutField, form, "")),
                age,
                formArt,
                listOf(),
                genre,
                lensOrDefault(annotationField, form, ""),
                listOf(),
                id,
                login,
            ),
        )
        return Response(Status.FOUND).header("Location", "/articles/$id")
    }
}

class IntParams(
    val age: Int,
    val form: Int,
    val genre: Int,
)
