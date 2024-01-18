package com.manish.nalandametro.graph

import android.util.Log
import com.google.android.gms.maps.model.LatLng
import com.manish.nalandametro.data.model.GraphData
import com.manish.nalandametro.data.model.Station
import com.manish.nalandametro.utils.Utils.distance
import java.util.Random
import java.util.UUID

object TestData {
    fun getGraphData(): GraphData {
        //Add Stations
        val graph = MetroGraph(GraphData())
        graph.addStation("Giriak", "GR", LatLng(25.027308, 85.528208))
        graph.addStation("Raitar", "RT", LatLng(25.057803, 85.534563))
        graph.addStation("Pawapuri", "PW", LatLng(25.089721, 85.534828))
        graph.addStation("Nanand", "ND", LatLng(25.090557, 85.498655))
        graph.addStation("Bakra", "BK", LatLng(25.111196, 85.524072))
        graph.addStation("Bihar Sharif", "BR", LatLng(25.178780, 85.513405))
        graph.addStation("Rajgir", "RJ", LatLng(25.025345, 85.418591))
        graph.addStation("Sithaura", "ST", LatLng(25.038227, 85.477538))
        graph.addStation("Nalanda", "ND", LatLng(25.126588, 85.460848))
        graph.addStation("Silao", "SI", LatLng(25.077123, 85.427912))

        //Add Random routes
        createRandomStations(graph)

        return graph.getGraphData()
    }

    private fun createRandomStations(graph: Graph) {
        val random = Random()
        val stations = graph.getGraphData().map?.keys?.toList() ?: return

        val n = stations.size
        val routesLen = 2 * n + random.nextInt(n)
        Log.d("TAGY", "createRandomStations: $stations")

        for (x in 1..routesLen) {
            val st1 = stations[random.nextInt(n)]
            val st2 = stations[random.nextInt(n)]

            Log.d("TAGY", "route: $x/$routesLen $st1,$st2")

            val dis = graph.getStationLocation(st1)?.distance(graph.getStationLocation(st2)!!)!!
            val disInKm: Int = (dis / 1000f).toInt()
            val rate = 1 + random.nextInt(2)
            val cost = rate * disInKm

            graph.addRoute(st1, st2, dis, cost)
        }
    }

    fun getRandomId() = UUID.randomUUID().toString()
}