package com.zapplications.calendarview.customview

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import com.zapplications.calendarview.databinding.ViewMonthlyCalendarHeaderBinding

class MonthlyCalendarHeaderView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
) : LinearLayout(context, attrs, defStyleAttr) {

    private val binding =
        ViewMonthlyCalendarHeaderBinding.inflate(LayoutInflater.from(context), this)

    var currentMonth: String?
        get() = binding.tvCurrentMonth.text.toString()
        set(value) {
            binding.tvCurrentMonth.text = value
        }

    var currentYear: String?
        get() = binding.tvCurrentYear.text.toString()
        set(value) {
            binding.tvCurrentYear.text = value
        }

    init {
        orientation = VERTICAL
    }

    fun onPreviousMonthClick(action: () -> Unit) {
        binding.btnPreviousMonth.setOnClickListener { action() }
    }

    fun onNextMonthClick(action: () -> Unit) {
        binding.btnNextMonth.setOnClickListener { action() }
    }
}
