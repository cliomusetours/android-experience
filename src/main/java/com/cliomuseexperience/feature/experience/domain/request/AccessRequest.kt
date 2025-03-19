package com.cliomuseexperience.feature.experience.domain.request

import com.google.gson.annotations.SerializedName


data class AccessRequest(
    @SerializedName("access_code")
    var accessCode: String?,
    @SerializedName("user_identifier")
    var userIdentifier: String?
)