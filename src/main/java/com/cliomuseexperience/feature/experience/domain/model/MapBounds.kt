package com.cliomuseexperience.feature.experience.domain.model

import com.google.gson.annotations.SerializedName

data class MapBounds(
    @SerializedName("northEast")
    var northEast: NorthEast?,
    @SerializedName("southWest")
    var southWest: SouthWest?
)