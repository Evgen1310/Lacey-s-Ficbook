package ru.yarsu.web.handlers.articles

import org.http4k.core.Body
import org.http4k.core.HttpHandler
import org.http4k.core.Request
import org.http4k.core.Response
import org.http4k.core.Status
import org.http4k.core.Status.Companion.NOT_FOUND
import org.http4k.core.with
import org.http4k.lens.FormField
import org.http4k.lens.Path
import org.http4k.lens.RequestContextLens
import org.http4k.lens.Validator
import org.http4k.lens.WebForm
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
import ru.yarsu.web.funs.lensOrNull
import ru.yarsu.web.models.ArticleEditVM
import ru.yarsu.web.users.User

class ArticleEditHandlerPOST(
    private val htmlView: ContextAwareViewRender,
    val storage: ArticleStorage,
    private val addon: AddonStorage,
    private val userLens: RequestContextLens<User?>,
    private val permissionsLens: RequestContextLens<Permissions>,
) :
    HttpHandler {
    private val pathLens = Path.int().of("id")
    private val nameField = FormField.nonBlankString().required("name")
    private val annotationField = FormField.nonBlankString().optional("annotation")
    private val ageField = FormField.int().required("age")
    private val formArtField = FormField.int().required("formArt")
    private val genreField = FormField.int().required("genre")
    private val tagsOldField = FormField.nonBlankString().multi.optional("tagsOld")
    private val chaptersOldField = FormField.int().multi.required("chaptersOld")
    private val namesOldField = FormField.nonBlankString().multi.required("namesOld")
    private val textsOldField = FormField.nonBlankString().multi.required("textsOld")
    private val tagsNewField = FormField.nonBlankString().optional("tagsNew")
    private val chaptersNewField = FormField.int().optional("chaptersNew")
    private val namesNewField = FormField.nonBlankString().optional("namesNew")
    private val textsNewField = FormField.nonBlankString().optional("textsNew")
    private val delChpField = FormField.int().multi.optional("delChp")
    private val formLens =
        Body.webForm(
            Validator.Feedback,
            nameField,
            annotationField,
            ageField,
            formArtField,
            genreField,
            tagsOldField,
            chaptersOldField,
            namesOldField,
            textsOldField,
            tagsNewField,
            chaptersNewField,
            namesNewField,
            textsNewField,
            delChpField,
        ).toLens()

    override fun invoke(request: Request): Response {
        val permissions = permissionsLens(request)
        val login = userLens(request)?.login ?: ""
        lensOrNull(pathLens, request)
            ?.let { id -> storage.getArticleId(id) }
            ?.let { entity ->
                if (!permissions.manageAllArticles) {
                    if (!(permissions.manageArticle && entity.user == login)) {
                        return Response(Status.UNAUTHORIZED)
                    }
                }
                val form = formLens(request)
                val formArt =
                    if (addon.getFormById(lensOrDefault(formArtField, form, -1)) == "Не выбрано") -1 else lensOrDefault(
                        formArtField,
                        form,
                        -1
                    )
                val genreArt =
                    if (addon.getGenreById(lensOrDefault(genreField, form, -1)) == "Не выбрано") -1 else lensOrDefault(
                        genreField,
                        form,
                        -1
                    )
                val errors =
                    prepareErrors(
                        form,
                        entity,
                    )
                if (errors.isNotEmpty()) {
                    val viewModel =
                        ArticleEditVM(
                            entity.nameArt,
                            form,
                            AgeRatingBook.entries,
                            addon.getAllForms(),
                            addon.getAllGenres(),
                            errors,
                            IntParams(
                                lensOrDefault(ageField, form, -1),
                                formArt,
                                genreArt,
                            ),
                        )
                    return Response(Status.OK).with(htmlView(request) of viewModel)
                }
                val chapters = addon.prepareChapters(
                    entity,
                    lensOrDefault(chaptersOldField, form, listOf()),
                    lensOrDefault(delChpField, form, listOf()),
                    lensOrDefault(namesOldField, form, listOf()),
                    lensOrDefault(textsOldField, form, listOf()),
                    lensOrDefault(chaptersNewField, form, -1),
                    lensOrDefault(namesNewField, form, ""),
                    lensOrDefault(textsNewField, form, ""),
                )
                val tags = addon.prepareTags(
                    lensOrDefault(tagsOldField, form, listOf()),
                    lensOrDefault(tagsNewField, form, ""),
                )
                val articleToDo = Article(
                    entity.dateAdd,
                    nameField(form),
                    lensOrDefault(ageField, form, -1),
                    lensOrDefault(formArtField, form, -1),
                    listOf(),
                    lensOrDefault(genreField, form, -1),
                    lensOrDefault(annotationField, form, ""),
                    listOf(),
                    entity.id,
                    login,
                )
                val id = storage.prepareArticle(articleToDo, tags, chapters)
                return Response(Status.FOUND).header("Location", "/articles/$id")
            } ?: return Response(NOT_FOUND)
    }

    private fun prepareErrors(
        form: WebForm,
        entity: Article,
    ): List<String> {
        val errors = form.errors.map { it.meta.name }.toMutableList()
        if (entity.chapters.size != lensOrDefault(namesOldField, form, listOf()).size) {
            errors.add("namesOld")
        }
        if (entity.chapters.size != lensOrDefault(chaptersOldField, form, listOf()).size) {
            errors.add("chaptersOld")
        }
        if (entity.chapters.size != lensOrDefault(textsOldField, form, listOf()).size) {
            errors.add("textsOld")
        }
        if (entity.tagsArt.isEmpty()) {
            errors.remove("tagsOld")
        }
        if (entity.chapters.isEmpty()) {
            errors.remove("chaptersOld")
            errors.remove("namesOld")
            errors.remove("textsOld")
        }
        if (lensOrNull(chaptersNewField, form) == null && lensOrNull(namesNewField, form) != null ||
            lensOrNull(chaptersNewField, form) != null && lensOrNull(namesNewField, form) == null
        ) {
            errors.add("newChapterErr")
        }
        return errors
    }
}
