package com.cliomuseexperience.feature.experience.ui.composables

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cliomuseexperience.core.presentation.composables.MapboxView
import com.cliomuseexperience.core.presentation.theme.CommissionerFontFamily
import com.cliomuseexperience.experiencecliomuse.R
import com.cliomuseexperience.feature.experience.domain.model.Item
import com.cliomuseexperience.feature.experience.domain.model.MapBounds
import com.cliomuseexperience.feature.experience.domain.model.Point
import com.cliomuseexperience.feature.experience.domain.model.StartingPoint
import com.cliomuseexperience.feature.experience.domain.model.Tour
import com.liulishuo.okdownload.OkDownloadProvider
import com.mapbox.common.MapboxOptions


@Composable
fun MapSection(
    mapBounds: MapBounds?,
    finishingPoint: StartingPoint?,
    startingPoint: StartingPoint?,
    itemsList: List<Item>,
    pointsList: List<Point>?,
    tour: Tour,
    customMapStyle : String,
    showRoute : Boolean,
    onMapListener: (Item) -> Unit
) {
    val accessToken = OkDownloadProvider.context.getString(R.string.mapbox_key)
    MapboxOptions.accessToken = accessToken

    Column(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        Spacer(modifier = Modifier.height(24.dp))

        Text(
            modifier = Modifier.padding(start = 16.dp, end = 16.dp),
            text = stringResource(id = R.string.starting_point),
            style = TextStyle(
                fontSize = 20.sp,
                fontFamily = CommissionerFontFamily,
                fontWeight = FontWeight(700),
                color = Color(0xFFE35056)
            )
        )

        Spacer(modifier = Modifier.height(24.dp))

        MapboxView(
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp),
            mapBounds = mapBounds,
            finishingPoint = finishingPoint,
            startingPoint = startingPoint,
            itemsList = itemsList,
            loadAnnotationsFlag = false,
            loadStartFinishPointIconsFlag = true,
            pointList = pointsList,
            loadRouteFlag = false,
            applyZoom = false,
            onAnnotationClick = {
                onMapListener(it)
            },
            loadCustomMapStyle = false,
            show3DButton = false,
            openGoogleMaps = true,
            customMapStyle = customMapStyle,
            showRoute = showRoute,
            tour = tour,
            showLocationButton = false,
            showPuck = false
        )


        Spacer(modifier = Modifier.height(24.dp))

        Row(
            modifier = Modifier
                .padding(start = 16.dp, end = 16.dp)
                .fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                startingPoint?.let { startPoint ->
                    MapSteps(
                        modifier = Modifier.padding(start = 0.dp, end = 0.dp),
                        image = R.drawable.start_icon,
                        point = startPoint
                    )
                }

                if (startingPoint != null && finishingPoint != null &&
                    (tour.startingPoint?.coordinates?.lat !=tour.finishingPoint?.coordinates?.lat || tour.startingPoint?.coordinates?.lon !=tour.finishingPoint?.coordinates?.lon)) {
                    // Show the dashed line only if starting and finishing points are different
                    VerticalDashedLine(
                        modifier = Modifier
                            .height(40.dp) // Adjust the height as needed
                            .padding(start = 19.dp, end = 15.dp)
                    )


                    MapSteps(
                        modifier = Modifier.padding(start = 0.dp, end = 0.dp),
                        image = R.drawable.finish_icon,
                        point = finishingPoint
                    )
                }
            }
        }
    }
}

@Composable
fun MapSteps(
    modifier: Modifier = Modifier,
    image: Int,
    point: StartingPoint
) {
    Row(
        modifier = modifier
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Image(
            modifier = Modifier
                .width(40.dp)
                .height(40.dp)
                .clip(RoundedCornerShape(4.dp)),
            painter = painterResource(id = image),
            contentScale = ContentScale.Crop,
            contentDescription = stringResource(id = R.string.ejemplo)
        )

        Spacer(modifier = Modifier.width(16.dp))

        Column {
            Text(
                text = point.name ?: "",
                style = TextStyle(
                    fontSize = 14.sp,
                    fontFamily = CommissionerFontFamily,
                    fontWeight = FontWeight(500),
                    color = Color(0xFF161616)
                )
            )

            Text(
                text = point.address ?: "",
                style = TextStyle(
                    fontSize = 14.sp,
                    fontFamily = CommissionerFontFamily,
                    fontWeight = FontWeight(400),
                    color = Color(0xFFCDCDCD)
                )
            )
        }
    }
}

@Composable
fun VerticalDashedLine(
    modifier: Modifier = Modifier,
    color: Color = Color.Black,
    dashLength: Dp = 4.dp,
    gapLength: Dp = 4.dp,
    lineThickness: Dp = 2.dp
) {
    Canvas(modifier = modifier.fillMaxHeight().width(lineThickness)) {
        val totalHeight = size.height
        val dashLengthPx = dashLength.toPx()
        val gapLengthPx = gapLength.toPx()
        var currentY = 0f

        while (currentY < totalHeight) {
            drawLine(
                color = color,
                start = Offset(x = size.width / 2, y = currentY),
                end = Offset(x = size.width / 2, y = currentY + dashLengthPx),
                strokeWidth = lineThickness.toPx()
            )
            currentY += dashLengthPx + gapLengthPx
        }
    }
}





