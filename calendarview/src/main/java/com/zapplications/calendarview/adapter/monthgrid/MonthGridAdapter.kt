package com.zapplications.calendarview.adapter.monthgrid

import android.os.Bundle
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import com.zapplications.calendarview.config.CalendarViewConfig
import com.zapplications.calendarview.customview.MonthView
import com.zapplications.core.data.DayItem
import com.zapplications.core.selection.SelectionManager
import com.zapplications.core.selection.SingleSelectionManager
import com.zapplications.core.validator.DateValidator

class MonthGridAdapter(
    private val calendarViewConfig: CalendarViewConfig,
    private val monthViewClickListener: MonthView.MonthViewClickListener,
    private val dateValidator: DateValidator?,
    private val selectionManager: SelectionManager = SingleSelectionManager()
) : ListAdapter<DayItem, MonthGridViewHolder>(MonthGridAdapterDiffUtil()) {

    companion object {
        const val IS_SELECTED_CHANGED = "is.selected.changed"
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = MonthGridViewHolder.from(
        parent = parent,
        viewConfig = calendarViewConfig
    )

    override fun onBindViewHolder(holder: MonthGridViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(
            dayItem = item,
            dateValidator = dateValidator,
            onDayClick = {
                val newList = selectionManager.onDaySelected(position, currentList)
                submitList(newList)
                (item as? DayItem.Day)?.let { monthViewClickListener.onSingleDayClick(it) }
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

    fun handleOnSelectItem(oldItem: DayItem.Day?, newItem: DayItem.Day?) {
        (selectionManager as? SingleSelectionManager)?.onDaySelectedByOldAndNewItem(
            oldItem = oldItem,
            newItem = newItem,
            currentList = currentList
        )
    }

    fun setSelectedPosition(position: Int) {
        selectionManager.setSelectedPosition(position)
    }
}
