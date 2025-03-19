package com.cliomuseexperience.feature.experience.domain.model

import com.google.gson.annotations.SerializedName

data class Category(
    @SerializedName("type")
    var type: String?,
    @SerializedName("values")
    var values: List<String>?
)