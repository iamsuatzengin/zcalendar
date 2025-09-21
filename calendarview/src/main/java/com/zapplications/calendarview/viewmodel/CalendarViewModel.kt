package com.zapplications.calendarview.viewmodel

import com.zapplications.core.data.DayItem
import com.zapplications.core.data.Event
import com.zapplications.core.extension.isSameMonthOrAfter
import com.zapplications.core.extension.isSameMonthOrBefore
import com.zapplications.core.generator.CalendarGenerator
import com.zapplications.core.selection.SelectionListener
import com.zapplications.core.validator.DateValidator
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.datetime.Clock
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.minus
import kotlinx.datetime.plus
import kotlinx.datetime.toLocalDateTime

class CalendarViewModel(
    private val calendarGenerator: CalendarGenerator,
    private val dateValidator: DateValidator?
) : SelectionListener {
    var calendarInitialState: CalendarInitialState = CalendarInitialState()
        private set

    private val _selectedDates = MutableStateFlow<List<LocalDate>>(emptyList())
    val selectedDatesStateFlow: StateFlow<List<LocalDate>> get() = _selectedDates

    private val _calendarState = MutableStateFlow(CalendarState.initial())
    val calendarState: StateFlow<CalendarState> get() = _calendarState

    val selectedDates: List<LocalDate> get() = _selectedDates.value

    internal fun setInitialState(
        firstDayOfWeek: DayOfWeek = DayOfWeek.MONDAY,
        currentDate: LocalDate? = null,
        minDate: LocalDate? = null,
        maxDate: LocalDate? = null,
        eventDates: Map<LocalDate, List<Event>>? = null
    ) {
        calendarInitialState = CalendarInitialState(
            firstDayOfWeek = firstDayOfWeek,
            currentDate = currentDate,
            minDate = minDate,
            maxDate = maxDate,
            eventDates = eventDates
        )
    }

    fun updateState(action: (CalendarInitialState) -> CalendarInitialState) {
        this.calendarInitialState = action(calendarInitialState)
    }

    fun addDateToSelected(date: LocalDate) {
        _selectedDates.update { list ->
            list.toMutableList().apply { add(date) }
        }
    }

    fun init(onInit: (DayItem.Day) -> Unit) {
        val daysOfWeek = calendarGenerator.getDaysOfWeek(firstDayOfWeek = calendarInitialState.firstDayOfWeek)
        val dayItems = getMonthDays()
        _calendarState.update {
            it.copy(
                dayItems = dayItems,
                daysOfWeek = daysOfWeek,
                currentDate = calendarInitialState.currentDate,
                previousButtonIsEnabled = true,
                nextButtonIsEnabled = true
            )
        }
        (dayItems.firstOrNull { it is DayItem.Day && it.isSelected } as? DayItem.Day)?.let {
            onInit(it)
        }
    }

    private fun getMonthDays(isUserInteraction: Boolean = false): List<DayItem> {
        val currentDate = calendarInitialState.currentDate ?: getLocalCurrentDate()
        updateState { it.copy(currentDate = currentDate) }
        return calendarGenerator.getMonthDays(
            currentDate = currentDate,
            firstDayOfWeek = calendarInitialState.firstDayOfWeek,
            eventDates = calendarInitialState.eventDates,
            selectedDates = selectedDates,
            minDate = calendarInitialState.minDate,
            maxDate = calendarInitialState.maxDate,
            dateValidator = dateValidator,
            isInitial = !isUserInteraction
        )
    }

    private fun getLocalCurrentDate(): LocalDate =
        Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date

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

    fun onPreviousMonthClick() {
        var currentState = _calendarState.value

        val currentDate = calendarInitialState.currentDate
        val previousDate = currentDate?.minus(1, DateTimeUnit.MONTH)
        val minDate = calendarInitialState.minDate

        if (previousDate != null && minDate != null && previousDate.isSameMonthOrBefore(minDate)) {
           currentState = currentState.copy(previousButtonIsEnabled = false)
        }

        updateState { it.copy(currentDate = previousDate) }
        currentState = currentState.copy(
            currentDate = previousDate,
            nextButtonIsEnabled = true,
            dayItems = getMonthDays(isUserInteraction = true)
        )
        _calendarState.update { currentState }
    }

    fun onNextMonthClick() {
        var currentState = _calendarState.value

        val currentDate = calendarInitialState.currentDate
        val nextDate = currentDate?.plus(1, DateTimeUnit.MONTH)
        val maxDate = calendarInitialState.maxDate
        if (nextDate != null && maxDate != null && nextDate.isSameMonthOrAfter(maxDate)) {
            currentState = currentState.copy(nextButtonIsEnabled = false)
        }
        updateState { it.copy(currentDate = nextDate) }
        currentState = currentState.copy(
            currentDate = nextDate,
            previousButtonIsEnabled = true,
            dayItems = getMonthDays(isUserInteraction = true)
        )

        _calendarState.update { currentState }
    }
}
