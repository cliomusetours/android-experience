package com.cliomuseapp.cliomuseapp.feature.tour.composables


import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.hilt.navigation.compose.hiltViewModel
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.cliomuseexperience.core.extensions.shouldShowCircles
import com.cliomuseexperience.core.extensions.toCardBackground
import com.cliomuseexperience.core.extensions.toFormatDuration
import com.cliomuseexperience.core.extensions.toIcon
import com.cliomuseexperience.core.extensions.toItemBorder
import com.cliomuseexperience.core.extensions.toTextColor
import com.cliomuseexperience.core.presentation.theme.CommissionerFontFamily
import com.cliomuseexperience.core.presentation.theme.SdkcolorNewExperienceGreen
import com.cliomuseexperience.core.presentation.theme.SdkcolorTransparent
import com.cliomuseexperience.experiencecliomuse.R
import com.cliomuseexperience.feature.experience.domain.model.Story
import com.cliomuseexperience.feature.map.ui.MapViewModel
import kotlinx.coroutines.delay
import java.util.Locale

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun StoriesListItem(
    modifier: Modifier = Modifier,
    index: Int,
    story: Story,
    onClick: () -> Unit,
    viewModel: MapViewModel = hiltViewModel()
) {
    val type: String? = story.category?.type
    val backgroundColor = type.toCardBackground()
    val borderColor = type.toItemBorder()

    // Parent box to hold card and circles
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .clickable { onClick() }
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(90.dp)
                .border(width = 1.dp, color = borderColor, shape = RoundedCornerShape(8.dp))
                .background(backgroundColor, shape = RoundedCornerShape(8.dp))
                .padding(16.dp)
        ) {
            Row(modifier = Modifier.fillMaxSize()) {
                Box(modifier = Modifier.fillMaxHeight()) {
                    Box(
                        modifier = Modifier
                            .size(17.dp)
                            .align(Alignment.TopCenter),
                        contentAlignment = Alignment.Center
                    ) {
                        if (story.isSelected) {
                            if (viewModel.isPlaying) {
                                // Show the audio visualizer when playing
                                AudioVisualizerView(
                                    modifier = Modifier.fillMaxSize(),
                                    playbackState = PlaybackState.Playing,
                                    numberOfBars = 6
                                )
                            } else {
                                // Show the audio visualizer in paused state
                                AudioVisualizerView(
                                    modifier = Modifier.fillMaxSize(),
                                    playbackState = PlaybackState.Paused,
                                    numberOfBars = 6
                                )
                            }
                        } else {
                            Text(
                                modifier = Modifier.fillMaxSize(),
                                text = index.toString(),
                                style = TextStyle(
                                    fontSize = 14.sp,
                                    fontFamily = CommissionerFontFamily,
                                    fontWeight = FontWeight(400),
                                    color = Color(0xFFCDCDCD),
                                )
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.width(16.dp))

                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .weight(1f)
                ) {
                    story.title?.let { title ->
                        Text(
                            modifier = Modifier
                                .align(Alignment.TopStart),
                            text = title,
                            style = TextStyle(
                                fontSize = 14.sp,
                                fontFamily = CommissionerFontFamily,
                                fontWeight = FontWeight(500),
                                color = Color(0xFF161616),
                            )
                        )
                    }

                    story.category?.values?.getOrNull(0)?.let { value ->
                        Row(
                            modifier = Modifier.align(Alignment.CenterStart).padding(top=14.dp),
                            horizontalArrangement = Arrangement.Start,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Image(
                                painter = painterResource(id = value.toIcon()),
                                modifier = Modifier
                                    .size(20.dp)
                                    .clip(CircleShape),
                                contentDescription = stringResource(id = R.string.image_description),
                                contentScale = ContentScale.Inside
                            )

                            Spacer(modifier = Modifier.width(8.dp))

                            Text(
                                text = value,
                                style = TextStyle(
                                    fontSize = 12.sp,
                                    fontFamily = CommissionerFontFamily,
                                    fontWeight = FontWeight(500),
                                    color = type.toTextColor(),
                                )
                            )
                        }
                    }
                    story.duration?.let { duration ->
                        Text(
                            modifier = Modifier.align(Alignment.CenterEnd).padding(top=14.dp),
                            text = duration.toFormatDuration(),
                            style = TextStyle(
                                fontSize = 12.sp,
                                fontFamily = CommissionerFontFamily,
                                fontWeight = FontWeight(400),
                                color = Color(0xFFCDCDCD),
                            )
                        )
                    }
                }
            }
        }


        if (type.shouldShowCircles()) {
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    // Offset the circles so they appear to cut into the line
                    .offset(x = (-24).dp, y = (-5).dp)
                    .zIndex(10f)
            ) {
                Box(
                    modifier = Modifier
                        .background(backgroundColor)
                ) {
                    CirclesRow(difficulty = story.difficulty ?: "")
                }
            }
        }
    }
}

@Composable
fun CirclesRow(difficulty: String) {
    val filledColor = SdkcolorNewExperienceGreen
    val emptyColor = SdkcolorTransparent
    val borderColor = Color(0xFF5296A5)

    val circleColors = when (difficulty.lowercase(Locale.ROOT)) {
        "easy" -> listOf(filledColor, emptyColor, emptyColor, emptyColor)
        "mild" -> listOf(filledColor, filledColor, emptyColor, emptyColor)
        "difficult" -> listOf(filledColor, filledColor, filledColor, emptyColor)
        "hard" -> listOf(filledColor, filledColor, filledColor, filledColor)
        else -> listOf(emptyColor, emptyColor, emptyColor, emptyColor)
    }

    Row(horizontalArrangement = Arrangement.spacedBy(3.dp), modifier = Modifier.padding(start = 4.dp,end=4.dp)) {
        circleColors.forEach { color ->
            CircleShape(color = color, size = 10.dp, borderColor = borderColor)
        }
    }
}

@Composable
fun CircleShape(color: Color, size: Dp, borderColor: Color) {
    Box(
        modifier = Modifier
            .size(size)
            .background(color, shape = CircleShape)
            .border(1.dp, borderColor, shape = CircleShape)
    )
}




@Composable
fun AudioVisualizerView(
    modifier: Modifier = Modifier,
    numberOfBars: Int = 4,
    barWidth: Float = 1.5f,
    playbackState: PlaybackState,
    playingColor: Color = Color.Red,
    pausedColor: Color = Color.Gray
) {
    var barHeights by remember { mutableStateOf(List(numberOfBars) { 8f }) }
    LaunchedEffect(playbackState) {
        while (true) {
            barHeights = when (playbackState) {
                PlaybackState.Playing -> {
                    // Randomize heights between 2 and 16
                    List(numberOfBars) { (2..16).random().toFloat() }
                }

                PlaybackState.Paused,
                PlaybackState.Stopped -> {
                    List(numberOfBars) { 2f }
                }
            }
            delay(180)
        }
    }

    val currentColor = if (playbackState == PlaybackState.Playing) playingColor else pausedColor

    Box(
        modifier = modifier
            .padding(4.dp),
        contentAlignment =Alignment.Center
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(1.dp),
            verticalAlignment =Alignment.CenterVertically
        ) {
            barHeights.forEach { height ->
                val animatedHeight by animateFloatAsState(targetValue = height, label = "")

                Box(
                    modifier = Modifier
                        .width(barWidth.dp)
                        .height(animatedHeight.dp)
                        .clip(RoundedCornerShape(5.dp))
                        .background(currentColor)
                )
            }
        }
    }
}

enum class PlaybackState {
    Playing,
    Paused,
    Stopped
}

