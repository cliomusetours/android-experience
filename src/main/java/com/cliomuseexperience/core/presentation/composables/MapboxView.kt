package com.cliomuseexperience.core.presentation.composables


import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.net.Uri
import android.view.MotionEvent
import android.widget.Toast
import androidx.appcompat.content.res.AppCompatResources
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterEnd
import androidx.compose.ui.Alignment.Companion.TopEnd
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.cliomuseexperience.core.extensions.getRoute
import com.cliomuseexperience.core.extensions.loadAnnotations
import com.cliomuseexperience.core.extensions.loadStartFinishPointIcons
import com.cliomuseexperience.core.presentation.theme.CommissionerFontFamily
import com.cliomuseexperience.feature.experience.domain.model.Item
import com.cliomuseexperience.feature.experience.domain.model.MapBounds
import com.cliomuseexperience.feature.experience.domain.model.Point
import com.cliomuseexperience.feature.experience.domain.model.StartingPoint
import com.cliomuseexperience.feature.experience.domain.model.Tour
import com.mapbox.android.gestures.MoveGestureDetector
import com.mapbox.maps.CameraBoundsOptions
import com.mapbox.maps.CameraOptions
import com.mapbox.maps.CoordinateBounds
import com.mapbox.maps.EdgeInsets
import com.mapbox.maps.ImageHolder
import com.mapbox.maps.MapView
import com.mapbox.maps.R
import com.mapbox.maps.Style
import com.mapbox.maps.extension.style.expressions.dsl.generated.interpolate
import com.mapbox.maps.plugin.LocationPuck2D
import com.mapbox.maps.plugin.gestures.OnMoveListener
import com.mapbox.maps.plugin.gestures.addOnMapClickListener
import com.mapbox.maps.plugin.gestures.gestures
import com.mapbox.maps.plugin.locationcomponent.OnIndicatorBearingChangedListener
import com.mapbox.maps.plugin.locationcomponent.OnIndicatorPositionChangedListener
import com.mapbox.maps.plugin.locationcomponent.location
import com.mapbox.geojson.Point as MapboxPoint

