package com.zapplications.core.selection

import com.zapplications.core.data.DayItem

class RangeSelectionManager : SelectionManager<Pair<DayItem.Day?, DayItem.Day?>> {
    private var selectedStartDay: DayItem.Day? = null
    private var selectedEndDay: DayItem.Day? = null

    private var selectedDateRange: Pair<DayItem.Day?, DayItem.Day?>? = null
    override var selectionListener: SelectionListener? = null

    override fun onDaySelected(
        date: DayItem.Day,
        currentList: List<DayItem>,
    ): List<DayItem> {
        if (selectedStartDay == null) {
            selectedStartDay = date
        } else if (selectedEndDay == null && date.date >= selectedStartDay!!.date) {
            selectedEndDay = date
        } else  {
            selectedStartDay = date
            selectedEndDay = null
        }

        val newList = currentList.toMutableList()

        newList.forEachIndexed { index, item ->
            if (item is DayItem.Day) {
                if (selectedStartDay != null && selectedEndDay == null) {
                    newList[index] = item.copy(isSelected = item.date == selectedStartDay?.date)
                } else {
                    newList[index] = item.copy(isSelected = (selectedStartDay?.date)?.rangeTo((selectedEndDay?.date!!))
                        ?.contains(item.date) == true)
                }
            } else {
                item
            }
        }

        selectedDateRange = selectedStartDay to selectedEndDay
        selectedDateRange?.let { selectionListener?.onRangeDayClick(it) }
        return newList
    }

    override fun getSelection(): Pair<DayItem.Day?, DayItem.Day?>? = selectedDateRange

    override fun setInitialDay(day: DayItem.Day) {
        selectedStartDay = day
    }

    override fun isDayItemSelectable(item: DayItem): Boolean {
        return item is DayItem.Day && item.isEnabled
    }

    fun isStartDate(dayItem: DayItem?) = (dayItem as? DayItem.Day)?.date == selectedStartDay?.date
    fun isEndDate(dayItem: DayItem?) = (dayItem as? DayItem.Day)?.date == selectedEndDay?.date

    override fun clearSelection() {
        selectedStartDay = null
        selectedEndDay = null
        selectedDateRange = null
    }
}
