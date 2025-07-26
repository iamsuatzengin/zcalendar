package com.zapplications.calendarview.customview

import android.content.Context
import android.util.AttributeSet
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.zapplications.calendarview.adapter.monthgrid.MonthGridAdapter
import com.zapplications.core.data.DayItem

class MonthView @JvmOverloads constructor(
    private val context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
) : RecyclerView(context, attrs, defStyleAttr) {

    var monthGridAdapter: MonthGridAdapter? = null
        private set(value) {
            field = value
            value?.let { adapter = it }
        }

    init {
        initView()
    }

    private fun initView() {
        layoutManager = GridLayoutManager(context, SPAN_COUNT)
        itemAnimator = null
        isNestedScrollingEnabled = false
        overScrollMode = OVER_SCROLL_NEVER
        setHasFixedSize(true)

        monthGridAdapter = MonthGridAdapter()
    }

    fun setCalendarList(dayItems: List<DayItem>) {
        monthGridAdapter?.setSelectedPosition(NO_POSITION)
        monthGridAdapter?.submitList(null)
        monthGridAdapter?.submitList(dayItems)
        requestLayout()
    }

    companion object {
        const val SPAN_COUNT = 7
    }
}
