package com.goanderco.simplecharts.core.ui.dialog

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
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
import androidx.compose.ui.text.input.TextFieldValue
import com.goanderco.R
import kotlinx.coroutines.delay


@Composable
public fun EditChartPageNameDialog(confirm:(String) -> Unit, dismiss: () -> Unit, value: String) {
    var pageName by remember {
        mutableStateOf(TextFieldValue(text = value, selection = TextRange(value.length)))
    }
    var isConfirmClicked by remember { mutableStateOf(false) }

    val focusRequester = remember { FocusRequester() }
    AlertDialog(
        title = { Text(stringResource(R.string.edit_page_name)) },
        text = {
            OutlinedTextField(
                modifier= Modifier.focusRequester(focusRequester),
                value = pageName,
                onValueChange = { pageName = it},
                label = { Text(stringResource(R.string.enter_page_name)) },
                supportingText = {
                   if (isConfirmClicked && pageName.text.isBlank())
                        Text(
                            stringResource(R.string.page_name_cannot_be_blank),
                            color = MaterialTheme.colorScheme.error)
                }
            )
        },
        confirmButton = {
            Button(onClick = {

                if (pageName.text.isNotBlank()) {
                    confirm(pageName.text)
                    dismiss()
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