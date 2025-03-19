package com.cliomuseexperience.feature.experience.ui.composables

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.ProgressIndicatorDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cliomuseexperience.core.presentation.theme.CommissionerFontFamily
import com.cliomuseexperience.experiencecliomuse.R
import com.cliomuseexperience.feature.experience.domain.DownloadStatus

enum class DeleteStatus {
    IN_PROGRESS,
    COMPLETE,
    FAILED
}

@Composable
fun ProgressModal(
    isDeleting: Boolean = false, // Indica si es un borrado
    progress: Float = 0f,
    totalSize: String? = null,
    currentSize: String? = null,
    percentage: String = "",
    stateProgress: DownloadStatus? = null,
    stateDelete: DeleteStatus? = null, // Estado de borrado
    cancelAction: () -> Unit = {},
    retryAction: () -> Unit = {}
) {
    val modalState by rememberUpdatedState(if (isDeleting) stateDelete else stateProgress)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0x80EFEFEF)),
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
                    painter = when (modalState) {
                        DownloadStatus.COMPLETE, DeleteStatus.COMPLETE -> painterResource(id = R.drawable.ic_downloaded_circle)
                        DownloadStatus.FAILED, DeleteStatus.FAILED -> painterResource(id = R.drawable.ic_download_failed_circle)
                        else -> painterResource(id = R.drawable.ic_downloading_circle)
                    },
                    contentDescription = stringResource(id = R.string.splash_GIF)
                )

                Spacer(modifier = Modifier.height(20.dp))

                Text(
                    when (modalState) {
                        DownloadStatus.COMPLETE, DeleteStatus.COMPLETE -> if (isDeleting) "Delete completed!" else "Download completed!"
                        DownloadStatus.FAILED, DeleteStatus.FAILED -> if (isDeleting) "Delete failed!" else "Download failed!"
                        else -> if (isDeleting) "Deleting..." else "Downloading..."
                    },
                    style = TextStyle(
                        fontSize = 18.sp,
                        fontFamily = CommissionerFontFamily,
                        fontWeight = FontWeight(700),
                        color = Color(0xFF3E3E3E)
                    )
                )

                Spacer(modifier = Modifier.height(10.dp))

                if (modalState != DeleteStatus.FAILED && modalState != DownloadStatus.FAILED) {
                    Spacer(modifier = Modifier.height(20.dp))
                    LinearProgressIndicator(
                        progress = progress,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 30.dp)
                            .height(8.dp),
                        color = Color(0xFF2FCC71),
                        trackColor = Color(0xFFF2F2F2),
                        strokeCap = ProgressIndicatorDefaults.LinearStrokeCap
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 30.dp)
                    ) {
                        if(totalSize != null && currentSize != null)
                        Text(
                            text = "$currentSize/$totalSize",
                            style = TextStyle(
                                fontSize = 14.sp,
                                fontFamily = CommissionerFontFamily,
                                fontWeight = FontWeight(400),
                                color = Color(0xFFCDCDCD)
                            )
                        )

                        Spacer(modifier = Modifier.weight(1f))

                        Text(
                            text = percentage,
                            style = TextStyle(
                                fontSize = 14.sp,
                                fontFamily = CommissionerFontFamily,
                                fontWeight = FontWeight(700),
                                color = Color(0xFF3E3E3E)
                            )
                        )
                    }
                } else {
                    Spacer(modifier = Modifier.height(30.dp))
                    Text(
                        text = if (isDeleting) "We couldn't complete the delete process. Please try again." else "We couldn't complete the download.",
                        style = TextStyle(
                            fontSize = 14.sp,
                            fontFamily = CommissionerFontFamily,
                            fontWeight = FontWeight(400),
                            color = Color(0xFFCDCDCD),
                            textAlign = TextAlign.Center
                        )
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    BlackButton(
                        text = "Retry",
                        enabled = true,
                        stateLoading = false,
                        onClick = retryAction
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    Text(
                        modifier = Modifier.clickable { cancelAction() },
                        text = if (isDeleting) "Cancel delete process" else "Cancel download process",
                        style = TextStyle(
                            fontSize = 16.sp,
                            fontFamily = CommissionerFontFamily,
                            fontWeight = FontWeight(400),
                            color = Color(0xFF161616)
                        )
                    )
                }
            }
        }
    }
}
