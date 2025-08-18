package com.zapplications.calendarview.adapter.monthgrid

import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.graphics.toColorInt
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.zapplications.calendarview.R
import com.zapplications.calendarview.config.CalendarViewConfig
import com.zapplications.calendarview.databinding.LayoutMonthGridItemBinding
import com.zapplications.calendarview.extensions.getColor
import com.zapplications.core.data.DayItem
import kotlin.Boolean

class MonthGridViewHolder(
    private val binding: LayoutMonthGridItemBinding,
    private val calendarViewConfig: CalendarViewConfig
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(
        dayItem: DayItem,
        onDayClick: () -> Unit,
        isStartDate: Boolean = false,
        isEndDate: Boolean = false,
        isSelectionRange: Boolean = false
    ) = with(binding) {
        if (dayItem is DayItem.Day) {
            tvDayValue.text = dayItem.dayOfMonth.toString()
            root.isEnabled = dayItem.isEnabled
            eventsDotView.isVisible = !dayItem.events.isNullOrEmpty()
            dayItem.events?.firstOrNull()?.eventIndicatorColor?.let { color ->
                eventsDotView.backgroundTintList = ColorStateList.valueOf(color.toColorInt())
            }

            handleIsSelectedChanged(
                isSelected = dayItem.isSelected,
                isEnabled = dayItem.isEnabled,
                isStartDate = isStartDate,
                isEndDate = isEndDate,
                isSelectionRange = isSelectionRange
            )

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
        isStartDate: Boolean = false,
        isEndDate: Boolean = false,
        isSelectionRange: Boolean = false
    ) = with(binding) {
        if (isSelected && isEnabled) {
            val backgroundRes = when {
                isStartDate -> R.drawable.day_grid_item_start_bg
                isEndDate -> R.drawable.day_grid_item_end_bg
                isSelectionRange -> R.drawable.day_grid_item_middle_bg
                else -> R.drawable.day_grid_item_bg
            }
            root.background = AppCompatResources.getDrawable(root.context, backgroundRes)

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
            root.background = null
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
