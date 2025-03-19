package com.cliomuseexperience.core.presentation.composables

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun MarqueeText(
    image: ImageBitmap,
    itemIndex: Int?,
    title: String,
    modifier: Modifier = Modifier,
    textStyle: TextStyle = TextStyle.Default,
    initialDelay: Long = 1000L, // delay before starting the animation
    speed: Float = 50f // speed of the marquee
) {
    val textWidth = remember { mutableStateOf(0f) }
    val containerWidth = remember { mutableStateOf(0f) }
    val animatedOffset = remember { Animatable(0f) }

    LaunchedEffect(textWidth.value, containerWidth.value) {
        if (textWidth.value > 0 && containerWidth.value > 0) {
            animatedOffset.snapTo(containerWidth.value)
            animatedOffset.animateTo(
                targetValue = -textWidth.value,
                animationSpec = infiniteRepeatable(
                    animation = tween(
                        durationMillis = ((textWidth.value + containerWidth.value) / speed * 500).toInt(),
                        easing = LinearEasing
                    ),
                    repeatMode = RepeatMode.Restart
                )
            )
        }
    }

    Row(
        modifier = Modifier.padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            bitmap = image ?: return@Row,
            contentDescription = null,
            modifier = Modifier
                .size(40.dp)
                .clip(RoundedCornerShape(8.dp)),
            contentScale = androidx.compose.ui.layout.ContentScale.Crop
        )

        Spacer(modifier = Modifier.size(16.dp))

        Box(
            modifier = modifier
                //  .background(Color.White) // For better visibility of the marquee
                .onGloballyPositioned { coordinates ->
                    containerWidth.value = coordinates.size.width.toFloat()
                }
                .clipToBounds() // Ensure the text does not go out of bounds
        ) {
            val index = if (itemIndex != null) {
                if (itemIndex < 10) "0$itemIndex. " else "$itemIndex. "
            } else {
                ""
            }


            Text(
                text = if(itemIndex != null) index + title else title,
                style = textStyle.copy(fontSize = 20.sp),
                modifier = Modifier
                    .offset { IntOffset(animatedOffset.value.toInt(), 0) }
                    .onGloballyPositioned { coordinates ->
                        textWidth.value = coordinates.size.width.toFloat()
                    }
            )
        }
    }
}

@Composable
fun MarqueeTextFold(
    image: ImageBitmap,
    itemIndex: Int?,
    title: String,
    modifier: Modifier = Modifier,
    textStyle: TextStyle = TextStyle.Default
) {
    Row(
        modifier = Modifier.padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            bitmap = image ?: return@Row,
            contentDescription = null,
            modifier = Modifier
                .size(40.dp)
                .clip(RoundedCornerShape(8.dp)),
            contentScale = ContentScale.Crop
        )

        Spacer(modifier = Modifier.size(16.dp))

        Box(
            modifier = modifier
                .fillMaxWidth() // Ensure the Box takes available width
                .clipToBounds() // Not strictly needed now, but can keep
        ) {
            val indexPrefix = if (itemIndex != null) {
                if (itemIndex < 10) "$itemIndex. " else "$itemIndex. "
            } else {
                ""
            }

            Text(
                text = if (itemIndex != null) indexPrefix + title else title,
                style = textStyle.copy(fontSize = 14.sp),
                // Remove offset and global positioning logic
                // Let the text wrap naturally if it's too long
            )
        }
    }
}


@Composable
fun MarqueeSingleLineText(
    text: String,
    textStyle: TextStyle = TextStyle.Default,
    speed: Float = 50f, // Control how fast the text moves
    initialDelay: Long = 1000L // Delay before starting the scroll
) {
    val textWidth = remember { mutableStateOf(0f) }
    val containerWidth = remember { mutableStateOf(0f) }
    val animatedOffset = remember { Animatable(0f) }

    // Only start animation if text doesn't fit fully
    LaunchedEffect(textWidth.value, containerWidth.value) {
        if (textWidth.value > containerWidth.value && containerWidth.value > 0) {
            // Start from the container's width (text starts off-screen to the right)
            animatedOffset.snapTo(containerWidth.value)

            // Delay before start
            kotlinx.coroutines.delay(initialDelay)

            animatedOffset.animateTo(
                targetValue = -textWidth.value,
                animationSpec = infiniteRepeatable(
                    animation = tween(
                        durationMillis = ((textWidth.value + containerWidth.value) / speed * 500).toInt(),
                        easing = LinearEasing
                    ),
                    repeatMode = RepeatMode.Restart
                )
            )
        } else {
            // If it fits, just ensure we are at offset 0
            animatedOffset.snapTo(0f)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clipToBounds()
            .onGloballyPositioned { coordinates ->
                containerWidth.value = coordinates.size.width.toFloat()
            }
    ) {
        Text(
            text = text,
            style = textStyle,
            maxLines = 1,
            overflow = TextOverflow.Clip,
            modifier = Modifier
                .offset { IntOffset(animatedOffset.value.toInt(), 0) }
                .onGloballyPositioned { coordinates ->
                    textWidth.value = coordinates.size.width.toFloat()
                }
        )
    }
}