@SuppressLint("RememberReturnType")
@Composable
fun MapboxView(
    modifier: Modifier = Modifier,
    mapBounds: MapBounds?,
    customMapStyle: String?,
    showRoute : Boolean,
    finishingPoint: StartingPoint?,
    startingPoint: StartingPoint?,
    itemsList: List<Item>?,
    pointList: List<Point>?,
    tour: Tour?,
    loadAnnotationsFlag: Boolean,
    loadStartFinishPointIconsFlag: Boolean,
    loadRouteFlag: Boolean,
    applyZoom: Boolean,
    show3DButton: Boolean,
    showLocationButton: Boolean,
    onAnnotationClick: (Item) -> Unit,
    loadCustomMapStyle: Boolean,
    openGoogleMaps: Boolean,
    showPuck: Boolean
) {
    val minZoom = 12.0  // Minimum allowed zoom level
    val maxZoom = 19.0 // Maximum allowed zoom level
    val context = LocalContext.current
    var currentPitch by remember { mutableDoubleStateOf(0.0) } // Initialize pitch to 0 degrees
    var is3D by remember { mutableStateOf(false) } // Boolean to track the toggle state
    val mapView = remember {
        MapView(context).apply {

            // Set up the touch listener to prevent parent interception
            setOnTouchListener { v, event ->
                when (event.action) {
                    MotionEvent.ACTION_DOWN, MotionEvent.ACTION_MOVE -> {
                        // Disallow parent to intercept touch events when interacting with the map
                        v.parent.requestDisallowInterceptTouchEvent(true)
                    }

                    MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                        // Allow parent to intercept touch events when not interacting with the map
                        v.parent.requestDisallowInterceptTouchEvent(false)

                        // Perform click action for accessibility purposes
                        if (event.action == MotionEvent.ACTION_UP) {
                            v.performClick()
                        }
                    }
                }
                false // Return false so the MapView handles its own touch events
            }
            // Set up the map click listener to open Google Maps
            if (openGoogleMaps) {
                mapboxMap.addOnMapClickListener { point ->
                    val latitude = point.latitude()
                    val longitude = point.longitude()
                    val uri = "geo:$latitude,$longitude"
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(uri))
                    intent.setPackage("com.google.android.apps.maps")
                    if (intent.resolveActivity(context.packageManager) != null) {
                        context.startActivity(intent)
                    } else {
                        Toast.makeText(context, "Google Maps is not installed", Toast.LENGTH_SHORT)
                            .show()
                    }
                    true
                }
            }
            if (mapBounds != null) {
                mapBounds.let { bounds ->
                    val coordinateBounds = bounds.southWest?.lon?.let { swLon ->
                        bounds.southWest?.lat?.let { swLat ->
                            MapboxPoint.fromLngLat(swLon, swLat)
                        }
                    }?.let { swPoint ->
                        bounds.northEast?.lon?.let { neLon ->
                            bounds.northEast?.lat?.let { neLat ->
                                MapboxPoint.fromLngLat(neLon, neLat)
                            }
                        }?.let { nePoint ->
                            CoordinateBounds(swPoint, nePoint)
                        }
                    }

                    coordinateBounds?.let {
                        val cameraOptions = mapboxMap.cameraForCoordinateBounds(
                            it,
                            EdgeInsets(50.0, 50.0, 50.0, 50.0)
                        ).toBuilder()
                            .zoom(13.5.coerceIn(minZoom, maxZoom))
                            .build()

                        mapboxMap.setCamera(cameraOptions)

                        val currentCameraState = mapboxMap.cameraState
                        val center = currentCameraState.center // Center point of the camera
                        val currentZoom = currentCameraState.zoom // Current zoom level


                        val latMargin = 0.02 // Adjust margin as needed
                        val lonMargin = 0.02

                        val restrictedSouthWest = MapboxPoint.fromLngLat(
                            center.longitude() - lonMargin,
                            center.latitude() - latMargin
                        )
                        val restrictedNorthEast = MapboxPoint.fromLngLat(
                            center.longitude() + lonMargin,
                            center.latitude() + latMargin
                        )

                        val restrictedBounds = CoordinateBounds(restrictedSouthWest, restrictedNorthEast)

                        val cameraBoundsOptions = CameraBoundsOptions.Builder()
                            .bounds(restrictedBounds) // Set bounds based on initial camera position
                            .minZoom(minZoom)
                            .maxZoom(maxZoom)
                            .build()
                        mapboxMap.setBounds(cameraBoundsOptions)

                        mapboxMap.setCamera(
                            CameraOptions.Builder()
                                .center(center)
                                .zoom(currentZoom)
                                .build()
                        )
                    }
                }
            } else {
                // Fallback logic for tours without bounds
                tour?.let { firstItem ->
                    val lat = firstItem.lat?.toDoubleOrNull()
                    val lon = firstItem.lon?.toDoubleOrNull()
                    if (lat != null && lon != null) {
                        val point = MapboxPoint.fromLngLat(lon, lat)
                        val cameraOptions = CameraOptions.Builder()
                            .center(point)
                            .zoom(13.5.coerceIn(minZoom, maxZoom))
                            .build()
                        mapboxMap.setCamera(cameraOptions)

                        // Restrict navigation to a small area around the center
                        val latRange = 0.02
                        val lonRange = 0.02

                        val restrictedSouthWest = MapboxPoint.fromLngLat(
                            lon - lonRange,
                            lat - latRange
                        )
                        val restrictedNorthEast = MapboxPoint.fromLngLat(
                            lon + lonRange,
                            lat + latRange
                        )

                        val restrictedBounds = CoordinateBounds(restrictedSouthWest, restrictedNorthEast)

                        val cameraBoundsOptions = CameraBoundsOptions.Builder()
                            .bounds(restrictedBounds)
                            .minZoom(minZoom)
                            .maxZoom(maxZoom)
                            .build()
                        mapboxMap.setBounds(cameraBoundsOptions)
                    }
                }
            }


            if (applyZoom) {
                itemsList?.firstOrNull()?.let { firstItem ->
                    val lat = firstItem.lat?.toDoubleOrNull()
                    val lon = firstItem.lon?.toDoubleOrNull()
                    if (lat != null && lon != null) {
                        val point = MapboxPoint.fromLngLat(lon, lat)
                        val cameraOptions = CameraOptions.Builder()
                            .center(point)
                            .zoom(16.0.coerceIn(minZoom, maxZoom))
                            .build()
                        mapboxMap.setCamera(cameraOptions)
                    }
                }
            }
            val mapStyle = if (loadCustomMapStyle) customMapStyle else null
            mapboxMap.loadStyle(mapStyle ?: Style.STANDARD) { style ->
                loadStartFinishPointIcons(
                    context,
                    style,
                    startingPoint,
                    finishingPoint,
                    loadStartFinishPointIconsFlag
                )
                if(showPuck)
                enableLocationComponentOldWay(this, context)
            }

            itemsList?.let {
                if (loadAnnotationsFlag)
                    loadAnnotations(context, this, itemsList) { item ->
                        onAnnotationClick(item)
                    }
                if (loadRouteFlag && showRoute) //If the showRoute flag is true, show the route
                    getRoute(context, mapboxMap, itemsList.first().id, pointList)
            }


        }

    }

    val onIndicatorBearingChangedListener = remember {
        OnIndicatorBearingChangedListener { bearing ->
            mapView.mapboxMap.setCamera(
                CameraOptions.Builder().bearing(bearing).build()
            )
        }
    }
    val onIndicatorPositionChangedListener = remember {
        OnIndicatorPositionChangedListener { point ->
            mapView.mapboxMap.setCamera(
                CameraOptions.Builder().center(point).build()
            )
            mapView.gestures.focalPoint = mapView.mapboxMap.pixelForCoordinate(point)
        }
    }
    val onMoveListener = remember {
        object : OnMoveListener {
            override fun onMove(detector: MoveGestureDetector): Boolean = false
            override fun onMoveBegin(detector: MoveGestureDetector) {
                // Stop tracking if user pans the map
                onCameraTrackingDismissed(
                    mapView,
                    onIndicatorPositionChangedListener,
                    onIndicatorBearingChangedListener,
                    this
                )
            }
            override fun onMoveEnd(detector: MoveGestureDetector) {}
        }
    }
    Box(modifier = modifier.fillMaxSize()) {
        AndroidView(
            factory = { mapView },
            modifier = Modifier.fillMaxSize()
        )

        if (show3DButton) {
            ThreeDToggleButton(
                is3D = is3D,
                onClick = {
                    is3D = !is3D
                    currentPitch = if (is3D) 60.0 else 0.0
                    mapView.mapboxMap.setCamera(
                        CameraOptions.Builder()
                            .pitch(currentPitch)
                            .build()
                    )
                },
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(end = 12.dp, bottom = 72.dp)
            )
        }

        if (showLocationButton) {
            FloatingActionButton(
                onClick = {
                    mapView.location.addOnIndicatorPositionChangedListener(
                        onIndicatorPositionChangedListener
                    )
                    mapView.location.addOnIndicatorBearingChangedListener(
                        onIndicatorBearingChangedListener
                    )
                    mapView.gestures.addOnMoveListener(onMoveListener)
                },
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(end = 12.dp, bottom = 12.dp)
                    .size(40.dp),
                containerColor = Color.White,
                contentColor = Color.Red
            ) {
                Icon(
                    imageVector = Icons.Default.MyLocation,
                    contentDescription = "Go to My Location",
                    tint = Color(0xFFE2545A)
                )
            }
        }
    }

}

