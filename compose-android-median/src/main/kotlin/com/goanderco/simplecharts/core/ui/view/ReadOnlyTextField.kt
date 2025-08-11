package com.goanderco.simplecharts.core.ui.view

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import com.goanderco.simplecharts.core.ui.clickableTextField

@Composable
public fun ReadOnlyTextField(modifier: Modifier=Modifier, onClick:() -> Unit, label: String, value: String?, default: String) {
    TextField(
        modifier = modifier
            .clickableTextField(value) { onClick() },
        value = value?: default,
        onValueChange = { },
        label = { Text(label) },
        readOnly = true,
        colors = value?.let{ TextFieldDefaults.colors()}?: TextFieldDefaults.colors(
            focusedTextColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
            unfocusedTextColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
        ),
        textStyle =  TextStyle(
            fontStyle = value?.let{ FontStyle.Normal}?: FontStyle.Italic
        ),
    )
}