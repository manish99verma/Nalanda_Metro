package com.manish.nalandametro.data.model

data class Station(
    val routes: MutableMap<String, Route>? = null,
    var stationId: String? = null,
    var mapPoint: MapPoint? = null,
    val id: String? = null
)