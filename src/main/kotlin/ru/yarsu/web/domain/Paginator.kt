package ru.yarsu.web.domain

import org.http4k.core.Uri
import org.http4k.core.query

const val RANGE = 3

class Paginator(
    private val viewPage: Int,
    private val allPages: Int,
    val uri: Uri,
) {

    /*!
    Тип: вычисление?
    Функция по получению списка ссылок пагинации назад.
    Является обёрткой для listPrevPageFromSource()
    Оперирует: строение Paginator
     */
    fun listPrevPage(): Map<Int, Uri> {
        return listPrevPageFromSource(viewPage, uri)
    }

    /*!
    Тип: вычисление?
    Функция по получению списка ссылок пагинации вперёд.
    Является обёрткой для listNextPageFromSource()
    Оперирует: строение Paginator
     */
    fun listNextPage(): Map<Int, Uri> {
        return listNextPageFromSource(viewPage, allPages, uri)
    }

    fun getStartPage(): Int {
        return viewPage
    }

    fun getAllPages(): Int {
        return allPages
    }

    fun getUri(i: Int = 0): Uri {
        if (i == allPages) {
            return getUriWithPage(i-1, uri)
        }
        return getUriWithPage(i, uri)
    }

    fun getNearStart(): Boolean {
        return isNearStart(viewPage)
    }

    fun getNearEnd(): Boolean {
        return isNearEnd(viewPage, allPages)
    }
}

/*!
Тип: вычисление
Функция по созданию списка пагинации назад для конкретных параметров
Оперирует: бизнес-правило
 */
fun listPrevPageFromSource(currentPage: Int, uri: Uri): Map<Int, Uri> {
    val start = getCurrentStartFromSource(currentPage)
    val rollback: MutableMap<Int, Uri> = mutableMapOf()
    for (i in start..<currentPage) {
        rollback[i] = getUriWithPage(i, uri)
    }
    return rollback
}

/*!
Тип: вычисление
Функция по созданию списка пагинации вперёд для конкретных параметров
Оперирует: бизнес-правило
 */
fun listNextPageFromSource(page: Int, allPages: Int, uri: Uri): Map<Int, Uri> {
    val end = getCurrentEndFromSource(page, allPages)
    val rollforward: MutableMap<Int, Uri> = mutableMapOf()
    for (i in page + 1..<end) {
        rollforward[i] = getUriWithPage(i, uri)
    }
    return rollforward
}

/*!
Тип: вычисление
Функция по проверке нахождения индекса текущей страницы к старту
Оперирует: бизнес-правило
 */
fun getCurrentStartFromSource(page: Int) =
    if (!isNearStart(page)) page - RANGE else 0

/*!
Тип: вычисление
Функция по проверке нахождения индекса текущей страницы к концу
Оперирует: бизнес-правило
 */
fun getCurrentEndFromSource(page: Int, allPages: Int) =
    if (!isNearEnd(page, allPages)) page + RANGE + 1 else allPages

/*!
Тип: вычисление
Функция по добавлению в ссылку параметра page
Оперирует: бизнес-правило
 */
fun getUriWithPage(page: Int, uri: Uri) =
    uri.query("page", page.toString())

fun isNearStart(page: Int) =
    page - RANGE - 2 < 0

fun isNearEnd(page: Int, allPages: Int) =
    page + RANGE + 3 > allPages
