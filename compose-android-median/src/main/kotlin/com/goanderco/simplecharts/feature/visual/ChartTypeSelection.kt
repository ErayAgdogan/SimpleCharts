package com.goanderco.simplecharts.feature.visual

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.staggeredgrid.LazyHorizontalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.PlainTooltip
import androidx.compose.material3.Text
import androidx.compose.material3.TooltipBox
import androidx.compose.material3.TooltipDefaults
import androidx.compose.material3.rememberTooltipState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.goanderco.R
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewmodel.compose.viewModel
import com.goanderco.simplecharts.core.viewmodel.ChartType
import com.goanderco.simplecharts.core.viewmodel.DataViewModel


@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
public fun ChartTypeSelection(viewmodel: DataViewModel = viewModel()) {
    var hide by rememberSaveable { mutableStateOf(false) }
    val currentPlotUIState by viewmodel.currentPlotUIState.collectAsState()

    Column {
        Box(Modifier
            .fillMaxWidth()
            .height(48.dp)
            .clickable { hide=!hide }) {
            Text(stringResource(R.string.select_visual_type), Modifier.padding(8.dp).align(Alignment.CenterStart))
            Icon(
                modifier= Modifier.padding(8.dp).align(Alignment.CenterEnd),
                painter = painterResource(
                    if (hide) R.drawable.baseline_arrow_drop_down_24
                    else R.drawable.baseline_arrow_drop_up_24
                ),
                contentDescription = null
            )
        }
        AnimatedVisibility(visible = !hide) {
            LazyHorizontalStaggeredGrid(
                horizontalItemSpacing = 6.dp,
                rows = StaggeredGridCells.Fixed(2),
                contentPadding = PaddingValues(8.dp),
                modifier = Modifier
                    .heightIn(max = 96.dp) // Set max height as fallbac

            )  {
                items(ChartType.entries)  { chartType ->
                    TooltipBox(
                        positionProvider = TooltipDefaults.rememberPlainTooltipPositionProvider(),
                        tooltip = {
                            PlainTooltip { Text(stringResource(chartType.descriptionID)) }
                        },
                        state = rememberTooltipState()
                    ) {

                        FilterChip(
                            modifier=Modifier,
                            selected = chartType == currentPlotUIState.chartType,
                            leadingIcon = { Icon(painterResource(chartType.iconResourceID), null)  },
                            onClick = { viewmodel.setChartType(chartType) },
                            label = { Text(stringResource(chartType.stringResourceID)) },
                            trailingIcon = {
                                if (chartType == currentPlotUIState.chartType)
                                    Icon(Icons.Default.Done, null)
                            }
                            )
                    }
                }


            }
        }


    }
}
