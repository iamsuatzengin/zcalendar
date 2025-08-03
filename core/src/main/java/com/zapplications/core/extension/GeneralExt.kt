package com.zapplications.core.extension


/**
 * Executes the given [block] only if the receiver is `null`.
 *
 * @param block The block to execute if the receiver is `null`.
 * @return The result of the [block] if the receiver is `null`, otherwise `null`.
 *
 * Example:
 * ```
 * val nullableValue: String? = null
 * val result = nullableValue.ifNull { "Value is null" } // result will be "Value is null"
 *
 * val nonNullValue: String? = "Hello"
 * val anotherResult = nonNullValue.ifNull { "Value is null" } // anotherResult will be null
 * ```
 */
inline fun <T, R> T?.ifNull(block: () -> R): R? = if (this == null) block() else null
