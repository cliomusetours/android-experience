package com.cliomuseexperience.feature.map.ui.composables


import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cliomuseexperience.experiencecliomuse.R
import com.cliomuseexperience.core.presentation.theme.CommissionerFontFamily
import com.cliomuseexperience.core.presentation.theme.SdkBlackText
import com.cliomuseexperience.core.presentation.theme.SdkYellowInfoBackground
import com.cliomuseexperience.feature.map.domain.UIEvent

@Composable
fun PlayerInfoFullScreen(
    itemTitle: String?,
    storyValue: Int?,
    storyBody: String?,
    storyBackground: Color?,
    storyTitle: String?,
    closeClickListener: () -> Unit = {},
    progress: Float,
    duration: Float,
    durationString: String,
    progressString: String,
    onUiEvent: (UIEvent) -> Unit,
    isPlaying: Boolean,
) {
    Column(modifier = Modifier.fillMaxSize()) {
        // Scrollable content
        LazyColumn(
            modifier = Modifier
                .weight(1f)  // Ensures the LazyColumn takes available space and is scrollable
                .background(storyBackground ?: SdkYellowInfoBackground)
                .padding(horizontal = 16.dp, vertical = 24.dp)
        ) {
            item {
                InfoStoryHeather(
                    itemTitle = itemTitle,
                    storyValue = storyValue,
                    storyTitle = storyTitle,
                    true,
                    titleColor = Color.White,
                    closeClickListener,
                )
            }
            item {
                Spacer(modifier = Modifier.height(16.dp))
            }

            storyTitle?.let { title ->
                item {
                    Text(
                        text = title,
                        style = TextStyle(
                            fontSize = 22.sp,
                            lineHeight = 35.sp,
                            fontFamily = CommissionerFontFamily,
                            fontWeight = FontWeight(600),
                            color = Color(0xFFFFFFFF),
                        )
                    )
                }
            }

            item {
                Spacer(modifier = Modifier.height(4.dp))
            }

            storyBody?.let { body ->
                item {
                    Text(
                        text = body,
                        style = TextStyle(
                            fontSize = 22.sp,
                            lineHeight = 35.sp,
                            fontFamily = CommissionerFontFamily,
                            fontWeight = FontWeight(600),
                            color = SdkBlackText,
                        )
                    )
                }
            }

        }

        // Fixed progress bar and controls with consistent background color
        Box(
            modifier = Modifier
                .background(storyBackground ?: SdkYellowInfoBackground)
                .padding(16.dp)
                .fillMaxWidth()
                .height(100.dp),
        ) {
            PlayerBar(
                progress = progress,
                duration = duration,
                durationString = durationString,
                progressString = progressString,
                onUiEvent = onUiEvent,
                color = Color.White,
            )

            IconButton(
                modifier = Modifier
                    .size(80.dp)
                    .align(Alignment.Center)
                    .padding(top = 24.dp)  ,
                // Aligns play/pause button to the center of the Box
                onClick = { onUiEvent(UIEvent.PlayPause) }
            ) {
                Image(
                    painter = painterResource(id = if (isPlaying) R.drawable.playercontrolspause else R.drawable.playercontrolsplay),
                    contentDescription = if (isPlaying) stringResource(id = R.string.pause) else stringResource(id = R.string.play)
                )
            }
        }
    }
}






