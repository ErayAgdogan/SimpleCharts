package com.goanderco.simplecharts.feature.visual

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.goanderco.R
import com.goanderco.simplecharts.core.viewmodel.DataViewModel
import com.goanderco.simplecharts.core.ui.DemoRadioGroup
import com.goanderco.simplecharts.core.ui.dialog.AspectRatioDialog
import com.goanderco.simplecharts.core.ui.view.ReadOnlyTextField
import com.goanderco.simplecharts.core.ui.view.TextFieldDroDown
import com.goanderco.simplecharts.core.viewmodel.ChartFlavour
import com.goanderco.simplecharts.core.viewmodel.ChartTheme

@Composable
public fun GeneralVisualOptionsScreen(viewmodel: DataViewModel = viewModel()) {
    val currentPlotUIState by viewmodel.currentPlotUIState.collectAsState()
    var showAspectRatioDialog by remember { mutableStateOf(false) }
    if (showAspectRatioDialog)
        AspectRatioDialog(
            onConfirm = viewmodel::setAspectRatio,
            dismiss = { showAspectRatioDialog = false },
            value = currentPlotUIState.aspectRatio
        )

    DemoRadioGroup(
        currentPlotUIState.preserveAspectRatio,
        onAspectChanged = viewmodel::setPreserveAspectRatio
    )
    ReadOnlyTextField(
        modifier = Modifier.padding(8.dp),
        onClick = { showAspectRatioDialog = true },
        label = stringResource(R.string.aspect_ratio),
        value = currentPlotUIState.aspectRatio.toString(),
        default = "0.1"
    )
    OutlinedTextField(
        modifier = Modifier.padding(8.dp),
        value = currentPlotUIState.title?:"",
        onValueChange = viewmodel::setTitle,
        label = { Text(stringResource(R.string.chart_title)) }
    )
    OutlinedTextField(
        modifier = Modifier.padding(8.dp),
        value = currentPlotUIState.subtitle?:"",
        onValueChange = viewmodel::setSubTitle,
        label = { Text(stringResource(R.string.chart_sub_title)) }

    )
    OutlinedTextField(
        modifier = Modifier.padding(8.dp),
        value = currentPlotUIState.xlab?:"",
        onValueChange = viewmodel::setXLab,
        label = { Text(stringResource(R.string.chart_xlab)) }
    )
    OutlinedTextField(
        modifier = Modifier.padding(8.dp),
        value = currentPlotUIState.ylab?:"",
        onValueChange = viewmodel::setYLab,
        label = { Text(stringResource(R.string.chart_ylab)) }

    )

    TextFieldDroDown(
        modifier = Modifier
            .padding(8.dp),
        label = stringResource(R.string.select_theme),
        options = ChartTheme.entries.associateWith { stringResource(it.stringResID) },
        onSelected = viewmodel::setTheme,
        selected = currentPlotUIState.theme,
    )
    TextFieldDroDown(
        modifier = Modifier
            .padding(8.dp),
        label = stringResource(R.string.select_color_scheme),
        options = ChartFlavour.entries.associateWith { stringResource(it.stringResID) },
        onSelected = viewmodel::setColorScheme,
        selected = currentPlotUIState.flavor,
    )


}

