package com.goanderco.simplecharts.feature.table

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.AlertDialog
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SmallFloatingActionButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.goanderco.R
import com.goanderco.simplecharts.core.viewmodel.DataViewModel
import com.goanderco.simplecharts.core.ui.dialog.AddOrEditColumnDialog
import com.goanderco.simplecharts.core.ui.dialog.ConfirmMakingRowColumnNameDialog
import com.goanderco.simplecharts.core.ui.dialog.EditCellValueDialog
import com.goanderco.simplecharts.core.ui.dialog.SimpleOptionsDialog
import java.util.Locale

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun TablePage(viewmodel: DataViewModel = viewModel()) {

    // Shared scroll states
    val verticalState = rememberLazyListState()
    val horizontalState = rememberScrollState()

    val dataset by viewmodel.dataset.collectAsState()


    // Memoize expensive calculations
    val columns = remember(dataset) { dataset.keys.toList() }
    val rowCount = remember(dataset) { dataset.values.firstOrNull()?.size ?: 0 }

    var tableDialogState by remember{
        mutableStateOf<TableDialogState>(TableDialogState.None)
    }

    when(val state = tableDialogState) {
        is TableDialogState.AddOrEditColumn -> AddOrEditColumnDialog(
            editColumn = if (state.editColumn) state.column!! else null,
            existingColNames = columns,
            confirm = { newColumnName ->
                if (state.editColumn)
                    viewmodel.editColumnName(state.column!!, newColumnName)
                else if (state.column==null)
                    viewmodel.addColumn(newColumnName)
                else
                    viewmodel.addColumn(state.column, newColumnName, addToRight = state.addToRight, isIndex=state.isIndex)
                tableDialogState = TableDialogState.None
            },
            dismiss = { tableDialogState = TableDialogState.None }
        )
        is TableDialogState.ColumnOptions ->  SimpleOptionsDialog(
            title = stringResource(R.string.column_options, state.column),
            optionResourceIDs = arrayOf(
                R.string.rename_column to R.drawable.baseline_edit_24,
                R.string.add_column_to_the_right to R.drawable.baseline_post_add_24,
                R.string.add_index_column_to_the_right to R.drawable.baseline_post_add_24,
                R.string.add_column_to_the_left to R.drawable.baseline_post_add_left_24,
                R.string.add_index_column_to_the_left to R.drawable.baseline_post_add_left_24,
                R.string.delete_column to R.drawable.baseline_delete_24
                ) ,
            onSelected = { optionIndex ->
                when (optionIndex) {
                    0 -> tableDialogState = TableDialogState.AddOrEditColumn(state.column, editColumn = true)
                    1 -> tableDialogState  = TableDialogState.AddOrEditColumn(state.column, addToRight = true)
                    2 -> tableDialogState  = TableDialogState.AddOrEditColumn(state.column, addToRight = true, isIndex = true)
                    3 -> tableDialogState = TableDialogState.AddOrEditColumn(state.column, addToRight = false)
                    4 -> tableDialogState = TableDialogState.AddOrEditColumn(state.column, addToRight = false, isIndex = true)
                    5 -> tableDialogState = TableDialogState.ConfirmDeletingColumn(state.column)
                }

            },
            dismiss = { tableDialogState = TableDialogState.None },
        )
        is TableDialogState.ConfirmDeletingColumn -> AlertDialog(
            title = { Text(stringResource(R.string.confirm_deleting_column)) },
            text = { Text(stringResource(R.string.confirm_deleting_column).lowercase()
                .replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.ROOT) else it.toString() } + ": ${state.column}") },
            confirmButton = { Button ({
                viewmodel.deleteColumn(state.column)
                tableDialogState = TableDialogState.None
            }) { Text(
                stringResource(R.string.confirm)
            ) } },
            dismissButton = { TextButton({ tableDialogState = TableDialogState.None }) {
                Text(stringResource(R.string.dismiss))
            } },
            onDismissRequest = { tableDialogState = TableDialogState.None }
        )
        is TableDialogState.RowOptions -> SimpleOptionsDialog(
            title = stringResource(R.string.row_options, state.rowIndex + 1),
            optionResourceIDs = arrayOf(
                R.string.make_this_row_column_names to R.drawable.baseline_title_24,
                R.string.add_a_row_on_top to R.drawable.baseline_post_add_24,
                R.string.add_a_row_below to R.drawable.baseline_post_add_down_24,
                R.string.delete_row to R.drawable.baseline_delete_24

            ),
            onSelected = {
                when(it) {
                    0 -> tableDialogState = TableDialogState.ConfirmMakingRowColumnName(state.rowIndex)
                    1 -> viewmodel.addRow(state.rowIndex)
                    2 -> viewmodel.addRow(state.rowIndex + 1)
                    3 -> viewmodel.deleteRow(state.rowIndex)
                }
            },
            dismiss = { tableDialogState = TableDialogState.None },
        )
        is TableDialogState.ConfirmMakingRowColumnName -> ConfirmMakingRowColumnNameDialog(
            confirm = viewmodel::makeRowColumn,
            dismiss = { tableDialogState = TableDialogState.None },
            index = state.rowIndex
        )
        is TableDialogState.EditCell -> EditCellValueDialog(
            title ="${state.column}:${state.rowIndex + 1}",
            cellValue = state.value,
            column = state.column,
            rowIndex = state.rowIndex,
            confirm = viewmodel::setCellValue ,
            dismiss = { tableDialogState= TableDialogState.None }
        )

        is TableDialogState.None -> { }

    }




    LazyColumn(
        state = verticalState,
        modifier = Modifier.fillMaxSize()
    ) {
        // 1) Header row
        stickyHeader(key = "header") {
            Header(
                openColumnOptions = { column-> tableDialogState = TableDialogState.ColumnOptions(column) },
                horizontalScrollState = horizontalState,
                columns = columns,
                addColumns = { tableDialogState = TableDialogState.AddOrEditColumn() },
            )
        }

        items(
            count = rowCount,
            key = { index -> "row_$index" }
        ) { index ->

            DataRow(
                rowIndex = index,
                onRowClicked = { tableDialogState = TableDialogState.RowOptions(index)  },
                horizontalState = horizontalState,
                columns = columns, // Pass columns instead of dataset
                dataset = dataset,
                showCellValueDialog = { column, row, value->
                    tableDialogState = TableDialogState.EditCell(column, row, value)
                                      },

            )
        }

        item(key = "add_row") {
            SmallFloatingActionButton(
                modifier = Modifier.padding(vertical = 4.dp),
                onClick = { viewmodel.addRow() }
            ) {
                Icon(Icons.Filled.Add, contentDescription = "Add Row")
            }
        }
    }
}

