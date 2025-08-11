package com.goanderco.simplecharts.feature.visual

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.goanderco.R
import com.goanderco.simplecharts.core.ui.dialog.SelectMultipleColumnDialog
import com.goanderco.simplecharts.core.ui.view.ReadOnlyTextField
import com.goanderco.simplecharts.core.ui.view.TextFieldDroDown
import com.goanderco.simplecharts.core.ui.view.TextFieldWithColumnDialog
import com.goanderco.simplecharts.core.viewmodel.ChartStat
import com.goanderco.simplecharts.core.viewmodel.ChartType
import com.goanderco.simplecharts.core.viewmodel.DataViewModel
import com.goanderco.simplecharts.core.viewmodel.DistributionType
import com.goanderco.simplecharts.core.viewmodel.FittingMethod
import com.goanderco.simplecharts.core.viewmodel.SmoothMethod
import com.goanderco.simplecharts.core.viewmodel.ChartType.*
import com.goanderco.simplecharts.core.viewmodel.EXCLUDE_COLOR_COLUMN
import com.goanderco.simplecharts.core.viewmodel.EXCLUDE_GROUP_COLUMN
import com.goanderco.simplecharts.core.viewmodel.EXCLUDE_TOOLTIP_COLUMNS
import com.goanderco.simplecharts.core.viewmodel.EXCLUDE_X_COLUMN
import com.goanderco.simplecharts.core.viewmodel.EXCLUDE_Y_COLUMN
import com.goanderco.simplecharts.core.viewmodel.INCLUDE_MARGINAL_PLOT
import com.goanderco.simplecharts.core.viewmodel.INCLUDE_SIZE_COLUMN


