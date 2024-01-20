package com.manish.nalandametro.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.manish.nalandametro.data.model.GraphData
import com.manish.nalandametro.data.model.MapPoint
import com.manish.nalandametro.data.repository.Repository
import com.manish.nalandametro.graph.Graph
import com.manish.nalandametro.graph.MetroGraph
import com.manish.nalandametro.graph.TestData
import com.manish.nalandametro.utils.Event
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(val repository: Repository) : ViewModel() {
    private var metroGraph: Graph? = null

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