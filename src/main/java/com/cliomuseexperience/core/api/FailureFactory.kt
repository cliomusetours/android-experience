package com.cliomuseexperience.core.api

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import okhttp3.ResponseBody
import java.net.SocketTimeoutException
import java.net.UnknownHostException

open class FailureFactory<R> {
    private val gson = Gson()

    open fun handleCode(code: Int, errorBody: ResponseBody?): Result<R> =
        Result.failure(when (code) {
            400 -> createApiError(errorBody)
            else -> RequestFailure.ApiError()
        })

    open fun handleException(exception: Throwable): Result<R> =
        Result.failure(when (exception) {
            is UnknownHostException, is SocketTimeoutException -> RequestFailure.NoConnectionError
            else -> RequestFailure.UnknownError
        })

    private fun createApiError(responseBody: ResponseBody?): RequestFailure.ApiError {
        return try {
            responseBody?.string()?.let { jsonString ->
                val type = object : TypeToken<List<ErrorResponse>>() {}.type
                val errorList: List<ErrorResponse> = gson.fromJson(jsonString, type)
                val messages = errorList.joinToString(separator = "\n") { it.message }
                val codes = errorList.map { it.code }

                RequestFailure.ApiError(
                    code = codes,
                    message = messages
                )
            } ?: RequestFailure.ApiError()
        } catch (exception: Exception) {
            RequestFailure.ApiError()
        }
    }
}