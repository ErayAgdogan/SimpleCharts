package com.goanderco.simplecharts.core.ui.dialog

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.goanderco.R

@Composable
public fun ConfirmMakingRowColumnNameDialog(confirm:(index: Int, deleteRow: Boolean)->Unit, dismiss:()->Unit, index:Int) {
    var deleteRow by remember { mutableStateOf(false) }
    AlertDialog(
        title = { Text(stringResource(R.string.confirm_making_the_row_column_names_at_index, index + 1)) },
        text = { Row (modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically){
            Text(stringResource(R.string.delete_the_row))
            Checkbox(
                checked = deleteRow,
                onCheckedChange = {
                    deleteRow = it
                }
            )
        }
        },
        confirmButton = { Button({
            dismiss()
            confirm(index, deleteRow)
        }) { Text(stringResource(R.string.confirm))} },
        dismissButton = { TextButton(dismiss) { Text(stringResource(R.string.dismiss)) } },
        onDismissRequest = { dismiss() }
    )
}