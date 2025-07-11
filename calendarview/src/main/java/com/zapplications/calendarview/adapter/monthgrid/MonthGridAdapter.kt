package com.zapplications.calendarview.adapter.monthgrid

import android.os.Bundle
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.zapplications.core.data.DayItem

class MonthGridAdapter: ListAdapter<DayItem, MonthGridViewHolder>(MonthGridAdapterDiffUtil()) {

    companion object {
        const val IS_SELECTED_CHANGED = "is.selected.changed"
    }

    private var selectedPosition = RecyclerView.NO_POSITION

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = MonthGridViewHolder.from(parent)

    override fun onBindViewHolder(holder: MonthGridViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item) {
            if (selectedPosition != position && item is DayItem.Day) {
                handleOnSelectItem(position)
            }
        }
    }

    override fun onBindViewHolder(
        holder: MonthGridViewHolder,
        position: Int,
        payloads: List<Any?>
    ) {
        if (payloads.isNotEmpty()) {
            val item = getItem(position)
            if (item is DayItem.Day) {
                payloads.forEach { payload ->
                    if (payload is Bundle && payload.containsKey(IS_SELECTED_CHANGED)) {
                        holder.handleIsSelectedChanged(item.isSelected, item.isEnabled)
                        return
                    }
                }
            }
        }

        super.onBindViewHolder(holder, position, payloads)
    }

    private fun handleOnSelectItem(position: Int) {
        val list = currentList.toMutableList()
        if (selectedPosition != RecyclerView.NO_POSITION) {
            val oldPosition = selectedPosition
            val oldItem = getItem(oldPosition)
            if (oldItem is DayItem.Day) {
                list[oldPosition] = oldItem.copy(isSelected = false)
            }
        }

        val newSelectedItem = getItem(position)
        if (newSelectedItem is DayItem.Day && !newSelectedItem.isSelected) {
            list[position] = newSelectedItem.copy(isSelected = true)
        }

        submitList(list)
        selectedPosition = position
    }
}
