package com.cliomuseexperience.feature.map.ui.composables

import androidx.compose.foundation.Image
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.ProgressIndicatorDefaults
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cliomuseexperience.core.presentation.composables.MarqueeSingleLineText
import com.cliomuseexperience.core.presentation.theme.SdkClioRed
import com.cliomuseexperience.experiencecliomuse.R
import com.cliomuseexperience.feature.map.domain.UIEvent

@Composable
fun PlayerControls(
    storyTitle: String?,
    storyCategory: String?,
    isPlaying: Boolean,
    isExpanded: Boolean,
    duration: Float,
    playResourceProvider: () -> Int,
    progressProvider: () -> Pair<Float, String>,
    remainingTimeProvider: () -> String,
    onUiEvent: (UIEvent) -> Unit
) {
    
    if (isExpanded) {
        ExpandedPlayerControls(
            onUiEvent = { onUiEvent(it) },
            duration = duration,
            playResourceProvider = playResourceProvider,
            progressProvider = progressProvider,
            remainingTimeProvider = remainingTimeProvider,
            isPlaying = isPlaying
        )
    } else {
        CollapsedPlayerControls(
            isPlaying = isPlaying,
            storyTitle = storyTitle,
            storyCategory = storyCategory,
            progressProvider = progressProvider,
            duration = duration,
            onUiEvent = { onUiEvent(it) }
        )
    }
}


