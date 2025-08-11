package com.goanderco.simplecharts.core.ui.dialog

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.goanderco.R
import com.goanderco.simplecharts.core.ui.view.TextFieldDroDown
import com.goanderco.simplecharts.core.viewmodel.DatasetFilterType
import com.goanderco.simplecharts.core.viewmodel.FilterUIState
import com.goanderco.simplecharts.core.ui.view.ReadOnlyTextField

@Composable
public fun DatasetFilterDialog(
    dismiss: () -> Unit,
    columns: Collection<String>,
    filter: FilterUIState,
    onConfirm: (FilterUIState) -> Unit
) {
    var showSelectColumnDialog by remember { mutableStateOf(false) }
    var filter by remember { mutableStateOf(filter) }

    if (showSelectColumnDialog)
        SelectColumnDialog(
            title = stringResource(R.string.select_column_for_filter),
            columns = columns,
            selectedColumn = filter.columnName,
            onColumnSelected = { if (it != null) filter = filter.copy(columnName = it) },
            dismiss = { showSelectColumnDialog = false },
            includeNullColumn = false
        )

    AlertDialog(
        title = { Text(stringResource(R.string.filter)) },
        text = {
            FilterItem(
                requestColumnSelection = { showSelectColumnDialog = true },
                onFilterTypeChange = { filter = filter.copy(filterType = it) },
                onFilterValueChange = { filter = filter.copy(filterValue = it) },
                columnName = filter.columnName,
                filterType = filter.filterType,
                filter = filter.filterValue?.toString()?:"",
            )
        },
        confirmButton = { Button({
            onConfirm(filter)
            dismiss()
        })  { Text(stringResource(R.string.confirm))}},
        dismissButton = { TextButton(dismiss) { Text(stringResource(R.string.dismiss))} },
        onDismissRequest = dismiss
    )
}


@Composable
public fun FilterItem(
    requestColumnSelection: ()-> Unit,
    onFilterTypeChange:(DatasetFilterType) -> Unit,
    onFilterValueChange: (String) -> Unit,
    columnName: String?,
    filterType: DatasetFilterType,
    filter: String,

) {

    Column (
        Modifier
            .fillMaxWidth()
            .heightIn(max = 900.dp)){

        ReadOnlyTextField(
            label = stringResource(R.string.column_name_to_filter),
            value = columnName,
            default = "NULL",
            onClick = requestColumnSelection
        )
        Spacer(Modifier.height(16.dp))
        TextFieldDroDown(
            modifier = Modifier,
            label = stringResource(R.string.operation),
            options = DatasetFilterType.entries.associateWith { stringResource(it.stringResID) },
            onSelected = onFilterTypeChange,
            selected = filterType
        )
        Spacer(Modifier.height(16.dp))
        OutlinedTextField(
            value =filter,
            onValueChange = onFilterValueChange
        )

    }
}