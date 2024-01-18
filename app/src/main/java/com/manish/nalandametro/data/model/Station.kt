package com.manish.nalandametro.data.model

import com.google.android.gms.maps.model.LatLng

data class Station(
    val routes: MutableMap<String, Route>? = null,
    var stationId: String? = null,
    var latLng: LatLng? = null,
    val id: String? = null
)