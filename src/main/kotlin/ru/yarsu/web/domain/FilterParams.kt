package ru.yarsu.web.domain

class FilterParams(
    val ageParams: List<Int>,
    val formParams: List<Int>,
    val error: Boolean,
)

/*!
Тип: вычисление, конструктор
Функция по созданию объекта класса FilterParams
Оперирует: строение FilterParams
 */
fun createFilterParams(ageParams: List<Int>?, formParams: List<Int>?, error: Boolean) =
    FilterParams(ageParams ?: emptyList(), formParams ?: emptyList(), error)
