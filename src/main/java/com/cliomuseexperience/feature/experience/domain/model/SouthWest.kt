package com.cliomuseexperience.feature.experience.domain.model

import com.google.gson.annotations.SerializedName

data class SouthWest(
    @SerializedName("lat")
    var lat: Double?,
    @SerializedName("lon")
    var lon: Double?
)