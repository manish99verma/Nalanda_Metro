package com.manish.nalandametro.data.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.firestore.FirebaseFirestore
import com.manish.nalandametro.data.model.GraphData
import com.manish.nalandametro.data.model.Station
import com.manish.nalandametro.utils.Resource

class MetroRepository : Repository {
    private val db = FirebaseFirestore.getInstance()
    private val _graphData = MutableLiveData<Resource<GraphData>>()
    override fun getGraphDataResult() = _graphData as LiveData<Resource<GraphData>>
    private val _updateGraphData = MutableLiveData<Resource<GraphData>>()
    override fun updateGraphDataResult() = _updateGraphData as LiveData<Resource<GraphData>>

    override fun getGraphData() {
        _graphData.postValue(Resource.loading(null))
        fetchGraphData(_graphData)
    }

    override fun updateData(graphData: GraphData) {
        _updateGraphData.postValue(Resource.loading(null))

        db.collection("data").document("graph_data").delete().addOnSuccessListener {
            db.collection("data").document("graph_data").set(graphData).addOnSuccessListener {
                fetchGraphData(_updateGraphData)
            }.addOnFailureListener {
                _updateGraphData.postValue(
                    Resource.error(
                        null,
                        "Please check you internet or wifi!"
                    )
                )
            }.addOnCanceledListener {
                _updateGraphData.postValue(
                    Resource.error(
                        null,
                        "Please check you internet or wifi!"
                    )
                )
            }
        }.addOnCanceledListener {
            _updateGraphData.postValue(Resource.error(null, "Please check you internet or wifi!"))
        }.addOnFailureListener {
            _updateGraphData.postValue(Resource.error(null, "Please check you internet or wifi!"))
        }

    }

    private fun fetchGraphData(liveData: MutableLiveData<Resource<GraphData>>) {
        db.collection("data").document("graph_data").get().addOnSuccessListener {
            val data = it.toObject(GraphData::class.java)
            liveData.postValue(Resource.success(data))
        }.addOnCanceledListener {
            liveData.postValue(
                Resource.error(
                    null,
                    "Please check you internet or wifi!"
                )
            )
        }.addOnFailureListener {
            liveData.postValue(
                Resource.error(
                    null,
                    "Please check you internet or wifi!"
                )
            )
        }
    }
}