package com.goanderco.simplecharts.core.util

import org.jetbrains.letsPlot.geom.geomCrossbar
import org.jetbrains.letsPlot.geom.geomLineRange
import org.jetbrains.letsPlot.geom.geomRect
import org.jetbrains.letsPlot.geom.geomSegment
import org.jetbrains.letsPlot.intern.Plot
import org.jetbrains.letsPlot.letsPlot
import org.jetbrains.letsPlot.scale.guides
import org.jetbrains.letsPlot.scale.scaleColorManual
import org.jetbrains.letsPlot.scale.scaleFillManual
import org.jetbrains.letsPlot.scale.scaleXContinuous
import org.jetbrains.letsPlot.tooltips.layerTooltips
import java.util.UUID

fun geomCandleStick(dataset: Map<String, List<Any?>>,
                    xColumn: String?,
                     yLowColumn: String?,
                     yHighColumn: String?,
                     yOpenColumn: String?,
                     yCloseColumn: String?,
                    tooltipsColumns: List<String>
): Plot {
    val enhancedDataset = dataset.toMutableMap()
    val candleColorColumn = UUID.randomUUID().toString()
    // Create enhanced dataset with width offsets
    val useCrossbar = yOpenColumn != null && yCloseColumn != null
    if (useCrossbar) {
        // Add colors
        val opens = dataset.getOrDefault(yOpenColumn, emptyList())
        val closes = dataset.getOrDefault(yCloseColumn, emptyList())
        enhancedDataset[candleColorColumn] = opens.zip(closes) { open, close ->
            try {
                if (close.toString().toDouble() < open.toString().toDouble()) "#F8766D"
                else  "#00BA38"
            }catch (e: Exception) {
                "#00BA38"
            }
        }
    }


    return letsPlot(enhancedDataset) {
    } + geomLineRange( tooltips = layerTooltips(*tooltipsColumns.toTypedArray())) {
        x = xColumn
        ymin = yLowColumn
        ymax = yHighColumn
    }.run {
        if (useCrossbar)
            this + geomCrossbar (
                tooltips = layerTooltips(*tooltipsColumns.toTypedArray()),
                size = 0.5,
                width = 0.9
            ){
                x = xColumn
                ymin = yOpenColumn
                ymax = yCloseColumn
                fill = candleColorColumn
                color = candleColorColumn
            } + scaleFillManual(
                values = mapOf("#F8766D" to "#F8766D", "#00BA38" to "#00BA38")
            ) + scaleColorManual(
                values = mapOf("#F8766D" to "#F8766D", "#00BA38" to "#00BA38")
            ) + guides(fill = "none", color = "none")

        else
            this
    }
}
