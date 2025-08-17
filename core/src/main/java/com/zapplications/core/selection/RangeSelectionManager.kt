package com.zapplications.core.selection

import com.zapplications.core.data.DayItem
import com.zapplications.core.selection.SelectionManager.Companion.NO_POSITION

class RangeSelectionManager : SelectionManager {
    private var selectedStartPosition: Int = NO_POSITION
    private var selectedEndPosition: Int = NO_POSITION

    override fun onDaySelected(
        position: Int,
        currentList: List<DayItem>,
    ): List<DayItem> {
        if (selectedStartPosition != NO_POSITION && selectedEndPosition != NO_POSITION) {
            selectedStartPosition = position
            selectedEndPosition = NO_POSITION
        }

        if (selectedStartPosition == NO_POSITION) {
            selectedStartPosition = position
        } else if (position > selectedStartPosition) {
            selectedEndPosition = position
        } else if (position < selectedStartPosition) {
            selectedEndPosition = selectedStartPosition
            selectedStartPosition = position
        }

        val newList = currentList.toMutableList().mapIndexed { index, item ->
            if (item is DayItem.Day) {
                if (selectedStartPosition != NO_POSITION && selectedEndPosition == NO_POSITION) {
                    item.copy(isSelected = index == selectedStartPosition)
                } else {
                    item.copy(
                        isSelected = index in selectedStartPosition..selectedEndPosition
                    )
                }

            } else item
        }

        return newList
    }

    override fun setSelectedPosition(position: Int) {
        selectedStartPosition = position
    }
}
