package com.cliomuseexperience.core.extensions

import com.cliomuseexperience.core.api.FailureFactory
import retrofit2.Response

fun <T, R> Response<T>.safeCall(
    transform: (T) -> R,
    errorFactory: FailureFactory<R> = FailureFactory()
): Result<R> {
    return try {
        val body = body()
        if (isSuccessful && body != null) {
            Result.success(transform(body))
        } else {
            errorFactory.handleCode(code = code(), errorBody = errorBody())
        }
    } catch (e: Exception) {
        Result.failure(e)
    }
}