package com.zapplications.core.data

import kotlinx.datetime.LocalDate

sealed interface DayItem {
    data class Day(
        val date: LocalDate,
        val dayOfMonth: Int,
        val events: List<Event>? = null,
        val isSelected: Boolean,
        val isEnabled: Boolean
    ) : DayItem

    data object Empty : DayItem
}
