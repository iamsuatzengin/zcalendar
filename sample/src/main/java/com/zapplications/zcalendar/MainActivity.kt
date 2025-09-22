package com.zapplications.zcalendar

import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.button.MaterialButton
import com.zapplications.calendarview.bottomsheet.builder.MonthlyCalendarBuilder
import com.zapplications.calendarview.config.CalendarViewConfig
import kotlinx.datetime.LocalDate

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        initMonthlyCalendarView()
    }

    private fun initMonthlyCalendarView() {
        val calendarSheet = MonthlyCalendarBuilder.single()
            .setCalendarViewConfig(
                CalendarViewConfig(
                    disabledTextColor = com.zapplications.calendarview.R.color.color_disabled_text_red,
                    showQuickSelectionBar = false
                )
            )
            .maxDate(LocalDate(2025, 10, 10))
            .build()

        calendarSheet.selectedDateListener {
            Log.e("MainActivity", "Selected date: $it")
        }

        findViewById<MaterialButton>(R.id.btnGetSelectedDates).setOnClickListener {
            calendarSheet.show(supportFragmentManager, "MonthlyCalendarSheet")
        }
    }
}
