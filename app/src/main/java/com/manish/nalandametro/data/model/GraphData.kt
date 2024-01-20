package com.manish.nalandametro.data.model

import java.io.Serializable

data class GraphData(
    val map: MutableMap<String, Station> = mutableMapOf()
):Serializable