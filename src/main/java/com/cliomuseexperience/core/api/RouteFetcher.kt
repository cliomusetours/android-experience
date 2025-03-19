package com.cliomuseapp.cliomuseapp.core.api

import android.util.Log
import com.cliomuseexperience.core.api.ApiService
import com.cliomuseexperience.core.api.FailureFactory
import com.cliomuseexperience.core.extensions.safeCall
import com.cliomuseexperience.experiencecliomuse.R
import com.cliomuseexperience.feature.experience.domain.model.Point
import com.cliomuseexperience.feature.map.domain.DirectionsResponse
import com.liulishuo.okdownload.OkDownloadProvider
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RouteFetcher( ) {

    private val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl("https://api.mapbox.com/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val mapboxApi: ApiService = retrofit.create(ApiService::class.java)


   suspend fun fetchRoute(
       tourPoints: List<Point>,
       tourId: Int
    ): Flow<Result<DirectionsResponse.Geometry?>> = flow {
        val coordinatesString = tourPoints.joinToString(";") { "${it.lon},${it.lat}" }
        val accessToken = OkDownloadProvider.context.getString(R.string.mapbox_key)

       Log.d("RouteFetcher", "Fetching route for tour ID: $tourId")

        val response: Response<DirectionsResponse> = mapboxApi.getRoute(
            coordinates = coordinatesString,
            alternatives = false,
            continueStraight = true,
            geometries = "geojson",
            overview = "full",
            steps = false,
            accessToken = accessToken
        )

        // Use safeCall with explicit types to ensure compatibility
        val result: Result<DirectionsResponse.Geometry?> = response.safeCall(
            transform = { directionsResponse ->
                directionsResponse.routes.firstOrNull()?.geometry
            },
            errorFactory = FailureFactory() // Optional: can customize with your own implementation
        )

        emit(result)
    }.catch { exception ->
        // Handle exceptions and emit a failure result
        emit(FailureFactory<DirectionsResponse.Geometry?>().handleException(exception))
       Log.e("RouteFetcher", "Failed to fetch route for tour ID $tourId: ${exception.message}")
    }
}

