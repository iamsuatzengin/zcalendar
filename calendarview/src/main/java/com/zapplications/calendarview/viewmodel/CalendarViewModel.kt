package com.zapplications.calendarview.viewmodel

import com.zapplications.core.data.DayItem
import com.zapplications.core.generator.CalendarGenerator
import com.zapplications.core.selection.SelectionListener
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.datetime.LocalDate

class CalendarViewModel(
    private val calendarGenerator: CalendarGenerator
) : SelectionListener {
    var calendarState: CalendarState = CalendarState()
        private set

    private val _selectedDates = MutableStateFlow<List<LocalDate>>(emptyList())
    val selectedDatesStateFlow: StateFlow<List<LocalDate>> get() = _selectedDates

    val selectedDates: List<LocalDate> get() = _selectedDates.value

    fun updateState(action: (CalendarState) -> CalendarState) {
        this.calendarState = action(calendarState)
    }

    fun addDateToSelected(date: LocalDate) {
        _selectedDates.update { list ->
            list.toMutableList().apply { add(date) }
        }
    }

    override fun onSingleDayClick(dayItem: DayItem.Day) {
        _selectedDates.update { listOf(dayItem.date) }
    }

    override fun onRangeDayClick(rangeItems: Pair<DayItem.Day?, DayItem.Day?>) {
        _selectedDates.update { list ->
            val startDate = rangeItems.first?.date
            val endDate = rangeItems.second?.date

            if (startDate != null && endDate == null) {
                listOf(startDate)
            } else if (startDate != null) {
                calendarGenerator.getDatesBetweenRangeItems(startDate = startDate, endDate = endDate)
            } else {
                emptyList()
            }
        }
    }

    override fun onMultipleDayClick(dayItems: Set<DayItem.Day>) {
        _selectedDates.update { dayItems.map { it.date } }
    }
}
