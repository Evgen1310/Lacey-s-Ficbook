package ru.yarsu.web.domain

import ru.yarsu.web.domain.article.AgeRatingBook
import ru.yarsu.web.domain.article.Form

class PebbleParams(
    val ages: List<AgeRatingBook>,
    val forms: List<Int>,
)