@Composable
fun Header(
    addColumns: () -> Unit,
    openColumnOptions: (String) -> Unit,
    horizontalScrollState: ScrollState,
    columns: List<String>,

) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp)
    ) {
        // Top-left corner placeholder
        Spacer(
            modifier = Modifier
                .width(48.dp)
                .height(48.dp)
                .background(MaterialTheme.colorScheme.primaryContainer)
                .border(1.dp, Color.Black)
        )

        // Column name headers, scroll in sync
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(horizontalScrollState)
        ) {
            columns.forEach { column ->
                HeaderCell(
                    text = column,
                    onClick = { openColumnOptions(column) }
                )
            }

            SmallFloatingActionButton(
                modifier = Modifier.padding(horizontal = 4.dp),
                onClick = addColumns
            ) {
                Icon(Icons.Filled.Add, contentDescription = "Add Column")
            }
        }
    }
}

@Composable
private fun HeaderCell(
    onClick: () -> Unit,
    text: String,
) {
    Text(
        text = text,
        modifier = Modifier
            .width(96.dp)
            .height(48.dp)
            .border(1.dp, Color.Black)
            .background(MaterialTheme.colorScheme.primaryContainer)
            .clickable { onClick() }
            .wrapContentSize(Alignment.Center)
            ,
        textAlign = TextAlign.Center,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis
    )
}

@Composable
private fun DataRow(
    rowIndex: Int,
    onRowClicked: () -> Unit,
    horizontalState: ScrollState,
    columns: List<String>, // Use columns list instead of dataset keys
    dataset: Map<String, List<Any?>>,
    showCellValueDialog: (String, Int, String) -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp)
    ) {
        // Sticky left column: row number
        RowNumberCell(
            onClick = { onRowClicked() },
            index = rowIndex ,
        )

        // Scrollable data cells
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(horizontalState)
        ) {
            columns.forEach { columnKey ->
                val cellValue = dataset[columnKey]?.getOrNull(rowIndex)

                DataCell(
                    value = cellValue,
                    onClick = { showCellValueDialog(columnKey, rowIndex, (cellValue?:"").toString()) }
                )
            }

            // Spacer for add column button alignment
            Spacer(Modifier.width(48.dp))
        }
    }
}

@Composable
private fun RowNumberCell(
    onClick:() -> Unit,
    index: Int,
) {
    Text(
        text = (index + 1).toString(),
        modifier = Modifier
            .size(48.dp)
            .border(1.dp, Color.Black)
            .background(MaterialTheme.colorScheme.primaryContainer)
            .clickable { onClick() }
            .wrapContentSize(Alignment.Center),
        textAlign = TextAlign.Center,
        maxLines = 1
    )
}

@Composable
private fun DataCell(
    value: Any?,
    onClick: () -> Unit,
) {

    Text(modifier=Modifier
        .height(48.dp)
        .width(96.dp)
        .border(1.dp, Color.Black)
        .padding(2.dp)
        .clickable { onClick() },
        text= (value?:"NULL").toString(),
        color = if (value == null)  MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            else MaterialTheme.colorScheme.onSurface,
        fontStyle = if (value == null) FontStyle.Italic else FontStyle.Normal,
        textAlign = if (value is Number) TextAlign.End else TextAlign.Start
    )

}