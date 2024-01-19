package com.manish.nalandametro.data.model

import org.osmdroid.util.GeoPoint

data class MapPoint(val latitude: Double? = null, val longitude: Double? = null) {
    fun toGeoPoint(): GeoPoint? {
        return if (longitude != null && latitude != null)
            GeoPoint(latitude, longitude)
        else null
    }
}