fun enableLocationComponentOldWay(mapView: MapView, context: Context) {
    val locationComponentPlugin = mapView.location
    val scaleExpression = interpolate {
        linear()
        zoom()
        literal(0.1)
    }

    // Enable the location component + custom puck
    locationComponentPlugin.updateSettings {
        enabled = true
        locationPuck = LocationPuck2D(
            bearingImage = AppCompatResources.getDrawable(
                context,
                R.drawable.mapbox_user_puck_icon
            )?.let { drawableToImageHolder(it, context) },
            shadowImage = AppCompatResources.getDrawable(
                context,
                R.drawable.mapbox_user_icon_shadow
            )?.let { drawableToImageHolder(it, context) },
            scaleExpression = scaleExpression.toJson()
        )
    }
}

fun onCameraTrackingDismissed(
    mapView: MapView,
    onPosition: OnIndicatorPositionChangedListener,
    onBearing: OnIndicatorBearingChangedListener,
    onMove: OnMoveListener
) {
    mapView.location.removeOnIndicatorPositionChangedListener(onPosition)
    mapView.location.removeOnIndicatorBearingChangedListener(onBearing)
    mapView.gestures.removeOnMoveListener(onMove)
}

//  Converts a drawable into an ImageHolder for the Mapbox 2D puck.
fun drawableToImageHolder(drawable: Drawable, context: Context): ImageHolder {
    val bitmap = Bitmap.createBitmap(
        drawable.intrinsicWidth,
        drawable.intrinsicHeight,
        Bitmap.Config.ARGB_8888
    )
    val canvas = Canvas(bitmap)
    drawable.setBounds(0, 0, canvas.width, canvas.height)
    drawable.draw(canvas)
    return ImageHolder.from(bitmap)
}


@Composable
fun ThreeDToggleButton(
    is3D: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(Color.White,
            contentColor = Color.Red
        ),
        shape = RoundedCornerShape(10.dp),
        elevation = ButtonDefaults.buttonElevation(4.dp),
        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
        modifier = modifier.size(40.dp)
    ) {
        // Use a Box to center the text within the button
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = if (is3D) "2D" else "3D",
                style = TextStyle(
                    fontSize = 16.sp,
                    fontFamily = CommissionerFontFamily,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFE2545A),

                    ),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}


@Preview(showBackground = true)
@Composable
fun PreviewThreeDimensionalToggleButton() {
    var is3D by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize()) {
        if (true) {
            ThreeDToggleButton(
                is3D = is3D,
                onClick = { is3D = !is3D },
                modifier = Modifier.align(Alignment.Center)
            )
        }
    }
}




