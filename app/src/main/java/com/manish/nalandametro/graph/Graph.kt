package com.manish.nalandametro.graph

import com.manish.nalandametro.data.model.GraphData
import com.manish.nalandametro.data.model.MapPoint
import com.manish.nalandametro.data.model.Station
import com.manish.nalandametro.utils.Resource

interface Graph {
    fun setGraphData(data: GraphData)
    fun addStation(name: String, stationId: String, mapPoint: MapPoint)
    fun containsStation(name: String): Boolean
    fun getStationLocation(name: String): MapPoint?
    fun addRoute(from: String, to: String, distance: Double, cost: Int): Boolean

    fun getRoute(
        from: String,
        to: String,
        pathType: MetroGraph.PathType
    ): Resource<CalculatedPath>

    fun getAvailableStationsCount(): Int
    fun getGraphData(): GraphData
    fun getStationsNamesList(): List<String>

    fun getNearestStations(from: String, count: Int): List<String>

    fun filterCities(query: String, limitToTop: Int): List<String>

    fun getStationSymbol(station:String):String?
}