package ru.yarsu.web.handlers.editor

import org.http4k.core.HttpHandler
import org.http4k.core.Request
import org.http4k.core.Response
import org.http4k.core.Status
import org.http4k.lens.Path
import org.http4k.lens.int
import ru.yarsu.web.domain.storage.AddonStorage
import ru.yarsu.web.funs.lensOrNull

class GenreDeleteHandlerPOST(private val addonStorage: AddonStorage) :
    HttpHandler {
    private val pathLens = Path.int().of("id")
    override fun invoke(request: Request): Response {
        lensOrNull(pathLens, request)
            ?.let { id ->
                addonStorage.getGenreById(id)
                    .let { genre ->
                        if (genre == "Не определено")
                            return Response(Status.NOT_FOUND)
                        addonStorage.removeGenre(id)
                        return Response(Status.FOUND).header("Location", "/redaction/genres")
                    }
            } ?: return Response(Status.NOT_FOUND)
    }
}
