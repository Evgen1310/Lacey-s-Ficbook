package ru.yarsu.web.funs

fun prepareErrorStrings(
    param: Boolean,
    articlesSize: Int,
): List<Int> {
    val result = mutableListOf(0, 0)
    if (param) {
        result[0] = 1
    }
    if (articlesSize == 0) {
        result[1] = 1
    }
    return result
}
