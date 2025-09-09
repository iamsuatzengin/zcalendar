package com.zapplications.core.generator

import com.zapplications.core.data.DayItem
import com.zapplications.core.data.Event
import com.zapplications.core.validator.MondayDisableMockValidator
import com.zapplications.core.validator.WeekdayValidator
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate
import org.junit.Assert
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class CalendarGeneratorTest {
    private lateinit var calendarGenerator: CalendarGenerator

    @Before
    fun setUp() {
        calendarGenerator = CalendarGenerator()
    }

    @Test
    fun testGetDaysOfWeek_FirstDayOfWeekMonday() {
        // Given
        val firstDayOfWeek = DayOfWeek.MONDAY

        // When
        val daysOfWeek = calendarGenerator.getDaysOfWeek(firstDayOfWeek)

        // Then
        assertEquals(7, daysOfWeek.size)
        assertEquals(DayOfWeek.MONDAY, daysOfWeek.first())
        assert(daysOfWeek[0] == DayOfWeek.MONDAY)
        assert(daysOfWeek[1] == DayOfWeek.TUESDAY)
        assert(daysOfWeek[6] == DayOfWeek.SUNDAY)
    }


    @Test
    fun testGetDaysOfWeek_FirstDayOfWeekSunday() {
        // Given
        val firstDayOfWeek = DayOfWeek.SUNDAY

        // When
        val daysOfWeek = calendarGenerator.getDaysOfWeek(firstDayOfWeek)

        // Then
        assertEquals(7, daysOfWeek.size)
        assertEquals(DayOfWeek.SUNDAY, daysOfWeek.first())
        assert(daysOfWeek[0] == DayOfWeek.SUNDAY)
        assert(daysOfWeek[1] == DayOfWeek.MONDAY)
        assert(daysOfWeek[6] == DayOfWeek.SATURDAY)
    }


    @Test
    fun `computeLeadingEmptySlots should return 2 empty slots when month starts on Wednesday and week starts on Monday`() {
        // Given
        val firstDayOfWeek = DayOfWeek.MONDAY
        val firstDowOfMonth = DayOfWeek.WEDNESDAY

        // When
        val leadingEmptySlots = calendarGenerator.computeLeadingEmptySlots(
            firstDow = firstDayOfWeek,
            firstDowOfMonth = firstDowOfMonth
        )

        // Then
        assertEquals(2, leadingEmptySlots)
    }

    @Test
    fun `computeLeadingEmptySlots should return zero empty slots when month starts on Monday and week starts on Monday`() {
        // Given
        val firstDayOfWeek = DayOfWeek.MONDAY
        val firstDowOfMonth = DayOfWeek.MONDAY

        // When
        val leadingEmptySlots = calendarGenerator.computeLeadingEmptySlots(
            firstDow = firstDayOfWeek,
            firstDowOfMonth = firstDowOfMonth
        )

        // Then
        assertEquals(0, leadingEmptySlots)
    }

    @Test
    fun `computeTrailingEmptySlots should return zero empty slots when totalSlots param is 7x`() {
        // Given
        val totalSlots = 35

        // When
        val trailingEmptySlots =
            calendarGenerator.computeTrailingEmptySlots(totalSlots = totalSlots)

        // Then
        assertEquals(0, trailingEmptySlots)
    }

    @Test
    fun `computeTrailingEmptySlots should return 3 empty slots when totalSlots param is 32`() {
        // Given
        val totalSlots = 32

        // When
        val trailingEmptySlots =
            calendarGenerator.computeTrailingEmptySlots(totalSlots = totalSlots)

        // Then
        assertEquals(3, trailingEmptySlots)
    }

    @Test
    fun `getMonthDays should return correct days for a given month and year`() {
        // Given
        val dateModel = LocalDate(2025, 6, 1)
        val firstDayOfWeek = DayOfWeek.MONDAY

        // When
        val days = calendarGenerator.getMonthDays(
            currentDate = dateModel,
            firstDayOfWeek = firstDayOfWeek,
            minDate = LocalDate(2025, 4, 30),
            isInitial = true
        )

        assertNotNull(days)
        assertEquals(42, days.size)

        val leadingEmptyCount = days.takeWhile { it is DayItem.Empty }
        assertEquals(6, leadingEmptyCount.size)

        val monthDaysCount = days.filterIsInstance<DayItem.Day>().count()
        assertEquals(30, monthDaysCount)

        val firstDay = days.filterIsInstance<DayItem.Day>().first()
        assertEquals(1, firstDay.dayOfMonth)
        assertEquals(dateModel.year, firstDay.date.year)
        assertEquals(dateModel.month, firstDay.date.month)

        println("list: $days")

        val lastDay = days.filterIsInstance<DayItem.Day>().last()
        assertEquals(30, lastDay.dayOfMonth)
        assertEquals(dateModel.year, lastDay.date.year)
        assertEquals(dateModel.month, lastDay.date.month)
    }

    @Test
    fun `getMonthDays should handle leap year correctly`() {
        // Given
        val dateModel = LocalDate(2024, 2, 10)
        val firstDayOfWeek = DayOfWeek.MONDAY

        // When
        val days = calendarGenerator.getMonthDays(dateModel, firstDayOfWeek)

        // Then
        assertNotNull(days)
        assertEquals(35, days.size)

        val monthDaysCount = days.filterIsInstance<DayItem.Day>().count()
        assertEquals(29, monthDaysCount)

        val lastDay = days.filterIsInstance<DayItem.Day>().last()
        assertEquals(29, lastDay.dayOfMonth)
    }

    @Test
    fun `getMonthDays should handle non-leap year correctly`() {
        // Given
        val dateModel = LocalDate(2025, 2, 10)
        val firstDayOfWeek = DayOfWeek.SUNDAY

        // When
        val days = calendarGenerator.getMonthDays(dateModel, firstDayOfWeek)

        // Then
        assertNotNull(days)
        assertEquals(35, days.size) // 6 (leading) + 28 (days) + 1 (trailing) = 35

        val monthDaysCount = days.filterIsInstance<DayItem.Day>().count()
        assertEquals(28, monthDaysCount)

        val lastDay = days.filterIsInstance<DayItem.Day>().last()
        assertEquals(28, lastDay.dayOfMonth)
    }

    @Test
    fun `getMonthDays should include events correctly`() {
        // Given
        val dateModel = LocalDate(2025, 11, 1) // Kasım 2025
        val firstDayOfWeek = DayOfWeek.SATURDAY
        val event1 = Event("Event A", "Description A", eventIndicatorColor = "color")
        val event2 = Event("Event B", "Description B", eventIndicatorColor = "color2")
        val eventDates = mapOf(
            LocalDate(2025, 11, 10) to listOf(event1),
            LocalDate(2025, 11, 25) to listOf(event1, event2)
        )

        // When
        val dayGridItems = calendarGenerator.getMonthDays(
            currentDate = dateModel,
            firstDayOfWeek = firstDayOfWeek,
            eventDates = eventDates,
            maxDate = LocalDate(2025, 11, 26)
        )
        val days = dayGridItems.filterIsInstance<DayItem.Day>()

        // Then
        val dayWithOneEvent = days.first { it.date == LocalDate(2025, 11, 10) }
        assertNotNull(dayWithOneEvent.events)
        assertEquals(1, dayWithOneEvent.events?.size)
        assertEquals(event1, dayWithOneEvent.events?.first())

        val dayWithTwoEvents = days.first { it.date == LocalDate(2025, 11, 25) }
        assertNotNull(dayWithTwoEvents.events)
        assertEquals(2, dayWithTwoEvents.events?.size)
        assertTrue(dayWithTwoEvents.events?.contains(event1) ?: false)
        assertTrue(dayWithTwoEvents.events?.contains(event2) ?: false)

        val dayWithNoEvent = days.first { it.date == LocalDate(2025, 11, 5) }
        Assert.assertNull(dayWithNoEvent.events)
    }

    @Test
    fun `getMonthDays should handle WeekdayValidator correctly`() {
        // Given
        // August 2025 - There are 10 weekends in August. [2, 3, 9, 10, 16, 17, 23, 24, 30, 31]
        val dateModel = LocalDate(2025, 8, 1)
        val firstDayOfWeek = DayOfWeek.MONDAY

        // When
        val dayGridItems = calendarGenerator.getMonthDays(
            currentDate = dateModel,
            firstDayOfWeek = firstDayOfWeek,
            dateValidator = WeekdayValidator()
        )

        val days = dayGridItems.filterIsInstance<DayItem.Day>()

        // Then
        assertEquals(31, days.size)

        val notValidDayItemsSize = days.filter { !it.isEnabled }.size
        assertEquals(10, notValidDayItemsSize)
    }

    @Test
    fun `getMonthDays should handle MondayDisableMockValidator correctly`() {
        // Given
        val dateModel = LocalDate(2025, 8, 1)
        val firstDayOfWeek = DayOfWeek.MONDAY

        // When
        val dayGridItems = calendarGenerator.getMonthDays(
            currentDate = dateModel,
            firstDayOfWeek = firstDayOfWeek,
            dateValidator = MondayDisableMockValidator()
        )

        val days = dayGridItems.filterIsInstance<DayItem.Day>()

        // Then
        val allMondayDaysIsDisable = days.filter {
            it.date.dayOfWeek == DayOfWeek.MONDAY
        }.all { !it.isEnabled }

        assertEquals(true, allMondayDaysIsDisable)
    }

    @Test
    fun `getDatesBetweenRangeItems should return empty list when startDate is null`() {
        // Given
        val endDate = LocalDate(2023, 1, 5)

        // When
        val result = calendarGenerator.getDatesBetweenRangeItems(null, endDate)

        // Then
        assertTrue(result.isEmpty())
    }

    @Test
    fun `getDatesBetweenRangeItems should return empty list when endDate is null`() {
        // Given
        val startDate = LocalDate(2023, 1, 1)

        // When
        val result = calendarGenerator.getDatesBetweenRangeItems(startDate, null)

        // Then
        assertTrue(result.isEmpty())
    }

    @Test
    fun `getDatesBetweenRangeItems should return empty list when both dates are null`() {
        // When
        val result = calendarGenerator.getDatesBetweenRangeItems(null, null)

        // Then
        assertTrue(result.isEmpty())
    }

    @Test
    fun `getDatesBetweenRangeItems should return empty list when endDate is before startDate`() {
        // Given
        val startDate = LocalDate(2023, 1, 5)
        val endDate = LocalDate(2023, 1, 1)

        // When
        val result = calendarGenerator.getDatesBetweenRangeItems(startDate, endDate)

        // Then
        assertTrue(result.isEmpty())
    }

    @Test
    fun `getDatesBetweenRangeItems should return single date when startDate equals endDate`() {
        // Given
        val date = LocalDate(2023, 1, 1)

        // When
        val result = calendarGenerator.getDatesBetweenRangeItems(date, date)

        // Then
        assertEquals(1, result.size)
        assertEquals(date, result.first())
    }

    @Test
    fun `getDatesBetweenRangeItems should return correct dates for a valid range within the same month`() {
        // Given
        val startDate = LocalDate(2023, 1, 1)
        val endDate = LocalDate(2023, 1, 5)
        val expectedDates = listOf(
            LocalDate(2023, 1, 1),
            LocalDate(2023, 1, 2),
            LocalDate(2023, 1, 3),
            LocalDate(2023, 1, 4),
            LocalDate(2023, 1, 5)
        )

        // When
        val result = calendarGenerator.getDatesBetweenRangeItems(startDate, endDate)

        // Then
        assertEquals(expectedDates.size, result.size)
        assertEquals(expectedDates, result)
    }

    @Test
    fun `getDatesBetweenRangeItems should return correct dates for a valid range spanning across months`() {
        // Given
        val startDate = LocalDate(2023, 1, 30)
        val endDate = LocalDate(2023, 2, 2)
        val expectedDates = listOf(
            LocalDate(2023, 1, 30),
            LocalDate(2023, 1, 31),
            LocalDate(2023, 2, 1),
            LocalDate(2023, 2, 2)
        )

        // When
        val result = calendarGenerator.getDatesBetweenRangeItems(startDate, endDate)

        // Then
        assertEquals(expectedDates.size, result.size)
        assertEquals(expectedDates, result)
    }

    @Test
    fun `getDatesBetweenRangeItems should return correct dates for a valid range spanning across years`() {
        // Given
        val startDate = LocalDate(2023, 12, 30)
        val endDate = LocalDate(2024, 1, 2)
        val expectedDates = listOf(
            LocalDate(2023, 12, 30),
            LocalDate(2023, 12, 31),
            LocalDate(2024, 1, 1),
            LocalDate(2024, 1, 2)
        )

        // When
        val result = calendarGenerator.getDatesBetweenRangeItems(startDate, endDate)

        // Then
        assertEquals(expectedDates.size, result.size)
        assertEquals(expectedDates, result)
    }

    @Test
    fun `getDatesBetweenRangeItems should handle leap year correctly when range includes February 29th`() {
        // Given
        val startDate = LocalDate(2024, 2, 27)
        val endDate = LocalDate(2024, 3, 2)
        val expectedDates = listOf(
            LocalDate(2024, 2, 27),
            LocalDate(2024, 2, 28),
            LocalDate(2024, 2, 29), // Leap day
            LocalDate(2024, 3, 1),
            LocalDate(2024, 3, 2)
        )

        // When
        val result = calendarGenerator.getDatesBetweenRangeItems(startDate, endDate)

        // Then
        assertEquals(expectedDates.size, result.size)
        assertEquals(expectedDates, result)
        assertTrue(result.contains(LocalDate(2024, 2, 29)))
    }

    @Test
    fun `getDatesBetweenRangeItems should handle non-leap year correctly when range includes February`() {
        // Given
        val startDate = LocalDate(2023, 2, 27)
        val endDate = LocalDate(2023, 3, 2)
        val expectedDates = listOf(
            LocalDate(2023, 2, 27),
            LocalDate(2023, 2, 28),
            // No February 29th
            LocalDate(2023, 3, 1),
            LocalDate(2023, 3, 2)
        )

        // When
        val result = calendarGenerator.getDatesBetweenRangeItems(startDate, endDate)

        // Then
        assertEquals(expectedDates.size, result.size)
        assertEquals(expectedDates, result)
    }

    @Test
    fun `getDatesBetweenRangeItems should return a large number of dates correctly`() {
        // Given
        val startDate = LocalDate(2023, 1, 1)
        val endDate = LocalDate(2023, 12, 31) // Entire year 2023 (not a leap year)
        val expectedSize = 365

        // When
        val result = calendarGenerator.getDatesBetweenRangeItems(startDate, endDate)

        // Then
        assertEquals(expectedSize, result.size)
        assertEquals(startDate, result.first())
        assertEquals(endDate, result.last())
    }
}