package ru.yarsu.web.handlers.articles

import org.http4k.core.HttpHandler
import org.http4k.core.Request
import org.http4k.core.Response
import org.http4k.core.Status
import org.http4k.core.Status.Companion.NOT_FOUND
import org.http4k.core.Status.Companion.UNAUTHORIZED
import org.http4k.core.with
import org.http4k.lens.FormField
import org.http4k.lens.Path
import org.http4k.lens.RequestContextLens
import org.http4k.lens.WebForm
import org.http4k.lens.int
import org.http4k.lens.nonBlankString
import ru.ac.uniyar.web.templates.ContextAwareViewRender
import ru.yarsu.db.DataBaseController
import ru.yarsu.web.domain.Permissions
import ru.yarsu.web.domain.article.AgeRatingBook
import ru.yarsu.web.funs.lensOrNull
import ru.yarsu.web.models.ArticleEditVM
import ru.yarsu.web.users.User

class ArticleEditHandler(
    private val htmlView: ContextAwareViewRender,
    private val userLens: RequestContextLens<User?>,
    private val permissionsLens: RequestContextLens<Permissions>,
    private val dataBaseController: DataBaseController,
) :
    HttpHandler {
    private val pathLens = Path.int().of("id")
    private val nameField = FormField.nonBlankString().required("name")
    private val annotationField = FormField.nonBlankString().optional("annotation")

//    private val authorField = FormField.nonBlankString().required("author")
//    private val aboutField = FormField.nonBlankString().optional("about")
    private val tagsOldField = FormField.nonBlankString().multi.required("tagsOld")
    private val chaptersOldField = FormField.int().multi.required("chaptersOld")
    private val namesOldField = FormField.nonBlankString().multi.required("namesOld")
    private val textsOldField = FormField.nonBlankString().multi.required("textsOld")

    override fun invoke(request: Request): Response {
        val permissions = permissionsLens(request)
        val login = userLens(request)?.login
        lensOrNull(pathLens, request)
            ?.let { id -> dataBaseController.getArticleById(id) }
            ?.let { entity ->
                if (!permissions.manageAllArticles) {
                    if (!(permissions.manageArticle && entity.user == login)) {
                        return Response(UNAUTHORIZED)
                    }
                }
                val formArt = if (dataBaseController.getFormById(entity.formArt).form == "Не выбрано") -1 else entity.formArt
                val genreArt = if (dataBaseController.getGenreById(entity.genre).genre == "Не выбрано") -1 else entity.genre
                val chapters = dataBaseController.getChaptersByIds(entity.chapters)
                val viewModel =
                    ArticleEditVM(
                        entity.nameArt,
                        WebForm().with(
                            nameField of entity.nameArt,
                            annotationField of entity.annotation,
//                            authorField of addon.getAuthorById(entity.author).name,
//                            aboutField of addon.getAuthorById(entity.author).description,
                            tagsOldField of dataBaseController.getTagsByIds(entity.tagsArt).map { it.tag },
                            chaptersOldField of chapters.map { it.chapter },
                            namesOldField of chapters.map { it.name },
                            textsOldField of chapters.map { it.content },
                        ),
                        AgeRatingBook.entries,
                        dataBaseController.getAllForms(),
                        dataBaseController.getAllGenres(),
                        listOf(),
                        IntParams(entity.censorAge, formArt, genreArt),
                    )
                return Response(Status.OK).with(htmlView(request) of viewModel)
            }
        return Response(NOT_FOUND)
    }
}
