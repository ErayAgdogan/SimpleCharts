package com.goanderco.simplecharts.core.ui.view

import androidx.compose.foundation.layout.Box
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun <T: Enum<T>> TextFieldDroDown(
    modifier: Modifier = Modifier,
    label: String,
    options: Map<T, String>,
    onSelected: (T) -> Unit,
    selected: T,
    ) {
    val expanded = remember { (mutableStateOf(false)) }

    Box(modifier = modifier) {
        @OptIn(ExperimentalMaterial3Api::class)
        (ExposedDropdownMenuBox(
        expanded = expanded.value,
        onExpandedChange = {
            expanded.value = !expanded.value
        },
//            modifier = Modifier.height(40.dp).width(40.dp)
    ) {
        TextField(
            modifier = Modifier
                .menuAnchor(MenuAnchorType.PrimaryNotEditable),
            value = options[selected].toString(),
            label = { Text(label) },
            onValueChange = {},
            readOnly = true,
            trailingIcon = {
                @OptIn(ExperimentalMaterial3Api::class)
                ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded.value)
            }
        )

        ExposedDropdownMenu(
            expanded = expanded.value,
            onDismissRequest = { expanded.value = false }
        ) {
            options.forEach { (value, text) ->
                DropdownMenuItem(
                    onClick = {
                        onSelected(value)
                        expanded.value = false
                    },
                    text = {
                        Text(text = text)
                    }
                )
            }
        }
    })
    }
}