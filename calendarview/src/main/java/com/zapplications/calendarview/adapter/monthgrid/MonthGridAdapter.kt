package com.zapplications.calendarview.adapter.monthgrid

import android.os.Bundle
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.zapplications.calendarview.config.CalendarViewConfig
import com.zapplications.calendarview.customview.MonthView
import com.zapplications.core.data.DayItem

class MonthGridAdapter(
    private val calendarViewConfig: CalendarViewConfig,
    private val monthViewClickListener: MonthView.MonthViewClickListener,
) : ListAdapter<DayItem, MonthGridViewHolder>(MonthGridAdapterDiffUtil()) {

    companion object {
        const val IS_SELECTED_CHANGED = "is.selected.changed"
    }

    private var selectedPosition = RecyclerView.NO_POSITION

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = MonthGridViewHolder.from(
        parent = parent,
        viewConfig = calendarViewConfig
    )

    override fun onBindViewHolder(holder: MonthGridViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(
            dayItem = item,
            onDayClick = {
                if (selectedPosition != position && item is DayItem.Day) {
                    handleOnSelectItem(position)
                    monthViewClickListener.onSingleDayClick(item)
                }
            }
        )
    }

    override fun onBindViewHolder(
        holder: MonthGridViewHolder,
        position: Int,
        payloads: List<Any?>,
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

    fun handleOnSelectItem(position: Int) {
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

    fun handleOnSelectItem(oldItem: DayItem.Day?, newItem: DayItem.Day?) {
        val list = currentList.toMutableList()

        val oldPosition = getDayItemPosition(oldItem)
        val oldItemIsSelectedFalse = (list[oldPosition] as? DayItem.Day)?.copy(isSelected = false)
        list[oldPosition] = oldItemIsSelectedFalse ?: list[oldPosition]

        val newPosition = getDayItemPosition(newItem)
        list[newPosition] = newItem

        submitList(list)
        selectedPosition = newPosition
    }

    fun setSelectedPosition(position: Int) {
        selectedPosition = position
    }

    fun getDayItemPosition(item: DayItem.Day?) =
        currentList.indexOfFirst { it is DayItem.Day && it.date == item?.date && it.dayOfMonth == item.dayOfMonth }
}
