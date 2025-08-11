package com.goanderco.simplecharts.core.util

import android.content.Context
import android.net.Uri
import java.lang.Exception

enum class DataFileType(val fileTypeName: String) {

    CSV("CSV"), TSV("TSV"), EXCEL("Excel");

    companion object {
        public fun getDataFileType(context: Context, uri: Uri): DataFileType {
            return when(context.contentResolver.getType(uri)) {
                in CSV_MIME_TYPE -> CSV
                in TSV_MIME_TYPE -> TSV
                in EXCEL_MIME_TYPE -> EXCEL
                else -> {throw Exception("Data file not found")}
            }
        }

    }
}


public val CSV_MIME_TYPE = arrayOf("text/csv", "text/comma-separated-values", "application/csv")
public val TSV_MIME_TYPE= arrayOf("text/tsv", "text/tab-separated-values", "application/tsv")
public val EXCEL_MIME_TYPE = arrayOf(
"application/excel",
"application/x-excel",
"application/x-msexcel",
"application/vnd.ms-excel", // .xls
"application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")