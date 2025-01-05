package ru.yarsu.web.domain.article

enum class AgeRatingBook(val value: String, val id: Int) {
    Since0("0+", 0),
    Since6("6+", 1),
    Since12("12+", 2),
    Since16("16+", 3),
    Since18("18+", 4),
    ;

    companion object : EnumFinder<Int, AgeRatingBook>(entries.associateBy { it.id })
}

abstract class EnumFinder<V, E>(private val valueMap: Map<V, E>) {
    fun from(value: V) = valueMap[value]
}
