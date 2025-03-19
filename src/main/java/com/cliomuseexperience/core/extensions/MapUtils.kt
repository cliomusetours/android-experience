package com.cliomuseexperience.core.extensions

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.drawable.VectorDrawable
import android.util.Log
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import com.cliomuseapp.cliomuseapp.core.api.RouteFetcher
import com.cliomuseexperience.experiencecliomuse.R
import com.cliomuseexperience.feature.experience.domain.model.Item
import com.cliomuseexperience.feature.experience.domain.model.Point
import com.cliomuseexperience.feature.experience.domain.model.StartingPoint
import com.cliomuseexperience.feature.map.domain.DirectionsResponse
import com.google.gson.Gson
import com.google.gson.JsonParser
import com.mapbox.geojson.Feature
import com.mapbox.geojson.LineString
import com.mapbox.maps.MapView
import com.mapbox.maps.MapboxMap
import com.mapbox.maps.Style
import com.mapbox.maps.extension.style.layers.addLayer
import com.mapbox.maps.extension.style.layers.generated.SymbolLayer
import com.mapbox.maps.extension.style.layers.generated.lineLayer
import com.mapbox.maps.extension.style.layers.getLayer
import com.mapbox.maps.extension.style.layers.properties.generated.IconAnchor
import com.mapbox.maps.extension.style.layers.properties.generated.LineCap
import com.mapbox.maps.extension.style.layers.properties.generated.LineJoin
import com.mapbox.maps.extension.style.sources.addSource
import com.mapbox.maps.extension.style.sources.generated.GeoJsonSource
import com.mapbox.maps.extension.style.sources.generated.geoJsonSource
import com.mapbox.maps.extension.style.sources.getSourceAs
import com.mapbox.maps.plugin.annotation.annotations
import com.mapbox.maps.plugin.annotation.generated.PointAnnotationOptions
import com.mapbox.maps.plugin.annotation.generated.createPointAnnotationManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


fun loadAnnotations(
    context: Context,
    mapView: MapView,
    itemsList: List<Item>,
    onAnnotationClick: (Item) -> Unit
) {
    val pointAnnotationManager = mapView.annotations.createPointAnnotationManager()

    itemsList.forEachIndexed { index, item ->
        if (item.lat != null && item.lat!!.isNotEmpty() && item.lon != null && item.lon!!.isNotEmpty()) {
            // Use the computed index (1-based) instead of the API provided index
            val iconBitmap = createIconWithIndex(context, index + 1)

            val annotationOptions = PointAnnotationOptions()
                .withPoint(
                    com.mapbox.geojson.Point.fromLngLat(
                        item.lon!!.toDouble(),
                        item.lat!!.toDouble()
                    )
                )
                .withIconImage(iconBitmap)
                .withIconAnchor(IconAnchor.BOTTOM)
                .withData(JsonParser.parseString(Gson().toJson(item)))

            val annotation = pointAnnotationManager.create(annotationOptions)

            pointAnnotationManager.addClickListener {
                val clickedItem = Gson().fromJson(it.getData().toString(), Item::class.java)
                onAnnotationClick(clickedItem)
                true
            }
        }
    }

    pointAnnotationManager.iconAllowOverlap = true
}

