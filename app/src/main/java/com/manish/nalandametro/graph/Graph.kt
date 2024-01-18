package com.manish.nalandametro.graph

import com.google.android.gms.maps.model.LatLng
import com.manish.nalandametro.data.model.GraphData
import com.manish.nalandametro.data.model.Station
import com.manish.nalandametro.utils.Resource

interface Graph {
    fun setGraphData(graphData: GraphData)
    fun addStation(name: String, stationId: String, latLng: LatLng)
    fun containsStation(name: String): Boolean
    fun getStationLocation(name:String):LatLng?
    fun addRoute(from: String, to: String, distance: Float, cost: Int): Boolean

    fun getRoute(
        from: String,
        to: String,
        pathType: MetroGraph.PathType
    ): Resource<CalculatedPath>

    fun getAvailableStationsCount(): Int
    fun getGraphData(): GraphData
    fun getStationsNamesList():List<String>
}