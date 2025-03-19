package com.cliomuseapp.cliomuseapp.feature.tour.composables

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import com.cliomuseexperience.core.extensions.toPointsListImage
import com.cliomuseexperience.feature.experience.domain.model.Item


@Composable
fun TourImageSection(
    item: Item?,
    selectedItemState: Item?,
    modifier: Modifier = Modifier
) {
    val imageBitmap = item?.imageFile?.toPointsListImage() ?: return

    // Determine if this is the currently selected (active) item
    val isActive = (item == selectedItemState)

    // Decide the desired height based on whether it's active
    val targetHeight = if (isActive) 358.dp else 313.dp

    val animatedHeight by animateDpAsState(targetValue = targetHeight, label = "")

    Column(
        modifier = modifier
            .padding(8.dp)
            .width(284.dp)
    ) {
        Box(
            modifier = Modifier
                .height(animatedHeight)
                .clip(RoundedCornerShape(8.dp))
        ) {
            Image(
                bitmap = imageBitmap,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}


