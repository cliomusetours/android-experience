package com.cliomuseexperience.feature.experience.ui.composables

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cliomuseexperience.core.presentation.theme.CommissionerFontFamily
import com.cliomuseexperience.core.presentation.theme.SdkBlueMarine
import com.cliomuseexperience.experiencecliomuse.R
import com.liulishuo.okdownload.OkDownloadProvider.context
import com.mapbox.common.MapboxOptions


@Composable
fun TourDetailFooter(
    modifier: Modifier = Modifier,
    image: ImageBitmap?,
    clikListener: () -> Unit = {}
) {
    // Initialize Mapbox access token to avoid crash in case tap is too fast and the map is not yet initialized.
    val accessToken = context.getString(R.string.mapbox_key)
    MapboxOptions.accessToken = accessToken

    Card(
        modifier = modifier
            .height(70.dp)
            .clickable { clikListener() },
        shape = RoundedCornerShape(6.dp),
        colors = CardDefaults.cardColors(containerColor = SdkBlueMarine),
        elevation = CardDefaults.cardElevation(6.dp),
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {

            image?.let { image ->
                Image(
                    modifier = Modifier
                        .width(40.dp)
                        .height(40.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(Color.White),
                    bitmap = image,
                    contentScale = ContentScale.Crop,
                    contentDescription = stringResource(id = R.string.ejemplo)
                )
            }

            Text(
                modifier = Modifier.padding(start = 20.dp),
                text = stringResource(id= R.string.start_your_tour_now),
                style = TextStyle(
                    fontSize = 18.sp,
                    fontFamily = CommissionerFontFamily,
                    fontWeight = FontWeight(600),
                    color = Color(0xFFFFFFFF)
                )
            )

            Spacer(modifier = Modifier.weight(1f))

            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(4.dp))
            ) {
                Icon(
                    modifier = Modifier
                        .fillMaxSize()
                        .align(Alignment.Center),
                    painter = painterResource(id = R.drawable.ic_arrow_opac_card),
                    contentDescription = stringResource(id = R.string.arrow_right_small),
                    tint = Color(0xFFFFFFFF)
                )
            }
        }
    }
}