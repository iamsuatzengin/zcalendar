package com.zapplications.core.selection

import com.zapplications.core.data.DayItem

class MultipleSelectionManager(override val selectionListener: SelectionListener) : SelectionManager<MutableSet<DayItem.Day>> {
    private val selectedDays = mutableSetOf<DayItem.Day>()

    override fun onDaySelected(date: DayItem.Day, currentList: List<DayItem>): List<DayItem> {
        val mutableCurrentList = currentList.toMutableList()
        val selectedDates = selectedDays.map { it.date }.toMutableList()

        val index = mutableCurrentList.indexOfFirst { it as? DayItem.Day == date }
        if (selectedDates.contains(date.date) && selectedDates.size > 1) {
            selectedDates.remove(date.date)
            selectedDays.removeIf { it == date }

            val updatedItem = date.copy(isSelected = false)
            mutableCurrentList[index] = updatedItem
            selectionListener.onMultipleDayClick(selectedDays)
            return mutableCurrentList
        }

        selectedDates.add(date.date)

        val updatedItem = date.copy(isSelected = true)
        mutableCurrentList[index] = updatedItem
        selectedDays.add(updatedItem)

        selectionListener.onMultipleDayClick(selectedDays)
        return mutableCurrentList
    }

    override fun setInitialDay(day: DayItem.Day) {
        selectedDays.add(day)
    }

    override fun getSelection(): MutableSet<DayItem.Day>? = selectedDays
}
