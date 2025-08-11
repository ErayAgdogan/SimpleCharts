package com.goanderco.simplecharts.core.viewmodel

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import com.goanderco.R

@Immutable
@Stable
data class FilterUIState(
    val columnName: String? = null,
    val filterType: DatasetFilterType = DatasetFilterType.EQUALS,
    val filterValue: Any? = null,
   // val filterValueSecond: Any? = null

)

enum class DatasetFilterType(val stringResID: Int) {
    EQUALS(R.string.equals),
    NOT_EQUALS(R.string.not_equals),
    CONTAINS(R.string.contains),
    REGEX(R.string.regex),
    GREATER_THAN(R.string.greater_than),
    LESS_THAN(R.string.less_than),
    GREATER_THAN_OR_EQUAL(R.string.greater_than_or_equal),
    LESS_THAN_OR_EQUAL(R.string.less_than_or_equal),

}

fun Map<String, List<Any?>>.filterDataset(filter: List<FilterUIState>): Map<String, List<Any?>> {

    // return dataset unchanged if there is not filter or
    // all filters column name is null
    if (filter.isEmpty() || filter.all { it.columnName == null }) return this

    val filterIndices = mutableSetOf<Int>()

    filter.forEach { (column, filterType, filterValue) ->
        if (column == null || filterValue == null) return@forEach

        val targetColumn = this.getOrDefault(column, null) ?: return@forEach
        // Pre-compile regex for efficiency if it's a regex filter
        val compiledRegex = if (filterType == DatasetFilterType.REGEX) {
            try { Regex(filterValue.toString()) }
            catch (e: Exception) { return@forEach }
        } else null

        // Get indices where the condition is met
        val calculatedFilter = targetColumn.mapIndexedNotNull { index, columnValue ->
            if (columnValue != null) {
                when (filterType) {
                    DatasetFilterType.EQUALS -> {
                        if (compareValues(columnValue, filterValue) == 0) index else null
                    }

                    DatasetFilterType.NOT_EQUALS -> {
                        if (compareValues(columnValue, filterValue) != 0) index else null
                    }

                    DatasetFilterType.CONTAINS -> {
                        if (containsString(columnValue, filterValue)) index else null
                    }
                    DatasetFilterType.REGEX -> {
                        if (compiledRegex != null && matchesRegexCompiled(columnValue, compiledRegex)) index else null
                    }
                    DatasetFilterType.GREATER_THAN -> {
                        if (compareNumerically(columnValue, filterValue) { a, b -> a > b }) index else null
                    }

                    DatasetFilterType.LESS_THAN -> {
                        if (compareNumerically(columnValue, filterValue) { a, b -> a < b }) index else null
                    }

                    DatasetFilterType.GREATER_THAN_OR_EQUAL -> {
                        if (compareNumerically(columnValue, filterValue) { a, b -> a >= b }) index else null
                    }

                    DatasetFilterType.LESS_THAN_OR_EQUAL -> {
                        if (compareNumerically(columnValue, filterValue) { a, b -> a <= b }) index else null
                    }

                    /*
                    DatasetFilterType.IN_RANGE -> {
                        if (filterValue2 != null && isInRange( columnValue,filterValue,filterValue2)) { index } else null
                    }
                    */
                }
            } else null

        }
        // get the intersect result for list of filters
        if (filterIndices.isEmpty())
            filterIndices += calculatedFilter
        else
            filterIndices.retainAll(calculatedFilter.toSet())

        if (filterIndices.isEmpty())
            return emptyMap()
    }
    val filterIndicesSorted = filterIndices.sorted()
    // Filter all columns based on matching indices
    return this.mapValues { (_, columnData) ->
        filterIndicesSorted.map { index -> columnData[index] }
    }
}

// Helper function to compare values (handles String, Long, Double)
private fun compareValues(value1: Any?, value2: Any?): Int {
    if (value1 == null && value2 == null) return 0
    if (value1 == null) return -1
    if (value2 == null) return 1

    return when {
        value1 is String && value2 is String -> value1.compareTo(value2)
        value1 is Number && value2 is Number -> value1.toDouble().compareTo(value2.toDouble())
        else -> value1.toString().compareTo(value2.toString())
    }
}

// Helper function for numerical comparisons
private fun compareNumerically(
    columnValue: Any?,
    filterValue: Any?,
    comparison: (Double, Double) -> Boolean
): Boolean {
    return try {
        val colVal = when (columnValue) {
            is Number -> columnValue.toDouble()
            is String -> columnValue.toDoubleOrNull() ?: return false
            else -> return false
        }

        val filterVal = when (filterValue) {
            is Number -> filterValue.toDouble()
            is String -> filterValue.toDoubleOrNull() ?: return false
            else -> return false
        }

        comparison(colVal, filterVal)
    } catch (e: Exception) {
        false
    }
}

// Helper function for range checking
private fun isInRange(value: Any?, rangeStart: Any?, rangeEnd: Any?): Boolean {
    if (rangeStart == null || rangeEnd == null) return false

    return try {
        val numValue = when (value) {
            is Number -> value.toDouble()
            is String -> value.toDoubleOrNull() ?: return false
            else -> return false
        }

        val startVal = when (rangeStart) {
            is Number -> rangeStart.toDouble()
            is String -> rangeStart.toDoubleOrNull() ?: return false
            else -> return false
        }

        val endVal = when (rangeEnd) {
            is Number -> rangeEnd.toDouble()
            is String -> rangeEnd.toDoubleOrNull() ?: return false
            else -> return false
        }

        numValue in startVal..endVal
    } catch (e: Exception) {
        false
    }
}

// Helper function for string contains check (case-insensitive)
private fun containsString(columnValue: Any?, filterValue: Any?): Boolean {
    return try {
        val colString = columnValue?.toString() ?: return false
        val filterString = filterValue?.toString() ?: return false
        colString.contains(filterString, ignoreCase = true)
    } catch (e: Exception) {
        false
    }
}

// Helper function for regex matching with pre-compiled regex
private fun matchesRegexCompiled(columnValue: Any?, compiledRegex: Regex): Boolean {
    return try {
        val valueString = columnValue?.toString() ?: return false
        compiledRegex.containsMatchIn(valueString)
    } catch (e: Exception) {
        false
    }
}

