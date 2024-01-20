package com.manish.nalandametro.graph

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.google.android.gms.maps.model.MapPoint
import com.google.common.truth.Truth.assertThat
import com.manish.nalandametro.data.model.Station
import com.manish.nalandametro.utils.Resource
import org.junit.Rule
import org.junit.Test
import java.util.UUID

class MetroGraphTest() {
    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    @Test
    fun `create with empty graph data returns empty`() {
        val graphData = GraphData(mutableMapOf())
        val graph = MetroGraph(graphData)

        val data = graph.getGraphData()
        assertThat(data.map).isEmpty()
    }

    @Test
    fun `create with correct data data returns true`() {
        val st1 = Station(
            mutableMapOf(),
            "S1",
            MapPoint(25.027308, 85.528208),
            UUID.randomUUID().toString()
        )
        val st2 = Station(
            mutableMapOf(),
            "S2",
            MapPoint(25.028308, 85.528208),
            UUID.randomUUID().toString()
        )
        val st3 = Station(
            mutableMapOf(),
            "S3",
            MapPoint(25.029308, 85.528208),
            UUID.randomUUID().toString()
        )

        val graphData = GraphData(mutableMapOf("Giriak" to st1, "Raitar" to st2, "Pawapuri" to st3))
        val graph = MetroGraph(graphData)

        val result = graph.getGraphData()
        assertThat(result).isEqualTo(graphData)
    }

    @Test
    fun `get routes with empty from`() {
        val graphData = GraphData(mutableMapOf())
        val graph = MetroGraph(graphData)

        val result = graph.getRoutes("", "St2", MetroGraph.PathType.CHEAPEST_PATH)
        assertThat(result.status).isEqualTo(Resource.Status.ERROR)
    }

    @Test
    fun `get routes with invalid from`() {
        val graphData = GraphData(mutableMapOf())
        val graph = MetroGraph(graphData)

        val result = graph.getRoutes("St1", "St2", MetroGraph.PathType.CHEAPEST_PATH)
        assertThat(result.status).isEqualTo(Resource.Status.ERROR)
    }

    @Test
    fun `get routes with empty to`() {
        val graphData = GraphData(mutableMapOf())
        val graph = MetroGraph(graphData)

        val result = graph.getRoutes("St1", "", MetroGraph.PathType.CHEAPEST_PATH)
        assertThat(result.status).isEqualTo(Resource.Status.ERROR)
    }

    @Test
    fun `get routes with invalid to`() {
        val graphData = GraphData(mutableMapOf())
        val graph = MetroGraph(graphData)

        val result = graph.getRoutes("St1", "St2", MetroGraph.PathType.CHEAPEST_PATH)
        assertThat(result.status).isEqualTo(Resource.Status.ERROR)
    }
}