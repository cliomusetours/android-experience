package com.cliomuseexperience.feature.experience.ui.composables

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cliomuseexperience.core.presentation.theme.CommissionerFontFamily
import com.cliomuseexperience.experiencecliomuse.R

@Composable
fun SponsorSection(
    sponsorTitle: String?,
    sponsor: String?,
    sponsorWebsite: String?,
    sponsorImage: ImageBitmap?,
    onClick: () -> Unit = {}
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()

    ) {
        Column(
            modifier = Modifier
                .padding(end = 16.dp)
                .width(90.dp),
            horizontalAlignment = Alignment.Start
        ) {

            sponsorTitle?.let { title ->
                Text(

                    text = title,
                    style = TextStyle(
                        fontSize = 18.sp,
                        fontFamily = CommissionerFontFamily,
                        fontWeight = FontWeight(600),
                        color = Color(0xFF161616),
                    ),
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }

            sponsorImage?.let  { image ->
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

        Column(
            modifier = Modifier.weight(1f)
        ) {
    sponsor?.let { sponsor ->
        sponsorWebsite?.let { sponsorWebsite ->
            Text(
                text = buildAnnotatedString {
                    append(sponsor)
                    append(" ")

                    withStyle(style = SpanStyle(color = Color.Red)) {
                        append(sponsorWebsite)
                    }
                },
                style = TextStyle(
                    fontSize = 16.sp,
                    fontFamily = CommissionerFontFamily,
                    fontWeight = FontWeight(500),
                    color = Color(0xFF161616),
                ),
                modifier = Modifier
                    .clickable { onClick() }
                    .padding(start = 8.dp,top=48.dp)
            )
        }
    }
}
}
}
}

//@Preview(showBackground = true)
//@Composable
//fun PreviewSponsorSection() {
//    SponsorSection(
//        sponsorImage = painterResource(id = R.drawable.image_acropoli),
//        sponsorTitle = "Sponsored by Ikos Resorts",
//        sponsor = "This self-guided tour is sponsored by Ikos Resorts, a collection of luxury resorts.",
//        sponsorWebsite = "www.ikosresorts.com",
//        onClick = { /* Handle click */ }
//    )
//}

