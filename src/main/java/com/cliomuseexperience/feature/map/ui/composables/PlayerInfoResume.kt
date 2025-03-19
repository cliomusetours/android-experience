package com.cliomuseexperience.feature.map.ui.composables


import android.os.Build
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cliomuseexperience.core.presentation.theme.CommissionerFontFamily
import com.cliomuseexperience.core.presentation.theme.SdkYellowInfo
import com.cliomuseexperience.experiencecliomuse.R


@Composable
fun PlayerInfoResume(
    itemTitle: String?,
    storyValue: Int?,
    storyBody: String?,
    storyColorBackground: Color?,
    storyTitle: String?,
    titleColor: Color,
    showMoreClickListener: () -> Unit = {}
) {

    Card(
        modifier = Modifier
            .fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = storyColorBackground ?: SdkYellowInfo,
        ),
        elevation = CardDefaults.cardElevation(8.dp)
    ) {

        Box {
            Column(
                modifier = Modifier
                    .padding(start = 32.dp, end = 32.dp, top = 24.dp, bottom = 24.dp)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                InfoStoryHeather(
                    itemTitle = itemTitle,
                    storyValue = storyValue,
                    storyTitle = storyTitle,
                    withCloseButton = false,
                    titleColor = titleColor,
                    closeClickListener = {}
                )

                Spacer(modifier = Modifier.height(24.dp))

                storyBody?.let { body ->
                    Text(
                        text = body,
                        maxLines = 6,
                        overflow = TextOverflow.Ellipsis,
                        style = TextStyle(
                            fontSize = 22.sp,
                            lineHeight = 35.sp,
                            fontFamily = CommissionerFontFamily,
                            fontWeight = FontWeight(600),
                            color = Color(0xB2161616)
                        )
                    )
                }
            }

            BlurredBackgroundWithButton(
                backgroundColor = storyColorBackground ?: Color.Transparent,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
            ) { showMoreClickListener() }
        }


    }
}

@Composable
fun BlurredBackgroundWithButton(
    modifier: Modifier,
    backgroundColor: Color,
    showMoreClickListener: () -> Unit
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(65.dp),
        contentAlignment = Alignment.CenterEnd
    ) {
        // Capa de desenfoque para el fondo
        Box(
            modifier = Modifier
                .matchParentSize()
                .background(backgroundColor)
                .blur(10.dp),
            contentAlignment = Alignment.CenterStart,

            ) {
            // when version > android 10

            val versionMessage = if(Build.VERSION.SDK_INT > Build.VERSION_CODES.Q) "Text mock for blur effect" else ""
            Text(
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .padding(start = 32.dp),
                text = versionMessage,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                style = TextStyle(
                    fontSize = 22.sp,
                    lineHeight = 35.sp,
                    fontFamily = CommissionerFontFamily,
                    fontWeight = FontWeight(600),
                    color = Color(0xB2161616)
                )
            )
        }

        // Botón sobre el fondo borroso
        Button(
            modifier = Modifier
                .height(34.dp)
                .padding(end = 16.dp)
                .align(Alignment.CenterEnd),
            shape = RoundedCornerShape(20.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF161616).copy(alpha = 0.5f),
                contentColor = Color(0xFFFFFFFF)
            ),
            onClick = { showMoreClickListener() },
        ) {

            Row(
                modifier = Modifier.padding(0.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {

                Text(
                    modifier = Modifier
                        .fillMaxHeight()
                        .padding(0.dp),
                    textAlign = TextAlign.Center,
                    text = stringResource(id = R.string.more),
                    style = TextStyle(
                        fontSize = 12.sp,
                        fontFamily = CommissionerFontFamily,
                        fontWeight = FontWeight(700),
                        color = Color(0xFFFFFFFF)
                    )
                )

                Spacer(modifier = Modifier.width(4.dp))

                Icon(
                    modifier = Modifier
                        .size(14.dp),
                    painter = painterResource(id = R.drawable.ic_expand),
                    contentDescription = stringResource(id = R.string.expand_info),
                )

            }
        }
    }
}
