package com.goanderco.simplecharts.core.ui.dialog

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.input.TextFieldValue
import com.goanderco.R
import kotlinx.coroutines.delay

@Composable
public fun EditCellValueDialog(
    title:String,
    cellValue: String,
    column: String,
    rowIndex: Int,
    confirm: (column:String, row: Int, value: String) -> Unit,
    dismiss: () -> Unit
) {

    var editedCellValue by remember {
        mutableStateOf(TextFieldValue(text = cellValue, selection = TextRange(cellValue.length)))
    }
    val focusRequester = remember { FocusRequester() }

    AlertDialog(
        title = { Text(title) },
        text = {
            TextField(
                modifier = Modifier.focusRequester(focusRequester),
                value = editedCellValue,
                onValueChange = {
                    editedCellValue = it
                },
                placeholder = {
                    Text(
                        text = stringResource(R.string.default_null_value),
                        modifier = Modifier.alpha(0.6f),
                        fontStyle = FontStyle.Italic
                    )
                },
                label = {
                    Text(stringResource(R.string.edit_cell_value))
                }
            )
        },
        confirmButton = {
            Button(onClick = {
                confirm(column, rowIndex, editedCellValue.text)
                dismiss()
            }) { Text(stringResource(R.string.confirm)) }
        },
        dismissButton = {  TextButton(onClick = dismiss) { Text(stringResource(R.string.dismiss)) } },
        onDismissRequest = dismiss
    )

    LaunchedEffect(focusRequester) {
        delay(100)
        focusRequester.requestFocus()
    }

}