@Composable
fun CollapsedPlayerControls(
    storyTitle: String?,
    storyCategory: String?,
    duration: Float,
    isPlaying: Boolean,
    progressProvider: () -> Pair<Float, String>,
    onUiEvent: (UIEvent) -> Unit
) {
    val (progress, progressString) = progressProvider()

    
    ProgressBarCollapsedSheet(
        progress = progress,
        color = SdkClioRed,
        backgroundColor = Color(0xFFCDCDCD),
        modifier = Modifier.padding(horizontal = 25.dp) .offset(y = (-18).dp)
    )

    Box(
        modifier = Modifier.fillMaxWidth()
    ) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 25.dp,end = 25.dp, bottom =25.dp,top=4.dp) ,
        ) {
            if (storyTitle != null || storyCategory != null) {
                // Swipeable Title and Category Area
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .pointerInput(Unit) {
                            var dragDistance = 0f
                            detectHorizontalDragGestures(
                                onHorizontalDrag = { change, dragAmount ->
                                    dragDistance += dragAmount
                                },
                                onDragEnd = {
                                    val swipeThreshold = 50.dp.toPx()
                                    when {
                                        dragDistance > swipeThreshold -> {
                                            // Swipe Right: Previous Story
                                            onUiEvent(UIEvent.SkipPrevious)
                                        }
                                        dragDistance < -swipeThreshold -> {
                                            // Swipe Left: Next Story
                                            onUiEvent(UIEvent.SkipNext)
                                        }
                                    }
                                    // Reset drag distance after handling
                                    dragDistance = 0f
                                },
                                onDragCancel = {
                                    // Reset drag distance on cancellation
                                    dragDistance = 0f
                                }
                            )
                        }
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth(),
                        verticalArrangement = Arrangement.Center
                    ) {
                        storyTitle?.let { title ->
                            MarqueeSingleLineText(
                                text = title,
                                textStyle = TextStyle(
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight(400),
                                    color = Color(0xFFFFFFFF),
                                ),
                                speed = 50f,
                                initialDelay = 300L // adjust as needed
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                        }

                        storyCategory?.let {
                            Text(
                                text = storyCategory, // .values?.get(0) ?: "",
                                style = TextStyle(
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight(500),
                                    color = Color(0xFFFFFFFF),
                                )
                            )
                        }
                    }
                }


                Row(
                    modifier = Modifier
                        .fillMaxHeight(),
                    verticalAlignment = Alignment.Top,
                ) {
                    IconButton(
                        modifier = Modifier
                            .size(40.dp)
                            .padding(end = 0.dp),
                        onClick = { onUiEvent(UIEvent.Backward) }) {
                        Image(
                            painter = painterResource(id = R.drawable.playercontrolsgobackwards10),
                            contentDescription = stringResource(id = R.string.rewind_10)
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    IconButton(
                        modifier = Modifier.size(40.dp),
                        onClick = { onUiEvent(UIEvent.PlayPause) }
                    ) {
                        Image(
                            painter = painterResource(id = if (isPlaying) R.drawable.playercontrolspause else R.drawable.playercontrolsplay),
                            contentDescription = if (isPlaying) stringResource(id = R.string.pause) else stringResource(
                                id = R.string.play
                            )
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    IconButton(
                        modifier = Modifier
                            .size(40.dp)
                            .padding(end = 0.dp),
                        onClick = { onUiEvent(UIEvent.Forward) }) {
                        Image(
                            painter = painterResource(id = R.drawable.playercontrolsgobackwards10),
                            contentDescription = stringResource(id = R.string.forward_10)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ExpandedPlayerControls(
    duration: Float,
    playResourceProvider: () -> Int,
    progressProvider: () -> Pair<Float, String>,
    remainingTimeProvider: () -> String, // New parameter for remaining time
    onUiEvent: (UIEvent) -> Unit,
    isPlaying: Boolean
) {
    val (progress, progressString) = progressProvider()
    val remainingTimeString = remainingTimeProvider() // Get remaining time string

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            PlayerBar(
                progress = progress,
                duration = duration,
                durationString = remainingTimeString, // Use remaining time string
                progressString = progressString,
                onUiEvent = { onUiEvent(it) }
            )
            PlayerControlButtons(
                playResourceProvider = playResourceProvider,
                onUiEvent = onUiEvent,
                isPlaying = isPlaying
            )
        }
    }
}


@Composable
fun PlayerControlButtons(
    playResourceProvider: () -> Int,
    onUiEvent: (UIEvent) -> Unit,
    isPlaying: Boolean,
) {

    Row(
        modifier = Modifier
            .fillMaxHeight(),
        verticalAlignment = Alignment.Top,
    ) {
        IconButton(
            modifier = Modifier
                .size(60.dp)
                .padding(end = 8.dp),
            onClick = { onUiEvent(UIEvent.Backward) }) {
            Image(
                painter = painterResource(id = R.drawable.playercontrolsgobackwards10),
                contentDescription = stringResource(id = R.string.rewind_10)
            )
        }

        Spacer(modifier = Modifier.width(8.dp))

        IconButton(
            modifier = Modifier
                .size(60.dp)
                .padding(end = 8.dp),
            onClick = { onUiEvent(UIEvent.SkipPrevious) }) {
            Image(
                painter = painterResource(id = R.drawable.playercontrolsskipbackwards),
                contentDescription = stringResource(id = R.string.rewind_10)
            )
        }

        Spacer(modifier = Modifier.width(8.dp))

        IconButton(
            modifier = Modifier.size(60.dp),
            onClick = {
                onUiEvent(UIEvent.PlayPause)
            }
        ) {
            Image(
                painter = painterResource(id = if (isPlaying) R.drawable.playercontrolspause else R.drawable.playercontrolsplay),
                contentDescription = if (isPlaying) stringResource(id = R.string.pause) else stringResource(
                    id = R.string.play
                )
            )
        }

        Spacer(modifier = Modifier.width(8.dp))

        IconButton(
            modifier = Modifier
                .size(60.dp)
                .padding(end = 8.dp),
            onClick = { onUiEvent(UIEvent.SkipNext) }) {
            Image(
                painter = painterResource(id = R.drawable.playercontrolsskipforward),
                contentDescription = stringResource(id = R.string.forward_10)
            )
        }

        Spacer(modifier = Modifier.width(8.dp))

        IconButton(
            modifier = Modifier
                .size(60.dp)
                .padding(end = 8.dp),
            onClick = { onUiEvent(UIEvent.Forward) }) {
            Image(
                painter = painterResource(id = R.drawable.playercontrolsgoforward10),
                contentDescription = stringResource(id = R.string.forward_10)
            )
        }
    }
}


@Composable
fun PlayerBar(
    progress: Float,
    duration: Float,
    durationString: String? = null, // This will now be the remaining time string
    progressString: String? = null,
    onUiEvent: (UIEvent) -> Unit = {},
    color: Color = SdkClioRed
) {
    val newProgressValue = remember { mutableStateOf(0f) }
    val useNewProgressValue = remember { mutableStateOf(false) }

    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Slider(
            value = if (useNewProgressValue.value) newProgressValue.value else progress,
            onValueChange = { newValue ->
                useNewProgressValue.value = true
                newProgressValue.value = newValue
                onUiEvent(UIEvent.UpdateProgress(newProgress = newValue))
                // isComplete
                if (newValue == duration) {
                    onUiEvent(UIEvent.SkipNext)
                }
            },
            onValueChangeFinished = {
                useNewProgressValue.value = false
            },
            modifier = Modifier
                .padding(horizontal = 8.dp),
            colors = SliderDefaults.colors(
                thumbColor = color, // Makes the thumb invisible
                activeTrackColor = color, // Dynamic color for the progress bar
                inactiveTrackColor = Color.Gray.copy(alpha = 0.3f) // Background color
            )
        )

        if (progressString != null && durationString != null) {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            ) {
                Text(
                    text = progressString,
                    color = Color.White,
                    fontSize = 14.sp
                )
                Text(
                    text = durationString,
                    color = Color.White,
                    fontSize = 14.sp
                )
            }
        }
    }
}


@Composable
fun ProgressBarCollapsedSheet(
    modifier: Modifier = Modifier,
    progress: Float,
    color: Color = Color(0xFFE35056),
    backgroundColor: Color = Color(0xFFCDCDCD)
) {
    val clampedProgress = progress.coerceIn(0f, 1f)

    LinearProgressIndicator(
        progress = { clampedProgress },
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp),
        color = color,
        trackColor = backgroundColor,
        strokeCap = ProgressIndicatorDefaults.LinearStrokeCap,
    )
}



@Preview
@Composable
fun PlayerBarPreview() {
    PlayerBar(
        progress = 0.5f,
        duration = 100f,
        durationString = "1:00",
        progressString = "0:30"
    )
}

