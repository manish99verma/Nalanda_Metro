package com.manish.nalandametro.utils

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.os.Build
import android.view.View
import android.view.View.MeasureSpec
import android.view.inputmethod.InputMethodManager
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity
import com.manish.nalandametro.data.model.MapPoint
import java.io.Serializable
import java.util.UUID
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.roundToInt
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

    fun <T : Serializable?> Intent.getSerializable(key: String, m_class: Class<T>): T {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
            this.getSerializableExtra(key, m_class)!!
        else
            this.getSerializableExtra(key) as T
    }

    fun setListViewHeightBasedOnChildren(listView: ListView) {
        val listAdapter = listView.adapter
            ?: // pre-condition
            return
        var totalHeight = 0
        val desiredWidth = MeasureSpec.makeMeasureSpec(listView.width, MeasureSpec.AT_MOST)
        for (i in 0 until listAdapter.count) {
            val listItem = listAdapter.getView(i, null, listView)
            listItem.measure(desiredWidth, MeasureSpec.UNSPECIFIED)
            totalHeight += listItem.measuredHeight
        }
        val params = listView.layoutParams
        params.height = totalHeight + listView.dividerHeight * (listAdapter.count - 1)

        listView.layoutParams = params
        listView.requestLayout()
    }

    fun convertDistanceToKM(inMeters: Double): Double {
        return ((inMeters / 1000) * 10.0).roundToInt() / 10.0
    }

    fun isConnectedToInternet(context: Context): Boolean {
        val cm =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val netInfo = cm.activeNetworkInfo
        return netInfo != null && netInfo.isConnectedOrConnecting
    }

    fun hideKeyboard(activity: Activity,root:View) {
        val imm = activity.getSystemService(AppCompatActivity.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(root.applicationWindowToken, 0)
    }
}