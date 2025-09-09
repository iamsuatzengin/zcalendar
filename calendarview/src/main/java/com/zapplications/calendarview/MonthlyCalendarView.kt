package com.zapplications.calendarview

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import androidx.core.view.isVisible
import com.zapplications.calendarview.adapter.monthgrid.MonthGridAdapter
import com.zapplications.calendarview.config.CalendarViewConfig
import com.zapplications.calendarview.databinding.ViewMonthlyCalendarBinding
import com.zapplications.calendarview.model.QuickSelectionButtonModel
import com.zapplications.core.data.DayItem
import com.zapplications.core.data.Event
import com.zapplications.core.extension.ifNull
import com.zapplications.core.extension.isSameMonthOrAfter
import com.zapplications.core.extension.isSameMonthOrBefore
import com.zapplications.core.generator.CalendarGenerator
import com.zapplications.core.selection.MultipleSelectionManager
import com.zapplications.core.selection.RangeSelectionManager
import com.zapplications.core.selection.SelectionListener
import com.zapplications.core.selection.SelectionManager
import com.zapplications.core.selection.SelectionType
import com.zapplications.core.selection.SingleSelectionManager
import com.zapplications.core.validator.DateValidator
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
) : LinearLayout(context, attrs, defStyleAttr), SelectionListener {

    private val binding = ViewMonthlyCalendarBinding.inflate(LayoutInflater.from(context), this)

    private val calendarGenerator = CalendarGenerator()

    val monthAdapter: MonthGridAdapter? get() = binding.viewMonthGrid.monthGridAdapter

    var calendarViewConfig: CalendarViewConfig = CalendarViewConfig()
        private set

    var firstDayOfWeek: DayOfWeek = DayOfWeek.MONDAY
        private set

    var eventDates: Map<LocalDate, List<Event>>? = null
        private set

    var minDate: LocalDate? = null
        private set

    var maxDate: LocalDate? = null
        private set

    var currentDate: LocalDate? = null
        private set(value) {
            field = value
            setCurrentMonth(value)
        }

    val selectedDates: MutableList<LocalDate> = mutableListOf()

    var quickSelectionButtons: List<QuickSelectionButtonModel>? = null
        private set

    private var dateValidator: DateValidator? = null
    private var selectionManager: SelectionManager<*> = SingleSelectionManager(this)

    private var onDateSelectedListener: OnDateSelectedListener? = null

    init {
        orientation = VERTICAL
        initView()
    }

    private fun initView() {
        binding.viewCalendarHeader.onPreviousMonthClick {
            val previousDate = currentDate?.minus(1, DateTimeUnit.MONTH)

            if (previousDate != null && minDate != null && previousDate.isSameMonthOrBefore(minDate)) {
                binding.viewCalendarHeader.setPreviousButtonIsEnabled(false)
            }

            currentDate = previousDate
            binding.viewCalendarHeader.setNextButtonIsEnabled(true)

            buildCalendar(isUserInteraction = true)
        }
        binding.viewCalendarHeader.onNextMonthClick {
            val nextDate = currentDate?.plus(1, DateTimeUnit.MONTH)

            if (nextDate != null && maxDate != null && nextDate.isSameMonthOrAfter(maxDate)) {
                binding.viewCalendarHeader.setNextButtonIsEnabled(false)
            }

            currentDate = nextDate
            binding.viewCalendarHeader.setPreviousButtonIsEnabled(true)

            buildCalendar(isUserInteraction = true)
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

    fun setMinDate(minDate: LocalDate): MonthlyCalendarView {
        this.minDate = minDate
        return this
    }

    fun setMaxDate(maxDate: LocalDate): MonthlyCalendarView {
        this.maxDate = maxDate
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

    fun setDateValidator(dateValidator: DateValidator): MonthlyCalendarView {
        this.dateValidator = dateValidator
        return this
    }

    fun setSelectionType(selectionType: SelectionType): MonthlyCalendarView {
        this.selectionManager = when (selectionType) {
            SelectionType.SINGLE -> SingleSelectionManager(this)
            SelectionType.RANGE -> RangeSelectionManager(this)
            SelectionType.MULTIPLE -> MultipleSelectionManager(this)
        }
        return this
    }

    fun buildCalendar(isUserInteraction: Boolean = false) {
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
            selectedDates = selectedDates,
            minDate = minDate,
            maxDate = maxDate,
            dateValidator = dateValidator,
            isInitial = !isUserInteraction
        )

        (dayItems.firstOrNull { it is DayItem.Day && it.isSelected } as? DayItem.Day)?.let {
            selectionManager.setInitialDay(it)
            selectedDates.add(it.date)
        }

        binding.viewMonthGrid.setCalendarList(dayItems)
    }

    private fun setAdapter() {
        monthAdapter.ifNull {
            binding.viewMonthGrid.setAdapterWithConfig(
                calendarViewConfig = calendarViewConfig,
                selectionManager = selectionManager
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
//        val oldSelectedDate = selectedDate
//        selectedDate = selectedDate?.copy(
//            date = date,
//            dayOfMonth = date.dayOfMonth,
//            isSelected = true
//        )
//
//        if (currentDate?.month != date.month) {
//            currentDate = selectedDate?.date
//            buildCalendar()
//        } else {
//            currentDate = selectedDate?.date
//            monthAdapter?.handleOnSelectItem(oldSelectedDate, selectedDate)
//        }
    }

    override fun onSingleDayClick(dayItem: DayItem.Day) {
        selectedDates.clear()
        selectedDates.add(dayItem.date)
        onDateSelectedListener?.onDateSelected(dayItem)
    }

    override fun onRangeDayClick(rangeItems: Pair<DayItem.Day?, DayItem.Day?>) {
        selectedDates.clear()
        if (rangeItems.first != null && rangeItems.second == null) {
            val date = rangeItems.first?.date
            if (date != null) selectedDates.add(date)
            return
        }

        val datesBetweenRangeItems = calendarGenerator.getDatesBetweenRangeItems(
            rangeItems.first?.date,
            rangeItems.second?.date
        )
        selectedDates.addAll(datesBetweenRangeItems)
    }

    override fun onMultipleDayClick(dayItems: Set<DayItem.Day>) {
        selectedDates.clear()
        selectedDates.addAll(dayItems.map { it.date })
    }

    fun setOnDateSelectedListener(onDateSelectedListener:  OnDateSelectedListener) {
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
