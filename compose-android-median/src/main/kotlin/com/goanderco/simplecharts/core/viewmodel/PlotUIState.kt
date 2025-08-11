package com.goanderco.simplecharts.core.viewmodel

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import com.goanderco.R


@Immutable
@Stable
data class PlotUIState(
    val chartName: String,
    val chartType: ChartType = ChartType.HISTOGRAM,
    val stat: ChartStat? = ChartStat.chartStatMap.getOrDefault(chartType,null)?.getOrNull(0),
    val distributionType: DistributionType = DistributionType.NORM,
    val fittingMethod: FittingMethod = FittingMethod.LM,
    val xColumn: String? = null,
    val yColumn: String? = null,
    val secondYColumn: String? = null,
    val sliceColumn: String? = null,
    val groupColumn: String? = null,
    val colorColumn: String? = null,
    val sizeColumn: String? = null,
    val tooltips: List<String> = emptyList(),
    val facetXColumn: String? = null,
    val facetYColumn: String? = null,

    // for candlestick chart
    val yLowColumn: String? = null,
    val yHighColumn: String? = null,
    val yOpenColumn: String? = null,
    val yCloseColumn: String? = null,

    val showDistributionTR: Boolean = false,
    val distributionTypeTR: ChartType = ChartType.DENSITY,
    val showDistributionLB: Boolean = false,
    val distributionTypeLB: ChartType = ChartType.DENSITY,

    val showSmooth: Boolean = false,
    val showSmoothLineSE: Boolean = true,
    val smoothMethod: SmoothMethod = SmoothMethod.LM,
    val smoothPolynomialDegree: Int = 1,

    val addBoxPlot: Boolean = false,

    val preserveAspectRatio: Boolean = false,
    val aspectRatio: Float = 1.25f,
    val title: String = "",
    val subtitle: String? = null,
    val xlab: String? = null,
    val ylab: String? = null,
    val theme: ChartTheme = ChartTheme.MINIMAL_2,
    val flavor: ChartFlavour = ChartFlavour.HIGH_CONTRAST_LIGHT,

    val filterUIState: List<FilterUIState> = emptyList()


)

// Option 2: Using helper function for readability
fun PlotUIState.validateColumns(validColumns: Collection<String>): PlotUIState {
    return copy(
        xColumn = xColumn?.takeIf { it in validColumns },
        yColumn = yColumn?.takeIf { it in validColumns },
        secondYColumn = secondYColumn?.takeIf { it in validColumns },
        sliceColumn = sliceColumn?.takeIf { it in validColumns },
        groupColumn = groupColumn?.takeIf { it in validColumns },
        colorColumn = colorColumn?.takeIf { it in validColumns },
        sizeColumn = sizeColumn?.takeIf { it in validColumns },
        tooltips = tooltips.filter { it in validColumns },
        facetXColumn = facetXColumn?.takeIf { it in validColumns },
        facetYColumn = facetYColumn?.takeIf { it in validColumns },

        yLowColumn = yLowColumn?.takeIf { it in validColumns },
        yHighColumn = yHighColumn?.takeIf { it in validColumns },
        yOpenColumn = yOpenColumn?.takeIf { it in validColumns },
        yCloseColumn=  yCloseColumn?.takeIf { it in validColumns },

        filterUIState = filterUIState.filter { if (it.columnName != null) it.columnName in validColumns else true }
    )
}

fun PlotUIState.copyWithChartType(chartType: ChartType): PlotUIState {
    return this.copy(
        chartType = chartType,
        stat = ChartStat.chartStatMap.getOrDefault(chartType,null)?.getOrNull(0),
    )
}


enum class SmoothMethod(val stringResID: Int) {
    LM(R.string.linear_model), LOESS(R.string.loess)
}

enum class ChartTheme (val stringResID: Int){
    MINIMAL(R.string.minimal), MINIMAL_2(R.string.minimal_2), BLACK_WHITE(R.string.black_white),
    GREY(R.string.grey), CLASSIC(R.string.classic),
    LIGHT(R.string.light), VOID(R.string.Void), NONE(R.string.none)
}

enum class ChartFlavour (val stringResID: Int){
    Darcula(R.string.darcula), SOLARIZED_LIGHT(R.string.solarized_light), SOLARIZED_DARK(R.string.solarized_dark),
    HIGH_CONTRAST_LIGHT(R.string.high_contrast_light), HIGH_CONTRAST_DARK(R.string.high_contrast_dark),
}