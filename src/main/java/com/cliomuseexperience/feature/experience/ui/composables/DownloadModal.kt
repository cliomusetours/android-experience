package com.cliomuseexperience.feature.experience.ui.composables

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.ProgressIndicatorDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cliomuseexperience.core.presentation.theme.CommissionerFontFamily
import com.cliomuseexperience.core.presentation.theme.SdkBlackOff
import com.cliomuseexperience.experiencecliomuse.R
import com.cliomuseexperience.feature.experience.domain.DownloadStatus


@Composable
fun DownloadModal(
    progress: Float,
    totalSize: String,
    currentSize: String,
    percentage: String,
    stateDownload: DownloadStatus?,
    cancelDownload: () -> Unit,
    retryDownload: () -> Unit
) {

    val modalState by rememberUpdatedState(stateDownload)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0x80EFEFEF))
            .pointerInput(Unit) {
                detectTapGestures { /* Consume tap events without doing anything */ }
            },
        contentAlignment = Alignment.Center
    ) {
        OutlinedCard(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 35.dp),
            elevation = CardDefaults.outlinedCardElevation(5.dp),
            shape = MaterialTheme.shapes.medium,
            colors = CardDefaults.cardColors(
                containerColor = White,
            ),
            border = BorderStroke(0.dp, White)
        ) {

            Column(
                modifier = Modifier
                    .padding(horizontal = 30.dp, vertical = 40.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Image(
                    modifier = Modifier
                        .height(50.dp)
                        .width(50.dp),
                    painter =
                    when (modalState) {
                        DownloadStatus.COMPLETE -> painterResource(id = R.drawable.ic_downloaded_circle)
                        DownloadStatus.FAILED -> painterResource(id = R.drawable.ic_download_failed_circle)
                        else -> painterResource(id = R.drawable.ic_downloading_circle)
                    },
                    contentDescription = stringResource(id = R.string.splash_GIF)
                )


                Spacer(modifier = Modifier.height(20.dp))
                Text(
                    when(stateDownload) {
                        DownloadStatus.COMPLETE -> "Download completed!"
                        DownloadStatus.FAILED -> "Download failed!"
                        else -> "Downloading..."
                    },
                    color = Color.Black,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    style = TextStyle(
                        fontSize = 18.sp,
                        fontFamily = CommissionerFontFamily,
                        fontWeight = FontWeight(700),
                        color = Color(0xFF3E3E3E),

                        )
                )

                Spacer(modifier = Modifier.height(10.dp))


                when (stateDownload) {
                    DownloadStatus.FAILED -> {

                        Text(
                            text = "We couldn't complete the download. Please try again in a few moments.",
                            style = TextStyle(
                                fontSize = 14.sp,
                                lineHeight = 20.sp,
                                fontFamily = CommissionerFontFamily,
                                fontWeight = FontWeight(400),
                                color = Color(0xFFCDCDCD),
                                textAlign = TextAlign.Center,
                            )
                        )

                        Spacer(modifier = Modifier.height(30.dp))

                        BlackButton(
                            text = "Retry",
                            enabled = true,
                            stateLoading = false,
                            onClick = {
                                retryDownload()
                            }
                        )

                        Spacer(modifier = Modifier.height(20.dp))

                        Text(
                            modifier = Modifier.clickable { cancelDownload() },
                            text = "Cancel download process",
                            style = TextStyle(
                                fontSize = 16.sp,
                                fontFamily = CommissionerFontFamily,
                                fontWeight = FontWeight(400),
                                color = Color(0xFF161616),
                            )
                        )
                    }

                    else -> {

                        Spacer(modifier = Modifier.height(20.dp))

                        LinearProgressIndicator(
                            progress = { progress },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 30.dp)
                                .height(8.dp),
                            color = Color(0xFF2FCC71),
                            trackColor = Color(0xFFF2F2F2),
                            strokeCap = ProgressIndicatorDefaults.LinearStrokeCap,
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 30.dp)
                        ) {
                            Text(
                                text = "$currentSize/$totalSize",
                                style = TextStyle(
                                    fontSize = 14.sp,
                                    fontFamily = CommissionerFontFamily,
                                    fontWeight = FontWeight(400),
                                    color = Color(0xFFCDCDCD),
                                )
                            )

                            Spacer(modifier = Modifier.weight(1f))

                            Text(
                                text = percentage,
                                style = TextStyle(
                                    fontSize = 14.sp,
                                    fontFamily = CommissionerFontFamily,
                                    fontWeight = FontWeight(700),
                                    color = Color(0xFF3E3E3E),
                                )
                            )
                        }
                    }
                }
            }

        }
    }
}


@Composable
fun BlackButton(
    text: String,
    enabled: Boolean = true,
    stateLoading: Boolean = false,
    onClick: () -> Unit
) {
    val color =
        if (enabled) White else SdkBlackOff

    TextButton(
        onClick = onClick,
        enabled = enabled,
        modifier = Modifier
            .padding(0.dp)
            .background(
                color = colorResource(id = R.color.black),
                shape = RoundedCornerShape(50.dp),
            )
            .border(
                BorderStroke(1.dp, color),
                shape = RoundedCornerShape(50.dp)
            )
            .wrapContentHeight(align = Alignment.CenterVertically)

    ) {
        Text(
            modifier = Modifier.padding(horizontal = 60.dp, vertical = 6.dp),
            text = text,
            style = TextStyle(
                fontSize = 16.sp,
                fontFamily = CommissionerFontFamily,
                fontWeight = FontWeight(400),
                color = Color(0xFFFFFFFF),
            )
        )
    }
}


@Preview
@Composable
fun DownloadModalPreview() {
    DownloadModal(
        progress = 0.5f,
        totalSize = "100MB",
        currentSize = "50MB",
        percentage = "50%",
        stateDownload = DownloadStatus.COMPLETE,
        cancelDownload = {},
        retryDownload = {})
}