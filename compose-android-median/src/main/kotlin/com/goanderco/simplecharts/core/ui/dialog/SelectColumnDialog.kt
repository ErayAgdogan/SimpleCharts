package com.goanderco.simplecharts.core.ui.dialog

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material3.Button
import androidx.compose.material3.TextButton
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
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
public fun SelectColumnDialog(
    title:String,
    columns: Collection<String>,
    selectedColumn: String?,
    onColumnSelected:(String?) -> Unit,
    dismiss: () -> Unit,
    includeNullColumn: Boolean = true
) {
    var selectedColumnByUser by rememberSaveable { mutableStateOf(selectedColumn) }
    val columns = remember {  if (includeNullColumn) listOf<String?>(null) + columns else columns.toList() }
    AlertDialog(
        title = { Text(title) },
        text = {
            LazyColumn  (Modifier.selectableGroup()){
                items(columns) { text ->
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .height(56.dp)
                            .selectable(
                                selected = (text == selectedColumnByUser),
                                onClick = {
                                    selectedColumnByUser = text

                                          },
                                role = Role.RadioButton
                            )
                            .padding(horizontal = 16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = (text == selectedColumnByUser),
                            onClick = null // null recommended for accessibility with screen readers
                        )
                        Text(
                            modifier = Modifier.alpha(text?.let { 1f }?:0.6f).padding(16.dp),
                            text = text?: stringResource(R.string.no_column),
                            style = MaterialTheme.typography.bodyLarge,
                            fontStyle = text?.let { FontStyle.Normal }?: FontStyle.Italic
                        )
                    }
                }

            }
        },
        confirmButton = { Button(onClick = {
            onColumnSelected(selectedColumnByUser)
            dismiss()
        }) { Text(stringResource(R.string.confirm))} },
        dismissButton = { TextButton(onClick = dismiss) { Text(stringResource(R.string.dismiss))} },
        onDismissRequest = { dismiss() }
    )
}