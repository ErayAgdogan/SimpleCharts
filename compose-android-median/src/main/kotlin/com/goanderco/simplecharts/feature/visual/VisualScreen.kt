package com.goanderco.simplecharts.feature.visual

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.PrimaryScrollableTabRow
import androidx.compose.material3.SecondaryTabRow
import androidx.compose.material3.SmallFloatingActionButton
import androidx.compose.material3.Tab
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.goanderco.R
import com.goanderco.simplecharts.core.ui.dialog.EditChartPageNameDialog
import com.goanderco.simplecharts.core.ui.dialog.SimpleOptionsDialog
import com.goanderco.simplecharts.core.viewmodel.DataViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jetbrains.letsPlot.skia.compose.PlotPanel
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun VisualPage(viewmodel: DataViewModel = viewModel()) {

    var selectedOptionPage by rememberSaveable { mutableIntStateOf(0) }

    val figure by viewmodel.figure.collectAsState()

    val currentPlotUIState by viewmodel.currentPlotUIState.collectAsState()
    val selectedPlotIndex by viewmodel.selectedPlotIndex.collectAsState()
    val chartNames by viewmodel.chartNames.collectAsState()

    val haptics = LocalHapticFeedback.current
    val context = LocalContext.current
    val cantDeleteLastPage = stringResource(R.string.cant_delete_the_last_page)
    var openPageOptionDialog by remember { mutableStateOf(false) }
    var pageOptionPosition by remember { mutableIntStateOf(0) }

    var openEditPageNameDialog by remember { mutableStateOf(false) }

    if (openPageOptionDialog)
        SimpleOptionsDialog(
            title = stringResource(R.string.page_options),
            optionResourceIDs = arrayOf(
                R.string.change_page_name to R.drawable.baseline_edit_24,
                R.string.add_page_to_right to R.drawable.baseline_add_chart_right_24,
                R.string.add_page_to_left to R.drawable.baseline_add_chart_left_24,
                R.string.delete_page to R.drawable.baseline_delete_24
            ),
            onSelected = { index->
                when(index) {
                    0 -> { openEditPageNameDialog = true }
                    1 -> { viewmodel.addPlot(pageOptionPosition + 1) }
                    2 -> { viewmodel.addPlot(pageOptionPosition) }
                    3 -> { viewmodel.deletePlot(pageOptionPosition,
                        lastItemError = { Toast.makeText(context, cantDeleteLastPage, Toast.LENGTH_SHORT).show() }) }
                }
            },
            dismiss = { openPageOptionDialog = false }
        )

    if (openEditPageNameDialog)
        EditChartPageNameDialog (
            confirm =  { viewmodel.setPlotPageName(pageOptionPosition, it) },
            dismiss = {  openEditPageNameDialog = false },
            value = chartNames[pageOptionPosition]
        )

    Column (modifier = Modifier
        .fillMaxSize()
        .verticalScroll(rememberScrollState())
        ,
        horizontalAlignment = Alignment.Start
        ) {
        PrimaryScrollableTabRow(
            modifier=Modifier.fillMaxWidth(),
            selectedTabIndex = selectedPlotIndex) {
            chartNames.forEachIndexed{ index, value ->

                Box (modifier = Modifier.combinedClickable(
                    onClick = { viewmodel.setSelectedPlotIndex(index) },
                    onLongClick = {
                        haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                        openPageOptionDialog = true
                        pageOptionPosition = index
                    }
                )){
                    Text(
                        modifier = Modifier
                            .padding(8.dp)
                            .wrapContentHeight()
                            .align(Alignment.Center),
                        // selected = index == selectedPlotIndex,
                        text = value,
                        textAlign = TextAlign.Center,
                        maxLines = 1
                    )

                }
                }

            Box(contentAlignment = Alignment.Center) {
                SmallFloatingActionButton(onClick = { viewmodel.addPlot() }) {
                    Icon(Icons.Default.Add, stringResource(R.string.add_plot))
                }
            }

        }

        PlotPanel(
            figure = figure,
            // figures[figureIndex.intValue].second,
            preserveAspectRatio = currentPlotUIState.preserveAspectRatio,
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(currentPlotUIState.aspectRatio)
        ) { computationMessages ->
        }

        ChartTypeSelection()
        SecondaryTabRow(selectedTabIndex = selectedOptionPage) {

            Tab(
                selected = selectedOptionPage == 0,
                onClick = { selectedOptionPage = 0 },
                text = { Text(stringResource(R.string.visual)) }
            )
            Tab(
                selected = selectedOptionPage == 1,
                onClick = { selectedOptionPage = 1 },
                text = { Text(stringResource(R.string.general)) }
            )
            Tab(
                selected = selectedOptionPage == 2,
                onClick = { selectedOptionPage = 2 },
                text = { Text(stringResource(R.string.filter)) }
            )
        }
        when(selectedOptionPage) {
            0 -> SpecialVisualOptions()
            1 -> GeneralVisualOptionsScreen()
            2 -> FilterScreen()
        }

    }
}

// Helper function to share the image
fun shareImage(context: Context, uri: Uri) {
    val shareIntent = Intent().apply {
        action = Intent.ACTION_SEND
        putExtra(Intent.EXTRA_STREAM, uri)
        type = "image/png"
        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
    }

    context.startActivity(Intent.createChooser(shareIntent, "Share Chart"))
}

// Helper function to save bitmap and create shareable URI
suspend fun saveBitmapAndGetUri(context: Context, bitmap: Bitmap): Uri? {
    return withContext(Dispatchers.IO) {
        try {
            // Create a temporary file in the cache directory
            val file = File(context.cacheDir, "shared_chart_${System.currentTimeMillis()}.png")

            // Save bitmap to file
            FileOutputStream(file).use { out ->
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
            }

            // Create content URI using FileProvider
            FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider", // Replace with your authority
                file
            )
        } catch (e: IOException) {
            e.printStackTrace()
            null
        }
    }
}




