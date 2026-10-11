package ru.yarsu.web.funs

import org.http4k.core.Uri
import org.http4k.core.findMultiple
import org.http4k.core.queries

/*!
Тип: вычисление
Функция по проверке наличия ошибки в запросе относительно возрастного рейтинга
Оперирует: бизнес-правило
 */
fun checkForErrorInAge(ages: List<Int>?, uri: Uri) =
    uri.queries().findMultiple("age").isNotEmpty() && ages == null

/*!
Тип: вычисление
Функция по проверке наличия ошибки в запросе относительно формы статьи
Оперирует: бизнес-правило
 */
fun checkForErrorInForm(forms: List<Int>?, uri: Uri) =
    uri.queries().findMultiple("formArt").isNotEmpty() && forms == null
