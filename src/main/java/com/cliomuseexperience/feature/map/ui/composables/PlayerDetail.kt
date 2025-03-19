package com.cliomuseexperience.feature.map.ui.composables

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.rememberTransformableState
import androidx.compose.foundation.gestures.transformable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import com.cliomuseexperience.core.presentation.composables.SmoothAppearDisappearAnimation
import com.cliomuseexperience.core.presentation.theme.CommissionerFontFamily
import com.cliomuseexperience.core.presentation.theme.SdkBlueMarine
import com.cliomuseexperience.experiencecliomuse.R
import com.cliomuseexperience.feature.experience.domain.model.Item
import com.cliomuseexperience.feature.map.domain.UIEvent
import com.cliomuseexperience.feature.map.ui.MapViewModel


@Composable
fun PlayerDetail(
    vm: MapViewModel,
    itemIndex: Int?,
    itemName: String?,
    storyTitle: String?,
    nextStepsList: List<Item>?,
    storyCategory: String?,
    audioFile: String?,
    storyBody: String?,
    storyColorBackground: Color,
    storyBackground: Color,
    itemsList: List<Item>?,
    progress: Float,
    storyValue: Int?,
    duration: Float,
    multimediaImage: ImageBitmap?,
    multimediaVideo: String?,
    isPlaying: Boolean,
    isExpanded: Boolean,
    resumeTitleColor: Color,
    onCompleteAudio: () -> Unit,
    onStepClickListener: (Item) -> Unit,
    playResourceProvider: () -> Int,
    progressProvider: () -> Pair<Float, String>,
    remainingTimeProvider: () -> String,
    slideProgressListener: (progress: Long) -> Unit,
    onUiEvent: (UIEvent) -> Unit
) {
    var showInfoStoryDetail by remember { mutableStateOf(false) }

    val showImageDialog by vm.showImageDialog.collectAsState()
    val showVideoDialog by vm.showVideoDialog.collectAsState()

    if (showImageDialog) {
        FullScreenImageDialog(imageBitmap = multimediaImage) {
            vm.setShowImageDialog(false)
        }
    }

    if (showVideoDialog) {
        FullScreenVideoDialog(videoUrl = multimediaVideo ?: "") {
            vm.setShowVideoDialog(false)
        }
    }

    Box {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(SdkBlueMarine)
                .padding(horizontal = 16.dp, vertical = 24.dp)
        ) {
            storyTitle?.let { title ->
                item {
                    Text(
                        text = title,
                        style = TextStyle(
                            fontSize = 22.sp,
                            fontFamily = CommissionerFontFamily,
                            fontWeight = FontWeight(700),
                            color = Color(0xFFFFFFFF),
                        )
                    )
                }

                item {
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }

            item {
                Text(
                    text = "${itemIndex}.${itemName}",
                    style = TextStyle(
                        fontSize = 14.sp,
                        fontFamily = CommissionerFontFamily,
                        fontWeight = FontWeight(400),
                        color = Color(0xFFCDCDCD),
                    )
                )
            }

            item {
                Spacer(modifier = Modifier.height(32.dp))
            }

            item {
                PlayerControls(
                    isPlaying = isPlaying,
                    duration = duration,
                    isExpanded = isExpanded,
                    storyTitle = storyTitle,
                    storyCategory = storyCategory,
                    onUiEvent = { onUiEvent(it) },
                    playResourceProvider = playResourceProvider,
                    progressProvider = progressProvider,
                    remainingTimeProvider = remainingTimeProvider
                )
            }

            item {
                Spacer(modifier = Modifier.height(40.dp))
            }

            item {
                PlayerInfoResume(
                    showMoreClickListener = { showInfoStoryDetail = true },
                    itemTitle = itemName,
                    storyValue = storyValue,
                    storyBody = storyBody,
                    storyColorBackground = storyColorBackground,
                    titleColor = resumeTitleColor,
                    storyTitle = storyTitle,
                )
            }

            item {
                Spacer(modifier = Modifier.height(32.dp))
            }

            // Conditionally show the MultimediaSection
            if (multimediaImage != null || !multimediaVideo.isNullOrEmpty()) {
                item {
                    MultimediaSection(
                        imageBitmap = multimediaImage,
                        videoUrl = multimediaVideo,
                        onShowImageDialog = { vm.setShowImageDialog(true) },
                        onShowVideoDialog = { vm.setShowVideoDialog(true) }
                    )
                }

                item {
                    Spacer(modifier = Modifier.height(32.dp))
                }
            }

            item {
                SmoothAppearDisappearAnimation(!nextStepsList.isNullOrEmpty()) {
                    NextStopsSection(
                        nextPoints = nextStepsList,
                        onStoryClickListener = { item ->
                            onStepClickListener(item)
                        }
                    )
                }
            }
        }

        AnimatedVisibility(
            visible = showInfoStoryDetail,
            enter = slideInVertically(
                initialOffsetY = { fullHeight -> fullHeight },
                animationSpec = tween(durationMillis = 300)
            ) + fadeIn(initialAlpha = 0.3f),
            exit = slideOutVertically(
                targetOffsetY = { fullHeight -> fullHeight },
                animationSpec = tween(durationMillis = 300)
            ) + fadeOut(targetAlpha = 0.0f)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Transparent)
                    .pointerInput(Unit) {
                        detectTapGestures { /* consume taps */ }
                    }
            ) {
                if (showInfoStoryDetail) {
                    Dialog(
                        onDismissRequest = { showInfoStoryDetail = false },
                        properties = DialogProperties(usePlatformDefaultWidth = false)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(storyBackground)
                        ) {
                            PlayerInfoFullScreen(
                                closeClickListener = { showInfoStoryDetail = false },
                                itemTitle = itemName,
                                storyValue = storyValue,
                                storyTitle = storyTitle,
                                storyBody = storyBody,
                                storyBackground = storyBackground,
                                duration = duration,
                                progress = progress,
                                durationString = remainingTimeProvider(),
                                onUiEvent = onUiEvent,
                                progressString = progressProvider().second,
                                isPlaying = isPlaying
                            )
                        }
                    }
                }
            }
        }
    }
}


