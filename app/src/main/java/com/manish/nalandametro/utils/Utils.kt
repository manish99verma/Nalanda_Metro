package com.manish.nalandametro.utils

import android.content.Context
import android.view.View
import android.view.inputmethod.InputMethodManager
import com.manish.nalandametro.data.model.MapPoint
import java.util.UUID
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.sqrt

object Utils {
    fun randomStringId() = UUID.randomUUID().toString()

    fun MapPoint.distance(point2: MapPoint): Double {
        if (this.latitude == null || this.longitude == null || point2.latitude == null || point2.longitude == null)
            return 0.0

        val R = 6371 // Radius of the earth

        val latDistance = Math.toRadians(point2.latitude - this.latitude)
        val lonDistance = Math.toRadians(point2.longitude - this.longitude)
        val a = (sin(latDistance / 2) * sin(latDistance / 2)
                + (cos(Math.toRadians(this.latitude)) * cos(Math.toRadians(point2.latitude))
                * sin(lonDistance / 2) * sin(lonDistance / 2)))
        val c = 2 * atan2(sqrt(a), sqrt(1 - a))
        var distance = R * c * 1000 // convert to meters

        distance = distance.pow(2.0)

        return sqrt(distance)
    }

     fun showKeyboard(view: View, context: Context) {
        val inputMethodManager =
            context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.toggleSoftInputFromWindow(
            view.applicationWindowToken,
            InputMethodManager.SHOW_FORCED, 0
        )
    }

}