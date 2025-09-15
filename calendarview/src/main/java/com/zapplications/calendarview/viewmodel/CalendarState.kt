package com.zapplications.calendarview.viewmodel

import com.zapplications.core.data.Event
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate

data class CalendarState(
    val firstDayOfWeek: DayOfWeek = java.time.DayOfWeek.MONDAY,
    val currentDate: LocalDate? = null,
    val minDate: LocalDate? = null,
    val maxDate: LocalDate? = null,
    var eventDates: Map<LocalDate, List<Event>>? = null
)
