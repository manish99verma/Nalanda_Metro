package com.manish.nalandametro.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.manish.nalandametro.data.model.GraphData
import com.manish.nalandametro.data.model.MapPoint
import com.manish.nalandametro.data.repository.Repository
import com.manish.nalandametro.graph.CalculatedPath
import com.manish.nalandametro.graph.Graph
import com.manish.nalandametro.graph.MetroGraph
import com.manish.nalandametro.graph.TestData
import com.manish.nalandametro.utils.CustomEvent
import com.manish.nalandametro.utils.Event
import com.manish.nalandametro.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(val repository: Repository) : ViewModel() {
    private var metroGraph: Graph? = null

    private val _getShortestPath = MutableLiveData<Event<Resource<CalculatedPath>>>()
    fun getShortestPathResult() = _getShortestPath as LiveData<Event<Resource<CalculatedPath>>>

    private val _getCheapestPath = MutableLiveData<Event<Resource<CalculatedPath>>>()
    fun getCheapestPathResult() = _getCheapestPath as LiveData<Event<Resource<CalculatedPath>>>

    private val _getPathWithLowestStops = MutableLiveData<Event<Resource<CalculatedPath>>>()
    fun getPathWithLowestStopsResult() =
        _getPathWithLowestStops as LiveData<Event<Resource<CalculatedPath>>>

    private val _filterStationsResult = MutableLiveData<Event<List<String>>>()
    fun getFilterStationResult() = _filterStationsResult as LiveData<Event<List<String>>>

    fun updateWithCustomDataResult() = repository.updateGraphDataResult()
    fun updateWithTestData() {
        val data = TestData.getGraphData()
        repository.updateData(data)
    }

    fun getGraphDataResult() = repository.getGraphDataResult()
    fun getGraphDataFromWeb() {
        repository.getGraphData()
    }

    fun setUpGraph(graphData: GraphData) {
        metroGraph = MetroGraph(graphData)
    }

    fun getCurrGraphData() = metroGraph?.getGraphData()

    fun getShortestPath(from: String, to: String) {
        getPath(_getShortestPath, from, to, MetroGraph.PathType.TIME_SAVER_PATH)
    }

    fun getCheapestPath(from: String, to: String) {
        getPath(_getCheapestPath, from, to, MetroGraph.PathType.CHEAPEST_PATH)
    }

    fun getPathWithLowestStops(from: String, to: String) {
        getPath(_getPathWithLowestStops, from, to, MetroGraph.PathType.LOWEST_STOP_PATHS)
    }

    private fun getPath(
        liveData: MutableLiveData<Event<Resource<CalculatedPath>>>,
        from: String,
        to: String,
        pathType: MetroGraph.PathType
    ) {
        if (from.isEmpty())
            liveData.postValue(
                Event(
                    Resource.error(
                        null,
                        "Start station cant' be empty"
                    )
                )
            )

        if (to.isEmpty())
            liveData.postValue(
                Event(
                    Resource.error(
                        null,
                        "End station cant' be empty"
                    )
                )
            )

        viewModelScope.launch {
            val result = metroGraph?.getRoute(from, to, pathType) ?: return@launch
            liveData.postValue(CustomEvent(result, false))
        }
    }

    fun getCurrStationsCount() = metroGraph?.getAvailableStationsCount()
    fun getCurrStationsList() = metroGraph?.getStationsNamesList()

    fun filterStations(query: String, withDelay: Boolean, limitToTop: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            if (withDelay)
                Thread.sleep(500)

            val result = metroGraph?.filterCities(query, limitToTop) ?: emptyList()
            _filterStationsResult.postValue(Event(result))
        }
    }

    fun getStationLocation(station: String): MapPoint? {
        return metroGraph?.getStationLocation(station)
    }
}