package com.goanderco.simplecharts.core.util

fun Map<String, List<Any?>>.addRow(): Map<String, List<Any?>> {
    return this.mapValues { (_, column) -> column + null }
}

fun Map<String, List<Any?>>.addRow(index: Int): Map<String, List<Any?>> {
    return this.mapValues { (_, column) -> column.take(index) + null + column.drop(index) }
}

fun Map<String, List<Any?>>.deleteRow(index: Int): Map<String, List<Any?>> {
    return this.mapValues { (_, column) -> column.take(index) + column.drop(index + 1) }
}

fun Map<String, List<Any?>>.addColumn(colName: String): Map<String, List<Any?>> {
    val mutableDataset = this.toMutableMap()
    val rowCount = mutableDataset.values.firstOrNull()?.size ?: 0
    val newColumn =  List(rowCount) { null }
    mutableDataset[colName] = newColumn
    return mutableDataset
}


fun Map<String, List<Any?>>.addColumn(
    columnName: String,
    newColumnName: String,
    addToRight:Boolean=true,
    isIndex: Boolean=false
): Map<String, List<Any?>> {
    val rowCount = this.values.firstOrNull()?.size ?: 0
    val newColumn = List(rowCount) { if(isIndex) it + 1 else null }

    // Create a new LinkedHashMap to maintain order
    val result = LinkedHashMap<String, List<Any?>>(this.size)

    // Iterate through existing columns and insert new column after the reference column
    for ((key, value) in this) {
        if (!addToRight && key == columnName)
            result[newColumnName] = newColumn
        result[key] = value
        if (addToRight && key == columnName)
            result[newColumnName] = newColumn
    }

    return result
}

fun Map<String, List<Any?>>.deleteColumn(colName: String): Map<String, List<Any?>> {
    val mutableDataset = this.toMutableMap()
    mutableDataset.remove(colName)
    return mutableDataset
}


fun Map<String, List<Any?>>.editColumnName(oldColumnName: String, newColumnName: String): Map<String, List<Any?>> {
    var newColumnName = newColumnName
    var counter = 1
    while (newColumnName in this.keys) {
        newColumnName = "$newColumnName ($counter)"
        counter++
    }
    return this.mapKeys { if (it.key == oldColumnName) newColumnName else it.key }
}

fun Map<String, List<Any?>>.makeRowColumnName(rowIndex: Int, deleteSourceRow: Boolean = false): Map<String, List<Any?>> {
    // Get all column names
    val columnNames = this.keys.toList()

    // Find the maximum number of rows across all columns
    val maxRows = this.values.maxOfOrNull { it.size } ?: 0

    // Check if the specified row index is valid
    if (rowIndex >= maxRows || rowIndex < 0) {
        throw IndexOutOfBoundsException("Row index $rowIndex is out of bounds. Available rows: 0 to ${maxRows - 1}")
    }

    // Extract the specified row to use as new column names
    val newColumnNames = columnNames.map { columnName ->
        val value = this[columnName]?.getOrNull(rowIndex)
        value?.toString() ?: "Column ${columnNames.indexOf(columnName) + 1}"
    }

    // Make sure column names are unique
    val uniqueColumnNames = mutableListOf<String>()
    val usedNames = mutableSetOf<String>()

    newColumnNames.forEach { name ->
        var uniqueName = name
        var counter = 1
        while (usedNames.contains(uniqueName)) {
            uniqueName = "${name} ($counter)"
            counter++
        }
        usedNames.add(uniqueName)
        uniqueColumnNames.add(uniqueName)
    }

    // Create the new dataset with the row as column names
    val newDataset = mutableMapOf<String, List<Any?>>()

    // Determine which rows to include (exclude the source row if requested)
    val rowIndices = if (deleteSourceRow) {
        (0 until maxRows).filter { it != rowIndex }
    } else {
        (0 until maxRows).toList()
    }

    // Build the new dataset
    uniqueColumnNames.forEachIndexed { colIndex, newColumnName ->
        val originalColumnName = columnNames[colIndex]
        val columnData = rowIndices.map { idx ->
            this[originalColumnName]?.getOrNull(idx)
        }
        newDataset[newColumnName] = columnData
    }

    return newDataset
}

public fun Map<String, List<Any?>>.atLeastTwoNumericalColumnWithThreeNumericalValue():Boolean  {
    val numericalRowIndexes = mutableSetOf<Int>()
    var columnsWithSharedNumericalRows = 0

    forEach { (_, column) ->
        if (numericalRowIndexes.isEmpty()) {
            // First column: collect all numerical row indices
            column.forEachIndexed { index, value ->
                if (value is Number) {
                    numericalRowIndexes.add(index)
                }
            }
        } else {
            // Subsequent columns: keep only indices that are also numerical in this column
            numericalRowIndexes.retainAll { index ->
                index < column.size && column[index] is Number
            }
        }

        // If we still have at least 3 shared numerical rows, count this column
        if (numericalRowIndexes.size >= 3) {
            columnsWithSharedNumericalRows++
        }
    }

    return columnsWithSharedNumericalRows >= 2
}