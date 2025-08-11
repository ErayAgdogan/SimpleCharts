package com.goanderco.simplecharts.core.util

import android.content.Context
import android.net.Uri
import com.github.doyaaaaaken.kotlincsv.dsl.csvReader
import java.util.LinkedHashMap


suspend fun readCsvFromUri(context: Context, uri: Uri, separator:Char?=null): Map<String, List<Any?>>? {
    context.contentResolver.openInputStream(uri)?.let { csvInputStream ->

        val csvData = csvReader{
            separator?.let { this.delimiter = it }
        }.readAll(csvInputStream)
        val csvDataWithSmartCast = csvData.map{ row -> row.map(::smartCastFromString) }
        // Get column count from the first row
        val columnCount = csvData.firstOrNull()?.size?:0

        // Transform rows to columns
        val columnsMap = LinkedHashMap<String, List<Any?>>(columnCount)


        for (columnIndex in 0 until columnCount) {
            val columnName = "Column ${columnIndex + 1}"

            // Extract all values for this column from all rows
            val columnData = csvDataWithSmartCast.map { row ->
                row.getOrNull(columnIndex) // Safe access in case rows have different lengths
            }

            columnsMap[columnName] = columnData
        }
        return columnsMap
    }
    return null
}