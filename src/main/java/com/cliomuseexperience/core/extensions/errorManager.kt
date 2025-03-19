package com.cliomuseexperience.core.extensions

import com.cliomuseexperience.core.api.RequestFailure
import com.cliomuseexperience.experiencecliomuse.R

fun RequestFailure.errorManager() =
    when (this) {
        is RequestFailure.NoConnectionError ->  R.string.connection_error_message
        is RequestFailure.ApiError -> message ?: ""
        else -> R.string.default_error_message
    }