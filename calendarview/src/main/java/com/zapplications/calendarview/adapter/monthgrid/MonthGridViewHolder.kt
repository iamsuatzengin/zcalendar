package com.zapplications.calendarview.adapter.monthgrid

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.zapplications.calendarview.R
import com.zapplications.calendarview.databinding.LayoutMonthGridItemBinding
import com.zapplications.calendarview.extensions.getColor
import com.zapplications.core.data.DayItem

class MonthGridViewHolder(
    private val binding: LayoutMonthGridItemBinding,
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(
        dayItem: DayItem,
        onDayClick: () -> Unit,
    ) = with(binding) {
        if (dayItem is DayItem.Day) {
            tvDayValue.text = dayItem.dayOfMonth.toString()
            root.isEnabled = dayItem.isEnabled
            eventsDotView.isVisible = !dayItem.events.isNullOrEmpty()
            eventsDotView.backgroundTintList =
                root.context.getColorStateList(R.color.color_day_selected)
            root.setOnClickListener {
                onDayClick.invoke()
            }

        } else {
            root.isEnabled = false
            root.backgroundTintList = null
            tvDayValue.text = null
        }
    }

    private fun textColor(isEnabled: Boolean) = if (isEnabled) {
        R.color.color_day_grid_item
    } else {
        R.color.color_day_grid_item_disabled
    }

    fun handleIsSelectedChanged(
        isSelected: Boolean,
        isEnabled: Boolean,
    ) = with(binding) {
        if (isSelected) {
            tvDayValue.setTextColor(
                binding.getColor(R.color.color_day_selected)
            )
            root.backgroundTintList = root.context.getColorStateList(
                R.color.color_day_selected_bg
            )
        } else {
            tvDayValue.setTextColor(
                binding.getColor(textColor(isEnabled))
            )
            root.backgroundTintList = null
        }
    }

    companion object {
        fun from(parent: ViewGroup) = MonthGridViewHolder(
            LayoutMonthGridItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }
}
