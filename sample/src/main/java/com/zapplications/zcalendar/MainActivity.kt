package com.zapplications.zcalendar

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.zapplications.calendarview.MonthlyCalendarView
import com.zapplications.core.generator.CalendarGenerator
import kotlinx.datetime.Clock
import kotlinx.datetime.DayOfWeek
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
        val calendarGenerator = CalendarGenerator()
        val list = calendarGenerator.getMonthDays(
            currentDate = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date,
            firstDayOfWeek = DayOfWeek.MONDAY
        )

        val rv = findViewById<MonthlyCalendarView>(R.id.viewMonthlyCalendar)
        rv.apply {
            setDaysOfWeek(calendarGenerator.getDaysOfWeek(firstDayOfWeek = DayOfWeek.MONDAY))
            this.currentDate = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date
            setCalendarList(list)
        }
    }
}