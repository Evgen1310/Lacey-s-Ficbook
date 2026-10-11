package ru.yarsu.web.funs

/*!
Тип: вычисление
Почему ты существуешь, я не помню
Оперирует: бизнес-логика
 */
fun prepareErrorStrings(
    hasFilterError: Boolean,
    articlesSize: Int
): List<Int> {
    return listOf(
        if (hasFilterError) 1 else 0, // Ошибка параметров
        if (articlesSize == 0) 1 else 0 // Нет статей
    )
}
