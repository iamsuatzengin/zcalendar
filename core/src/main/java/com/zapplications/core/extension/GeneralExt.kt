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

/**
 * Executes the given [action] only if both [firstItem] and [secondItem] are not `null`.
 */
fun <Type1, Type2, ReturnType> letBoth(
    firstItem: Type1?,
    secondItem: Type2?,
    action: (Type1, Type2) -> ReturnType
): ReturnType? {
    return if (firstItem != null && secondItem != null) {
        action(firstItem, secondItem)
    } else null
}
