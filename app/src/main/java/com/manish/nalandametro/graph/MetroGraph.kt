package com.manish.nalandametro.graph

import com.google.android.gms.maps.model.LatLng
import com.manish.nalandametro.data.model.GraphData
import com.manish.nalandametro.data.model.Route
import com.manish.nalandametro.data.model.Station
import com.manish.nalandametro.utils.Resource
import com.manish.nalandametro.utils.Utils
import java.util.PriorityQueue

class MetroGraph(private var graphData: GraphData) : Graph {
    data class Node(
        val dest: String,
        val distance: Float,
        val cost: Int,
        val stops: Int
    )

    override fun setGraphData(graphData: GraphData) {
        this.graphData = graphData
    }

    override fun addStation(name: String, stationId: String, latLng: LatLng) {
        if (graphData.map == null)
            return

        if (graphData.map!!.containsKey(name)) {
            val station = graphData.map!![name]!!
            station.stationId = stationId
            station.latLng = latLng
        } else {
            graphData.map!![name] =
                Station(mutableMapOf(), stationId, latLng, Utils.randomStringId())
        }
    }

    override fun containsStation(name: String) = graphData.map?.containsKey(name)!!
    override fun getStationLocation(name: String): LatLng? {
        if (!graphData.map?.containsKey(name)!!)
            return null

        return graphData.map!![name]?.latLng
    }

    override fun addRoute(from: String, to: String, distance: Float, cost: Int): Boolean {
        if (!graphData.map?.containsKey(from)!! || !graphData.map?.containsKey(to)!! || from == to)
            return false

        val station1 = graphData.map!![from]!!
        val station2 = graphData.map!![to]!!

        station1.routes?.set(to, Route(Utils.randomStringId(), cost, distance))
        station2.routes?.set(from, Route(Utils.randomStringId(), cost, distance))

        return true
    }

    override fun getRoute(
        from: String,
        to: String,
        pathType: PathType
    ): Resource<CalculatedPath> {
        if (!graphData.map?.containsKey(from)!!)
            return Resource.error(null, "Station not found: $from")
        if (!graphData.map?.containsKey(to)!!)
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
                Comparator.comparingDouble { n -> n.distance.toDouble() })
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
        val path = mutableListOf<String>()

        val visited = mutableMapOf<String, Int>()
        val queue = PriorityQueue(comparator)

        for (pair in graphData.map!!) {
            visited[pair.key] = Int.MAX_VALUE
        }

        visited[from] = 0
        queue.add(Node(from, 0f, 0, 0))

        while (!queue.isEmpty()) {
            val curr = queue.poll() ?: continue

            if (curr.dest == to) {
                path.add(to)
                return CalculatedPath(
                    pathType,
                    path,
                    curr.distance,
                    curr.cost,
                    curr.stops
                )
            }

            if (!visited.containsKey(curr.dest))
                continue

            visited.remove(curr.dest)
            path.add(curr.dest)

            val routes = graphData.map!![curr.dest]?.routes ?: continue

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
                        curr.stops + 1
                    )
                )
            }
        }

        return null
    }

    override fun getAvailableStationsCount(): Int {
        return graphData.map?.size ?: 0
    }

    override fun getGraphData(): GraphData {
        return graphData
    }

    override fun getStationsNamesList(): List<String> {
        return graphData.map?.keys?.toList() ?: emptyList()
    }

    enum class PathType {
        CHEAPEST_PATH,
        TIME_SAVER_PATH,
        LOWEST_STOP_PATHS,
    }
}