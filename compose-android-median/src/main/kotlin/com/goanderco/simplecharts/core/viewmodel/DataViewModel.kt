package com.goanderco.simplecharts.core.viewmodel

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.goanderco.simplecharts.core.util.DataFileType
import com.goanderco.simplecharts.core.util.addColumn
import com.goanderco.simplecharts.core.util.addRow
import com.goanderco.simplecharts.core.util.atLeastTwoNumericalColumnWithThreeNumericalValue
import com.goanderco.simplecharts.core.util.deleteColumn
import com.goanderco.simplecharts.core.util.deleteRow
import com.goanderco.simplecharts.core.util.editColumnName
import com.goanderco.simplecharts.core.util.makeRowColumnName
import kotlinx.coroutines.launch
import java.util.LinkedHashMap
import com.goanderco.simplecharts.core.util.smartCastFromString
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import com.goanderco.simplecharts.core.util.readDataFileFromUri
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.mapLatest
import org.jetbrains.letsPlot.geom.geomHistogram
import org.jetbrains.letsPlot.ggplot
import org.jetbrains.letsPlot.intern.Plot
import org.jetbrains.letsPlot.letsPlot
import org.jetbrains.letsPlot.themes.themeVoid
import org.jetbrains.letsPlot.tooltips.layerTooltips


private const val RECEIVED_DATA_URI_KEY = "RECEIVED_DATA_URI_KEY"
private const val DEBOUNCE_MILLIS: Long = 500

class DataViewModel(private val savedStateHandle: SavedStateHandle): ViewModel() {


    // Using MutableStateFlow for observable state
    private val _dataset = MutableStateFlow<Map<String, List<Any?>>>(
        value = emptyMap()
    )



    val columns = _dataset.map{ it.keys.toList() }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val dataSummary = _dataset.map{ dataset ->
        DataSummaryUIState(dataset = dataset)
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        DataSummaryUIState()
    )



    private val _showProgress = MutableStateFlow(false)
    val showProgress = _showProgress.asStateFlow()

    fun readDataFile(context: Context, dataFileType: DataFileType, uri: Uri, makeFirstRowColumn: Boolean) {

        viewModelScope.launch(Dispatchers.IO) {
            _showProgress.emit(true)
            try {
                readDataFileFromUri(context,dataFileType, uri, makeFirstRowColumn)?.
                let {  _dataset.emit(it) }
            }catch (e: Exception) {
                Log.e("read_file", "error: ${e}")
            }

            _showProgress.emit(false)
        }
    }

    fun setCellValue(
        key: String,
        index: Int,
        value: String
    ) {
         viewModelScope.launch(){

            val mutableDataset = _dataset.value.toMutableMap()

            val currentList = mutableDataset[key]
            if (currentList != null && index >= 0 && index < currentList.size) {
                val updatedList = currentList.toMutableList().apply {
                    val castedValue = smartCastFromString(value)
                    set(index, castedValue)
                }

                mutableDataset[key] = updatedList
                _dataset.emit(LinkedHashMap(mutableDataset))

            }
         }
    }



    fun addColumn(colName: String) {
        viewModelScope.launch() {
            _dataset.emit(_dataset.value.addColumn(colName))
        }
    }
    fun addColumn(colName: String, newColName: String, addToRight: Boolean, isIndex: Boolean) {
        viewModelScope.launch() {
            _dataset.emit(_dataset.value.addColumn(colName, newColName, addToRight, isIndex))
        }
    }



    fun deleteColumn(column: String) {
        viewModelScope.launch() {
            _dataset.emit(_dataset.value.deleteColumn(column))
        }
    }

    fun editColumnName(oldColumnName: String, newColumnName: String) {
        viewModelScope.launch {
            _dataset.emit(_dataset.value.editColumnName(oldColumnName, newColumnName))
        }
    }

    public fun makeRowColumn(index: Int, deleteRow: Boolean) {
        viewModelScope.launch {
            _dataset.emit(_dataset.value.makeRowColumnName(index, deleteSourceRow = deleteRow))
        }
    }

    public fun addRow() {
        if (_dataset.value.isEmpty())
            addColumn("Column 1")
        viewModelScope.launch() {
            _dataset.emit(_dataset.value.addRow())
        }
    }

    public fun addRow(index: Int) {
        viewModelScope.launch() {
            _dataset.emit(_dataset.value.addRow(index))
        }
    }

    public fun deleteRow(index: Int) {
        viewModelScope.launch() {
            _dataset.emit(_dataset.value.deleteRow(index))
        }
    }



    private val _selectedPlotIndex = MutableStateFlow(0)
    val selectedPlotIndex = _selectedPlotIndex.asStateFlow()

