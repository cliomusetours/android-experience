package com.cliomuseexperience.feature.experience.domain.model

import com.google.gson.annotations.SerializedName

data class Point(
    @SerializedName("lat")
    var lat: String?,
    @SerializedName("lon")
    var lon: String?,
    @SerializedName("sequence")
    var sequence: Int?
)