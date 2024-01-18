package com.manish.nalandametro.ui

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.manish.nalandametro.BuildConfig
import com.manish.nalandametro.R
import com.manish.nalandametro.data.model.GraphData
import com.manish.nalandametro.data.pref.PrefManager
import com.manish.nalandametro.utils.Resource
import com.manish.nalandametro.utils.Utils.toGeoPoint
import dagger.hilt.android.AndroidEntryPoint
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.ItemizedIconOverlay.OnItemGestureListener
import org.osmdroid.views.overlay.ItemizedOverlayWithFocus
import org.osmdroid.views.overlay.OverlayItem
import org.osmdroid.views.overlay.Polyline
import javax.inject.Inject


@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private lateinit var mapView: MapView
    private lateinit var viewModel: MainViewModel

    @Inject
    lateinit var prefManager: PrefManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        configureMapView()
        setContentView(R.layout.activity_main)
        viewModel = ViewModelProvider(this)[MainViewModel::class.java]

        mapView = findViewById(R.id.mapview)

        viewModel.getGraphDataFromWeb()
        viewModel.getGraphDataResult().observe(this) {
            Log.d("TAGY", "onCreate: receivedData: ${it.status}, ${it.data}")

            if (it.status == Resource.Status.SUCCESS && it.data != null) {
                viewModel.setUpGraph(it.data)
                refreshMapItems(viewModel.getCurrGraphData())
            } else if (it.status == Resource.Status.FAILED) {
                Toast.makeText(this@MainActivity, it.msg, Toast.LENGTH_SHORT)
                    .show()
            }
        }

//        viewModel.updateWithTestData()
//        viewModel.updateWithCustomDataResult().observe(this){
//            Log.d("TAGY", "onCreate: updatedData: ${it.status}, ${it.data}")
//
//            if (it.status == Resource.Status.SUCCESS && it.data != null) {
//                viewModel.setUpGraph(it.data)
//                refreshMapItems(viewModel.getCurrGraphData())
//            } else if (it.status == Resource.Status.FAILED) {
//                Toast.makeText(this@MainActivity, it.msg, Toast.LENGTH_SHORT)
//                    .show()
//            }
//        }

        initMapView()
    }

    private fun configureMapView() {
        val config = Configuration.getInstance()
        config.load(this, prefManager.getDefaultPref())
        config.userAgentValue = BuildConfig.APPLICATION_ID
        config.isDebugMode = true
    }

    private fun initMapView(){
        //Tile
        mapView.setTileSource(TileSourceFactory.MAPNIK)

        //Controller
        val mapController = mapView.controller
        mapController.setZoom(14.0)
        mapController.setCenter(GeoPoint(25.027308, 85.528208))
    }

    private fun refreshMapItems(graphData: GraphData?) {
        if (graphData == null)
            return

        //Add routes
        val lines = mutableListOf<Polyline>()
        val stations = mutableListOf<OverlayItem>()
        graphData.map?.forEach { i ->
            //Station
            val location = i.value.latLng?.toGeoPoint()
            val item = OverlayItem(i.key, "", location)
            item.setMarker(resources.getDrawable(R.drawable.train__1_))
            stations.add(item)

            //Routes
            i.value.routes!!.forEach { j ->
                val point2 = graphData.map?.get(j.key)?.latLng?.toGeoPoint()

                val myPath = Polyline()
                myPath.addPoint(location)
                myPath.addPoint(point2)
                myPath.color = R.color.line_color

                lines.add(myPath)
            }
        }

        mapView.overlays.addAll(lines)

        //your items
//        val gPt0 = GeoPoint(25.027308, 85.528208)
//        val gPt1 = GeoPoint(25.089721, 85.534828)
//        val myPath = Polyline()
//        myPath.addPoint(gPt0)
//        myPath.addPoint(gPt1)
//        myPath.color = R.color.line_color
//
//        mapView.overlays.add(myPath)
//
//        val items = listOf(
//            OverlayItem("Giriak", "", GeoPoint(25.027308, 85.528208)),
//            OverlayItem("Pawapuri", "", GeoPoint(25.089721, 85.534828))
//        )
//        for (item in items) {
//            item.setMarker(resources.getDrawable(R.drawable.train__1_))
//        }

        //the overlay
        var overlay = ItemizedOverlayWithFocus<OverlayItem>(
            stations,
            object : OnItemGestureListener<OverlayItem> {
                override fun onItemSingleTapUp(index: Int, item: OverlayItem): Boolean {
                    //do something
                    return true
                }

                override fun onItemLongPress(index: Int, item: OverlayItem): Boolean {
                    return false
                }
            },
            this
        )

        overlay.setFocusItemsOnTap(true)
        mapView.overlays.add(overlay)

        //My location
//        val lOverlay = MyLocationNewOverlay(GpsMyLocationProvider(this@MainActivity), mapView)
//        lOverlay.enableMyLocation()
//
//        mapView.overlays.add(lOverlay)
    }

    override fun onResume() {
        super.onResume()
        mapView.onResume()
    }

    override fun onPause() {
        super.onPause()
        mapView.onPause()
    }
}