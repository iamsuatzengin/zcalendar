package com.zapplications.core.selection

import com.zapplications.core.data.DayItem

class MultipleSelectionManager : SelectionManager<MutableSet<DayItem.Day>> {
    private val selectedPositions = mutableSetOf<Int>()
    private val selectedDays = mutableSetOf<DayItem.Day>()

    override fun onDaySelected(position: Int, currentList: List<DayItem>): List<DayItem> {
        val mutableCurrentList = currentList.toMutableList()
        if (selectedPositions.contains(position) && selectedPositions.size > 1) {
            selectedPositions.remove(position)
            selectedDays.removeIf { it.date == (mutableCurrentList[position] as DayItem.Day).date }

            val updatedItem = (mutableCurrentList[position] as DayItem.Day).copy(isSelected = false)
            mutableCurrentList[position] = updatedItem
            return mutableCurrentList
        }

        selectedPositions.add(position)

        selectedPositions.forEach { pos ->
            val dayItem = mutableCurrentList.getOrNull(pos)
            if (dayItem is DayItem.Day) {
                val updatedItem = dayItem.copy(isSelected = true)
                mutableCurrentList[pos] = updatedItem
                selectedDays.add(updatedItem)
            }
        }

        return mutableCurrentList
    }

    override fun setInitialPosition(position: Int) {
        selectedPositions.ifEmpty { selectedPositions.add(position) }
    }

    override fun getSelection(): MutableSet<DayItem.Day>? = selectedDays
}