fun createIconWithIndex(context: Context, index: Int): Bitmap {
    // Load the base icon
    val baseIcon =
        ContextCompat.getDrawable(context, R.drawable.ic_baseline_location_filled_on_24)
            ?.toBitmap(90, 90) ?: return Bitmap.createBitmap(90, 90, Bitmap.Config.ARGB_8888)

    // Create a mutable bitmap to draw on
    val bitmap = Bitmap.createBitmap(90, 90, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(bitmap)
    val paint = Paint()

    // Draw the base icon onto the canvas
    canvas.drawBitmap(baseIcon, 0f, 0f, paint)

    // Set up the paint for the text
    paint.color = Color.WHITE
    paint.textSize = 28f
    paint.isAntiAlias = true
    paint.textAlign = Paint.Align.CENTER

    // Draw the text in the center of the bitmap
    val xPos = canvas.width / 2
    val yPos = (canvas.height / 2 - (paint.descent() + paint.ascent()) / 2)

    canvas.drawText(index.toString(), xPos.toFloat(), yPos, paint)

    return bitmap
}

fun loadStartFinishPointIcons(
    context: Context,
    style: Style,
    startingPoint: StartingPoint?,
    finishingPoint: StartingPoint?,
    loadStartFinishPointIconsFlag: Boolean
) {
    if (!loadStartFinishPointIconsFlag) return

    val startingPointCoordinates = startingPoint?.coordinates?.let { coords ->
        val lon = coords.lon
        val lat = coords.lat
        // Check if either coordinate is null or blank
        if (!lon.isNullOrBlank() && !lat.isNullOrBlank()) {
            // Only convert if both strings contain a valid number
            com.mapbox.geojson.Point.fromLngLat(lon.toDouble(), lat.toDouble())
        } else {
            null // Return null if either coordinate is missing or empty
        }
    }

    val finishingPointCoordinates = finishingPoint?.coordinates?.let { coords ->
        // Check if finishing point is different from starting point
        if (coords.lon != startingPoint?.coordinates?.lon || coords.lat != startingPoint?.coordinates?.lat) {
            val lon = coords.lon
            val lat = coords.lat
            if (!lon.isNullOrBlank() && !lat.isNullOrBlank()) {
                com.mapbox.geojson.Point.fromLngLat(lon.toDouble(), lat.toDouble())
            } else {
                null
            }
        } else {
            null // Return null if finishing point is the same as the starting point
        }
    }

    // Function to convert a VectorDrawable to Bitmap
    fun getBitmapFromVectorDrawable(context: Context, drawableId: Int): Bitmap {
        val drawable = ContextCompat.getDrawable(context, drawableId) as VectorDrawable
        val bitmap = Bitmap.createBitmap(
            drawable.intrinsicWidth,
            drawable.intrinsicHeight,
            Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(bitmap)
        drawable.setBounds(0, 0, canvas.width, canvas.height)
        drawable.draw(canvas)
        return bitmap
    }


    val originalBitmapStarting = getBitmapFromVectorDrawable(context, R.drawable.start_icon)
    val scaledBitmapStarting = Bitmap.createScaledBitmap(originalBitmapStarting, 60, 60, false)
    style.addImage("marker-starting", scaledBitmapStarting)


    if (finishingPointCoordinates != null) {
        val originalBitmapFinishing =
            getBitmapFromVectorDrawable(context, R.drawable.finish_icon)
        val scaledBitmapFinishing =
            Bitmap.createScaledBitmap(originalBitmapFinishing, 60, 60, false)
        style.addImage("marker-finishing", scaledBitmapFinishing)
    }

    finishingPointCoordinates?.let {
        val source = GeoJsonSource.Builder("marker-source-finishing")
            .feature(Feature.fromGeometry(it))
            .build()
        style.addSource(source)

        val layer = SymbolLayer("marker-layer-finishing", "marker-source-finishing")
            .iconImage("marker-finishing")
            .iconSize(1.3)
        style.addLayer(layer)
    }


    startingPointCoordinates?.let {
        val source = GeoJsonSource.Builder("marker-source-starting")
            .feature(Feature.fromGeometry(it))
            .build()
        style.addSource(source)

        val layer = SymbolLayer("marker-layer-starting", "marker-source-starting")
            .iconImage("marker-starting")
            .iconSize(1.3)
        style.addLayer(layer)
    }
}

fun getRoute(context: Context, mapboxMap: MapboxMap, tourId: Int, pointList: List<Point>?) {
    val routeManager = RouteManager(context)
    val chunksCount = routeManager.getChunksCount(tourId)
    val routes = routeManager.readRouteChunksFromFile(tourId, chunksCount)

    if (routes.isNotEmpty()) {
        routes.forEach { route ->
            // Draw on the main thread to ensure UI updates are reflected
            CoroutineScope(Dispatchers.Main).launch {
                drawRoute(route, mapboxMap)
            }
        }
    } else {
        val routeFetcher =
            RouteFetcher() // Ensure the context and other dependencies are properly provided
        val chunks = pointList?.chunked(24)

        chunks?.forEachIndexed { index, chunk ->
            CoroutineScope(Dispatchers.IO).launch {
                routeFetcher.fetchRoute(chunk, tourId).collect { result ->
                    result.onSuccess { route ->
                        route?.let {
                            // Switch to the main thread for drawing
                            withContext(Dispatchers.Main) {
                                drawRoute(it, mapboxMap)
                                Log.e(
                                    "Route",
                                    "Route drawn successfully for chunk $index of tour ID $tourId"
                                )
                            }
                            routeManager.saveRouteToFile(it, tourId, index, chunk.size)
                        }
                    }.onFailure { exception ->
                        Log.e(
                            "Route",
                            "Failed to fetch route for chunk $index of tour ID $tourId: ${exception.message}"
                        )
                    }
                }
            }
        }
    }
}


fun drawRoute(geometry: DirectionsResponse.Geometry, mapboxMap: MapboxMap) {
    val points = geometry.coordinates.map { com.mapbox.geojson.Point.fromLngLat(it[0], it[1]) }
    val lineString = LineString.fromLngLats(points)
    mapboxMap.getStyle { style ->
        val uniqueSourceId =
            "route_source_${System.currentTimeMillis()}" // Unique ID using time
        val uniqueLayerId = "route_layer_${System.currentTimeMillis()}" // Unique ID for layer

        // Remove old source and layer if needed or just create new ones
        if (style.getSourceAs<GeoJsonSource>(uniqueSourceId) != null) {
            style.removeStyleLayer(uniqueSourceId)
        }
        if (style.getLayer(uniqueLayerId) != null) {
            style.removeStyleLayer(uniqueLayerId)
        }

        // Add new source with a unique ID
        style.addSource(geoJsonSource(uniqueSourceId) {
            geometry(lineString)
        })

        // Add new layer with a unique ID
        style.addLayer(lineLayer(uniqueLayerId, uniqueSourceId) {
            lineCap(LineCap.ROUND)
            lineJoin(LineJoin.ROUND)
            lineWidth(5.0)
            lineColor("#0077ff")
            lineOpacity(0.5)
        })
    }
}
