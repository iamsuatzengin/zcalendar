package com.zapplications.calendarview.customview

import android.content.Context
import android.util.AttributeSet
import android.util.TypedValue
import android.widget.TextView
import androidx.annotation.ColorRes
import androidx.appcompat.content.res.AppCompatResources
import androidx.constraintlayout.helper.widget.Flow
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.view.setPadding
import com.zapplications.calendarview.R
import com.zapplications.calendarview.model.QuickSelectionButtonModel
import kotlinx.datetime.LocalDate

class QuickSelectionBarLayout @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0,
) : ConstraintLayout(context, attrs, defStyleAttr) {

    fun setQuickSelectionButtons(
        buttons: List<QuickSelectionButtonModel>,
        @ColorRes textColor: Int,
        @ColorRes backgroundColor: Int,
        action: (LocalDate) -> Unit
    ) {
        val flow = createFlow()
        val constraintSet = ConstraintSet().apply {
            clone(this@QuickSelectionBarLayout)
            clear(flow.id, ConstraintSet.TOP)
            clear(flow.id, ConstraintSet.END)
            clear(flow.id, ConstraintSet.START)
            connect(flow.id, ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START)
            connect(flow.id, ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP)
            connect(flow.id, ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.END)
        }

        constraintSet.applyTo(this)
        addView(flow)

        val referenceIds = IntArray(buttons.size)

        buttons.forEachIndexed { index, buttonModel ->
            val button = createQuickSelectionButton(
                buttonModel = buttonModel,
                textColor = textColor,
                backgroundColor = backgroundColor,
                action
            )

            referenceIds[index] = button.id

            addView(button)
        }

        flow.referencedIds = referenceIds
        post { requestLayout() }
    }

    private fun createQuickSelectionButton(
        buttonModel: QuickSelectionButtonModel,
        @ColorRes textColor: Int,
        @ColorRes backgroundColor: Int,
        action: (LocalDate) -> Unit
    ) = TextView(context).apply {
        id = generateViewId()
        text = buttonModel.title

        setOnClickListener {
            action(buttonModel.onClick.invoke())
        }

        setPadding(
            context.resources.getDimension(R.dimen.quick_selection_button_padding).toInt()
        )
        setTextSize(
            TypedValue.COMPLEX_UNIT_PX,
            context.resources.getDimension(R.dimen.text_size_quick_selection_button)
        )
        background =
            AppCompatResources.getDrawable(context, R.drawable.calendar_quick_selection_button_bg)

        backgroundTintList = AppCompatResources.getColorStateList(context, backgroundColor)

        setTextColor(
            AppCompatResources.getColorStateList(context, textColor)
        )

        layoutParams = LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT)
    }

    private fun createFlow() = Flow(context).apply {
        id = generateViewId()
        setWrapMode(Flow.WRAP_ALIGNED)
        setMaxElementsWrap(4)
        setHorizontalGap(
            context.resources.getDimension(R.dimen.flow_horizontal_gap).toInt()
        )
        setHorizontalAlign(Flow.HORIZONTAL_ALIGN_START)
        setHorizontalStyle(Flow.CHAIN_PACKED)
        setOrientation(Flow.HORIZONTAL)
        layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)
    }
}
