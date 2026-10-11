package ru.yarsu.web.domain.storage

import kotlin.math.ceil

/*!
Тип: вычисление
 */
fun <IN: Any> checkValueInCollectionsWithIgnore (value: IN?, ignore: IN? = null, collection: Collection<IN>): Boolean {
    return (value != null) && (value != ignore) && (value in collection)
}

/*!
Тип: вычисление
Функция по удалению элемента из предложенной коллекции Max<Int, Any>
Оперирует: бизнес-логика
 */
fun <IN: Any> removeValueFromMap(id: Int, storage: Map<Int, IN>) =
    storage - id

/*!
Тип: вычисление
Функция по добавлению элемента в предложенную коллекцию Max<Int, Any>
Оперирует: бизнес-логика
 */
fun <IN: Any> addValueToMap(id: Int, element: IN, storage: Map<Int, IN>) =
    storage + (id to element)

/*!
Тип: вычисление
 */
fun newIdForMap(list: Map<Int, *>): Int {
    val ids = list.keys
    var id = ids.maxOrNull()?.plus(1) ?: 1
    while (id in ids)
        id += 1
    return id
}

/*!
Тип: вычисление
 */
fun <IN: Any> getValuesByIdsFromMap(ids: List<Int>, collection: Map<Int, IN>) =
    collection.filter { it.key in ids }.values

/*!
Тип: вычисление
 */
fun <IN: Any> valuesByPage(
    pageNumber: Int,
    list: List<IN>,
): List<IN> {
    val resultList = mutableListOf<IN>()
    for (i in 0..<INPAGE_DOP) {
        if (i + pageNumber * INPAGE_DOP < list.size) {
            resultList.add(list[i + pageNumber * INPAGE_DOP])
        }
    }
    return resultList
}

/*!
Тип: вычисление
Функция для определения количества страниц любой коллекции
Оперирует: бизнес-правила
 */
fun getPageAmount(inpage: Int, collection: Collection<*>): Int {
    return ceil(collection.size.toDouble() / inpage).toInt()
}