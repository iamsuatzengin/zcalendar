package com.zapplications.calendarview

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import com.zapplications.calendarview.adapter.monthgrid.MonthGridAdapter
import com.zapplications.calendarview.config.CalendarViewConfig
import com.zapplications.calendarview.databinding.ViewMonthlyCalendarBinding
import com.zapplications.calendarview.viewmodel.CalendarState
import com.zapplications.core.extension.ifNull
import com.zapplications.core.selection.SelectionManager

class MonthlyCalendarView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
) : LinearLayout(context, attrs, defStyleAttr) {

    private val binding = ViewMonthlyCalendarBinding.inflate(LayoutInflater.from(context), this)

    val monthAdapter: MonthGridAdapter? get() = binding.viewMonthGrid.monthGridAdapter

    private var actionListener: CalendarActionListener? = null

    init {
        orientation = VERTICAL
        initView()
    }

    private fun initView() {
        binding.viewCalendarHeader.onPreviousMonthClick {
            actionListener?.onPreviousMonthClick()
        }
        binding.viewCalendarHeader.onNextMonthClick {
            actionListener?.onNextMonthClick()
        }
    }

    fun setCalendarActionListener(actionListener: CalendarActionListener) {
        this.actionListener = actionListener
    }

    fun renderState(calendarState: CalendarState) = with(binding){
        viewCalendarHeader.apply {
            this@apply.currentMonth = calendarState.currentDate?.month
            this@apply.currentYear = calendarState.currentDate?.year
            setNextButtonIsEnabled(calendarState.nextButtonIsEnabled)
            setPreviousButtonIsEnabled(calendarState.previousButtonIsEnabled)
        }
        viewDaysOfWeekTitles.setDaysOfWeek(calendarState.daysOfWeek)
        viewMonthGrid.setCalendarList(calendarState.dayItems)
    }

    fun <T> setAdapter(selectionManager: SelectionManager<T>?,  calendarViewConfig: CalendarViewConfig) {
        monthAdapter.ifNull {
            selectionManager?.let {
                binding.viewMonthGrid.setAdapterWithConfig(
                    calendarViewConfig = calendarViewConfig,
                    selectionManager = selectionManager
                )
            }
        }
    }

    interface CalendarActionListener {
        fun onPreviousMonthClick()
        fun onNextMonthClick()
    }
}
