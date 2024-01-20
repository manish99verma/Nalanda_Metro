package com.manish.nalandametro.graph

import com.manish.nalandametro.data.model.GraphData
import com.manish.nalandametro.data.model.MapPoint
import com.manish.nalandametro.data.model.Route
import com.manish.nalandametro.data.model.Station
import com.manish.nalandametro.utils.Resource
import com.manish.nalandametro.utils.Utils
import com.manish.nalandametro.utils.Utils.distance
import java.util.PriorityQueue

class MetroGraph(private var data: GraphData) : Graph {
    private var citySearchManager: CitySearchManager? = null

    data class Node(
        val dest: String,
        val distance: Double,
        val cost: Int,
        val stops: Int,
        val prev: Node? = null
    )

    override fun setGraphData(data: GraphData) {
        this.data = data
    }

    override fun addStation(name: String, stationId: String, mapPoint: MapPoint) {
        val map = data.map

        if (map.containsKey(name)) {
            val station = map[name]!!
            station.stationId = stationId
            station.mapPoint = mapPoint
        } else {
            map[name] =
                Station(mutableMapOf(), stationId, mapPoint, Utils.randomStringId())
        }
    }

    override fun containsStation(name: String) = data.map.containsKey(name)
    override fun getStationLocation(name: String): MapPoint? {
        return data.map[name]?.mapPoint
    }

    override fun addRoute(from: String, to: String, distance: Double, cost: Int): Boolean {
        val map = data.map

        if (!map.containsKey(from) || !map.containsKey(to) || from == to)
            return false

        val station1 = map[from]!!
        val station2 = map[to]!!

        station1.routes?.set(to, Route(Utils.randomStringId(), cost, distance))
        station2.routes?.set(from, Route(Utils.randomStringId(), cost, distance))

        return true
    }

    override fun getRoute(
        from: String,
        to: String,
        pathType: PathType
    ): Resource<CalculatedPath> {
        val map = data.map

        if (!map.containsKey(from))
            return Resource.error(null, "Station not found: $from")
        if (!map.containsKey(to))
            return Resource.error(null, "Station not found: $to")
        if (from == to)
            return Resource.error(null, "Start station can not be end station!")

        //Cheapest Path
        if (pathType == PathType.CHEAPEST_PATH) {
            val path = pathCalculator(from, to, pathType, Comparator.comparingInt { n -> n.cost })
                ?: return Resource.error(null, "Station $from and $to is not connected")
            return Resource.success(path)
        }

        //Time saver path
        if (pathType == PathType.TIME_SAVER_PATH) {
            val path = pathCalculator(
                from,
                to,
                pathType,
                Comparator.comparingDouble { n -> n.distance })
                ?: return Resource.error(null, "Station $from and $to is not connected")
            return Resource.success(path)
        }

        //Lowest price path
        if (pathType == PathType.LOWEST_STOP_PATHS) {
            val path = pathCalculator(from, to, pathType, object : Comparator<Node> {
                override fun compare(o1: Node?, o2: Node?): Int {
                    if (o1 == null || o2 == null)
                        return -1
                    if (o1.stops != o2.stops)
                        return o1.stops - o2.stops
                    return o1.cost - o2.cost
                }
            }) ?: return Resource.error(null, "Station $from and $to is not connected")
            return Resource.success(path)
        }

        return Resource.error(null, "Something went wrong while getting path!")
    }

    private fun pathCalculator(
        from: String,
        to: String,
        pathType: PathType,
        comparator: Comparator<Node>
    ): CalculatedPath? {
        val map = data.map

        val visited = mutableMapOf<String, Int>()
        val queue = PriorityQueue(comparator)

        for (pair in map) {
            visited[pair.key] = Int.MAX_VALUE
        }

        visited[from] = 0
        queue.add(Node(from, 0.0, 0, 0))

        while (!queue.isEmpty()) {
            val curr = queue.poll() ?: continue

            if (curr.dest == to) {
                val pathList = mutableListOf<String>()
                var p: Node? = curr
                while (p != null) {
                    pathList.add(p.dest)
                    p = p.prev
                }

                return CalculatedPath(
                    pathType,
                    pathList.reversed(),
                    curr.distance,
                    curr.cost,
                    curr.stops
                )
            }

            if (!visited.containsKey(curr.dest))
                continue

            visited.remove(curr.dest)

            val routes = map[curr.dest]?.routes ?: continue

            for (r in routes) {
                val oldCost = visited.getOrDefault(r.key, 0)
                if (curr.cost + r.value.cost!! >= oldCost)
                    continue

                val route = r.value
                queue.add(
                    Node(
                        r.key,
                        curr.distance + route.distance!!,
                        curr.cost + route.cost!!,
                        curr.stops + 1,
                        curr
                    )
                )
            }
        }

        return null
    }

    override fun getAvailableStationsCount(): Int {
        return data.map.size
    }

    override fun getGraphData(): GraphData {
        return data
    }

    override fun getStationsNamesList(): List<String> {
        return data.map.keys.toList()
    }

    override fun getNearestStations(from: String, count: Int): List<String> {
        data class Node(val name: String, val dist: Double)

        val point1 = data.map[from]?.mapPoint ?: return emptyList()
        val queue = PriorityQueue(count, object : Comparator<Node> {
            override fun compare(o1: Node, o2: Node): Int {
                if (o2.dist < o1.dist)
                    return -1
                return 1
            }
        })

        data.map.forEach {
            if (it.key != from) {
                val point2 = it.value.mapPoint
                if (point2 != null) {
                    queue.add(Node(it.key, point1.distance(point2)))
                    if (queue.size > count)
                        queue.poll()
                }
            }
        }

        return queue.toList().reversed().map { it.name }
    }

    override fun filterCities(query: String, limitToTop: Int): List<String> {
        if (citySearchManager == null)
            citySearchManager = CitySearchManager(data.map.keys.toList())

        return citySearchManager?.filterCities(query, limitToTop) ?: emptyList()
    }

    override fun getStationSymbol(station: String): String? {
        return data.map[station]?.stationId
    }

    enum class PathType {
        CHEAPEST_PATH,
        TIME_SAVER_PATH,
        LOWEST_STOP_PATHS,
    }
}