package com.zapplications.core.generator

import com.zapplications.core.data.DayItem
import com.zapplications.core.data.Event
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
            minDate = LocalDate(2025, 4, 30)
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
}