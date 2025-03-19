package com.cliomuseexperience.core.extensions

import android.content.Context
import com.cliomuseexperience.feature.map.domain.DirectionsResponse
import com.google.gson.Gson
import java.io.FileNotFoundException

class RouteManager(private val context: Context) {
    private val gson = Gson()

    fun saveRouteToFile(route: DirectionsResponse.Geometry, tourId: Int, index: Int, totalChunks: Int) {
        val routeJson = gson.toJson(route)
        context.openFileOutput("route_${tourId}_chunk_$index.json", Context.MODE_PRIVATE).use {
            it.write(routeJson.toByteArray())
        }
        context.getSharedPreferences("RouteData", Context.MODE_PRIVATE).edit().putInt("chunksCount_$tourId", totalChunks).apply()
    }

    fun getChunksCount(tourId: Int): Int {
        return context.getSharedPreferences("RouteData", Context.MODE_PRIVATE).getInt("chunksCount_$tourId", 0)
    }

    fun readRouteChunksFromFile(tourId: Int, chunksCount: Int): List<DirectionsResponse.Geometry> {
        return (0 until chunksCount).mapNotNull { index ->
            try {
                val routeJson = context.openFileInput("route_${tourId}_chunk_$index.json").bufferedReader().use { it.readText() }
                gson.fromJson(routeJson, DirectionsResponse.Geometry::class.java)
            } catch (e: FileNotFoundException) {
                null
            }
        }
    }
}