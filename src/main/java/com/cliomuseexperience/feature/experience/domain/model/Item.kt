package com.cliomuseexperience.feature.experience.domain.model

import com.google.gson.annotations.SerializedName

data class Item(
    @SerializedName("id")
    var id: Int,
    @SerializedName("imageFile")
    var imageFile: String?,
    @SerializedName("index")
    var index: Int?,
    @SerializedName("langId")
    var langId: Int?,
    @SerializedName("lat")
    var lat: String?,
    @SerializedName("lon")
    var lon: String?,
    @SerializedName("name")
    var name: String?,
    @SerializedName("secret")
    var secret: String?,
    @SerializedName("stories")
    var stories: List<Story>?,
    @SerializedName("thumbFile")
    var thumbFile: String?,
    @SerializedName("thumb_landscape_filename")
    var thumbLandscapeFilename: String?
)