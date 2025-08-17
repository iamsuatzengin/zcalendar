package com.zapplications.calendarview.adapter.monthgrid

import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.graphics.toColorInt
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.zapplications.calendarview.config.CalendarViewConfig
import com.zapplications.calendarview.databinding.LayoutMonthGridItemBinding
import com.zapplications.calendarview.extensions.getColor
import com.zapplications.core.validator.DateValidator
import com.zapplications.core.data.DayItem

class MonthGridViewHolder(
    private val binding: LayoutMonthGridItemBinding,
    private val calendarViewConfig: CalendarViewConfig
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(
        dayItem: DayItem,
        onDayClick: () -> Unit,
        dateValidator: DateValidator?,
    ) = with(binding) {
        if (dayItem is DayItem.Day) {
            val isValid = dateValidator?.isValid(dayItem.date) ?: true
            tvDayValue.text = dayItem.dayOfMonth.toString()
            root.isEnabled = isValid && dayItem.isEnabled
            eventsDotView.isVisible = !dayItem.events.isNullOrEmpty()
            dayItem.events?.firstOrNull()?.eventIndicatorColor?.let { color ->
                eventsDotView.backgroundTintList = ColorStateList.valueOf(color.toColorInt())
            }
            handleIsSelectedChanged(isSelected = dayItem.isSelected, isEnabled = isValid && dayItem.isEnabled)

            root.setOnClickListener { onDayClick.invoke() }

        } else {
            root.isEnabled = false
            root.backgroundTintList = null
            tvDayValue.text = null
        }
    }

    private fun textColor(isEnabled: Boolean) = if (isEnabled) {
        calendarViewConfig.unselectedTextColor
    } else {
        calendarViewConfig.disabledTextColor
    }

    fun handleIsSelectedChanged(
        isSelected: Boolean,
        isEnabled: Boolean,
    ) = with(binding) {
        if (isSelected && isEnabled) {
            tvDayValue.setTextColor(
                binding.getColor(calendarViewConfig.selectedTextColor)
            )
            root.backgroundTintList = root.context.getColorStateList(
                calendarViewConfig.selectedBackgroundColor
            )
        } else {
            tvDayValue.setTextColor(
                binding.getColor(textColor(isEnabled))
            )
            root.backgroundTintList = null
        }
    }

    companion object {
        fun from(parent: ViewGroup, viewConfig: CalendarViewConfig) = MonthGridViewHolder(
            binding = LayoutMonthGridItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            ),
            calendarViewConfig = viewConfig
        )
    }
}
