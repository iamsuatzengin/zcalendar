package com.zapplications.calendarview.bottomsheet.builder

import com.zapplications.calendarview.MonthlyCalendarView
import com.zapplications.calendarview.bottomsheet.MonthlyCalendarSheet
import com.zapplications.calendarview.config.CalendarViewConfig
import com.zapplications.core.data.DayItem
import com.zapplications.core.data.Event
import com.zapplications.core.selection.MultipleSelectionManager
import com.zapplications.core.selection.RangeSelectionManager
import com.zapplications.core.selection.SelectionManager
import com.zapplications.core.selection.SingleSelectionManager
import com.zapplications.core.validator.DateValidator
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate

class MonthlyCalendarBuilder<T> {
    var firstDayOfWeek: DayOfWeek = DayOfWeek.MONDAY
    var calendarViewConfig: CalendarViewConfig = CalendarViewConfig()
    var currentDate: LocalDate? = null
    var eventDates: Map<LocalDate, List<Event>>? = null
    var minDate: LocalDate? = null
    var maxDate: LocalDate? = null
    var dateValidator: DateValidator? = null
    var selectionManager: SelectionManager<T>? = null
        private set

    companion object {
        fun single(): MonthlyCalendarBuilder<DayItem.Day> = MonthlyCalendarBuilder<DayItem.Day>().apply {
            selectionManager = SingleSelectionManager()
        }
        fun range(): MonthlyCalendarBuilder<Pair<DayItem.Day?, DayItem.Day?>> = MonthlyCalendarBuilder<Pair<DayItem.Day?, DayItem.Day?>>().apply {
            selectionManager = RangeSelectionManager()
        }
        fun multiple(): MonthlyCalendarBuilder<Set<DayItem.Day>> = MonthlyCalendarBuilder<Set<DayItem.Day>>().apply {
            selectionManager = MultipleSelectionManager()
        }
    }

    fun setFirstDayOfWeek(firstDayOfWeek: DayOfWeek): MonthlyCalendarBuilder<T> {
        this.firstDayOfWeek = firstDayOfWeek
        return this
    }

    fun setCalendarViewConfig(calendarViewConfig: CalendarViewConfig): MonthlyCalendarBuilder<T> {
        this.calendarViewConfig = calendarViewConfig
        return this
    }

    /**
     * Set the current date or initially the selected date.
     *
     * This will also update the displayed month and year in the header.
     *
     * @param currentDate The [LocalDate] to set as the current date.
     * @return This [MonthlyCalendarView] instance for chaining.
     */
    fun setCurrentDate(currentDate: LocalDate): MonthlyCalendarBuilder<T> {
        this.currentDate = currentDate
        return this
    }

    /**
     * For now, this method will not be used.
     */
    fun setEventDates(eventDates: Map<LocalDate, List<Event>>): MonthlyCalendarBuilder<T> {
        this.eventDates = eventDates
        return this
    }

    fun minDate(minDate: LocalDate): MonthlyCalendarBuilder<T> {
        this.minDate = minDate
        return this
    }

    fun maxDate(maxDate: LocalDate): MonthlyCalendarBuilder<T> {
        this.maxDate = maxDate
        return this
    }

    fun dateValidator(dateValidator: DateValidator): MonthlyCalendarBuilder<T> {
        this.dateValidator = dateValidator
        return this
    }

    fun build(): MonthlyCalendarSheet<T> = MonthlyCalendarSheet.newInstance(this)
}
