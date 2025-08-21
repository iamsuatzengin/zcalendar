package com.zapplications.core.selection

import com.zapplications.core.data.DayItem

class MultipleSelectionManager : SelectionManager {
    private val selectedPositions = mutableSetOf<Int>()

    override fun onDaySelected(position: Int, currentList: List<DayItem>): List<DayItem> {
        val mutableCurrentList = currentList.toMutableList()
        if (selectedPositions.contains(position) && selectedPositions.size > 1) {
            selectedPositions.remove(position)
            mutableCurrentList[position] = (mutableCurrentList[position] as DayItem.Day).copy(isSelected = false)
            return mutableCurrentList
        }

        selectedPositions.add(position)

        selectedPositions.forEach { pos ->
            val dayItem = mutableCurrentList.getOrNull(pos)
            if (dayItem is DayItem.Day) {
                mutableCurrentList[pos] = dayItem.copy(isSelected = true)
            }
        }

        return mutableCurrentList
    }

    override fun setInitialPosition(position: Int) {
        selectedPositions.ifEmpty { selectedPositions.add(position) }
    }
}
