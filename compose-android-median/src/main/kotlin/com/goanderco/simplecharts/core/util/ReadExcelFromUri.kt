package com.goanderco.simplecharts.core.util

import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import org.apache.poi.hssf.usermodel.HSSFWorkbook
import org.apache.poi.ss.usermodel.Cell
import org.apache.poi.ss.usermodel.CellType
import org.apache.poi.ss.usermodel.DateUtil
import org.apache.poi.ss.usermodel.Sheet
import org.apache.poi.ss.usermodel.Workbook
import org.apache.poi.xssf.usermodel.XSSFWorkbook


fun readExcelFile(context: Context, uri: Uri): Map<String, List<Any?>> {
    val inputStream = context.contentResolver.openInputStream(uri)
    val fileType = determineExcelFileType(context, uri)

    return try {
        val workbook: Workbook = when (fileType) {
            ExcelFileType.XLSX -> XSSFWorkbook(inputStream)
            ExcelFileType.XLS -> HSSFWorkbook(inputStream)
            ExcelFileType.UNKNOWN -> throw IllegalArgumentException("Unsupported or unknown file format")
        }

        val sheet: Sheet = workbook.getSheetAt(0)

        // Read headers (first row)
        val headerRow = sheet.getRow(0)


        // Organize data by columns directly
        val columns = List<List<Any?>>(
            headerRow.lastCellNum.toInt()
        ) { columnIndex ->
            List<Any?>(sheet.lastRowNum) { rowIndex->
                val row = sheet.getRow(rowIndex)
                val cell = row?.getCell(columnIndex)
                getCellValue(cell)
            }
        }


        val dataset = LinkedHashMap<String, List<Any?>>(columns.size)
        for ((index, column) in columns.withIndex())
            dataset["Column ${index+1}"] = column

        dataset
    } catch (e: Exception) {
        e.printStackTrace()
        emptyMap()
    }
}

private fun determineExcelFileType(context: Context, uri: Uri): ExcelFileType {
    // First try MIME type
    val mimeType = context.contentResolver.getType(uri)
    when (mimeType) {
        "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet" -> return ExcelFileType.XLSX
        "application/vnd.ms-excel" -> return ExcelFileType.XLS
    }

    // Fallback to extension
    val extension = getFileExtension(context, uri)?.lowercase()
    return when (extension) {
        "xlsx" -> ExcelFileType.XLSX
        "xls" -> ExcelFileType.XLS
        else -> ExcelFileType.UNKNOWN
    }
}

private fun getFileExtension(context: Context, uri: Uri): String? {
    return when (uri.scheme) {
        "content" -> {
            val cursor = context.contentResolver.query(uri, null, null, null, null)
            cursor?.use {
                if (it.moveToFirst()) {
                    val displayNameIndex = it.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                    if (displayNameIndex != -1) {
                        val displayName = it.getString(displayNameIndex)
                        displayName?.substringAfterLast('.', "")
                    } else null
                } else null
            }
        }
        "file" -> uri.path?.substringAfterLast('.', "")
        else -> null
    }
}

enum class ExcelFileType {
    XLSX, XLS, UNKNOWN
}

private fun getCellValue(cell: Cell?): Any? {

    return when (cell?.cellType) {
        CellType.STRING -> cell.stringCellValue
        CellType.NUMERIC -> {
            if (DateUtil.isCellDateFormatted(cell)) {
                cell.dateCellValue.toString()
            } else {
                cell.numericCellValue.toString().removeSuffix(".0")
            }
        }
        CellType.BOOLEAN -> cell.booleanCellValue.toString()
        CellType.FORMULA -> cell.cellFormula
        else -> ""
    }.smartCast()
}