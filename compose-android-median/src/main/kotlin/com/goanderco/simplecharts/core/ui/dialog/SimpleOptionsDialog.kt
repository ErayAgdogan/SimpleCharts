package com.goanderco.simplecharts.core.ui.dialog

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.goanderco.R

@Composable
public fun SimpleOptionsDialog(
    title: String,
    optionResourceIDs: Array<Pair<Int, Int?>>,
    onSelected: (Int) -> Unit,
    dismiss: () -> Unit
) {

    AlertDialog(
        title = { Text(title) },
        text = {
            LazyColumn (modifier=Modifier.fillMaxWidth()){
                itemsIndexed(optionResourceIDs) { index, (stringID, iconID) ->
                    Row(
                        modifier=Modifier
                            .fillMaxWidth()
                            .height(56.dp)
                            .clickable{
                                dismiss()
                                onSelected(index)

                                      },
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        iconID?.let {  Icon(painterResource(it), null) }
                        Spacer(modifier=Modifier.width(16.dp))
                        Text(text = stringResource(stringID))
                    }
                }

            }
        },
        confirmButton = {  },
        dismissButton = { TextButton(onClick = dismiss) { Text(stringResource(R.string.dismiss)) } },
        onDismissRequest = dismiss
    )
}