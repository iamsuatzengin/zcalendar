package com.zapplications.calendarview.bottomsheet

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.doOnAttach
import androidx.lifecycle.lifecycleScope
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.zapplications.calendarview.MonthlyCalendarView
import com.zapplications.calendarview.R
import com.zapplications.calendarview.bottomsheet.builder.MonthlyCalendarBuilder
import com.zapplications.calendarview.databinding.BottomSheetMonthlyCalendarBinding
import com.zapplications.calendarview.viewmodel.CalendarViewModel
import com.zapplications.core.generator.CalendarGenerator
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDate

fun interface SelectedDateListener {
    fun onDaySelected(date: List<LocalDate>)
}

class MonthlyCalendarSheet<T> : BottomSheetDialogFragment(), MonthlyCalendarView.CalendarActionListener {

    private var _binding: BottomSheetMonthlyCalendarBinding? = null
    private val binding get() = _binding!!
    private var options: MonthlyCalendarBuilder<T>? = null
    private var calendarViewModel: CalendarViewModel? = null
    private var selectedDateListener: SelectedDateListener? = null

    val selectedDates: List<LocalDate> get() = calendarViewModel?.selectedDates.orEmpty()

    companion object {
        fun <T> newInstance(options: MonthlyCalendarBuilder<T>) = MonthlyCalendarSheet<T>().apply {
            this.options = options
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        _binding = BottomSheetMonthlyCalendarBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        collectStates()
        initCalendarView()

        lifecycleScope.launch {
            calendarViewModel?.selectedDatesStateFlow?.collect {
                selectedDateListener?.onDaySelected(it)
            }
        }
    }

    private fun initCalendarView() = options?.apply {
        val calendarGenerator = CalendarGenerator()

        calendarViewModel = CalendarViewModel(calendarGenerator, dateValidator).apply {
            setInitialState(
                firstDayOfWeek = firstDayOfWeek,
                currentDate = currentDate,
                minDate = minDate,
                maxDate = maxDate,
                eventDates = eventDates
            )
        }
        selectionManager?.selectionListener = calendarViewModel

        binding.viewMonthlyCalendar.apply {
            setCalendarActionListener(this@MonthlyCalendarSheet)
            setAdapter(selectionManager, calendarViewConfig)
        }

        calendarViewModel?.init { dayItem ->
            selectionManager?.setInitialDay(dayItem)
            calendarViewModel?.addDateToSelected(dayItem.date)
        }
    }

    private fun collectStates() {
        binding.viewMonthlyCalendar.doOnAttach {
            lifecycleScope.launch {
                calendarViewModel?.calendarState?.collect { state ->
                    binding.viewMonthlyCalendar.renderState(state)
                }
            }
        }
    }

    fun selectedDateListener(listener: SelectedDateListener) {
        selectedDateListener = listener
    }

    override fun onPreviousMonthClick() {
        calendarViewModel?.onPreviousMonthClick()
    }

    override fun onNextMonthClick() {
        calendarViewModel?.onNextMonthClick()
    }

    override fun getTheme(): Int = R.style.ZBottomSheetTheme
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        options?.selectionManager?.clearSelection()
    }
}
