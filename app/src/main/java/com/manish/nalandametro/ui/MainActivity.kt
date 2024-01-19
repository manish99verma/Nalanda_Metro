package com.manish.nalandametro.ui

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.AdapterView.OnItemClickListener
import android.widget.ArrayAdapter
import android.widget.SearchView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import androidx.lifecycle.ViewModelProvider
import com.manish.nalandametro.BuildConfig
import com.manish.nalandametro.R
import com.manish.nalandametro.data.model.GraphData
import com.manish.nalandametro.data.model.MapPoint
import com.manish.nalandametro.data.pref.PrefManager
import com.manish.nalandametro.databinding.ActivityMainBinding
import com.manish.nalandametro.utils.Resource
import com.manish.nalandametro.utils.Utils
import dagger.hilt.android.AndroidEntryPoint
import org.osmdroid.api.IMapController
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.overlay.ItemizedIconOverlay.OnItemGestureListener
import org.osmdroid.views.overlay.ItemizedOverlayWithFocus
import org.osmdroid.views.overlay.Overlay
import org.osmdroid.views.overlay.OverlayItem
import org.osmdroid.views.overlay.Polyline
import javax.inject.Inject


@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var viewModel: MainViewModel
    private lateinit var mapController: IMapController
    private val stationToOverLayItemMap = mutableMapOf<String, OverlayItem>()
    private var stationsOverlay: ItemizedOverlayWithFocus<OverlayItem>? = null
    private var centerCity = "Bihar Sharif"

    @Inject
    lateinit var prefManager: PrefManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        configureMapView()

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        viewModel = ViewModelProvider(this)[MainViewModel::class.java]


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

        /*        viewModel.updateWithTestData()
                viewModel.updateWithCustomDataResult().observe(this){
                    Log.d("TAGY", "onCreate: updatedData: ${it.status}, ${it.data}")

                    if (it.status == Resource.Status.SUCCESS && it.data != null) {
                        viewModel.setUpGraph(it.data)
                        refreshMapItems(viewModel.getCurrGraphData())
                    } else if (it.status == Resource.Status.FAILED) {
                        Toast.makeText(this@MainActivity, it.msg, Toast.LENGTH_SHORT)
                            .show()
                    }
                }*/

        binding.mapView.setTileSource(TileSourceFactory.MAPNIK)
        mapController = binding.mapView.controller

        //Center Focus
        setFocus()
        binding.fabFocusLocation.setOnClickListener {
            setFocus()
        }

        //Searching
        binding.searchBtn.setOnClickListener { v ->
            binding.mainToolbarLayout.visibility = View.INVISIBLE
            binding.searchView.visibility = View.VISIBLE

            Utils.showKeyboard(binding.searchView, this@MainActivity)
            binding.searchView.requestFocus()
        }

        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                viewModel.filterStations(query, false, 3)
                return false
            }

            override fun onQueryTextChange(newText: String): Boolean {
                viewModel.filterStations(newText, true, 3)
                return false
            }
        })

        viewModel.getFilterStationResult().observe(this) { event ->
            setUpSearchListView(event.getContentIfNotHandled())
        }
    }

    private fun setUpSearchListView(list: List<String>?) {
        Log.d("TAGY", "setUpSearchListView: $list")

        if (list.isNullOrEmpty()) {
            binding.listView.adapter = null
            return
        }

        val adapter =
            ArrayAdapter(this@MainActivity, android.R.layout.simple_list_item_1, list)

        binding.listView.adapter = adapter

        binding.listView.onItemClickListener = object : OnItemClickListener {
            override fun onItemClick(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                centerCity = list[position]
                setFocus()

                dismissSearchView()
            }
        }
    }

    private fun configureMapView() {
        val config = Configuration.getInstance()
        config.load(this, prefManager.getDefaultPref())
        config.userAgentValue = BuildConfig.APPLICATION_ID
        config.isDebugMode = true
    }

    private fun setFocus() {
        val point = viewModel.getStationLocation(centerCity)
        if (point == null)
            mapController.setCenter(GeoPoint(25.178780, 85.513405))
        else
            mapController.animateTo(point.toGeoPoint())

        stationsOverlay?.focusedItem = stationToOverLayItemMap[centerCity]
        mapController.setZoom(14.0)
    }

    private fun refreshMapItems(graphData: GraphData?) {
        if (graphData == null)
            return

        Log.d("TAGY", "refreshMapItems: Displaying on map")

        //List routes
        val lines = mutableListOf<Polyline>()
        stationToOverLayItemMap.clear()
        val trainDrawable = ResourcesCompat.getDrawable(resources, R.drawable.train__1_, null)

        graphData.map.forEach { i ->
            //Station
            val location = i.value.mapPoint?.toGeoPoint()
            val item = OverlayItem(i.key, "", location)
            item.setMarker(trainDrawable)
            stationToOverLayItemMap[i.key] = item

            //Routes
            i.value.routes!!.forEach { j ->
                val point2 = graphData.map[j.key]?.mapPoint?.toGeoPoint()

                val myPath = Polyline()
                myPath.addPoint(location)
                myPath.addPoint(point2)
                myPath.color = R.color.line_color

                lines.add(myPath)
            }
        }

        Log.d("TAGY", "map: $stationToOverLayItemMap")
        //stations click callback
        stationsOverlay = ItemizedOverlayWithFocus<OverlayItem>(
            stationToOverLayItemMap.values.toList(),
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

        //adding routes
        binding.mapView.overlays.addAll(lines)

        //adding stations
        stationsOverlay?.setFocusItemsOnTap(true)
        binding.mapView.overlays.add(stationsOverlay)

        binding.mapView.invalidate()
        setFocus()
    }

    override fun onResume() {
        super.onResume()
        binding.mapView.onResume()
    }

    override fun onPause() {
        super.onPause()
        binding.mapView.onPause()
    }

    override fun onBackPressed() {
        if (binding.searchView.visibility == View.VISIBLE) {
            dismissSearchView()
        } else
            super.onBackPressed()
    }

    private fun dismissSearchView() {
        setUpSearchListView(null)

        binding.searchView.clearFocus()
        binding.searchView.setQuery("", false)
        binding.searchView.visibility = View.INVISIBLE
        binding.mainToolbarLayout.visibility = View.VISIBLE
    }

}