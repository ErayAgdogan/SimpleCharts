package com.goanderco.simplecharts.core.viewmodel

import com.goanderco.R
import com.goanderco.simplecharts.core.viewmodel.ChartType
import org.jetbrains.letsPlot.Stat
import org.jetbrains.letsPlot.intern.layer.StatOptions

enum class ChartStat (val stat: StatOptions, val stringResID: Int){
    IDENTITY(Stat.identity, R.string.identity),
    BIN(Stat.bin(), R.string.bin),
    COUNT(Stat.count(), R.string.count),
    SMOOTH(Stat.smooth(), R.string.smooth),
    DENSITY(Stat.density(), R.string.density),
    SUM(Stat.sum() , R.string.sum),
    COUNT2D(Stat.count2d() , R.string.count2d),
    DENSITY_RIDGES(Stat.densityRidges(), R.string.density_ridges);

    companion object {
        val chartStatMap = mapOf(
            ChartType.HISTOGRAM to listOf(BIN, IDENTITY, COUNT, SMOOTH, DENSITY),
            ChartType.SCATTER to listOf(IDENTITY, COUNT, BIN, SMOOTH, DENSITY, SUM),
            ChartType.LINE to listOf(IDENTITY, COUNT, BIN, SMOOTH, DENSITY),
            ChartType.BAR to listOf(COUNT, IDENTITY, BIN, SMOOTH, DENSITY),
            ChartType.PIE to listOf(COUNT2D, IDENTITY),
            ChartType.DENSITY to listOf(DENSITY, IDENTITY, COUNT, BIN, SMOOTH),
            ChartType.RIDGE_LINE to listOf(DENSITY_RIDGES, IDENTITY),
            ChartType.JITTER to listOf(IDENTITY, COUNT, BIN, SMOOTH, DENSITY),
            ChartType.AREA to listOf(IDENTITY, COUNT, BIN, SMOOTH, DENSITY)

        )
    }
}

