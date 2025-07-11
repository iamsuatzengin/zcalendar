package com.zapplications.core.extension

/**
 * Checks if the integer represents a leap year.
 *
 * A year is a leap year if it is divisible by 4,
 * unless it is divisible by 100 but not by 400.
 *
 * @return `true` if the integer is a leap year, `false` otherwise.
 *
 * Example:
 * ```kotlin
 * println(2000.isLeapYear()) // Output: true
 * println(1900.isLeapYear()) // Output: false
 * println(2024.isLeapYear()) // Output: true
 * println(2023.isLeapYear()) // Output: false
 * ```
 */
fun Int.isLeapYear(): Boolean = this % 4 == 0 && (this % 100 != 0 || this % 400 == 0)
