package com.manish.nalandametro.graph

import android.util.Log
import com.manish.nalandametro.data.model.GraphData
import com.manish.nalandametro.data.model.MapPoint
import com.manish.nalandametro.utils.Utils.distance
import java.util.Random
import java.util.UUID

object TestData {
    fun getGraphData(): GraphData {
        //Add Stations
        val graph = MetroGraph(GraphData())
        graph.addStation("Giriak", "GR", MapPoint(25.027308, 85.528208))
        graph.addStation("Raitar", "RT", MapPoint(25.057803, 85.534563))
        graph.addStation("Pawapuri", "PW", MapPoint(25.089721, 85.534828))
        graph.addStation("Nanand", "ND", MapPoint(25.090557, 85.498655))
        graph.addStation("Bakra", "BK", MapPoint(25.111196, 85.524072))
        graph.addStation("Bihar Sharif", "BR", MapPoint(25.178780, 85.513405))
        graph.addStation("Rajgir", "RJ", MapPoint(25.025345, 85.418591))
        graph.addStation("Sithaura", "ST", MapPoint(25.038227, 85.477538))
        graph.addStation("Nalanda", "ND", MapPoint(25.126588, 85.460848))
        graph.addStation("Silao", "SI", MapPoint(25.077123, 85.427912))

        //Add Random routes
        createRandomStations(graph)

        return graph.getGraphData()
    }

    private fun createRandomStations(graph: Graph) {
        val random = Random()
        val stations = graph.getStationsNamesList()

        val n = stations.size
        Log.d("TAGY", "createRandomStations: $stations")

        var count = 0
        for (x in 0 until n) {
            val st1 = stations[x]

            val randomCount = 2 + random.nextInt(2)
            for (st2 in graph.getNearestStations(st1,randomCount)) {
                Log.d("TAGY", "route: ${++count} $st1,$st2")

                val dis = graph.getStationLocation(st1)?.distance(graph.getStationLocation(st2)!!)!!
                val disInKm: Int = (dis / 1000f).toInt()
                val rate = 1 + random.nextInt(2)
                val cost = rate * disInKm

                graph.addRoute(st1, st2, dis, cost)
            }
        }
    }

    fun getRandomId() = UUID.randomUUID().toString()
}