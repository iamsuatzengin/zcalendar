package com.zapplications.core.selection

import com.zapplications.core.data.DayItem

interface SelectionListener {
    fun onSingleDayClick(dayItem: DayItem.Day)
    fun onRangeDayClick(rangeItems: Pair<DayItem.Day?, DayItem.Day?>)
    fun onMultipleDayClick(dayItems: Set<DayItem.Day>)
}