package com.zapplications.core.selection

import com.zapplications.core.data.DayItem
import com.zapplications.core.selection.SelectionManager.Companion.NO_POSITION

class RangeSelectionManager : SelectionManager<Pair<DayItem.Day?, DayItem.Day?>> {
    private var selectedStartPosition: Int = NO_POSITION
    private var selectedEndPosition: Int = NO_POSITION

    private var selectedDateRange: Pair<DayItem.Day?, DayItem.Day?>? = null

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
            selectedStartPosition = position
            selectedEndPosition = NO_POSITION
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
        val selectedStartDate = newList.getOrNull(selectedStartPosition) as? DayItem.Day
        val selectedEndDate = newList.getOrNull(selectedEndPosition) as? DayItem.Day
        selectedDateRange = selectedStartDate to selectedEndDate

        return newList
    }

    override fun getSelection(): Pair<DayItem.Day?, DayItem.Day?>? = selectedDateRange

    override fun setInitialPosition(position: Int) {
        selectedStartPosition = position
    }

    fun isStartDate(position: Int) = position == selectedStartPosition
    fun isEndDate(position: Int) = position == selectedEndPosition
}
