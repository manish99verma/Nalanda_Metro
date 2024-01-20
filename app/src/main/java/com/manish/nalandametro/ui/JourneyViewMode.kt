package com.manish.nalandametro.ui

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.manish.nalandametro.data.model.GraphData
import com.manish.nalandametro.graph.CalculatedPath
import com.manish.nalandametro.graph.Graph
import com.manish.nalandametro.graph.MetroGraph
import com.manish.nalandametro.utils.Event
import com.manish.nalandametro.utils.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class JourneyViewMode() : ViewModel() {
    private var metroGraph: Graph? = null

    private val _filterStationsResult = MutableLiveData<Event<List<String>>>()
    fun getFilterStationResult() = _filterStationsResult as LiveData<Event<List<String>>>

    private val _getShortestPath = MutableLiveData<Event<Resource<CalculatedPath>>>()
    fun getShortestPathResult(): LiveData<Event<Resource<CalculatedPath>>> = _getShortestPath

    private val _getCheapestPath = MutableLiveData<Event<Resource<CalculatedPath>>>()
    fun getCheapestPathResult(): LiveData<Event<Resource<CalculatedPath>>> = _getCheapestPath

    private val _getPathWithLowestStops = MutableLiveData<Event<Resource<CalculatedPath>>>()
    fun getPathWithLowestStopsResult() =
        _getPathWithLowestStops as LiveData<Event<Resource<CalculatedPath>>>

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
            if (metroGraph == null) {
                liveData.postValue(Event(Resource.error(null, "Data not found!")))
            } else {
                val result = metroGraph!!.getRoute(from, to, pathType)
                liveData.postValue(Event(result))
            }
        }
    }

    fun convertPathToString(list: List<String>?): String {
        if (list.isNullOrEmpty())
            return ""

        return buildString {
            for (i in list.indices) {
                val symbol = metroGraph?.getStationSymbol(list[i]) ?: continue
                append(list[i])
                append('(')
                append(symbol)
                append(')')

                if (i != list.lastIndex)
                    append(" - ")
            }
        }
    }

    fun setUpGraph(graphData: GraphData) {
        viewModelScope.launch(Dispatchers.IO) {
            metroGraph = MetroGraph(graphData)
        }
    }

    fun filterStations(query: String, withDelay: Boolean, limitToTop: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            if (withDelay)
                Thread.sleep(500)

            val result = metroGraph?.filterCities(query, limitToTop) ?: emptyList()
            _filterStationsResult.postValue(Event(result))
        }
    }
}