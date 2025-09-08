package com.zapplications.calendarview.customview

import android.content.Context
import android.util.AttributeSet
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.zapplications.calendarview.adapter.monthgrid.MonthGridAdapter
import com.zapplications.calendarview.config.CalendarViewConfig
import com.zapplications.core.data.DayItem
import com.zapplications.core.selection.SelectionManager

class MonthView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
) : RecyclerView(context, attrs, defStyleAttr) {

    var monthGridAdapter: MonthGridAdapter? = null
        private set(value) {
            field = value
            value?.let { adapter = it }
        }

    init {
        layoutManager = GridLayoutManager(context, SPAN_COUNT)
        itemAnimator = null
        isNestedScrollingEnabled = false
        overScrollMode = OVER_SCROLL_NEVER
        setHasFixedSize(true)
    }

    fun setAdapterWithConfig(
        calendarViewConfig: CalendarViewConfig,
        selectionManager: SelectionManager<*>
    ) {
        monthGridAdapter = MonthGridAdapter(
            calendarViewConfig = calendarViewConfig,
            selectionManager = selectionManager
        )
    }

    fun setCalendarList(dayItems: List<DayItem>) {
        monthGridAdapter?.submitList(dayItems) {
            updateHeight()
        }
    }

    private fun updateHeight() {
        val currentList = monthGridAdapter?.currentList
        val childHeight = layoutManager?.getChildAt(
            currentList?.indexOfFirst { it is DayItem.Day } ?: -1
        )?.height

        childHeight?.let { cHeight ->
            val lp = layoutParams
            val size = currentList?.size ?: 0
            lp.height = cHeight * (size / SPAN_COUNT)
            layoutParams = lp
        }
    }

    companion object {
        const val SPAN_COUNT = 7
    }
}
