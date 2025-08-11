package com.goanderco.simplecharts.feature.visual

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.goanderco.R
import com.goanderco.simplecharts.core.ui.dialog.DatasetFilterDialog
import com.goanderco.simplecharts.core.viewmodel.DataViewModel
import com.goanderco.simplecharts.core.viewmodel.DatasetFilterType

@Composable
public fun FilterScreen(viewmodel: DataViewModel = viewModel()) {
    val filters by viewmodel.filters.collectAsState()
    val columns by viewmodel.columns.collectAsState()

    var showFilterDialog by remember { mutableStateOf(false to -1) }
    if (showFilterDialog.first)
        DatasetFilterDialog(
            dismiss = { showFilterDialog = false to -1 },
            columns = columns,
            filter = filters[showFilterDialog.second],
            onConfirm = { viewmodel.setFilter(showFilterDialog.second, it) }
        )
    LazyColumn (modifier=Modifier
        .fillMaxWidth()
        .heightIn(max = 900.dp), // or .height(specificHeight)
        contentPadding = PaddingValues(8.dp)
    ){
        if (filters.isEmpty())
            item {
                Box(modifier=Modifier.fillMaxWidth().padding(vertical=32.dp), contentAlignment = Alignment.Center){
                    Row {
                        Icon(painterResource(R.drawable.baseline_filter_alt_off_24), null)
                        Spacer(Modifier.padding(8.dp))
                        Text(stringResource(R.string.no_filter))
                    }
                }
            }
        itemsIndexed(filters) { index, filter ->
            Column(modifier=Modifier.fillMaxWidth().clickable { showFilterDialog = true to index }) {
                IconButton(
                    modifier = Modifier.align(Alignment.End),
                    onClick = {viewmodel.deleterFilter(index)}
                ) {
                    Icon(Icons.Default.Clear, stringResource(R.string.delete_filter))
                }
                Text(text= buildAnnotatedString{


                    if (filter.columnName == null)
                        withStyle(SpanStyle(fontStyle = FontStyle.Italic)) {
                            append(stringResource(R.string.no_column))
                        }
                    else
                        append(filter.columnName)
                    append(" ")
                    withStyle(SpanStyle(fontWeight = FontWeight.Bold)) {
                        when(filter.filterType) {
                            DatasetFilterType.EQUALS -> "=="
                            DatasetFilterType.NOT_EQUALS -> "!="
                            DatasetFilterType.CONTAINS -> "∋"
                            DatasetFilterType.GREATER_THAN -> ">"
                            DatasetFilterType.LESS_THAN -> "<"
                            DatasetFilterType.GREATER_THAN_OR_EQUAL -> ">="
                            DatasetFilterType.LESS_THAN_OR_EQUAL -> "<="
                            DatasetFilterType.REGEX -> "~"
                        }.let(::append)
                    }
                    append(" ")
                    if (filter.filterValue?.toString().isNullOrEmpty())
                        withStyle(SpanStyle(fontStyle = FontStyle.Italic)) {
                            append("NULL")
                        }
                    else
                        append(filter.filterValue?.toString())
                }
                )
                Spacer(Modifier.height(8.dp))
                HorizontalDivider()
                Spacer(Modifier.height(8.dp))
            }


        }
        item {
            Box (Modifier.fillMaxWidth()){
                FloatingActionButton(
                    modifier = Modifier
                        .wrapContentSize()
                        .align(Alignment.CenterEnd),
                onClick = viewmodel::addFilter) { Icon(Icons.Default.Add, stringResource(R.string.add_filter)) }
            }

        }

    }
}
