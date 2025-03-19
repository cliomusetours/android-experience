package com.cliomuseexperience.feature.map.domain

import com.google.gson.annotations.SerializedName

data class DirectionsResponse(
    @SerializedName("routes")
    val routes: List<Route>
) {
    data class Route(
        @SerializedName("geometry")
        val geometry: Geometry
    )

    data class Geometry(
        @SerializedName("type")
        val type: String,
        @SerializedName("coordinates")
        val coordinates: List<List<Double>>
    )
}
