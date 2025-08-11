package com.goanderco.simplecharts.feature.table

sealed class TableDialogState {
    data object None: TableDialogState()
    data class AddOrEditColumn(
        val column: String? = null,
        val addToRight: Boolean = false,
        val editColumn: Boolean= false,
        val isIndex: Boolean = false
    ): TableDialogState()
    data class ColumnOptions(val column: String): TableDialogState()
    data class RowOptions(val rowIndex: Int): TableDialogState()
    data class ConfirmMakingRowColumnName(val rowIndex: Int): TableDialogState()
    data class ConfirmDeletingColumn(val column: String): TableDialogState()
    data class EditCell(val column:String, val rowIndex: Int, val value: String): TableDialogState()
}