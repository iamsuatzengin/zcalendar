# ZCalendar Library 📅

[![Maven Central](https://img.shields.io/maven-central/v/io.github.iamsuatzengin/calendarview.svg?label=Maven%20Central)](https://search.maven.org/search?q=g:io.github.zapplications%20AND%20a:calendarview)

`ZCalendar` is a modern and highly customizable calendar and date picker library designed for your Android applications. Developed in Kotlin with modern Android development practices in mind.

<br>

## ✨ Features

- **Highly Customizable:** Control every detail, from colors and fonts to the appearance of days and selector icons.
- **Multiple Selection Modes:** Support for single, multiple, and range date selection.
- **Easy Integration:** Add it to your project with just a few lines of code and start using it right away.
- **Performance-Focused:** Optimized for smooth performance even with large date ranges, thanks to its `RecyclerView`-based structure.
- **BottomSheet Support:** Designed for use within a modal `BottomSheet`.

## 📥 Installation

1.  Ensure you have `mavenCentral()` in your root `settings.gradle.kts` file. (It's included by default in new projects.)

    ```kotlin
    // settings.gradle.kts
    dependencyResolutionManagement {
        repositories {
            google()
            mavenCentral()
        }
    }
    ```

2.  Add the following dependency to your module's `build.gradle.kts` file:

    ```kotlin
    // build.gradle.kts
    dependencies {
        // Please replace '[LATEST-VERSION]' with the latest version number
        implementation("io.github.zapplications:calendarview:[LATEST-VERSION]")
    }
    ```

## 🚀 Basic Usage

You can use with bottom sheet in single selection mode:

```kotlin
// MainActivity.kt
override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)

    val calendarSheet = MonthlyCalendarBuilder.single() // this can be range() or multiple().
        .setCalendarViewConfig(
            CalendarViewConfig(
                disabledTextColor = com.zapplications.calendarview.R.color.color_disabled_text_red,
                showQuickSelectionBar = false
            )
        )
        .maxDate(LocalDate(2025, 10, 10))
        .build()
    button.setOnClickListener {
        calendarSheet.show(supportFragmentManager, "MonthlyCalendarSheet")
    }
}
```

## 🎨 Customization

`ZCalendar` can be easily customized via [CalendarViewConfig](https://github.com/iamsuatzengin/zcalendar/blob/main/calendarview/src/main/java/com/zapplications/calendarview/config/CalendarViewConfig.kt).

```kotlin
/**
 * Configuration class for customizing the appearance and behavior of the CalendarView.
 *
 * @property showQuickSelectionBar Whether the quick selection bar should be visible.
 * @property selectedTextColor The color resource ID for the text of a selected day.
 * @property unselectedTextColor The color resource ID for the text of an unselected day.
 * @property disabledTextColor The color resource ID for the text of a disabled day.
 * @property selectedBackgroundColor The color resource ID for the background of a selected day.
 * @property quickSelectionBarBackgroundColor The color resource ID for the background of the quick selection bar.
 * @property quickSelectionBarTextColor The color resource ID for the text of the quick selection bar.
 */
data class CalendarViewConfig(
    val showQuickSelectionBar: Boolean = false,

    @get:ColorRes
    val selectedTextColor: Int = R.color.color_day_selected,

    @get:ColorRes
    val unselectedTextColor: Int = R.color.color_day_grid_item,

    @get:ColorRes
    val disabledTextColor: Int = R.color.color_day_grid_item_disabled,

    @get:ColorRes
    val selectedBackgroundColor: Int = R.color.color_day_selected_bg,

    @get:ColorRes
    val quickSelectionBarBackgroundColor: Int = R.color.color_calendar_quick_selection_bar_bg,

    @get:ColorRes
    val quickSelectionBarTextColor: Int = R.color.color_day_grid_item,
)

```


## 🤝 Contributing

Contributions are welcome! Please contribute to the project by opening an issue or submitting a pull request.

## 📄 License

```
MIT License

Copyright (c) 2025 [Your Name or Organization Name]

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
```
