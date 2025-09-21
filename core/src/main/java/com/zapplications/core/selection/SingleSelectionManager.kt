package com.zapplications.core.selection

import com.zapplications.core.data.DayItem
import com.zapplications.core.selection.SelectionManager.Companion.NO_POSITION

class SingleSelectionManager : SelectionManager<DayItem.Day> {
    private var selectedItem: DayItem.Day? = null
    override var selectionListener: SelectionListener? = null

    override fun onDaySelected(
        date: DayItem.Day,
        currentList: List<DayItem>,
    ): List<DayItem> {
        val newList = currentList.toMutableList()
        if (selectedItem != null) {
            val oldItem = selectedItem
            oldItem?.let {
                val position = currentList.indexOfFirst { (it as? DayItem.Day)?.date == oldItem.date }
                if (position != NO_POSITION) newList[position] = oldItem.copy(isSelected = false)
            }
        }

        if (!date.isSelected) {
            val position = currentList.indexOfFirst { it as? DayItem.Day == date }
            newList[position] = date.copy(isSelected = true)
        }

        selectedItem = date

        selectedItem?.let { selectionListener?.onSingleDayClick(it) }
        return newList
    }

    override fun getSelection(): DayItem.Day? = selectedItem

    override fun setInitialDay(day: DayItem.Day) {
        selectedItem = day
    }

    fun onDaySelectedByOldAndNewItem(oldItem: DayItem.Day?, newItem: DayItem.Day?, currentList: List<DayItem?>): List<DayItem?> {
        val newList= currentList.toMutableList()

        val oldPosition = getDayItemPosition(oldItem, currentList)
        val oldItemIsSelectedFalse = (newList[oldPosition] as? DayItem.Day)?.copy(isSelected = false)
        newList[oldPosition] = oldItemIsSelectedFalse ?: newList[oldPosition]

        val newPosition = getDayItemPosition(newItem, currentList)
        newList[newPosition] = newItem

        return newList
    }

    private fun getDayItemPosition(item: DayItem.Day?, currentList: List<DayItem?>) =
        currentList.indexOfFirst { it is DayItem.Day && it.date == item?.date && it.dayOfMonth == item.dayOfMonth }
}
