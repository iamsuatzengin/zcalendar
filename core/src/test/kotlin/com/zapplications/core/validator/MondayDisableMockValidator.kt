package com.zapplications.core.validator

import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate

class MondayDisableMockValidator : DateValidator {
    override fun isValid(date: LocalDate): Boolean = date.dayOfWeek != DayOfWeek.MONDAY
}
