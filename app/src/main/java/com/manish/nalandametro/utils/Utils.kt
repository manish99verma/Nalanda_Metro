package com.manish.nalandametro.utils

import com.google.android.gms.maps.model.LatLng
import org.osmdroid.util.GeoPoint
import java.util.UUID
import kotlin.math.absoluteValue

object Utils {
    fun randomStringId() = UUID.randomUUID().toString()

    fun LatLng.distance(point2: LatLng): Float {
        val R = 6371 // Radius of the earth

        val latDistance = Math.toRadians(point2.latitude - this.latitude)
        val lonDistance = Math.toRadians(point2.longitude - this.longitude)
        val a = (Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + (Math.cos(Math.toRadians(this.latitude)) * Math.cos(Math.toRadians(point2.latitude))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2)))
        val c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a))
        var distance = R * c * 1000 // convert to meters

        distance = Math.pow(distance, 2.0)

        return Math.sqrt(distance).toFloat().absoluteValue
    }

    fun LatLng.toGeoPoint() = GeoPoint(latitude, longitude)
}