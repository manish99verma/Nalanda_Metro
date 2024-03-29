package com.manish.nalandametro.graph

data class CalculatedPath(
    val type: MetroGraph.PathType,
    val path: List<String>,
    val distance: Double,
    val cost: Int,
    val stops: Int
)