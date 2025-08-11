package com.goanderco.simplecharts.core.ui.dialog

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
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.unit.dp
import com.goanderco.R


@Composable
public fun SelectMultipleColumnDialog(
    title:String,
    columns: Collection<String>,
    selectedColumns: Collection<String>,
    onColumnSelected:(Collection<String>) -> Unit,
    dismiss: () -> Unit
) {
    var selectedColumnsByUser by remember { mutableStateOf(selectedColumns.toSet()) }
    fun onChecked(text: String) {
        if (text !in selectedColumnsByUser)
            selectedColumnsByUser += text
        else
            selectedColumnsByUser -= text
    }
    AlertDialog(
        title = { Text(title) },
        text = {
            LazyColumn  (Modifier.selectableGroup()){
                items(columns.toTypedArray()) { text ->
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .height(56.dp)
                            .selectable(
                                selected = (text in selectedColumnsByUser ),
                                onClick = { onChecked(text) },
                                role = Role.Checkbox
                            )
                            .padding(horizontal = 16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Checkbox(
                            checked = ( text in selectedColumnsByUser),
                            onCheckedChange = { onChecked(text) },
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
            onColumnSelected(selectedColumnsByUser)
            dismiss()
        }) { Text(stringResource(R.string.confirm)) } },
        dismissButton = { TextButton(onClick = dismiss) { Text(stringResource(R.string.dismiss)) } },
        onDismissRequest = { dismiss() }
    )
}