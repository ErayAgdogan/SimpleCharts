package com.goanderco.simplecharts.core.util

import android.content.Context
import android.net.Uri

suspend fun readDataFileFromUri(context: Context, dataFileType: DataFileType, uri: Uri,
                                firstRowIsColumn: Boolean= false): Map<String, List<Any?>>? {
    return when(dataFileType) {
        DataFileType.CSV -> readCsvFromUri(context, uri)
        DataFileType.TSV -> readCsvFromUri(context, uri, '\t')
        DataFileType.EXCEL -> readExcelFile(context, uri)
    }?.run {
        if (firstRowIsColumn)
            this.makeRowColumnName(0, true)
        else this
    }
}