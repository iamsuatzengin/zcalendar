package com.zapplications.core.selection

import com.zapplications.core.data.DayItem

/**
 * Manages the selection of items within a list of [DayItem]s.
 *
 * This interface defines the contract for handling item selection logic,
 * such as marking an item as selected, updating the list state, and
 * determining if an item is eligible for selection.
 */
interface SelectionManager<T> {
    var selectionListener: SelectionListener?

    fun onDaySelected(date: DayItem.Day, currentList: List<DayItem>): List<DayItem>
    fun setInitialDay(day: DayItem.Day)

    fun getSelection(): T?

    fun isDayItemSelectable(item: DayItem) = item is DayItem.Day
            && item.isEnabled
            && !item.isSelected

    fun clearSelection()

    companion object {
        const val NO_POSITION = -1
    }
}
