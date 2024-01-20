package com.manish.nalandametro.data.model

import org.osmdroid.util.GeoPoint
import java.io.Serializable

data class MapPoint(val latitude: Double? = null, val longitude: Double? = null):Serializable {
    fun toGeoPoint(): GeoPoint? {
        return if (longitude != null && latitude != null)
            GeoPoint(latitude, longitude)
        else null
    }
}
