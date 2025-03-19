package com.cliomuseexperience.core.api

sealed class RequestFailure(message: String = "") : Throwable(message) {
    class ApiError(var code: List<Int> = ArrayList(), message: String = "") : RequestFailure(message)
    object NoConnectionError : RequestFailure()
    object UnknownError : RequestFailure()
}