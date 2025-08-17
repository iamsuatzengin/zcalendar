package com.zapplications.calendarview.adapter

import com.zapplications.core.data.DayItem

sealed interface MonthGridChangePayload {
    data class IsSelectedChanged(val newItem: DayItem.Day): MonthGridChangePayload
}
