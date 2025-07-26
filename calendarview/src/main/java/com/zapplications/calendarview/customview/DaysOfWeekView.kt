package com.zapplications.calendarview.customview

import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.util.AttributeSet
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.TextView
import com.zapplications.calendarview.R
import kotlinx.datetime.DayOfWeek
import java.time.format.TextStyle
import java.util.Locale

class DaysOfWeekView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
) : LinearLayout(context, attrs, defStyleAttr) {

    init {
        orientation = HORIZONTAL
    }

    fun setDaysOfWeek(
        daysOfWeek: List<DayOfWeek>,
        weekDaysTitleColor: Int = Color.GRAY,
        weekDaysTypeFace: Typeface? = null
    ) {
        removeAllViews()
        val titlesLayoutParams =
            LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT).apply {
                weight = 1f
                marginStart = context.resources.getDimensionPixelSize(R.dimen.margin_small)
            }

        daysOfWeek.forEach { day ->
            val dayView = TextView(context).apply {
                text = day.getDisplayName(TextStyle.SHORT, Locale.getDefault())
                layoutParams = titlesLayoutParams
                textSize = context.resources.getDimension(R.dimen.text_size_weekdays_title)
                setTextColor(weekDaysTitleColor)
                typeface = weekDaysTypeFace
            }

            addView(dayView)
        }
    }
}
