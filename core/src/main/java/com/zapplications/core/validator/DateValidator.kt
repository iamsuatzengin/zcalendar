package com.zapplications.core.validator

import kotlinx.datetime.LocalDate

/**
 * An interface for validating dates.
 *
 * Implementations of this interface can define custom rules
 * for determining whether a given [LocalDate] is considered valid.
 */
interface DateValidator {
    fun isValid(date: LocalDate): Boolean
}
