package com.goanderco.simplecharts.core.viewmodel

import com.goanderco.simplecharts.core.util.geomCandleStick
import org.jetbrains.letsPlot.bistro.qq.qqPlot
import org.jetbrains.letsPlot.bistro.residual.residualPlot
import org.jetbrains.letsPlot.bistro.waterfall.waterfallPlot
import org.jetbrains.letsPlot.facet.facetGrid
import org.jetbrains.letsPlot.geom.geomArea
import org.jetbrains.letsPlot.geom.geomAreaRidges
import org.jetbrains.letsPlot.geom.geomBar
import org.jetbrains.letsPlot.geom.geomBin2D
import org.jetbrains.letsPlot.geom.geomBoxplot
import org.jetbrains.letsPlot.geom.geomDensity
import org.jetbrains.letsPlot.geom.geomDensity2DFilled
import org.jetbrains.letsPlot.geom.geomHistogram
import org.jetbrains.letsPlot.geom.geomJitter
import org.jetbrains.letsPlot.geom.geomLine
import org.jetbrains.letsPlot.geom.geomPie
import org.jetbrains.letsPlot.geom.geomPoint
import org.jetbrains.letsPlot.geom.geomSmooth
import org.jetbrains.letsPlot.geom.geomViolin
import org.jetbrains.letsPlot.geom.geomYDotplot
import org.jetbrains.letsPlot.ggmarginal
import org.jetbrains.letsPlot.intern.Plot
import org.jetbrains.letsPlot.label.ggtitle
import org.jetbrains.letsPlot.label.xlab
import org.jetbrains.letsPlot.label.ylab
import org.jetbrains.letsPlot.letsPlot
import org.jetbrains.letsPlot.themes.flavorDarcula
import org.jetbrains.letsPlot.themes.flavorHighContrastDark
import org.jetbrains.letsPlot.themes.flavorHighContrastLight
import org.jetbrains.letsPlot.themes.flavorSolarizedDark
import org.jetbrains.letsPlot.themes.flavorSolarizedLight
import org.jetbrains.letsPlot.themes.themeBW
import org.jetbrains.letsPlot.themes.themeClassic
import org.jetbrains.letsPlot.themes.themeGrey
import org.jetbrains.letsPlot.themes.themeLight
import org.jetbrains.letsPlot.themes.themeMinimal
import org.jetbrains.letsPlot.themes.themeMinimal2
import org.jetbrains.letsPlot.themes.themeNone
import org.jetbrains.letsPlot.themes.themeVoid
import org.jetbrains.letsPlot.tooltips.layerTooltips

private val marginalPlotAllowed = setOf(ChartType.SCATTER, ChartType.TWO_D_DENSITY, ChartType.TWO_D_BIN)

