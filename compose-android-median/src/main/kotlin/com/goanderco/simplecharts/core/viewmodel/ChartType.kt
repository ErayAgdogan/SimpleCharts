package com.goanderco.simplecharts.core.viewmodel

import com.goanderco.R
import com.goanderco.simplecharts.core.viewmodel.ChartType.*
import java.util.EnumSet

enum class ChartType(val stringResourceID: Int, val descriptionID: Int, val iconResourceID: Int, val code: String="none") {
    HISTOGRAM(R.string.histogram, R.string.histogram_explanation, R.drawable.baseline_bar_chart_24, code="histogram"),
    SCATTER(R.string.scatter, R.string.scatter_explanation, R.drawable.baseline_scatter_plot_24),
    LINE(R.string.line, R.string.line_explanation, R.drawable.baseline_stacked_line_chart_24),
    BAR(R.string.bar, R.string.bar_explanation, R.drawable.baseline_stacked_bar_chart_24),
    PIE(R.string.pie, R.string.pie_explanation, R.drawable.baseline_pie_chart_24),
    BOX(R.string.box, R.string.box_explanation, R.drawable.baseline_boxplot_chart_24, code="boxplot"),
    DENSITY(R.string.density, R.string.density_explanation, R.drawable.baseline_density_chart, code="density"), // replace the icon
    RIDGE_LINE(R.string.ridgeline, R.string.ridgeline_explanation, R.drawable.baseline_ridge_chart),
    VIOLIN(R.string.violin, R.string.violin_explanation, R.drawable.baseline_violin_chart),
    BEESWARM(R.string.beeswarm, R.string.beeswarm_explanation, R.drawable.baseline_jitter_plot_24),
    JITTER(R.string.jitter, R.string.jitter_explanation, R.drawable.baseline_jitter_plot_24),
    AREA(R.string.area, R.string.area_explanation, R.drawable.baseline_area_chart_24),

    TWO_D_BIN(R.string.two_d_bin, R.string.two_d_bin_explanation, R.drawable.baseline_bind_two_d_chart_24),
    TWO_D_DENSITY(R.string.two_d_density, R.string.two_d_density_explanation, R.drawable.baseline_density_two_d_chart),

    CANDLESTICK(R.string.candle_stick, R.string.candle_stick_description, R.drawable.baseline_candlestick_chart_24),
    WATERFALL(R.string.waterfall, R.string.waterfall_explanation, R.drawable.baseline_waterfall_chart_24),
    QQPLOT(R.string.qqplot, R.string.qqplotdescription, R.drawable.baseline_qq_plot_24),
    RESIDUAL(R.string.residual, R.string.residual_description, R.drawable.baseline_residual_plot_24)
}

val EXCLUDE_X_COLUMN = EnumSet.of(PIE, QQPLOT)
val EXCLUDE_Y_COLUMN = EnumSet.of(PIE, CANDLESTICK)
val EXCLUDE_GROUP_COLUMN = EnumSet.of(TWO_D_BIN, TWO_D_DENSITY, CANDLESTICK, RESIDUAL)
val EXCLUDE_COLOR_COLUMN = EnumSet.of(WATERFALL, TWO_D_BIN, TWO_D_DENSITY, CANDLESTICK, QQPLOT, RESIDUAL)
val INCLUDE_SIZE_COLUMN = EnumSet.of(SCATTER, JITTER)
val EXCLUDE_TOOLTIP_COLUMNS =  EnumSet.of(WATERFALL, RESIDUAL, QQPLOT)

val INCLUDE_MARGINAL_PLOT = EnumSet.of(SCATTER, TWO_D_BIN, TWO_D_DENSITY, RESIDUAL)

