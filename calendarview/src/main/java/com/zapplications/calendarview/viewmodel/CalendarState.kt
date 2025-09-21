package com.zapplications.calendarview.viewmodel

import com.zapplications.core.data.DayItem
import com.zapplications.core.data.Event
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate

data class CalendarInitialState(
    val firstDayOfWeek: DayOfWeek = DayOfWeek.MONDAY,
    val currentDate: LocalDate? = null,
    val minDate: LocalDate? = null,
    val maxDate: LocalDate? = null,
    val eventDates: Map<LocalDate, List<Event>>? = null
)

data class CalendarState(
    val dayItems: List<DayItem>,
    val daysOfWeek: List<DayOfWeek>,
    val currentDate: LocalDate? = null,
    val previousButtonIsEnabled: Boolean,
    val nextButtonIsEnabled: Boolean
) {
    companion object {
        fun initial(): CalendarState = CalendarState(
            dayItems = emptyList(),
            daysOfWeek = emptyList(),
            currentDate = null,
            previousButtonIsEnabled = false,
            nextButtonIsEnabled = false
        )
    }
}