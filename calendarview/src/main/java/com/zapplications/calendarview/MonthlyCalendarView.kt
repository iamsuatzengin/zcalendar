package com.zapplications.calendarview

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import androidx.core.view.isVisible
import com.zapplications.calendarview.adapter.monthgrid.MonthGridAdapter
import com.zapplications.calendarview.config.CalendarViewConfig
import com.zapplications.calendarview.customview.MonthView
import com.zapplications.calendarview.databinding.ViewMonthlyCalendarBinding
import com.zapplications.calendarview.model.QuickSelectionButtonModel
import com.zapplications.core.data.DayItem
import com.zapplications.core.data.Event
import com.zapplications.core.extension.ifNull
import com.zapplications.core.generator.CalendarGenerator
import kotlinx.datetime.Clock
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.minus
import kotlinx.datetime.plus
import kotlinx.datetime.toLocalDateTime
import java.time.DayOfWeek

class MonthlyCalendarView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
) : LinearLayout(context, attrs, defStyleAttr), MonthView.MonthViewClickListener {

    private val binding = ViewMonthlyCalendarBinding.inflate(LayoutInflater.from(context), this)

    private val calendarGenerator = CalendarGenerator()

    val monthAdapter: MonthGridAdapter? get() = binding.viewMonthGrid.monthGridAdapter

    var calendarViewConfig: CalendarViewConfig = CalendarViewConfig()
        private set

    var firstDayOfWeek: DayOfWeek = DayOfWeek.MONDAY
        private set

    var eventDates: Map<LocalDate, List<Event>>? = null
        private set

    var disabledDates: Set<LocalDate>? = null
        private set

    var currentDate: LocalDate? = null
        private set(value) {
            field = value
            setCurrentMonth(value)
        }

    var selectedDate: DayItem.Day? = null
        private set

    var quickSelectionButtons: List<QuickSelectionButtonModel>? = null
        private set

    private var onDateSelectedListener: OnDateSelectedListener? = null

    init {
        orientation = VERTICAL
        initView()
    }

    private fun initView() {
        binding.viewCalendarHeader.onPreviousMonthClick {
            currentDate = currentDate?.minus(1, DateTimeUnit.MONTH)
            buildCalendar()
        }
        binding.viewCalendarHeader.onNextMonthClick {
            currentDate = currentDate?.plus(1, DateTimeUnit.MONTH)
            buildCalendar()
        }
    }

    fun setFirstDayOfWeek(firstDayOfWeek: DayOfWeek): MonthlyCalendarView {
        this.firstDayOfWeek = firstDayOfWeek
        return this
    }

    fun setCalendarViewConfig(calendarViewConfig: CalendarViewConfig): MonthlyCalendarView {
        this.calendarViewConfig = calendarViewConfig

        binding.clQuickSelectionBarLayout.isVisible = calendarViewConfig.showQuickSelectionBar

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
    fun setCurrentDate(currentDate: LocalDate): MonthlyCalendarView {
        this.currentDate = currentDate
        return this
    }

    /**
     * For now, this method will not be used.
     */
    private fun setEventDates(eventDates: Map<LocalDate, List<Event>>): MonthlyCalendarView {
        this.eventDates = eventDates
        return this
    }

    fun setDisabledDates(disabledDates: Set<LocalDate>): MonthlyCalendarView {
        this.disabledDates = disabledDates
        return this
    }

    private fun setCurrentMonth(currentDate: LocalDate?) {
        binding.viewCalendarHeader.currentMonth = currentDate?.month
        binding.viewCalendarHeader.currentYear = currentDate?.year
    }

    fun setQuickSelectionButtons(quickSelectionButtons: List<QuickSelectionButtonModel>): MonthlyCalendarView {
        check(quickSelectionButtons.size <= 3) {
            "Quick selection buttons must not be more than 3"
        }
        this.quickSelectionButtons = quickSelectionButtons
        return this
    }

    fun buildCalendar() {
        setAdapter()
        setQuickLinkButtons()
        val daysOfWeeks = calendarGenerator.getDaysOfWeek(firstDayOfWeek = firstDayOfWeek)
        binding.viewDaysOfWeekTitles.setDaysOfWeek(daysOfWeeks)

        val current = currentDate ?: getLocalCurrentDate()
        currentDate = current

        val dayItems = calendarGenerator.getMonthDays(
            currentDate = current,
            firstDayOfWeek = firstDayOfWeek,
            eventDates = eventDates,
            disabledDates = disabledDates,
            selectedDate = selectedDate?.date ?: current
        )

        selectedDate.ifNull {
            selectedDate =
                dayItems.firstOrNull { (it as? DayItem.Day)?.isSelected == true } as? DayItem.Day
        }

        val initialSelectedPosition =
            selectedDate?.let { dayItem -> dayItems.indexOf(dayItem as? DayItem) }
        binding.viewMonthGrid.setCalendarList(dayItems, initialSelectedPosition)
    }

    private fun setAdapter() {
        monthAdapter.ifNull {
            binding.viewMonthGrid.setAdapterWithConfig(
                calendarViewConfig = calendarViewConfig,
                monthViewClickListener = this@MonthlyCalendarView
            )
        }
    }

    private fun setQuickLinkButtons() {
        if (binding.clQuickSelectionBarLayout.childCount > 1 && !calendarViewConfig.showQuickSelectionBar) return

        binding.clQuickSelectionBarLayout.setQuickSelectionButtons(
            buttons = quickSelectionButtons ?: getDefaultQuickSelectionButtons(),
            textColor = R.color.color_day_grid_item,
            backgroundColor = R.color.color_calendar_quick_selection_bar_bg,
            action = {
                setSelectedDateAfterQuickSelection(it)
            }
        )
    }

    private fun setSelectedDateAfterQuickSelection(date: LocalDate) {
        val oldSelectedDate = selectedDate
        selectedDate = selectedDate?.copy(
            date = date,
            dayOfMonth = date.dayOfMonth,
            isSelected = true
        )

        if (currentDate?.month != date.month) {
            currentDate = selectedDate?.date
            buildCalendar()
        } else {
            currentDate = selectedDate?.date
            monthAdapter?.handleOnSelectItem(oldSelectedDate, selectedDate)
        }
    }

    override fun onSingleDayClick(dayItem: DayItem.Day) {
        selectedDate = dayItem
        onDateSelectedListener?.onDateSelected(dayItem)
    }

    fun setOnDateSelectedListener(onDateSelectedListener: OnDateSelectedListener) {
        this.onDateSelectedListener = onDateSelectedListener
    }

    private fun getLocalCurrentDate(): LocalDate =
        Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date

    private fun getDefaultQuickSelectionButtons(): List<QuickSelectionButtonModel> = listOf(
        QuickSelectionButtonModel(
            title = context.getString(R.string.button_text_quick_selection_bar_today),
            onClick = { getLocalCurrentDate() }
        ),
        QuickSelectionButtonModel(
            title = context.getString(R.string.button_text_quick_selection_bar_tomorrow),
            onClick = { getLocalCurrentDate().plus(1, DateTimeUnit.DAY) }
        ),
        QuickSelectionButtonModel(
            title = context.getString(R.string.button_text_quick_selection_bar_next_week),
            onClick = { getLocalCurrentDate().plus(1, DateTimeUnit.WEEK) }
        ),
    )

    fun interface OnDateSelectedListener {
        fun onDateSelected(date: DayItem.Day)
    }
}
