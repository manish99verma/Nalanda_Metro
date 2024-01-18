package com.manish.nalandametro.data.repository

import androidx.lifecycle.LiveData
import com.manish.nalandametro.data.model.GraphData
import com.manish.nalandametro.utils.Resource

interface Repository {
    fun getGraphData()

    fun updateData(graphData: GraphData)

    fun getGraphDataResult():LiveData<Resource<GraphData>>
    fun updateGraphDataResult():LiveData<Resource<GraphData>>

//    suspend fun addStation(
//        name: String,
//        station: Station
//    ): Resource<GraphData>
//
//    suspend fun addRoute(
//        from: String,
//        to: String,
//        route: Route
//    ): Resource<GraphData>
}