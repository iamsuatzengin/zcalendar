package com.zapplications.calendarview.customview

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import com.zapplications.calendarview.databinding.ViewMonthlyCalendarHeaderBinding
import kotlinx.datetime.Month
import java.time.format.TextStyle
import java.util.Locale

class MonthlyCalendarHeaderView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
) : LinearLayout(context, attrs, defStyleAttr) {

    private val binding =
        ViewMonthlyCalendarHeaderBinding.inflate(LayoutInflater.from(context), this)

    var currentMonthDisplayName: String? = null

    var currentMonth: Month? = null
        set(value) {
            field = value
            currentMonthDisplayName = value?.getDisplayName(TextStyle.FULL, Locale.getDefault())
            setCurrentMonthYear()
        }

    var currentYear: Int? = null
        set(value) {
            field = value
            setCurrentMonthYear()
        }

    init {
        orientation = HORIZONTAL
    }

    private fun setCurrentMonthYear() {
        binding.tvCurrentMonth.text = "$currentMonthDisplayName $currentYear"
    }

    fun onPreviousMonthClick(action: () -> Unit) {
        binding.btnPreviousMonth.setOnClickListener { action() }
    }

    fun onNextMonthClick(action: () -> Unit) {
        binding.btnNextMonth.setOnClickListener { action() }
    }

    internal fun setPreviousButtonIsEnabled(isEnabled: Boolean) {
        binding.btnPreviousMonth.isEnabled = isEnabled
    }

    internal fun setNextButtonIsEnabled(isEnabled: Boolean) {
        binding.btnNextMonth.isEnabled = isEnabled
    }
}
