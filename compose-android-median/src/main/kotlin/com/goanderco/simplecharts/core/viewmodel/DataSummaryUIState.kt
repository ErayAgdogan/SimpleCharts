package com.goanderco.simplecharts.core.viewmodel

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import com.goanderco.simplecharts.core.util.calculateMax
import com.goanderco.simplecharts.core.util.calculateMean
import com.goanderco.simplecharts.core.util.calculateMedian
import com.goanderco.simplecharts.core.util.calculateMin
import com.goanderco.simplecharts.core.util.calculateQ1
import com.goanderco.simplecharts.core.util.calculateQ3
import com.goanderco.simplecharts.core.util.calculateStandardDeviation
import com.goanderco.simplecharts.core.util.countUniqueNotNull
import com.goanderco.simplecharts.core.util.frequency
import com.goanderco.simplecharts.core.util.frequent
import java.text.DecimalFormat


@Immutable
@Stable
data class DataSummaryUIState(
    val nullCounts: List<Int> = emptyList(),
    val distinctCounts: List<Int> = emptyList(),
    val frequent: List<Any?> = emptyList(),
    val frequency: List<Int?> = emptyList(),
    val mean: List<String?> = emptyList(),
    val std: List<String?> = emptyList(),
    val min: List<String?> = emptyList(),
    val Q1: List<String?> = emptyList(),
    val median: List<String?> = emptyList(),
    val Q3: List<String?> = emptyList(),
    val max: List<String?> = emptyList()
    ) {
    constructor(dataset: Map<String, List<Any?>>) :
            this(
                nullCounts = dataset.values.map { it.count { it == null } },
                distinctCounts = dataset.values.map { it.countUniqueNotNull() },
                frequent = dataset.values.map { it.frequent() },
                frequency = dataset.values.map{ it.frequency() },
                mean = dataset.values.map {  it.calculateMean()?.let(DecimalFormat("#.###")::format) },
                std = dataset.values.map { it.calculateStandardDeviation()?.let(DecimalFormat("#.###")::format) },
                min = dataset.values.map { it.calculateMin()?.let(DecimalFormat("#.###")::format) },
                Q1 = dataset.values.map { it.calculateQ1()?.let(DecimalFormat("#.###")::format) },
                median = dataset.values.map { it.calculateMedian()?.let(DecimalFormat("#.###")::format) },
                Q3 = dataset.values.map { it.calculateQ3()?.let(DecimalFormat("#.###")::format) },
                max = dataset.values.map { it.calculateMax()?.let(DecimalFormat("#.###")::format) },
            )
}