public fun createPlot(dataset: Map<String, List<Any?>>, plotUIState: PlotUIState): Plot {

    return when(plotUIState.chartType) {
        ChartType.BAR -> letsPlot(dataset) {} +  geomBar(
            tooltips = layerTooltips(*plotUIState.tooltips.toTypedArray()),
            stat =  plotUIState.stat!!.stat
        ) {
            plotUIState.xColumn?.let{ x = it }
            plotUIState.yColumn?.let{ y = it }
            plotUIState.groupColumn?.let{ group = it }
            plotUIState.colorColumn?.let {
                fill = it
                color = it
            }
        }
        ChartType.HISTOGRAM -> letsPlot(dataset) {} +  geomHistogram (
            tooltips = layerTooltips(*plotUIState.tooltips.toTypedArray()),
            stat =  plotUIState.stat!!.stat
        ){
            plotUIState.xColumn?.let{ x = it }
            plotUIState.yColumn?.let{ y = it }
            plotUIState.groupColumn?.let{ group = it }
            plotUIState.colorColumn?.let {
                fill = it
                color = it
            }
        }
        ChartType.PIE -> letsPlot(dataset) {} + geomPie (
            tooltips = layerTooltips(*plotUIState.tooltips.toTypedArray()),
            stat =  plotUIState.stat!!.stat
        ){
            plotUIState.sliceColumn?.let{ slice = it }
            plotUIState.colorColumn?.let { fill = it }
            plotUIState.groupColumn?.let{ group = it }
        }
        ChartType.BOX ->  letsPlot(dataset) {} +  geomBoxplot(alpha=0.25) {
            plotUIState.xColumn?.let{ x = it }
            plotUIState.yColumn?.let{ y = it }
            plotUIState.groupColumn?.let{ group = it }
            plotUIState.colorColumn?.let {
                color = it
                fill = it
            }
        }
        ChartType.DENSITY -> letsPlot(dataset) {} +  geomDensity (alpha = .25,
            tooltips = layerTooltips(*plotUIState.tooltips.toTypedArray()),
            stat =  plotUIState.stat!!.stat
        ){
            plotUIState.xColumn?.let{ x = it }
            plotUIState.yColumn?.let{ y = it }
            plotUIState.groupColumn?.let{ group = it }
            plotUIState.colorColumn?.let {
                color = it
                fill = it

            }
        }
        ChartType.LINE -> letsPlot(dataset) {} +  geomLine(
            tooltips = layerTooltips(*plotUIState.tooltips.toTypedArray()),
            stat =  plotUIState.stat!!.stat
        ) {
            plotUIState.xColumn?.let{ x = it }
            plotUIState.yColumn?.let{ y = it }
            plotUIState.groupColumn?.let{ group = it }
            plotUIState.colorColumn?.let{
                color = it
            }
        }
        ChartType.RIDGE_LINE -> letsPlot(dataset) {} +  geomAreaRidges(alpha=0.25,
            tooltips = layerTooltips(*plotUIState.tooltips.toTypedArray()),
            stat =  plotUIState.stat!!.stat
        ) {
            plotUIState.xColumn?.let{ x = it }
            plotUIState.yColumn?.let{ y = it }
            plotUIState.groupColumn?.let{ group = it }
            plotUIState.colorColumn?.let{
                color = it
                fill = it
            }
        }
        ChartType.VIOLIN -> letsPlot(dataset) {
            plotUIState.xColumn?.let{ x = it }
            plotUIState.yColumn?.let{ y = it }
            plotUIState.groupColumn?.let{ group = it }
        } +  geomViolin(
            alpha=0.5,
            tooltips = layerTooltips(*plotUIState.tooltips.toTypedArray()),
            showHalf = if (plotUIState.secondYColumn == null) 0 else -1,
            trim = false
        ) {

            plotUIState.colorColumn?.let{
                color = it
                fill = it
            }
        }.run {
            if (plotUIState.addBoxPlot && plotUIState.secondYColumn == null)
                this + geomBoxplot(width=.25){
                    plotUIState.colorColumn?.let{
                        fill = it
                    }
                }
            else this
        }.run {
            if (plotUIState.secondYColumn != null)
                this + geomViolin(
                    alpha=0.5,
                    tooltips = layerTooltips(*plotUIState.tooltips.toTypedArray()),
                    showHalf = 1,
                    trim = false
                ){
                    y = plotUIState.secondYColumn
                    plotUIState.colorColumn?.let{
                        color = it
                        fill = it
                    }
                }
            else this

        }
        ChartType.BEESWARM -> letsPlot(dataset) {} +  geomYDotplot(
            tooltips = layerTooltips(*plotUIState.tooltips.toTypedArray())
        ) {
            plotUIState.xColumn?.let{ x = it }
            plotUIState.yColumn?.let{ y = it }
            plotUIState.groupColumn?.let{ group = it }
            plotUIState.colorColumn?.let{
                color = it
                fill = it
            }
        }
        ChartType.SCATTER -> letsPlot(dataset) {
            plotUIState.xColumn?.let{ x = it }
            plotUIState.yColumn?.let{ y = it }
            plotUIState.groupColumn?.let{ group = it }
            plotUIState.colorColumn?.let {
                color = it
                fill = it
            }
        } +  geomPoint(
            tooltips = layerTooltips(*plotUIState.tooltips.toTypedArray()),
            stat =  plotUIState.stat!!.stat
        ) {
            plotUIState.sizeColumn?.let{ size = it }
        }.run {
            if (plotUIState.showSmooth)
                this + geomSmooth(
                    se = plotUIState.showSmoothLineSE,
                    method = when(plotUIState.smoothMethod) {
                        SmoothMethod.LM -> "lm"
                        SmoothMethod.LOESS -> "loess"
                    },
                    deg = plotUIState.smoothPolynomialDegree.let { if (it<1) 1 else it }
                ) {
                }
            else this
        }
        ChartType.JITTER ->  letsPlot(dataset) {} +  geomJitter (
            width=.25,
            alpha=0.5,
            tooltips = layerTooltips(*plotUIState.tooltips.toTypedArray()),
            stat =  plotUIState.stat!!.stat
        ){
            plotUIState.xColumn?.let{ x = it }
            plotUIState.yColumn?.let{ y = it }
            plotUIState.groupColumn?.let{ group = it }
            plotUIState.colorColumn?.let {
                color = it
            }

            plotUIState.sizeColumn?.let{ size = it }
        }
        ChartType.AREA -> letsPlot(dataset) {} +  geomArea(
            alpha=0.25,
            tooltips = layerTooltips(*plotUIState.tooltips.toTypedArray()),
            stat =  plotUIState.stat!!.stat
        ) {
            plotUIState.xColumn?.let{ x = it }
            plotUIState.yColumn?.let{ y = it }
            plotUIState.groupColumn?.let{ group = it }
            plotUIState.colorColumn?.let{
                color = it
                fill = it

            }
        }
        ChartType.WATERFALL -> if (plotUIState.xColumn == null || plotUIState.yColumn == null)
            letsPlot(dataset) + geomPoint()
        else
            waterfallPlot(
                data = dataset,
                x = plotUIState.xColumn,
                y = plotUIState.yColumn,
                group = plotUIState.groupColumn,
            )
        ChartType.TWO_D_BIN -> letsPlot(dataset) {
            x = plotUIState.xColumn
            y = plotUIState.yColumn
        } + geomBin2D(
            tooltips = layerTooltips(*plotUIState.tooltips.toTypedArray())
        ) {

        }
        ChartType.TWO_D_DENSITY -> letsPlot(dataset){
            x = plotUIState.xColumn
            y = plotUIState.yColumn
        } + geomDensity2DFilled(
            tooltips = layerTooltips(*plotUIState.tooltips.toTypedArray())
        ) {

            fill = "..level.."
        }

        ChartType.CANDLESTICK -> geomCandleStick(
            dataset,
            xColumn = plotUIState.xColumn,
            yLowColumn = plotUIState.yLowColumn,
            yHighColumn = plotUIState.yHighColumn,
            yOpenColumn = plotUIState.yOpenColumn,
            yCloseColumn = plotUIState.yCloseColumn,
            tooltipsColumns = plotUIState.tooltips
        )
        ChartType.QQPLOT -> if(plotUIState.yColumn == null)
            letsPlot(dataset) + geomPoint()
        else
            qqPlot(
            data = dataset,
            sample = plotUIState.yColumn,
            group = plotUIState.groupColumn,
            distribution = plotUIState.distributionType.code
        )
        ChartType.RESIDUAL -> if (plotUIState.xColumn != null && plotUIState.yColumn != null)
            residualPlot(
                dataset,
                x = plotUIState.xColumn,
                y = plotUIState.yColumn,
                method = plotUIState.fittingMethod.method,
                marginal = getMarginalText(plotUIState)
        ) else letsPlot(dataset) + geomPoint()
    }.run{
        if (
            plotUIState.showDistributionTR &&
            plotUIState.chartType in marginalPlotAllowed
            )
            this + ggmarginal(sides = "tr", layer =
                when(plotUIState.distributionTypeTR) {
                    ChartType.DENSITY -> geomDensity(alpha=0.25)
                    ChartType.BOX -> geomBoxplot(alpha = 0.25)
                    else -> geomHistogram(alpha=0.25) { }
                }) else this
    }.run{
        if (
            plotUIState.showDistributionLB &&
            plotUIState.chartType in marginalPlotAllowed
            )
            this + ggmarginal(sides = "lb", layer =
                when(plotUIState.distributionTypeLB) {
                    ChartType.DENSITY -> geomDensity(alpha=0.25)
                    ChartType.BOX -> geomBoxplot(alpha = 0.25)
                    else -> geomHistogram(alpha=0.25) { }
                }) else this
    } + facetGrid(
                x = plotUIState.facetXColumn.takeUnless { plotUIState.chartType == ChartType.WATERFALL },
                y = plotUIState.facetYColumn.takeUnless { plotUIState.chartType == ChartType.WATERFALL }
    ) + ggtitle(
        title = plotUIState.title,
        subtitle = plotUIState.subtitle
    ).run {
        plotUIState.xlab?.let { this + xlab(it) }?: this
    }.run {
        plotUIState.ylab?.let { this + ylab(it) }?: this
    } + when(plotUIState.theme) {
        ChartTheme.MINIMAL -> themeMinimal()
        ChartTheme.MINIMAL_2 -> themeMinimal2()
        ChartTheme.BLACK_WHITE -> themeBW()
        ChartTheme.GREY -> themeGrey()
        ChartTheme.CLASSIC -> themeClassic()
        ChartTheme.LIGHT -> themeLight()
        ChartTheme.VOID -> themeVoid()
        ChartTheme.NONE -> themeNone()

    } + when(plotUIState.flavor) {
        ChartFlavour.Darcula -> flavorDarcula()
        ChartFlavour.SOLARIZED_LIGHT -> flavorSolarizedLight()
        ChartFlavour.SOLARIZED_DARK -> flavorSolarizedDark()
        ChartFlavour.HIGH_CONTRAST_LIGHT -> flavorHighContrastLight()
        ChartFlavour.HIGH_CONTRAST_DARK -> flavorHighContrastDark()
    }

}

private fun getMarginalText(plotUIState: PlotUIState) : String {
    var marginal = ""
    if (plotUIState.showDistributionTR)
        marginal = plotUIState.distributionTypeTR.code + ":tr" + if (plotUIState.showDistributionLB) ", " else ""
    if (plotUIState.showDistributionLB)
        marginal += plotUIState.distributionTypeLB.code + ":lb"
    if (marginal.isEmpty()) return "none"

    return marginal
}