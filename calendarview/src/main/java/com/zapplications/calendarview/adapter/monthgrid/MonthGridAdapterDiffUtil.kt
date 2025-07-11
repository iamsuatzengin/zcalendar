package com.zapplications.calendarview.adapter.monthgrid

import android.os.Bundle
import androidx.recyclerview.widget.DiffUtil
import com.zapplications.core.data.DayItem

class MonthGridAdapterDiffUtil: DiffUtil.ItemCallback<DayItem>() {
    override fun areItemsTheSame(oldItem: DayItem, newItem: DayItem): Boolean {
        if (oldItem::class != newItem::class) return false

        val old = oldItem as? DayItem.Day
        val new = newItem as? DayItem.Day
        if (new == null) return false

        return old != null && old.date.toString() == new.date.toString()
    }

    override fun areContentsTheSame(oldItem: DayItem, newItem: DayItem): Boolean = oldItem == newItem

    override fun getChangePayload(oldItem: DayItem, newItem: DayItem): Any? {
        if (oldItem is DayItem.Day && newItem is DayItem.Day) {
            if (oldItem.isSelected != newItem.isSelected &&
                oldItem.date == newItem.date &&
                oldItem.dayOfMonth == newItem.dayOfMonth &&
                oldItem.events == newItem.events &&
                oldItem.isEnabled == newItem.isEnabled
            ) {
                return Bundle().apply { putBoolean(MonthGridAdapter.IS_SELECTED_CHANGED, true) }
            }
        }
        return null
    }
}
