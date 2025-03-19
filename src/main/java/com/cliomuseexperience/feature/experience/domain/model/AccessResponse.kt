package com.cliomuseexperience.feature.experience.domain.model

import com.google.gson.annotations.SerializedName

data class AccessResponse(
    @SerializedName("tours")
    var tours: List<Tour>?,
    @SerializedName("user")
    var user: User?
)