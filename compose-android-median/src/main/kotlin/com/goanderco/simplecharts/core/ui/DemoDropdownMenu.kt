
package com.goanderco.simplecharts.core.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.material3.*
import androidx.compose.ui.Modifier


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DemoDropdownMenu(
    options: List<String>,
    selectedIndex: MutableState<Int>
) {
    val expanded = remember { (mutableStateOf(false)) }

    Box() {
        @OptIn(ExperimentalMaterial3Api::class)
        ExposedDropdownMenuBox (
            expanded = expanded.value,
            onExpandedChange = {
                expanded.value = !expanded.value
            },
//            modifier = Modifier.height(40.dp).width(40.dp)
        ) {
            TextField(
                modifier = Modifier
                    .menuAnchor(MenuAnchorType.PrimaryNotEditable),
                value = options[selectedIndex.value],
                onValueChange = {},
                readOnly = true,
                trailingIcon = {
                    @OptIn(ExperimentalMaterial3Api::class)
                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded.value)
                }
            )

            ExposedDropdownMenu (
                expanded = expanded.value,
                onDismissRequest = { expanded.value = false }
            ) {
                options.forEachIndexed { index, name ->
                    DropdownMenuItem(
                        onClick = {
                            selectedIndex.value = index
                            expanded.value = false
                        },
                        text = {
                            Text(text = name)
                        }
                    )
                }
            }
        }
    }
}