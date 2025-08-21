package com.zapplications.core.selection

import com.zapplications.core.data.DayItem
import com.zapplications.core.selection.SelectionManager.Companion.NO_POSITION

class SingleSelectionManager : SelectionManager {
    private var selectedPosition: Int = NO_POSITION

    override fun onDaySelected(
        position: Int,
        currentList: List<DayItem>,
    ): List<DayItem> {
        val newList = currentList.toMutableList()
        if (selectedPosition != NO_POSITION) {
            val oldPosition = selectedPosition
            val oldItem = currentList.getOrNull(oldPosition)
            if (oldItem is DayItem.Day) {
                newList[oldPosition] = oldItem.copy(isSelected = false)
            }
        }

        val newSelectedItem = currentList.getOrNull(position)
        if (newSelectedItem is DayItem.Day && !newSelectedItem.isSelected) {
            newList[position] = newSelectedItem.copy(isSelected = true)
        }

        selectedPosition = position
        return newList
    }

    override fun setInitialPosition(position: Int) {
        selectedPosition = position
    }

    fun onDaySelectedByOldAndNewItem(oldItem: DayItem.Day?, newItem: DayItem.Day?, currentList: List<DayItem?>): List<DayItem?> {
        val newList= currentList.toMutableList()

        val oldPosition = getDayItemPosition(oldItem, currentList)
        val oldItemIsSelectedFalse = (newList[oldPosition] as? DayItem.Day)?.copy(isSelected = false)
        newList[oldPosition] = oldItemIsSelectedFalse ?: newList[oldPosition]

        val newPosition = getDayItemPosition(newItem, currentList)
        newList[newPosition] = newItem

        selectedPosition = newPosition

        return newList
    }

    private fun getDayItemPosition(item: DayItem.Day?, currentList: List<DayItem?>) =
        currentList.indexOfFirst { it is DayItem.Day && it.date == item?.date && it.dayOfMonth == item.dayOfMonth }
}
