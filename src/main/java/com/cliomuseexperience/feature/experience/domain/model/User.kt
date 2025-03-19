package com.cliomuseexperience.feature.experience.domain.model

import com.google.gson.annotations.SerializedName

data class User(
    @SerializedName("security_token")
    var securityToken: String?,
    @SerializedName("user_id")
    var userId: Int
)