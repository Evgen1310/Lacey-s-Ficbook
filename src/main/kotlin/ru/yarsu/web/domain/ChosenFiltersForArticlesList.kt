package ru.yarsu.web.domain

import ru.yarsu.web.domain.article.AgeRatingBook

data class ChosenFiltersForArticlesList (
    val ages: List<AgeRatingBook>,
    val forms: List<Int>,
)

/*!
Тип: вычисление, конструктор
Функция по созданию объекта класса ChosenFiltersForArticlesList
Оперирует: строение ChosenFiltersForArticlesList, строение FilterParams
 */
fun prepareChosenFilters(filterParams: FilterParams): ChosenFiltersForArticlesList {
    val ages = filterParams.ageParams.map { AgeRatingBook.from(it) ?: AgeRatingBook.Since18 }
    return ChosenFiltersForArticlesList(ages, filterParams.formParams)
}
