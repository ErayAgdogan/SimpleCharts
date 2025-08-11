package com.goanderco.simplecharts.core.ui.dialog


import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import com.goanderco.R
import kotlinx.coroutines.delay

private const val LOWER_RANGE = 0.1f
private const val UPPER_RANGE = 4f
@Composable
public fun AspectRatioDialog(onConfirm:(Float) -> Unit, dismiss: () -> Unit, value: Float) {
    val focusRequester = remember{ FocusRequester() }
    var text by remember {
        mutableStateOf(TextFieldValue(text = value.toString(), selection = TextRange(value.toString().length))) }
    var error by remember { mutableStateOf(AspectRatioError.NO_ERROR) }
    AlertDialog(
        title = { Text(stringResource(R.string.enter_aspect_ratio)) },
        text = {
            OutlinedTextField(
                modifier = Modifier.focusRequester(focusRequester),
                value = text,
                onValueChange = {
                    text = it
                    it.text.toFloatOrNull().also {
                        if (it == null) error = AspectRatioError.PARSE_ERROR
                        else if (it < LOWER_RANGE || it > UPPER_RANGE) error = AspectRatioError.RANGE_ERROR
                        else error = AspectRatioError.NO_ERROR
                    }
                },
                supportingText = {
                    when(error) {
                        AspectRatioError.NO_ERROR -> { }
                        AspectRatioError.PARSE_ERROR -> Text(stringResource(R.string.number_error_cant_parse_to_number),
                            color = MaterialTheme.colorScheme.error)
                        AspectRatioError.RANGE_ERROR ->  Text(stringResource(R.string.the_number_must_be_between,
                            LOWER_RANGE.toString(), UPPER_RANGE.toString()),
                            color = MaterialTheme.colorScheme.error)
                    }
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )
        },
        onDismissRequest = dismiss,
        dismissButton = { TextButton(dismiss) { Text(stringResource(R.string.dismiss)) } },
        confirmButton = { Button(onClick = {
            if (error == AspectRatioError.NO_ERROR){
                onConfirm(text.text.toFloat())
                dismiss()
            }
        }) { Text(stringResource(R.string.confirm)) } }
    )
    LaunchedEffect(Unit) {
        delay(100)
        focusRequester.requestFocus()
    }
}

enum class AspectRatioError{
    NO_ERROR,
    PARSE_ERROR,
    RANGE_ERROR
}