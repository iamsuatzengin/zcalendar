package com.zapplications.calendarview.adapter.monthgrid

import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import com.zapplications.calendarview.adapter.MonthGridChangePayload
import com.zapplications.calendarview.config.CalendarViewConfig
import com.zapplications.calendarview.customview.MonthView
import com.zapplications.core.data.DayItem
import com.zapplications.core.selection.MultipleSelectionManager
import com.zapplications.core.selection.RangeSelectionManager
import com.zapplications.core.selection.SelectionManager
import com.zapplications.core.selection.SingleSelectionManager

class MonthGridAdapter(
    private val calendarViewConfig: CalendarViewConfig,
    private val monthViewClickListener: MonthView.MonthViewClickListener,
    private val selectionManager: SelectionManager<*>
) : ListAdapter<DayItem, MonthGridViewHolder>(MonthGridAdapterDiffUtil()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = MonthGridViewHolder.from(
        parent = parent,
        viewConfig = calendarViewConfig
    )

    override fun onBindViewHolder(holder: MonthGridViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(
            dayItem = item,
            onDayClick = { onDaySelected(position) },
            isStartDate = selectionManager is RangeSelectionManager && selectionManager.isStartDate(position),
            isEndDate = selectionManager is RangeSelectionManager && selectionManager.isEndDate(position),
            isSelectionRange = selectionManager is RangeSelectionManager
        )
    }

    override fun onBindViewHolder(
        holder: MonthGridViewHolder,
        position: Int,
        payloads: List<Any?>,
    ) {
        when(val latestPayloads = payloads.lastOrNull()) {
            is MonthGridChangePayload.IsSelectedChanged -> {
                holder.handleIsSelectedChanged(
                    isSelected = latestPayloads.newItem.isSelected,
                    isEnabled = latestPayloads.newItem.isEnabled,
                    isStartDate = selectionManager is RangeSelectionManager && selectionManager.isStartDate(position),
                    isEndDate = selectionManager is RangeSelectionManager && selectionManager.isEndDate(position),
                    isSelectionRange = selectionManager is RangeSelectionManager
                )
            }

            else -> super.onBindViewHolder(holder, position, payloads)
        }
    }

    private fun onDaySelected(position: Int) {
        val item = getItem(position)
        if (!selectionManager.isDayItemSelectable(item) && selectionManager !is MultipleSelectionManager) return

        val newList = selectionManager.onDaySelected(position, currentList)
        submitList(newList)
        (item as? DayItem.Day)?.let { monthViewClickListener.onSingleDayClick(it) }
    }

    /**
     * only for single selection and this called from [com.zapplications.calendarview.MonthlyCalendarView]
     *
     * Maybe I can move this to [com.zapplications.calendarview.MonthlyCalendarView]
     */
    fun handleOnSelectItem(oldItem: DayItem.Day?, newItem: DayItem.Day?) {
        (selectionManager as? SingleSelectionManager)?.onDaySelectedByOldAndNewItem(
            oldItem = oldItem,
            newItem = newItem,
            currentList = currentList
        )
    }
}
