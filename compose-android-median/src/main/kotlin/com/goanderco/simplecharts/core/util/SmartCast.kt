package com.goanderco.simplecharts.core.util

// return null if it is empty
// cast to double if there is a point in number with optionally sign
// cast to int if it only contains number and sign
val doubleRegex = Regex("^[+-]?\\d*\\.\\d+$")
val intRegex = Regex("^[+-]?\\d+$")
fun smartCastFromString(value: String): Any? {
    if (value.isEmpty()) return null

    // Try long first (integers are more common)
    value.toLongOrNull()?.let { return it }

    // Try double
    value.toDoubleOrNull()?.let { return it }

    // Return as string
    return value
}

fun String.smartCast(): Any? {
    return smartCastFromString(this)
}