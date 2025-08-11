package com.goanderco.simplecharts.core.ui.dialog

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import com.goanderco.R
import com.goanderco.simplecharts.core.util.DataFileType


@Composable
public fun PickDataFileDialog(
    onSelectFile:(DataFileType) -> Unit,
    firstRowHeader: Boolean,
    onFirstRowIsHeader: (Boolean) -> Unit,
    dismiss: () -> Unit
) {
    var selectedFileTypeByUser by remember{ mutableStateOf(DataFileType.CSV) }


    AlertDialog(
        title = { Text(stringResource(R.string.select_data_file)) },
        text = {
            LazyColumn  (Modifier.selectableGroup()){
                item {
                    Row (modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically){
                        Text(stringResource(R.string.first_row_is_header))
                        Checkbox(
                            checked = firstRowHeader,
                            onCheckedChange = {
                                onFirstRowIsHeader(it)
                            }
                        )
                    }

                }
                items(DataFileType.entries.toTypedArray()) { text ->
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .height(56.dp)
                            .selectable(
                                selected = (text == selectedFileTypeByUser),
                                onClick = {
                                    selectedFileTypeByUser = text

                                },
                                role = Role.RadioButton
                            )
                            .padding(horizontal = 16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = (text == selectedFileTypeByUser),
                            onClick = null // null recommended for accessibility with screen readers
                        )
                        Text(
                            modifier = Modifier.padding(16.dp),
                            text = text.fileTypeName,
                            style = MaterialTheme.typography.bodyLarge,
                        )
                    }
                }

            }
        },
        confirmButton = { Button(onClick = {
            onSelectFile(selectedFileTypeByUser)
            dismiss()
        }) { Text(stringResource(R.string.select)) } },
        dismissButton = { TextButton(onClick = dismiss) { Text(stringResource(R.string.dismiss)) } },
        onDismissRequest = { dismiss() }
    )
}