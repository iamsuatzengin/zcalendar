package com.zapplications.calendarview

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import com.zapplications.calendarview.adapter.monthgrid.MonthGridAdapter
import com.zapplications.calendarview.config.CalendarViewConfig
import com.zapplications.calendarview.databinding.ViewMonthlyCalendarBinding
import com.zapplications.calendarview.viewmodel.CalendarViewModel
import com.zapplications.core.data.DayItem
import com.zapplications.core.data.Event
import com.zapplications.core.extension.ifNull
import com.zapplications.core.extension.isSameMonthOrAfter
import com.zapplications.core.extension.isSameMonthOrBefore
import com.zapplications.core.generator.CalendarGenerator
import com.zapplications.core.selection.MultipleSelectionManager
import com.zapplications.core.selection.RangeSelectionManager
import com.zapplications.core.selection.SelectionManager
import com.zapplications.core.selection.SelectionType
import com.zapplications.core.selection.SingleSelectionManager
import com.zapplications.core.validator.DateValidator
import kotlinx.coroutines.flow.StateFlow
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
) : LinearLayout(context, attrs, defStyleAttr) {

    private val binding = ViewMonthlyCalendarBinding.inflate(LayoutInflater.from(context), this)

    private val calendarGenerator = CalendarGenerator()

    val monthAdapter: MonthGridAdapter? get() = binding.viewMonthGrid.monthGridAdapter

    var calendarViewConfig: CalendarViewConfig = CalendarViewConfig()
        private set

    private var dateValidator: DateValidator? = null

    private var calendarViewModel: CalendarViewModel = CalendarViewModel(calendarGenerator)

    private var selectionManager: SelectionManager<*> = SingleSelectionManager(calendarViewModel)

    init {
        orientation = VERTICAL
        initView()
    }

    val selectedDatesStateFlow: StateFlow<List<LocalDate>>
        get() = calendarViewModel.selectedDatesStateFlow

    val selectedDates: List<LocalDate> get() = calendarViewModel.selectedDates

    private fun initView() {
        binding.viewCalendarHeader.onPreviousMonthClick {
            val currentDate = calendarViewModel.calendarState.currentDate
            val previousDate = currentDate?.minus(1, DateTimeUnit.MONTH)
            val minDate = calendarViewModel.calendarState.minDate
            if (previousDate != null && minDate != null && previousDate.isSameMonthOrBefore(minDate)) {
                binding.viewCalendarHeader.setPreviousButtonIsEnabled(false)
            }

            calendarViewModel.updateState {
                it.copy(currentDate = previousDate).also { state -> setCurrentMonth(state.currentDate) }
            }
            binding.viewCalendarHeader.setNextButtonIsEnabled(true)

            buildCalendar(isUserInteraction = true)
        }
        binding.viewCalendarHeader.onNextMonthClick {
            val currentDate = calendarViewModel.calendarState.currentDate
            val nextDate = currentDate?.plus(1, DateTimeUnit.MONTH)
            val maxDate = calendarViewModel.calendarState.maxDate
            if (nextDate != null && maxDate != null && nextDate.isSameMonthOrAfter(maxDate)) {
                binding.viewCalendarHeader.setNextButtonIsEnabled(false)
            }
            calendarViewModel.updateState {
                it.copy(currentDate = nextDate).also { state -> setCurrentMonth(state.currentDate) }
            }

            binding.viewCalendarHeader.setPreviousButtonIsEnabled(true)

            buildCalendar(isUserInteraction = true)
        }
    }

    fun setFirstDayOfWeek(firstDayOfWeek: DayOfWeek): MonthlyCalendarView {
        calendarViewModel.updateState { it.copy(firstDayOfWeek = firstDayOfWeek) }
        return this
    }

    fun setCalendarViewConfig(calendarViewConfig: CalendarViewConfig): MonthlyCalendarView {
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
    fun setCurrentDate(currentDate: LocalDate): MonthlyCalendarView {
        calendarViewModel.updateState { it.copy(currentDate = currentDate) }
        setCurrentMonth(currentDate)
        return this
    }

    /**
     * For now, this method will not be used.
     */
    private fun setEventDates(eventDates: Map<LocalDate, List<Event>>): MonthlyCalendarView {
        calendarViewModel.updateState { it.copy(eventDates = eventDates) }
        return this
    }

    fun setMinDate(minDate: LocalDate): MonthlyCalendarView {
        calendarViewModel.updateState { it.copy(minDate = minDate) }
        return this
    }

    fun setMaxDate(maxDate: LocalDate): MonthlyCalendarView {
        calendarViewModel.updateState { it.copy(maxDate = maxDate) }
        return this
    }

    private fun setCurrentMonth(currentDate: LocalDate?) {
        binding.viewCalendarHeader.currentMonth = currentDate?.month
        binding.viewCalendarHeader.currentYear = currentDate?.year
    }

    fun setDateValidator(dateValidator: DateValidator): MonthlyCalendarView {
        this.dateValidator = dateValidator
        return this
    }

    fun setSelectionType(selectionType: SelectionType): MonthlyCalendarView {
        this.selectionManager = when (selectionType) {
            SelectionType.SINGLE -> SingleSelectionManager(calendarViewModel)
            SelectionType.RANGE -> RangeSelectionManager(calendarViewModel)
            SelectionType.MULTIPLE -> MultipleSelectionManager(calendarViewModel)
        }
        return this
    }

    fun buildCalendar(isUserInteraction: Boolean = false) {
        setAdapter()
        val daysOfWeeks =
            calendarGenerator.getDaysOfWeek(firstDayOfWeek = calendarViewModel.calendarState.firstDayOfWeek)
        binding.viewDaysOfWeekTitles.setDaysOfWeek(daysOfWeeks)

        val current = calendarViewModel.calendarState.currentDate ?: getLocalCurrentDate()

        calendarViewModel.updateState { it.copy(currentDate = current) }
        val dayItems = calendarGenerator.getMonthDays(
            currentDate = current,
            firstDayOfWeek = calendarViewModel.calendarState.firstDayOfWeek,
            eventDates = calendarViewModel.calendarState.eventDates,
            selectedDates = selectedDates,
            minDate = calendarViewModel.calendarState.minDate,
            maxDate = calendarViewModel.calendarState.maxDate,
            dateValidator = dateValidator,
            isInitial = !isUserInteraction
        )

        if (!isUserInteraction) {
            (dayItems.firstOrNull { it is DayItem.Day && it.isSelected } as? DayItem.Day)?.let {
                selectionManager.setInitialDay(it)
                calendarViewModel.addDateToSelected(it.date)
            }
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

    private fun getLocalCurrentDate(): LocalDate =
        Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date

}
