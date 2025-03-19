package com.cliomuseexperience.feature.experience.domain.model

import com.google.gson.annotations.SerializedName

data class Story(
    @SerializedName("audioFile")
    var audioFile: String?,
    @SerializedName("body")
    var body: String?,
    @SerializedName("category")
    var category: Category?,
    @SerializedName("difficulty")
    var difficulty: String?,
    @SerializedName("duration")
    var duration: Long?,
    @SerializedName("id")
    var id: Int,
    @SerializedName("imageFile")
    var imageFile: String?,
    @SerializedName("langId")
    var langId: Int?,
    @SerializedName("sequence_id")
    var sequenceId: Int?,
    @SerializedName("title")
    var title: String?,
    @SerializedName("videoFile")
    var videoFile: String?,
    @SerializedName("isSelected")
    var isSelected: Boolean = false
)