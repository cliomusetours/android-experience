package com.cliomuseexperience.feature.map.ui.composables


import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.viewinterop.AndroidView
import com.davemorrissey.labs.subscaleview.ImageSource
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView

@Composable
fun IndoorImageScreen(
    indoorImage: ImageBitmap?,
    modifier: Modifier = Modifier
) {
    indoorImage?.let { imageBitmap ->
        Box(
            modifier = modifier
                .background(Color.White)
        ) {
            AndroidView(
                modifier = Modifier.matchParentSize(),
                factory = { context ->
                    SubsamplingScaleImageView(context).apply {
                        setImage(ImageSource.bitmap(imageBitmap.asAndroidBitmap()))
                        setMinimumScaleType(SubsamplingScaleImageView.SCALE_TYPE_CENTER_INSIDE)
                        maxScale = 10.0f
                        isZoomEnabled = true
                        isPanEnabled = true
                        isQuickScaleEnabled = true
                    }
                },
                update = {
                    // You can update view settings here if needed
                }
            )
        }
    } ?: run {
        // Fallback UI if the image isn't available.
        Text(
            text = "Image not available",
            modifier = Modifier.fillMaxSize(),
            color = Color.White,
            textAlign = TextAlign.Center
        )
    }
}