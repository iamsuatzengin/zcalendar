package com.zapplications.calendarview.config

import androidx.annotation.ColorRes
import com.zapplications.calendarview.R

/**
 * Configuration class for customizing the appearance and behavior of the CalendarView.
 *
 * @property showQuickSelectionBar Whether the quick selection bar should be visible.
 * @property selectedTextColor The color resource ID for the text of a selected day.
 * @property unselectedTextColor The color resource ID for the text of an unselected day.
 * @property disabledTextColor The color resource ID for the text of a disabled day.
 * @property selectedBackgroundColor The color resource ID for the background of a selected day.
 * @property quickSelectionBarBackgroundColor The color resource ID for the background of the quick selection bar.
 * @property quickSelectionBarTextColor The color resource ID for the text of the quick selection bar.
 */
data class CalendarViewConfig(
    val showQuickSelectionBar: Boolean = false,

    @get:ColorRes
    val selectedTextColor: Int = R.color.color_day_selected,

    @get:ColorRes
    val unselectedTextColor: Int = R.color.color_day_grid_item,

    @get:ColorRes
    val disabledTextColor: Int = R.color.color_day_grid_item_disabled,

    @get:ColorRes
    val selectedBackgroundColor: Int = R.color.color_day_selected_bg,

    @get:ColorRes
    val quickSelectionBarBackgroundColor: Int = R.color.color_calendar_quick_selection_bar_bg,

    @get:ColorRes
    val quickSelectionBarTextColor: Int = R.color.color_day_grid_item,
)
