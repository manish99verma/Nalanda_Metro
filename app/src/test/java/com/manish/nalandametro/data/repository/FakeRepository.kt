package com.manish.nalandametro.data.repository

import com.manish.nalandametro.data.model.GraphData
import com.manish.nalandametro.data.model.Route
import com.manish.nalandametro.data.model.Station
import com.manish.nalandametro.utils.Resource

class FakeRepository : Repository {
    private lateinit var graphData: GraphData
    private var connectedToInternet = true

    private fun setInternetConnection(isConnected: Boolean) {
        connectedToInternet = isConnected
    }

    override suspend fun getGraphData(): Resource<GraphData> {
        return if (connectedToInternet)
            Resource.success(graphData)
        else
            Resource.error(null, "Please check your internet connection!")
    }

    override suspend fun updateData(graphData: GraphData): Resource<GraphData> {
        return if (connectedToInternet) {
            this.graphData = graphData
            Resource.success(graphData)
        } else {
            Resource.error(null, "Please check your internet connection!")
        }
    }

    override suspend fun addStation(
        name: String,
        station: Station
    ): Resource<GraphData> {
        if (!connectedToInternet) {
            return Resource.error(null, "Please check your internet connection!")
        }

        graphData.map[name] = station
        return Resource.success(graphData)
    }

    override suspend fun addRoute(
        from: String,
        to: String,
        route: Route
    ): Resource<GraphData> {
        if (!connectedToInternet)
            return Resource.error(null, "Please check your internet connection!")

        if (!graphData.map.containsKey(from)) {
            return Resource.error(graphData, "$from is not in our Station List!")
        }

        if (!graphData.map.containsKey(to)) {
            return Resource.error(graphData, "$to is not in our Station List!")
        }

        graphData.map[from]?.routes?.set(to, route)
        graphData.map[to]?.routes?.set(from, route)

        return Resource.success(graphData)
    }
}
