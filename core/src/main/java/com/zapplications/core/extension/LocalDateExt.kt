package com.zapplications.core.extension

import kotlinx.datetime.LocalDate

fun LocalDate?.isSameMonthOrBefore(otherDate: LocalDate?): Boolean {
    if (this == null || otherDate == null) return false

    return this.year <= otherDate.year && this.month <= otherDate.month
}

fun LocalDate?.isSameMonthOrAfter(otherDate: LocalDate?): Boolean {
    if (this == null || otherDate == null) return false

    return this.year >= otherDate.year && this.month >= otherDate.month
}
