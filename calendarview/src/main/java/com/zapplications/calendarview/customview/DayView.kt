package com.zapplications.calendarview.customview

import android.content.Context
import android.content.res.ColorStateList
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.RelativeLayout
import androidx.annotation.ColorInt
import androidx.core.view.isVisible
import com.zapplications.calendarview.databinding.ViewDayBinding

class DayView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : RelativeLayout(context, attrs, defStyleAttr) {

    private val binding = ViewDayBinding.inflate(LayoutInflater.from(context), this)

    var dayValue: String?
        get() = binding.tvDayValue.text.toString()
        set(value) {
            binding.tvDayValue.text = value
        }

    var eventDotIsVisible: Boolean
        get() = binding.eventsDotView.isVisible
        set(value) {
            binding.eventsDotView.isVisible = value
        }

    var eventDotColorStateList: ColorStateList?
        get() = binding.eventsDotView.backgroundTintList
        set(value) {
            binding.eventsDotView.backgroundTintList = value
        }

    @get:ColorInt
    var textColor: Int
        get() = binding.tvDayValue.currentTextColor
        set(value) {
            binding.tvDayValue.setTextColor(value)
        }
}

