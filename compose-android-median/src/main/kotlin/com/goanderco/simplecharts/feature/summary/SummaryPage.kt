package com.goanderco.simplecharts.feature.summary

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.goanderco.R
import com.goanderco.simplecharts.core.viewmodel.DataViewModel
import org.jetbrains.letsPlot.bistro.corr.CorrPlot
import org.jetbrains.letsPlot.ggplot
import org.jetbrains.letsPlot.skia.compose.PlotPanel


@OptIn(ExperimentalFoundationApi::class)
@Composable
public fun SummaryPage(viewmodel: DataViewModel = viewModel()) {
    val dataset by viewmodel.dataset.collectAsState()
    val columns by viewmodel.columns.collectAsState()
    val dataSummary by viewmodel.dataSummary.collectAsState()

    val scrollState = rememberScrollState()

    val figures by viewmodel.summaryHistograms.collectAsState()

    val correlationMatrixTitle = stringResource(R.string.correlation_matrix_numerical)

    val atLeastTwoNumericalColumn by viewmodel.atLeastTwoNumericalColumnWithThreeNumericalValue.collectAsState()
    val correlationFigure = remember (dataset, atLeastTwoNumericalColumn){
        if (atLeastTwoNumericalColumn) CorrPlot(dataset, title = correlationMatrixTitle).tiles().build()
        else ggplot {  }
    }


    LazyColumn {

        stickyHeader {
            Header(scrollState, columns)
        }

        item {

                Row (modifier = Modifier.fillMaxSize()){
                    Text(
                        text = stringResource(R.string.distribution),
                        modifier = Modifier
                            .size(96.dp)
                            .border(1.dp, Color.Black)
                            .background(MaterialTheme.colorScheme.primaryContainer)
                            .wrapContentSize(Alignment.Center),
                        textAlign = TextAlign.Center,
                    )
                    Row (modifier = Modifier.fillMaxSize().horizontalScroll(scrollState)){
                    figures.forEach { figure ->
                        PlotPanel(
                            figure = figure,
                            // figures[figureIndex.intValue].second,
                            preserveAspectRatio = false,
                            modifier = Modifier
                                .height(96.dp)
                                .width(96.dp)
                                .border(1.dp, Color.Black)
                        ) { computationMessages ->

                        }
                    }

            }

            }
        }
        item { DataRow(stringResource(R.string.null_count), dataSummary.nullCounts, scrollState) }
        item { DataRow(stringResource(R.string.distinct), dataSummary.distinctCounts, scrollState) }
        item { DataRow(stringResource(R.string.frequent), dataSummary.frequent, scrollState) }
        item { DataRow(stringResource(R.string.frequency), dataSummary.frequency, scrollState) }
        item { DataRow(stringResource(R.string.mean), dataSummary.mean, scrollState) }
        item { DataRow(stringResource(R.string.std), dataSummary.std, scrollState) }
        item { DataRow(stringResource(R.string.min), dataSummary.min, scrollState) }
        item { DataRow(stringResource(R.string.q1), dataSummary.Q1, scrollState) }
        item { DataRow(stringResource(R.string.median), dataSummary.median, scrollState) }
        item { DataRow(stringResource(R.string.q3), dataSummary.Q3, scrollState) }
        item { DataRow(stringResource(R.string.max), dataSummary.max, scrollState) }

        item { Spacer(modifier = Modifier.size(16.dp)) }
        if (atLeastTwoNumericalColumn)
            item {
                PlotPanel(
                    figure = correlationFigure,
                    modifier= Modifier.fillMaxWidth().aspectRatio(1f),
                    preserveAspectRatio = true,
                    ) { }
            }


    }

}



@Composable
fun Header(
    horizontalScrollState: ScrollState,
    columns: List<String>,
) {


        // Column name headers, scroll in sync
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
        ) {
            Spacer(
                modifier = Modifier
                    .width(96.dp)
                    .height(48.dp)
                    .background(MaterialTheme.colorScheme.primaryContainer)
                    .border(1.dp, Color.Black)
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .horizontalScroll(horizontalScrollState)
            ) {
                columns.forEach { col ->
                    HeaderCell(
                        text = col
                    )
                }
            }
    }
}

@Composable
private fun HeaderCell(
    text: String,
) {
    Text(
        text = text,
        modifier = Modifier
            .width(96.dp)
            .height(48.dp)
            .border(1.dp, Color.Black)
            .background(MaterialTheme.colorScheme.primaryContainer)
            .wrapContentSize(Alignment.Center),
        textAlign = TextAlign.Center,
    )
}

@Composable
private fun DataRow(
    dataName: String,
    data: List<Any?>,
    horizontalScrollState: ScrollState
) {

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp)
    ) {
        Text(
            text = dataName,
            modifier = Modifier
                .width(96.dp)
                .height(48.dp)
                .border(1.dp, Color.Black)
                .background(MaterialTheme.colorScheme.primaryContainer)
                .wrapContentSize(Alignment.Center),
            textAlign = TextAlign.Center,
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .horizontalScroll(horizontalScrollState)
        ) {
            data.forEach { data ->

                DataCell2(
                    value = data
                )
            }
        }
}
}
@Composable
fun DataCell2(
    value: Any?,
) {

    Text(modifier=Modifier
        .height(48.dp)
        .width(96.dp)

        .border(1.dp, Color.Black)
        .padding(2.dp),

        text= value?.toString() ?: "NULL",
        color = if (value == null)  MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
        else MaterialTheme.colorScheme.onSurface,
        fontStyle = if (value == null) FontStyle.Italic else FontStyle.Normal
    )

}