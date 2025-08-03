package com.zapplications.core.generator

import com.zapplications.core.data.DayItem
import com.zapplications.core.data.Event
import com.zapplications.core.extension.getLength
import com.zapplications.core.extension.isLeapYear
import kotlinx.datetime.LocalDate
import kotlinx.datetime.number
import java.time.DayOfWeek

/**
 * A utility class responsible for generating calendar-related data, such as
 * lists of days for a week or a month.
 */
class CalendarGenerator {
    companion object {
        private const val FIRST_DAY_OF_MONTH_NUMBER = 1
        private const val PLUS_ONE = 1
        private const val WEEK_DAYS_COUNT = 7
    }

    /**
     * Gets a list of all days of the week starting from the specified day.
     *
     * @param firstDayOfWeek The first day of the week to start the list from.
     * @return A list containing all 7 days of the week, starting with [firstDayOfWeek].
     */
    fun getDaysOfWeek(firstDayOfWeek: DayOfWeek): List<DayOfWeek> {
        val daysOfWeek = mutableListOf<DayOfWeek>()
        var currentDayOfWeek = firstDayOfWeek

        repeat(WEEK_DAYS_COUNT) {
            daysOfWeek.add(currentDayOfWeek)
            currentDayOfWeek = currentDayOfWeek.plus(PLUS_ONE.toLong())
        }

        return daysOfWeek
    }

    /**
     * Generates a [DayItem]s representing a single month's view in a calendar.
     *
     * This function creates a grid of days for the specified month and year,
     * taking into account the desired first day of the week. It populates the grid
     * with [DayItem.Day] for actual days and [DayItem.Empty] for placeholder slots
     * at the beginning and end of the month to align with the week structure.
     * It also allows for marking specific dates as disabled and associating events
     * with dates.
     *
     * @param currentDate The [LocalDate] representing any day within the month to be displayed.
     *                  The year and month from this model will be used.
     * @param firstDayOfWeek The [DayOfWeek] to be considered as the first day of the week
     *                       (e.g., [DayOfWeek.MONDAY], [DayOfWeek.SUNDAY]). This affects
     *                       the layout of the days in the grid.
     * @param disabledDates An optional set of [LocalDate] objects representing dates that
     *                      should be marked as disabled in the calendar.
     * @param eventDates An optional map where keys are [LocalDate] objects and values are
     *                   lists of [EventData] associated with that date.
     * @return A [DayItem] list containing a list of [DayItem.Day]s and [DayItem.Empty] that represent the days
     *         and empty slots for the specified month, arranged according to the
     *         [firstDayOfWeek].
     */
    fun getMonthDays(
        currentDate: LocalDate,
        firstDayOfWeek: DayOfWeek,
        selectedDate: LocalDate? = null,
        disabledDates: Set<LocalDate>? = null,
        eventDates: Map<LocalDate, List<Event>>? = null
    ): List<DayItem> {

        val month = currentDate.month
        val year = currentDate.year
        val dayGridItems = mutableListOf<DayItem>()

        val firstDayOfMonth = LocalDate(year, month, FIRST_DAY_OF_MONTH_NUMBER)
        val leadingEmptySlots = computeLeadingEmptySlots(firstDayOfWeek, firstDayOfMonth.dayOfWeek)

        repeat(leadingEmptySlots) { dayGridItems.add(DayItem.Empty) }

        val currentMonthLength = month.getLength(isLeapYear = year.isLeapYear())
        for (dayOfMonth in FIRST_DAY_OF_MONTH_NUMBER..currentMonthLength) {
            val currentDate = LocalDate(year, month.number, dayOfMonth)
            val isEnabled = !(disabledDates?.contains(currentDate) ?: false)
            val events = eventDates?.get(currentDate)
            dayGridItems.add(
                DayItem.Day(
                    date = currentDate,
                    dayOfMonth = dayOfMonth,
                    events = events,
                    isSelected = currentDate == selectedDate,
                    isEnabled = isEnabled
                )
            )
        }
        val trailingEmptySlots = computeTrailingEmptySlots(dayGridItems.size)
        repeat(trailingEmptySlots) { dayGridItems.add(DayItem.Empty) }

        return dayGridItems
    }

    fun computeLeadingEmptySlots(firstDow: DayOfWeek, firstDowOfMonth: DayOfWeek): Int {
        val daysOfWeek = getDaysOfWeek(firstDow)
        return daysOfWeek.indexOf(firstDowOfMonth)
    }

    fun computeTrailingEmptySlots(totalSlots: Int): Int = (7 - (totalSlots % 7)) % 7
}
