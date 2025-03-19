package com.cliomuseexperience.feature.experience.domain.model

import com.google.gson.annotations.SerializedName

data class Coordinates(
    @SerializedName("lat")
    var lat: String?,
    @SerializedName("lon")
    var lon: String?
)