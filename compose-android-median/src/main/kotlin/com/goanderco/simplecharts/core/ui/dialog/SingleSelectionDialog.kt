package com.goanderco.simplecharts.core.ui.dialog

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import com.goanderco.R


@Composable
public fun SingleSelectionDialog(
    title:String,
    options: Array<String>,
    selection: String,
    onConfirm:(selection:String) -> Unit,
    dismiss: () -> Unit
) {

    var selectedOptionByUser by rememberSaveable { mutableStateOf(selection) }
    var selectedOptionIndexByUser by rememberSaveable { mutableIntStateOf(options.indexOf(selection)) }
    AlertDialog(
        title = { Text(title) },
        text = {
            LazyColumn  (Modifier.selectableGroup()){
                itemsIndexed(options) { index, text ->
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .height(56.dp)
                            .selectable(
                                selected = (text == selectedOptionByUser),
                                onClick = {
                                    selectedOptionByUser = text
                                    selectedOptionIndexByUser = index
                                },
                                role = Role.RadioButton
                            )
                            .padding(horizontal = 16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = (text == selectedOptionByUser),
                            onClick = null // null recommended for accessibility with screen readers
                        )
                        Text(
                            modifier = Modifier.padding(16.dp),
                            text = text,
                            style = MaterialTheme.typography.bodyLarge,
                        )
                    }
                }

            }
        },
        confirmButton = { Button(onClick = {
            onConfirm(selectedOptionByUser)
            dismiss()
        }) { Text(stringResource(R.string.confirm)) } },
        dismissButton = { TextButton(onClick = dismiss) { Text(stringResource(R.string.dismiss)) } },
        onDismissRequest = { dismiss() }
    )
}