@Composable
fun MultimediaSection(
    imageBitmap: ImageBitmap?,
    videoUrl: String?,
    onShowImageDialog: () -> Unit,
    onShowVideoDialog: () -> Unit
) {
    Column {
        Text(
            text = stringResource(id = R.string.multimedia),
            style = TextStyle(
                fontSize = 20.sp,
                fontFamily = CommissionerFontFamily,
                fontWeight = FontWeight(700),
                color = Color(0xFFFFFFFF)
            )
        )

        Spacer(modifier = Modifier.height(16.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .clickable {
                    if (!videoUrl.isNullOrEmpty()) {
                        onShowVideoDialog()
                    } else {
                        onShowImageDialog()
                    }
                }
        ) {
            if (!videoUrl.isNullOrEmpty()) {
                AndroidView(
                    modifier = Modifier.fillMaxSize(),
                    factory = {
                        PlayerView(it).apply {
                            player = ExoPlayer.Builder(it).build().apply {
                                setMediaItem(MediaItem.fromUri(videoUrl))
                                prepare()
                                playWhenReady = false
                            }
                        }
                    }
                )
            } else {
                Image(
                    modifier = Modifier
                        .fillMaxSize(),
                    bitmap = imageBitmap ?: ImageBitmap(1, 1),
                    contentDescription = stringResource(id = R.string.image_tour),
                    contentScale = ContentScale.Crop
                )
            }
        }
    }
}


@Composable
fun FullScreenImageDialog(imageBitmap: ImageBitmap?, onDismiss: () -> Unit) {
    val scale = remember { mutableStateOf(1f) }
    val offsetX = remember { mutableStateOf(0f) }
    val offsetY = remember { mutableStateOf(0f) }

    val transformableState = rememberTransformableState { zoomChange, panChange, _ ->
        val newScale = maxOf(1f, scale.value * zoomChange)
        scale.value = newScale

        if (newScale > 1f) {
            val scaledWidth = (imageBitmap?.width ?: 1) * newScale
            val scaledHeight = (imageBitmap?.height ?: 1) * newScale

            val maxXOffset = (scaledWidth - (imageBitmap?.width ?: 1)) / 2
            val maxYOffset = (scaledHeight - (imageBitmap?.height ?: 1)) / 2

            offsetX.value = (offsetX.value + panChange.x).coerceIn(-maxXOffset, maxXOffset)
            offsetY.value = (offsetY.value + panChange.y).coerceIn(-maxYOffset, maxYOffset)
        } else {
            offsetX.value = 0f
            offsetY.value = 0f
        }
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(SdkBlueMarine)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .transformable(state = transformableState)
            ) {
                Image(
                    modifier = Modifier
                        .fillMaxSize()
                        .graphicsLayer(
                            scaleX = scale.value,
                            scaleY = scale.value,
                            translationX = offsetX.value,
                            translationY = offsetY.value
                        ),
                    bitmap = imageBitmap ?: ImageBitmap(1, 1),
                    contentDescription = stringResource(id = R.string.image_tour),
                    contentScale = ContentScale.Fit
                )
            }

            // Close button positioned at the top end
            IconButton(
                onClick = onDismiss,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(16.dp)
                    .background(Color(0x80000000), shape = CircleShape)
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = stringResource(id = R.string.close),
                    tint = Color.White
                )
            }
        }
    }
}


@Composable
fun FullScreenVideoDialog(videoUrl: String, onDismiss: () -> Unit) {
    Dialog(onDismissRequest = onDismiss) {
        val context = LocalContext.current
        val exoPlayer = remember {
            ExoPlayer.Builder(context).build().apply {
                setMediaItem(MediaItem.fromUri(videoUrl))
                prepare()
                playWhenReady = true
            }
        }

        DisposableEffect(Unit) {
            onDispose {
                exoPlayer.release()
            }
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .clickable { onDismiss() })
    }
}



