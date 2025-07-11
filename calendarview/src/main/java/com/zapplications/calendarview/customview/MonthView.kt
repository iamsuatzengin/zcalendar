package com.zapplications.calendarview.customview

import android.content.Context
import android.util.AttributeSet
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.zapplications.calendarview.adapter.monthgrid.MonthGridAdapter

class MonthView @JvmOverloads constructor(
    private val context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
): RecyclerView(context, attrs, defStyleAttr) {

    var monthGridAdapter: MonthGridAdapter? = null
        set(value) {
            field = value
            value?.let { adapter = it }
        }

    fun init() {
        layoutManager = GridLayoutManager(context, SPAN_COUNT)
        itemAnimator = null
        isNestedScrollingEnabled = false
        overScrollMode = OVER_SCROLL_NEVER
        setHasFixedSize(true)
    }

    companion object {
        const val SPAN_COUNT = 7
    }
}
