package ru.yarsu.web.domain

import org.http4k.core.Uri
import org.http4k.core.query

const val RANGE = 3

class Paginator(
    private var viewPage: Int,
    private var allPages: Int,
    val uri: Uri,
) {
    private val nearStart = viewPage - RANGE - 2 < 0
    private val nearEnd = viewPage + RANGE + 3 > allPages

    fun listPrevPage(): Map<Int, Uri> {
        val rollback: MutableMap<Int, Uri> = mutableMapOf()
        val start = if (!nearStart) viewPage - RANGE else 0
        for (i in start..<viewPage) {
            rollback[i] = uri.query("page", i.toString())
        }
        return rollback
    }

    fun listNextPage(): Map<Int, Uri> {
        val rollforward: MutableMap<Int, Uri> = mutableMapOf()
        val end = if (!nearEnd) viewPage + RANGE + 1 else allPages
        for (i in viewPage + 1..<end) {
            rollforward[i] = uri.query("page", i.toString())
        }
        return rollforward
    }

    fun getStartPage(): Int {
        return viewPage
    }

    fun getAllPages(): Int {
        return allPages
    }

    fun getUri(i: Int = 0): Uri {
        if (i == allPages) {
            return uri.query("page", (i - 1).toString())
        }
        return uri.query("page", i.toString())
    }

    fun getNearStart(): Boolean {
        return nearStart
    }

    fun getNearEnd(): Boolean {
        return nearEnd
    }
}
