package com.zapplications.zcalendar

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.zapplications.calendarview.adapter.monthgrid.MonthGridAdapter
import com.zapplications.calendarview.customview.MonthView
import com.zapplications.core.generator.CalendarGenerator
import kotlinx.datetime.DayOfWeek
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

        val list = CalendarGenerator().getMonthDays(
            currentDate = LocalDate(2025, 7, 1),
            firstDayOfWeek = DayOfWeek.MONDAY
        )

        val rv = findViewById<MonthView>(R.id.monthRv)

        rv.apply {
            init()
            monthGridAdapter = MonthGridAdapter()
            monthGridAdapter?.submitList(list)
        }
    }
}