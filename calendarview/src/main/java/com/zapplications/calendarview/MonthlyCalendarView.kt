package com.zapplications.calendarview

import android.content.Context
import android.util.AttributeSet
import android.view.Gravity
import android.view.LayoutInflater
import android.widget.LinearLayout
import com.zapplications.calendarview.databinding.ViewMonthlyCalendarBinding
import com.zapplications.core.data.DayItem
import com.zapplications.core.generator.CalendarGenerator
import kotlinx.datetime.Clock
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.minus
import kotlinx.datetime.plus
import kotlinx.datetime.toLocalDateTime
import java.time.format.TextStyle
import java.util.Locale

class MonthlyCalendarView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {

    private val binding = ViewMonthlyCalendarBinding.inflate(LayoutInflater.from(context), this)

    private val calendarGenerator = CalendarGenerator()
    var currentDate: LocalDate? = null
        set(value) {
            setCurrentMonth(value)
            field = value
        }

    init {
        orientation = VERTICAL
        gravity = Gravity.CENTER_HORIZONTAL
    }

    init {
        initView()
    }

    private fun initView() {
        binding.viewCalendarHeader.onPreviousMonthClick {
            currentDate = currentDate?.minus(1, DateTimeUnit.MONTH)
            updateCalendar()
        }
        binding.viewCalendarHeader.onNextMonthClick {
            currentDate = currentDate?.plus(1, DateTimeUnit.MONTH)
            updateCalendar()
        }

    }

    fun setDaysOfWeek(daysOfWeek: List<DayOfWeek>) {
        binding.viewDaysOfWeekTitles.setDaysOfWeek(daysOfWeek)
    }

    fun setCurrentMonth(currentDate: LocalDate?) {
        binding.viewCalendarHeader.currentMonth = currentDate?.month?.getDisplayName(TextStyle.FULL, Locale.getDefault())
        binding.viewCalendarHeader.currentYear = currentDate?.year?.toString()
    }

    fun setCalendarList(dayItems: List<DayItem>) {
        binding.viewMonthGrid.setCalendarList(dayItems)
    }

    private fun updateCalendar() {
        val list = calendarGenerator.getMonthDays(
            currentDate = currentDate ?: Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date,
            firstDayOfWeek = DayOfWeek.MONDAY
        )
        setCalendarList(list)
    }
}
