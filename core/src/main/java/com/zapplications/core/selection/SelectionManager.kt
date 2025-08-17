package com.zapplications.core.selection

import com.zapplications.core.data.DayItem

/**
 * Interface for managing the selection of items in a list of [DayItem].
 */
interface SelectionManager {
    fun onDaySelected(position: Int, currentList: List<DayItem>): List<DayItem>
    fun setSelectedPosition(position: Int)

    companion object {
        const val NO_POSITION = -1
    }
}