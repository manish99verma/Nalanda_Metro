package com.manish.nalandametro.ui

import android.opengl.Visibility
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.ListView
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.ViewModelProvider
import com.manish.nalandametro.data.model.GraphData
import com.manish.nalandametro.databinding.ActivityJourneyBinding
import com.manish.nalandametro.utils.Resource
import com.manish.nalandametro.utils.Utils.convertDistanceToKM
import com.manish.nalandametro.utils.Utils.getSerializable
import com.manish.nalandametro.utils.Utils.setListViewHeightBasedOnChildren
import dagger.hilt.android.AndroidEntryPoint
import java.lang.StringBuilder

@AndroidEntryPoint
class JourneyActivity : AppCompatActivity() {
    private lateinit var binding: ActivityJourneyBinding
    private lateinit var viewModel: JourneyViewMode

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityJourneyBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //Set up viewmodel
        viewModel = ViewModelProvider(this)[JourneyViewMode::class.java]
        val graphData = intent.getSerializable("graph_data", GraphData::class.java)
        viewModel.setUpGraph(graphData)

        // Back btn
        binding.btnBack.setOnClickListener {
            finish()
        }

        // Suggestion station
        lateinit var receiverListView: ListView
        lateinit var senderEditText: EditText

        binding.edtStartStation.addTextChangedListener {
            receiverListView = binding.listViewStart
            senderEditText = binding.edtStartStation

            if (it != null)
                viewModel.filterStations(it.toString(), true, 3)
            else
                setUpSearchListView(null, receiverListView, senderEditText)
        }

        binding.edtEndStation.addTextChangedListener {
            receiverListView = binding.listViewEnd
            senderEditText = binding.edtEndStation

            if (it != null)
                viewModel.filterStations(it.toString(), true, 3)
            else
                setUpSearchListView(null, receiverListView, senderEditText)
        }

        viewModel.getFilterStationResult().observe(this) { event ->
            if (senderEditText.isFocused)
                setUpSearchListView(
                    event.getContentIfNotHandled(),
                    receiverListView,
                    senderEditText
                )
            else
                setUpSearchListView(null, receiverListView, senderEditText)
        }

        // Get Route
        getRoutesSetUp()
    }

    private fun setUpSearchListView(list: List<String>?, listView: ListView, editText: EditText) {
        Log.d("TAGY", "setUpSearchListView: $list")

        if (list.isNullOrEmpty()) {
            listView.adapter = null
            listView.visibility = View.GONE
            return
        }

        val adapter =
            ArrayAdapter(this@JourneyActivity, android.R.layout.simple_list_item_1, list)

        listView.visibility = View.VISIBLE
        listView.adapter = adapter
        setListViewHeightBasedOnChildren(listView)
        listView.onItemClickListener = object : AdapterView.OnItemClickListener {
            override fun onItemClick(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                editText.clearFocus()
                editText.setText(list[position])
                listView.adapter = null
                listView.visibility = View.GONE
            }
        }

    }

    private fun getRoutesSetUp() {
        binding.btnGetRoute.setOnClickListener {
            val st1 = binding.edtStartStation.text.toString()
            val st2 = binding.edtEndStation.text.toString()

            viewModel.getShortestPath(st1, st2)
            viewModel.getCheapestPath(st1, st2)
        }

        viewModel.getShortestPathResult().observe(this) {
            val res = it.getContentIfNotHandled()
            if (res == null) {
                binding.layoutTimeSaverPath.visibility = View.GONE
                return@observe
            }

            if (res.status == Resource.Status.ERROR) {
                if (res.msg != null)
                    Toast.makeText(this, res.msg, Toast.LENGTH_SHORT)
                        .show()
                binding.layoutTimeSaverPath.visibility = View.GONE
            } else if (res.status == Resource.Status.SUCCESS) {
                val calculatedPath = res.data ?: return@observe

                binding.apply {
                    layoutTimeSaverPath.visibility = View.VISIBLE
                    txtPath.text = viewModel.convertPathToString(calculatedPath.path)
                    txtDistance.text =
                        StringBuilder("${convertDistanceToKM(calculatedPath.distance)} KM")

                    txtCost.text = StringBuilder("Rs ${calculatedPath.cost}")
                    txtStops.text = calculatedPath.stops.toString()
                }
            }
        }

        viewModel.getCheapestPathResult().observe(this) {
            val res = it.getContentIfNotHandled()
            if (res == null) {
                binding.layoutCheapestPath.visibility = View.GONE
                return@observe
            }

            if (res.status == Resource.Status.ERROR) {
                if (res.msg != null)
                    Toast.makeText(this, res.msg, Toast.LENGTH_SHORT)
                        .show()
                binding.layoutCheapestPath.visibility = View.GONE
            } else if (res.status == Resource.Status.SUCCESS) {
                val calculatedPath = res.data ?: return@observe

                binding.apply {
                    layoutCheapestPath.visibility = View.VISIBLE
                    txtPathCheap.text = viewModel.convertPathToString(calculatedPath.path)
                    txtDistanceCheap.text =
                        StringBuilder("${convertDistanceToKM(calculatedPath.distance)} KM")

                    txtCostCheap.text = StringBuilder("Rs ${calculatedPath.cost}")
                    stopsTxtCheap.text = calculatedPath.stops.toString()
                }
            }
        }
    }

}