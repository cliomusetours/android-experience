package com.cliomuseexperience.feature.experience.ui.composables


import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cliomuseexperience.core.extensions.toTourImage
import com.cliomuseexperience.core.presentation.theme.CommissionerFontFamily
import com.cliomuseexperience.experiencecliomuse.R

@Composable
fun TourDetailHeader(
    tourImage: String?,
    tourName: String?,
    authorName: String?,
    onBackClickListener: () -> Unit
) {

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(530.dp)
    ) {



            Image(
            bitmap = tourImage?.toTourImage() ?: return@Box,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop,
            contentDescription = stringResource(id = R.string.image_tour)
        )


        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(30.dp),
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.Bottom
        ) {
            Row {
                authorName?.let {
                    Text(
                        text = stringResource(id = R.string.author),
                        style = TextStyle(
                            fontSize = 16.sp,
                            fontFamily = CommissionerFontFamily,
                            fontWeight = FontWeight(700),
                            color = Color(0xFFFFFFFF),
                            shadow = Shadow(
                                color = Color.Black, // The shadow color (usually black for lighter backgrounds)
                                blurRadius = 12f // How much blur to apply to the shadow
                            )
                        )
                    )
                    Text(
                        text = it,
                        style = TextStyle(
                            fontSize = 16.sp,
                            fontFamily = CommissionerFontFamily,
                            fontWeight = FontWeight(400),
                            color = Color(0xFFFFFFFF),
                            shadow = Shadow(
                                color = Color.Black, // The shadow color (usually black for lighter backgrounds)
                                blurRadius = 12f // How much blur to apply to the shadow
                            )
                        )
                    )
                }

            }

            tourName?.let {
                Text(
                    text = it,
                    style = TextStyle(
                        fontSize = 40.sp,
                        fontFamily = CommissionerFontFamily,
                        fontWeight = FontWeight(700),
                        color = Color(0xFFFFFFFF),
                        shadow = Shadow(
                            color = Color.Black, // The shadow color (usually black for lighter backgrounds)
                            blurRadius = 12f // How much blur to apply to the shadow
                        )

                        )
                )
            }
        }


    }
}