@Composable
public fun SpecialVisualOptions(viewmodel: DataViewModel = viewModel()) {

    var openTooltipColumnDialog by remember { mutableStateOf(false) }
    val columns by viewmodel.columns.collectAsState()
    val currentPlotUIState by viewmodel.currentPlotUIState.collectAsState()

    if (openTooltipColumnDialog)
        SelectMultipleColumnDialog(
            title = stringResource(R.string.select_tooltips_columns),
            columns = columns,
            selectedColumns = currentPlotUIState.tooltips,
            onColumnSelected = viewmodel::setTooltipColumns,
            dismiss = { openTooltipColumnDialog = false }
        )


    if (currentPlotUIState.chartType in ChartStat.chartStatMap.keys)
        TextFieldDroDown(
            modifier = Modifier.padding(8.dp),
            label = stringResource(R.string.select_chart_stat),
            options = ChartStat.chartStatMap.get(currentPlotUIState.chartType)
                !!.associateWith { stringResource(it.stringResID) },
            onSelected = viewmodel::setChartStat,
            selected = currentPlotUIState.stat!!
        )
    if (currentPlotUIState.chartType == QQPLOT)
        TextFieldDroDown(
            modifier = Modifier.padding(8.dp),
            label = stringResource(R.string.select_distribution_function),
            options = DistributionType.entries.associateWith { stringResource(it.stringResID) },
            onSelected = viewmodel::setDistributionType,
            selected = currentPlotUIState.distributionType
        )
    if (currentPlotUIState.chartType == RESIDUAL)
        TextFieldDroDown(
            modifier = Modifier.padding(8.dp),
            label = stringResource(R.string.select_fitting_method),
            options = FittingMethod.entries.associateWith { stringResource(it.stringResID) },
            onSelected = viewmodel::setFittingMethod,
            selected = currentPlotUIState.fittingMethod
        )

    if (currentPlotUIState.chartType !in EXCLUDE_X_COLUMN)
        TextFieldWithColumnDialog(
            modifier = Modifier.padding(8.dp),
            title = stringResource(R.string.select_x_column),
            value = currentPlotUIState.xColumn,
            default = stringResource(R.string.no_column),
            onColumnSelected = viewmodel::setXColumn,
            columns = columns,

        )

    if (currentPlotUIState.chartType !in EXCLUDE_Y_COLUMN)
        TextFieldWithColumnDialog(
            modifier = Modifier.padding(8.dp),
            title = stringResource(R.string.select_y_column),
            value = currentPlotUIState.yColumn,
            default = stringResource(R.string.no_column),
            columns = columns,
            onColumnSelected = viewmodel::setYColumn
        )
    // Candlestick specific columns
    if (currentPlotUIState.chartType == CANDLESTICK) {
        TextFieldWithColumnDialog(
            modifier = Modifier.padding(8.dp),
            title = stringResource(R.string.select_y_low_column),
            value = currentPlotUIState.yLowColumn,
            default = stringResource(R.string.no_column),
            columns = columns,
            onColumnSelected = viewmodel::setYLowColumn
        )
        TextFieldWithColumnDialog(
            modifier = Modifier.padding(8.dp),
            title = stringResource(R.string.select_y_open_column),
            value = currentPlotUIState.yOpenColumn,
            default = stringResource(R.string.no_column),
            columns = columns,
            onColumnSelected = viewmodel::setYOpenColumn
        )
        TextFieldWithColumnDialog(
            modifier = Modifier.padding(8.dp),
            title = stringResource(R.string.select_y_close_column),
            value = currentPlotUIState.yCloseColumn,
            default = stringResource(R.string.no_column),
            columns = columns,
            onColumnSelected = viewmodel::setYCloseColumn
        )
        TextFieldWithColumnDialog(
            modifier = Modifier.padding(8.dp),
            title = stringResource(R.string.select_y_high_column),
            value = currentPlotUIState.yHighColumn,
            default = stringResource(R.string.no_column),
            columns = columns,
            onColumnSelected = viewmodel::setYHighColumn
        )
    }

    // Right side of the VIOLIN chart
    if (currentPlotUIState.chartType == VIOLIN)
        TextFieldWithColumnDialog(
            modifier = Modifier.padding(8.dp),
            title = stringResource(R.string.select_second_y_column),
            value = currentPlotUIState.secondYColumn,
            default = stringResource(R.string.no_column),
            columns = columns,
            onColumnSelected = viewmodel::setSecondYColumn
        )

    // Slice column for PIE chart
    if (currentPlotUIState.chartType == PIE)
        TextFieldWithColumnDialog(
            modifier = Modifier.padding(8.dp),
            title = stringResource(R.string.select_slice_column),
            value = currentPlotUIState.sliceColumn,
            default = stringResource(R.string.no_column),
            columns = columns,
            onColumnSelected = viewmodel::setSliceColumn
        )
    if (currentPlotUIState.chartType !in EXCLUDE_GROUP_COLUMN)
        TextFieldWithColumnDialog(
            modifier = Modifier.padding(8.dp),
            title = stringResource(R.string.select_group_column),
            value = currentPlotUIState.groupColumn,
            default = stringResource(R.string.no_column),
            columns = columns,
            onColumnSelected = viewmodel::setGroupColumn
        )

    if (currentPlotUIState.chartType !in EXCLUDE_COLOR_COLUMN)
        TextFieldWithColumnDialog(
            modifier = Modifier.padding(8.dp),
            title = stringResource(R.string.select_color_column),
            value = currentPlotUIState.colorColumn,
            default = stringResource(R.string.no_column),
            columns = columns,
            onColumnSelected = viewmodel::setColorColumn,
        )

    if (currentPlotUIState.chartType in INCLUDE_SIZE_COLUMN)
        TextFieldWithColumnDialog(
            modifier = Modifier.padding(8.dp),
            title = stringResource(R.string.select_size_column),
            value = currentPlotUIState.sizeColumn,
            default = stringResource(R.string.no_column),
            columns = columns,
            onColumnSelected = viewmodel::setSizeColumn
            )

    if (currentPlotUIState.chartType !in EXCLUDE_TOOLTIP_COLUMNS)
        ReadOnlyTextField(
            modifier = Modifier.padding(8.dp),
            onClick = { openTooltipColumnDialog = true },
            label = stringResource(R.string.select_tooltips_columns),
            value = currentPlotUIState.tooltips.joinToString(", ").takeUnless { it.isEmpty() },
            default = stringResource(R.string.no_column)
        )

    if (currentPlotUIState.chartType != WATERFALL)
        TextFieldWithColumnDialog(
            modifier = Modifier.padding(8.dp),
            title = stringResource(R.string.select_facet_x_column),
            value = currentPlotUIState.facetXColumn,
            default = stringResource(R.string.no_column),
            columns = columns,
            onColumnSelected = viewmodel::setFacetXColumn
        )

    if (currentPlotUIState.chartType != WATERFALL)
        TextFieldWithColumnDialog(
            modifier = Modifier.padding(8.dp),
            title = stringResource(R.string.select_facet_y_column),
            value = currentPlotUIState.facetYColumn,
            default = stringResource(R.string.no_column),
            columns = columns,
            onColumnSelected = viewmodel::setFacetYColumn
        )

    val distributionCharts = remember { arrayOf(ChartType.HISTOGRAM, ChartType.DENSITY, ChartType.BOX) }

    if (currentPlotUIState.chartType in INCLUDE_MARGINAL_PLOT)
        Row (modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp), verticalAlignment = Alignment.CenterVertically){
            Text(stringResource(R.string.show_distribution_at_top_right))
            Checkbox(checked = currentPlotUIState.showDistributionTR, viewmodel::setShowDistributionTR)
        }

    AnimatedVisibility(
        visible = currentPlotUIState.chartType in INCLUDE_MARGINAL_PLOT &&
                currentPlotUIState.showDistributionTR
    ) {
        TextFieldDroDown(
            Modifier.padding(horizontal=32.dp, vertical = 8.dp),
            options = distributionCharts.associateWith { stringResource(it.stringResourceID) },
            label = stringResource(R.string.select_distribution_chart),
            onSelected = viewmodel::setDistributionChartTR,
            selected = currentPlotUIState.distributionTypeTR
        )
    }

    if (currentPlotUIState.chartType in INCLUDE_MARGINAL_PLOT )
        Row (modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp), verticalAlignment = Alignment.CenterVertically){
            Text(stringResource(R.string.show_distribution_at_left_bottom))
            Checkbox(checked = currentPlotUIState.showDistributionLB, viewmodel::setShowDistributionLB)
        }
    AnimatedVisibility(
        visible = currentPlotUIState.chartType in INCLUDE_MARGINAL_PLOT &&
                currentPlotUIState.showDistributionLB
    ) {
        TextFieldDroDown(
            Modifier.padding(horizontal=32.dp, vertical = 8.dp),
            options = distributionCharts.associateWith { stringResource(it.stringResourceID) },
            label = stringResource(R.string.select_distribution_chart),
            onSelected = viewmodel::setDistributionChartLB,
            selected = currentPlotUIState.distributionTypeLB
        )
    }

    if (currentPlotUIState.chartType in remember{ setOf(SCATTER) })
        Row (modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp), verticalAlignment = Alignment.CenterVertically){
            Text(stringResource(R.string.show_smooth_line))
            Checkbox(checked = currentPlotUIState.showSmooth, viewmodel::setSmoothVisibility)
    }
    AnimatedVisibility(
        visible = currentPlotUIState.chartType == SCATTER &&
                currentPlotUIState.showSmooth
    ) {
        Column (modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp)){

            Row (modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp), verticalAlignment = Alignment.CenterVertically){
                Text(stringResource(R.string.show_smooth_line_standard_error))
                Checkbox(checked = currentPlotUIState.showSmoothLineSE, viewmodel::setSmoothSEVisibility)
            }

            TextFieldDroDown(
                Modifier.padding(8.dp),
                options = SmoothMethod.entries.associateWith { stringResource(it.stringResID) },
                label = stringResource(R.string.select_smooth_method),
                onSelected = viewmodel::setSmoothMethod,
                selected = currentPlotUIState.smoothMethod
            )
            if (currentPlotUIState.smoothMethod == SmoothMethod.LM)
                OutlinedTextField(
                    modifier = Modifier.padding(8.dp),
                    value = currentPlotUIState.smoothPolynomialDegree.toString(),
                    label = { Text(stringResource(R.string.smooth_polynomial_degree)) },
                    onValueChange = { viewmodel.setSmoothPolynomialDegree(it.toIntOrNull()?:1) },
                    trailingIcon = {
                        Column {
                            IconButton(
                                { viewmodel.setSmoothPolynomialDegree(currentPlotUIState.smoothPolynomialDegree + 1) },
                                    modifier=Modifier.height(24.dp)
                            ) {
                                Icon(painterResource(R.drawable.baseline_arrow_drop_up_24), null) }
                            IconButton({
                                currentPlotUIState.smoothPolynomialDegree.takeIf { it > 1 }?.let {
                                    viewmodel.setSmoothPolynomialDegree(it - 1)
                                }
                                 },  modifier=Modifier.height(24.dp)) {
                                Icon(painterResource(R.drawable.baseline_arrow_drop_down_24), null) }
                        }
                    },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )

        }

    }

    if (currentPlotUIState.chartType == VIOLIN)
        Row (modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp), verticalAlignment = Alignment.CenterVertically){
            Text(stringResource(R.string.add_box_plot))
            Checkbox(checked = currentPlotUIState.addBoxPlot, viewmodel::setBoxPlot)
        }

}

