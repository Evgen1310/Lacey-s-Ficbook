package ru.yarsu.web.handlers.articles

import org.http4k.core.HttpHandler
import org.http4k.core.Request
import org.http4k.core.Response
import org.http4k.core.Status.Companion.NOT_FOUND
import org.http4k.core.Status.Companion.OK
import org.http4k.core.with
import org.http4k.lens.FormField
import org.http4k.lens.Path
import org.http4k.lens.RequestContextLens
import org.http4k.lens.WebForm
import org.http4k.lens.int
import org.http4k.lens.nonBlankString
import ru.ac.uniyar.web.templates.ContextAwareViewRender
import ru.yarsu.web.domain.Permissions
import ru.yarsu.web.domain.article.Article
import ru.yarsu.web.domain.article.ArticleCreationParams
import ru.yarsu.web.domain.article.Chapter
import ru.yarsu.web.domain.storage.AddonStorage
import ru.yarsu.web.domain.storage.ArticleStorage
import ru.yarsu.web.funs.checkPermissionForArticleEditing
import ru.yarsu.web.funs.lensOrNull
import ru.yarsu.web.models.createArticleEditVM
import ru.yarsu.web.users.User

class ArticleEditHandler(
    private val htmlView: ContextAwareViewRender,
    private val storage: ArticleStorage,
    private val addon: AddonStorage,
    private val userLens: RequestContextLens<User?>,
    private val permissionsLens: RequestContextLens<Permissions>,
) :
    HttpHandler {
    private val pathLens = Path.int().of("id")
    private val nameField = FormField.nonBlankString().required("name")
    private val annotationField = FormField.nonBlankString().optional("annotation")
    private val tagsOldField = FormField.nonBlankString().multi.required("tagsOld")
    private val chaptersOldField = FormField.int().multi.required("chaptersOld")
    private val namesOldField = FormField.nonBlankString().multi.required("namesOld")
    private val textsOldField = FormField.nonBlankString().multi.required("textsOld")

    override fun invoke(request: Request): Response {
        val permissions = permissionsLens(request)
        val login = userLens(request)?.login

        lensOrNull(pathLens, request)
            ?.let { id -> storage.getArticleFromStorage(id) }
            ?.let { article ->
                val permissionsStatus = checkPermissionForArticleEditing(permissions, login, article.user)
                if (permissionsStatus != OK)
                    return Response(permissionsStatus)

                val formArt = addon.getFormIdOrDefault(article.formArt)
                val genreArt = addon.getGenreIdOrDefault(article.genre)
                val chapters = addon.getChaptersOrDefault(article.chapters)

                val articleParams = ArticleCreationParams(article.censorAge, formArt, genreArt)
                val forms = addon.getAllForms()
                val genres = addon.getAllGenres()

                val viewModel =
                    createArticleEditVM(
                        article.nameArt,
                        webFormForArticleEdit(article, chapters),
                        forms,
                        genres,
                        creationParams = articleParams
                    )

                return Response(OK).with(htmlView(request) of viewModel)
            }
        return Response(NOT_FOUND)
    }

    private fun webFormForArticleEdit(article: Article, chapters: List<Chapter>) =
        WebForm().with(
            nameField of article.nameArt,
            annotationField of article.annotation,
            tagsOldField of addon.getTagsByIds(article.tagsArt),
            chaptersOldField of chapters.map { it.chapter },
            namesOldField of chapters.map { it.name },
            textsOldField of chapters.map { it.content },
        )
}
