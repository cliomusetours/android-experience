package com.cliomuseexperience.core.api
import com.google.gson.annotations.SerializedName

@Suppress("unused")
    //(generateAdapter = true)



data class ErrorResponse(
    @SerializedName("code")
    var code: Int,
    @SerializedName("msg")
    var message: String
)