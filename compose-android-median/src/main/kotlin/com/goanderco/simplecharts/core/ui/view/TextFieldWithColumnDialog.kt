package com.goanderco.simplecharts.core.ui.view

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.goanderco.simplecharts.core.ui.dialog.SelectColumnDialog

@Composable
fun TextFieldWithColumnDialog(
    modifier: Modifier = Modifier,
    title: String,
    value: String?,
    default: String,
    onColumnSelected: (String?) -> Unit,
    columns: Collection<String>,
)  {
    var openColumnDialog by remember { mutableStateOf(false) }
    if (openColumnDialog)
        SelectColumnDialog(
            title = title,
            columns = columns,
            selectedColumn = value,
            onColumnSelected = onColumnSelected,
            dismiss = { openColumnDialog=false }
        )
    ReadOnlyTextField(modifier, { openColumnDialog=true }, title, value, default)
}