    public fun setSelectedPlotIndex(value: Int) {
        viewModelScope.launch {
            _selectedPlotIndex.emit(value)
        }

    }

    private val _plotUIStateList = MutableStateFlow(
        List(1) {  PlotUIState("Chart 1") }
    )

    val plotUIStateList = _plotUIStateList.combine(columns) { plotUIList, columns ->

        plotUIList.map { it.validateColumns(columns) }
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        List(1) {  PlotUIState("Chart 1") }
    )

    val currentPlotUIState:StateFlow<PlotUIState> =
        combine(plotUIStateList, selectedPlotIndex) { uiStateList, index ->
            uiStateList[index]
         }.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            PlotUIState("empty")
        )
    val currentPlotUIStateDebounced = currentPlotUIState
        .debounce(DEBOUNCE_MILLIS)
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            PlotUIState("empty")
        )
    val chartNames:StateFlow<List<String>> = plotUIStateList.map {
        it.map { it.chartName }
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        emptyList()
    )

    private var addPlotPageCounter = 2

    public fun addPlot(index: Int? = null) {
        viewModelScope.launch {
            val newPlotUIState = PlotUIState("Chart ${addPlotPageCounter}")
            addPlotPageCounter++
            val newPlotDataList = plotUIStateList.value.toMutableList()
            if (index == null)
                newPlotDataList.add(newPlotUIState)
            else
                newPlotDataList.add(index, newPlotUIState)
            _plotUIStateList.emit(newPlotDataList)
        }
    }

    public fun deletePlot(index: Int, lastItemError:() -> Unit) {
        viewModelScope.launch {
            if (plotUIStateList.value.size == 1)
                return@launch lastItemError()
            if (index == selectedPlotIndex.value && index != 0)
                _selectedPlotIndex.emit(selectedPlotIndex.value - 1)
            val newPlotDataList = plotUIStateList.value.toMutableList()
            newPlotDataList.removeAt(index)
            _plotUIStateList.emit(newPlotDataList)
        }
    }

    private fun changePlotUIState(index: Int? = null, change: (PlotUIState) -> PlotUIState) {
        viewModelScope.launch {
            val mutablePlotUIStateList = plotUIStateList.value.toMutableList()
            val selectedIndex = index ?: selectedPlotIndex.value
            val plotUI = mutablePlotUIStateList[selectedIndex]
            mutablePlotUIStateList[selectedIndex] = change(plotUI)
            _plotUIStateList.emit(mutablePlotUIStateList)
        }
    }

    private fun changeFilterUIState(index: Int, change: (FilterUIState) -> FilterUIState) {
        changePlotUIState {
            val mutableFilters = it.filterUIState.toMutableList()
            val filter = mutableFilters[index]
            mutableFilters[index] = change(filter)
            it.copy(filterUIState = mutableFilters)
        }
    }

    val filters:StateFlow<List<FilterUIState>> = currentPlotUIState.map {
        it.filterUIState
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        emptyList()
    )

    val dataset = _dataset.asStateFlow()


    public fun addFilter() {
        changePlotUIState { it.copy(filterUIState = it.filterUIState + FilterUIState()) }
    }
    public fun deleterFilter(index: Int) {
        changePlotUIState { it.copy(filterUIState = it.filterUIState.take(index) + it.filterUIState.drop(index + 1)) }
    }

    public fun setFilter(index: Int, filter: FilterUIState) {
        changeFilterUIState (index){ filter }
    }


    public fun setPlotPageName(index: Int, name:String) {
        changePlotUIState (index = index){ it.copy(chartName = name) }
    }
    public fun setChartType(chartType: ChartType) {
        changePlotUIState { it.copyWithChartType(chartType = chartType) }
    }
    public fun setChartStat(value: ChartStat) {
        changePlotUIState { it.copy(stat = value) }
    }
    public fun setDistributionType(value: DistributionType) {
        changePlotUIState { it.copy(distributionType = value) }
    }
    public fun setFittingMethod(value: FittingMethod) {
        changePlotUIState { it.copy(fittingMethod = value) }
    }
    public fun setXColumn(column: String?) {
        changePlotUIState{ it.copy(xColumn = column) }
    }
    fun setYColumn(column: String?) {
        changePlotUIState{ it.copy(yColumn = column) }
    }
    fun setYLowColumn(column: String?) {
        changePlotUIState{ it.copy(yLowColumn = column) }
    }
    fun setYOpenColumn(column: String?) {
        changePlotUIState{ it.copy(yOpenColumn = column) }
    }
    fun setYCloseColumn(column: String?) {
        changePlotUIState{ it.copy(yCloseColumn = column) }
    }
    fun setYHighColumn(column: String?) {
        changePlotUIState{ it.copy(yHighColumn = column) }
    }

    fun setSecondYColumn(column: String?) {
        changePlotUIState{ it.copy(secondYColumn = column) }
    }
    fun setSliceColumn(value: String?) {
        changePlotUIState{ it.copy(sliceColumn = value) }
    }
    fun setGroupColumn(column: String?) {
        changePlotUIState { it.copy(groupColumn = column) }
    }
    fun setColorColumn(column: String?) {
        changePlotUIState{ it.copy(colorColumn = column) }
    }
    fun setSizeColumn(column: String?) {
        changePlotUIState{ it.copy(sizeColumn = column) }
    }
    fun setTooltipColumns(columns: Collection<String>) {
        changePlotUIState { it.copy(tooltips = columns.toList()) }
    }
    fun setFacetXColumn(column: String?) {
        changePlotUIState{ it.copy(facetXColumn = column) }
    }
    fun setFacetYColumn(column: String?) {
        changePlotUIState{ it.copy(facetYColumn = column) }
    }
    fun setShowDistributionTR(value: Boolean) {
        changePlotUIState { it.copy(showDistributionTR = value) }
    }
    fun setDistributionChartTR(value: ChartType) {
        changePlotUIState { it.copy(distributionTypeTR = value) }
    }
    fun setShowDistributionLB(value: Boolean) {
        changePlotUIState { it.copy(showDistributionLB = value) }
    }
    fun setDistributionChartLB(value: ChartType) {
        changePlotUIState { it.copy(distributionTypeLB = value) }
    }
    fun setSmoothVisibility(value: Boolean) {
        changePlotUIState{ it.copy(showSmooth = value) }
    }
    fun setSmoothSEVisibility(value: Boolean) {
        changePlotUIState{ it.copy(showSmoothLineSE = value) }
    }

    fun setSmoothMethod(value: SmoothMethod) {
        changePlotUIState{ it.copy(smoothMethod = value) }
    }
    fun setSmoothPolynomialDegree(value: Int) {
        changePlotUIState { it.copy(smoothPolynomialDegree = value) }
    }
    fun setBoxPlot(value: Boolean) {
        changePlotUIState{ it.copy(addBoxPlot = value) }
    }

    public fun setPreserveAspectRatio(value:Boolean) {
        changePlotUIState{ it.copy(preserveAspectRatio = value) }
    }
    public fun setAspectRatio(value: Float) {
        changePlotUIState { it.copy(aspectRatio = value) }
    }
    public fun setTitle(title: String) {
        changePlotUIState{ it.copy(title = title) }
    }
    public fun setSubTitle(subtitle: String) {
        changePlotUIState{ it.copy(subtitle = subtitle.takeUnless(String::isEmpty)) }
    }
    public fun setXLab(xlab: String) {
        changePlotUIState{ it.copy(xlab = xlab.takeUnless(String::isEmpty)) }
    }
    public fun setYLab(ylab: String) {
        changePlotUIState{ it.copy(ylab = ylab.takeUnless(String::isEmpty)) }
    }

    public fun setTheme(value: ChartTheme) {
        changePlotUIState{ it.copy(theme = value) }
    }

    public fun setColorScheme(value: ChartFlavour) {
        changePlotUIState{ it.copy(flavor = value) }
    }

    private val filteredDataset = _dataset.combine(filters) { dataset, filters ->
        dataset.filterDataset(filters)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    val figure: StateFlow<Plot> = filteredDataset.combine(currentPlotUIStateDebounced){ dataset, uistate ->
        Pair(dataset, uistate)
    }.mapLatest { (dataset, uistate) ->
        createPlot(dataset, uistate)
    }.flowOn(Dispatchers.Default)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), ggplot() + geomHistogram())

    val summaryHistograms: StateFlow<List<Plot>> = _dataset.map { dataset ->
        val columns = dataset.keys.toList()
        List(columns.size) { index ->
            letsPlot(dataset) {
                x = columns[index]

            } + geomHistogram(
                tooltips = layerTooltips()
                    .format("@{..x..}", ".2f")
                    .format("@{..count..}", "{.0f}")
                    .title("@{..x..}")
                    .line("@{..count..}"),
                bins = 10,
                fill = "#FFDAD6"

            ){

            } + themeVoid()

        }

    }.flowOn(Dispatchers.Default)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val atLeastTwoNumericalColumnWithThreeNumericalValue = _dataset.map { dataset->
        dataset.atLeastTwoNumericalColumnWithThreeNumericalValue()
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)
}

