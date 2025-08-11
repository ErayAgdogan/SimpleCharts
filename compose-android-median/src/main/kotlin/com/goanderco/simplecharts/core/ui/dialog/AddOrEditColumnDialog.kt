package com.goanderco.simplecharts.core.ui.dialog

import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Text
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import com.goanderco.R
import kotlinx.coroutines.delay

@Composable
public fun AddOrEditColumnDialog(editColumn: String? = null, existingColNames: List<String>, confirm:(String) -> Unit, dismiss: () -> Unit) {

    val focusRequester = remember { FocusRequester() }

    var columnName by remember {
        mutableStateOf(TextFieldValue(text = editColumn?:"", selection = TextRange(editColumn?.length?:0)))
    }

    var isColNameExist by remember { mutableStateOf(false) }
    var isConfirmClicked by remember { mutableStateOf(false) }

    LaunchedEffect(columnName) {
        isColNameExist = existingColNames.contains(columnName.text)
    }
    AlertDialog(
        title = { Text(stringResource(editColumn?.let { R.string.edit_column }?:R.string.add_column)) },
        text = {
            OutlinedTextField(
                modifier = Modifier.focusRequester(focusRequester),
                value = columnName,
                onValueChange = { columnName = it},
                label = { Text(stringResource(R.string.enter_column_name)) },
                supportingText = {
                    if (isColNameExist)
                        Text(stringResource(R.string.this_column_is_already_exist),
                            color = MaterialTheme.colorScheme.error)
                    else if (isConfirmClicked && columnName.text.isBlank())
                        Text(stringResource(R.string.column_name_cannot_be_blank),
                                color = MaterialTheme.colorScheme.error)
                },

            )
        },
        confirmButton = {
            Button(onClick = {

                if (!isColNameExist && columnName.text.isNotBlank()) {
                    dismiss()
                    confirm(columnName.text)
                }
                isConfirmClicked = true

            }) { Text(stringResource(R.string.confirm)) }
        },
        dismissButton = {  TextButton(onClick = dismiss) { Text(stringResource(R.string.dismiss)) } },
        onDismissRequest = dismiss
    )

    LaunchedEffect(Unit) {
        delay(100)
        focusRequester.requestFocus()
    }
}