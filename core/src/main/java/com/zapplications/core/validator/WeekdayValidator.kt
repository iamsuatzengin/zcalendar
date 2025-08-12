package com.zapplications.core.validator

import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate

/**
 * A [DateValidator] that validates if a given date is a weekday (Monday to Friday).
 *
 * This validator checks if the provided [LocalDate] falls on any day from Monday to Friday,
 * considering Saturday and Sunday as invalid.
 *
 * @see DateValidator
 */
class WeekdayValidator : DateValidator {
    override fun isValid(date: LocalDate): Boolean {
        return date.dayOfWeek != DayOfWeek.SATURDAY && date.dayOfWeek != DayOfWeek.SUNDAY
    }
}
