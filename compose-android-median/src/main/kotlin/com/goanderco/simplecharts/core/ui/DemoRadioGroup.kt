
package com.goanderco.simplecharts.core.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp

@Suppress("FunctionName")
@Composable
fun DemoRadioGroup(
    preserveAspectRatio: Boolean,
    onAspectChanged: (Boolean) -> Unit
) {
    val radioOptions: List<String> = listOf("Yes", "No")

    // Yes <=> 0
    // No <=> 1
    var selectedIndex = if (preserveAspectRatio) 0 else 1

    Row(
        modifier = Modifier.selectableGroup().padding(8.dp) //.fillMaxWidth(),
//        horizontalArrangement = Arrangement.Center,
//        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(
            verticalArrangement = Arrangement.Center,
        ) {
            Text("Preserve aspect ratio", modifier = Modifier.padding(end = 8.dp))
        }
        radioOptions.forEachIndexed { index, label ->
            Column(
                modifier = Modifier.selectable(
                    selected = (radioOptions[selectedIndex] == label),
                    onClick = {
                        selectedIndex = index
                        onAspectChanged(when (index) {
                            0 -> true
                            else -> false
                        })
                    },
                    role = Role.RadioButton
                ),
                verticalArrangement = Arrangement.Center,
//                horizontalAlignment = Alignment.CenterHorizontally

            ) {
                Row(
                    modifier = Modifier.padding(end = 8.dp)
                ) {
                    RadioButton(
                        selected = (index == selectedIndex),
                        onClick = null
                    )
                    Text(text = radioOptions[index])
                }
            }
        }
    }
}