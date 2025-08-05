package com.zapplications.calendarview.model

import kotlinx.datetime.LocalDate

typealias QuickSelectionButtonClick = () -> LocalDate

data class QuickSelectionButtonModel(
    val title: String,
    val onClick: QuickSelectionButtonClick,
)
