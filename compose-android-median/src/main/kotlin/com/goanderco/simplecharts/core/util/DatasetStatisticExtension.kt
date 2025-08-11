package com.goanderco.simplecharts.core.util

import kotlin.math.sqrt

// Alternative implementation using distinct() instead of toSet()
fun List<Any?>.countUniqueNotNull(): Int {
    return this.filterNotNull().toSet().size
}

fun List<Any?>.frequent(): Any? {
    val nonNullElements = this.filterNotNull()
    if (nonNullElements.isEmpty()) return null

    val frequencyMap = nonNullElements.groupingBy { it }.eachCount()
    val maxFrequency = frequencyMap.values.maxOrNull() ?: return null

    return nonNullElements.first { frequencyMap[it] == maxFrequency }
}

fun List<Any?>.frequency(): Int? {
    val nonNullElements = this.filterNotNull()
    if (nonNullElements.isEmpty()) return null

    return nonNullElements.groupingBy { it }.eachCount().values.maxOrNull()
}

/**
 * Helper function to convert Any? to Double if it's a number, or return null
 */
private fun Any?.toNumericOrNull(): Double? {
    return when (this) {
        is Number -> this.toDouble()
        is String -> this.toDoubleOrNull()
        else -> null
    }
}

/**
 * Helper function to extract numeric values from List<Any?> and validate the list
 * Returns null if any non-null value is not numeric
 */
private fun List<Any?>.getNumericValues(): List<Double>? {
    val nonNullValues = this.filterNotNull()

    // Check if all non-null values are numeric
    for (value in nonNullValues) {
        if (value.toNumericOrNull() == null) {
            return null // Found non-numeric value
        }
    }

    // Convert all values to Double
    return nonNullValues.mapNotNull { it.toNumericOrNull() }
}

/**
 * Calculate mean (average) of numeric values in the list
 * Returns null if list contains non-numeric values or is empty
 */
fun List<Any?>.calculateMean(): Double? {
    val numericValues = getNumericValues() ?: return null
    if (numericValues.isEmpty()) return null

    return numericValues.average()
}

/**
 * Calculate standard deviation of numeric values in the list
 * Returns null if list contains non-numeric values or is empty
 */
fun List<Any?>.calculateStandardDeviation(): Double? {
    val numericValues = getNumericValues() ?: return null
    if (numericValues.isEmpty()) return null

    val mean = numericValues.average()
    val variance = numericValues.map { (it - mean) * (it - mean) }.average()
    return sqrt(variance)
}

/**
 * Calculate minimum value in the list
 * Returns null if list contains non-numeric values or is empty
 */
fun List<Any?>.calculateMin(): Double? {
    val numericValues = getNumericValues() ?: return null
    if (numericValues.isEmpty()) return null

    return numericValues.minOrNull()
}

/**
 * Calculate maximum value in the list
 * Returns null if list contains non-numeric values or is empty
 */
fun List<Any?>.calculateMax(): Double? {
    val numericValues = getNumericValues() ?: return null
    if (numericValues.isEmpty()) return null

    return numericValues.maxOrNull()
}

/**
 * Calculate percentile for a given percentage (0-100)
 * Returns null if list contains non-numeric values or is empty
 */
private fun List<Any?>.calculatePercentile(percentile: Double): Double? {
    val numericValues = getNumericValues() ?: return null
    if (numericValues.isEmpty()) return null

    val sortedValues = numericValues.sorted()
    val index = (percentile / 100.0) * (sortedValues.size - 1)

    return when (index) {
        0.0 -> sortedValues[0]
        (sortedValues.size - 1).toDouble() -> sortedValues[sortedValues.size - 1]
        else -> {
            val lowerIndex = index.toInt()
            val upperIndex = lowerIndex + 1
            val weight = index - lowerIndex

            sortedValues[lowerIndex] * (1 - weight) + sortedValues[upperIndex] * weight
        }
    }
}

/**
 * Calculate 25th percentile (first quartile)
 * Returns null if list contains non-numeric values or is empty
 */
fun List<Any?>.calculateQ1(): Double? = calculatePercentile(25.0)

/**
 * Calculate 50th percentile (median)
 * Returns null if list contains non-numeric values or is empty
 */
fun List<Any?>.calculateMedian(): Double? = calculatePercentile(50.0)

/**
 * Calculate 75th percentile (third quartile)
 * Returns null if list contains non-numeric values or is empty
 */
fun List<Any?>.calculateQ3(): Double? = calculatePercentile(75.0)


