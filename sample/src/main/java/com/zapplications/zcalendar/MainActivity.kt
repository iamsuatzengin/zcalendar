package com.zapplications.zcalendar

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.zapplications.calendarview.MonthlyCalendarView
import com.zapplications.calendarview.config.CalendarViewConfig
import com.zapplications.core.validator.WeekdayValidator
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

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
        val rv = findViewById<MonthlyCalendarView>(R.id.viewMonthlyCalendar)

        rv.setCalendarViewConfig(
            CalendarViewConfig(
                disabledTextColor = com.zapplications.calendarview.R.color.color_disabled_text_red,
                showQuickSelectionBar = false
            )
        ).setMaxDate(
            LocalDate(2025, 10, 10)
        ).setCurrentDate(
            Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date
        ).setDateValidator(
            WeekdayValidator()
        ).buildCalendar()

        rv.setOnDateSelectedListener {
            println("Selected date: $it")
        }
    }
}
