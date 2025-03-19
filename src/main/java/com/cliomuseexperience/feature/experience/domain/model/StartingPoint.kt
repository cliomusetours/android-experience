package com.cliomuseexperience.feature.experience.domain.model

import com.google.gson.annotations.SerializedName

data class StartingPoint(
    @SerializedName("address")
    var address: String?,
    @SerializedName("coordinates")
    var coordinates: Coordinates?,
    @SerializedName("name")
    var name: String?
)