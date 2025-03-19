package com.cliomuseexperience.core.api

import com.cliomuseexperience.feature.experience.domain.model.AccessResponse
import com.cliomuseexperience.feature.experience.domain.request.AccessRequest
import com.cliomuseexperience.feature.map.domain.DirectionsResponse
import com.google.gson.annotations.SerializedName
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query


interface ApiService {

    companion object {
        const val CONTENT_TYPE = "Content-Type: application/json"
    }

    @Headers(CONTENT_TYPE)
    @POST("api/node/v2/auth/getaccess")
    suspend fun getAccess(
        @Body accessRequest: AccessRequest,
        @Header("x-api-key") xApiKey: String,
        @Header("device") device: String
    ): Response<AccessResponse>


    @Headers(CONTENT_TYPE)
    @GET("directions/v5/mapbox/walking/{coordinates}")
    suspend fun getRoute(
        @Path("coordinates") coordinates: String,
        @Query("alternatives") alternatives: Boolean,
        @Query("continue_straight") continueStraight: Boolean,
        @Query("geometries") geometries: String,
        @Query("overview") overview: String,
        @Query("steps") steps: Boolean,
        @Query("access_token") accessToken: String
    ): Response<DirectionsResponse>

    @Headers(CONTENT_TYPE)
    @POST("/api/node/v2/tours/{tour_id}/statuses/{status_id}")
    suspend fun updateDownloadTourStatus(
        @Path("tour_id") tourId: Int,
        @Path("status_id") statusId: Int,
        @Header("token") token: String,
        @Header("device") device: String,
        @Body request: DownloadStatusRequest
    ): Response<DownloadStatusResponse>
}

data class DownloadStatusRequest(
    @SerializedName("lang_id")
    val langId: Int,

    @SerializedName("status_message")
    val statusMessage: String? = null,

    @SerializedName("operating_system")
    val operatingSystem: String? = null,

    @SerializedName("operating_system_version")
    val operatingSystemVersion: String? = null,

    @SerializedName("app_version")
    val appVersion: String? = null
)


data class DownloadStatusResponse(
    @SerializedName("status_id")
    val statusId: Int,

    @SerializedName("status_text")
    val statusText: Int
)