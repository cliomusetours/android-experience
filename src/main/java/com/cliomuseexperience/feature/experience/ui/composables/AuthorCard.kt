package com.cliomuseexperience.feature.experience.ui.composables

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cliomuseexperience.core.extensions.toTourAuthorImage
import com.cliomuseexperience.core.presentation.theme.CommissionerFontFamily
import com.cliomuseexperience.experiencecliomuse.R

@Composable
fun AuthorCard(
    modifier: Modifier = Modifier,
    authorLogo: String?,
    authorName: String?
) {
        Row(
            modifier = modifier
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            authorLogo?.let { logo ->
                Image(
                    modifier = Modifier
                        .width(40.dp)
                        .height(40.dp)
                        .clip(CircleShape)
                        .background(Color.Black),
                    bitmap = logo.toTourAuthorImage() ?: return@let,
                    contentScale = ContentScale.Crop,
                    contentDescription = stringResource(id = R.string.ejemplo)
                )
                Spacer(modifier = Modifier.width(16.dp))
            }

            authorName?.let {name ->
                Column {
                    Text(
                        text = stringResource(id = R.string.tours_author),
                        style = TextStyle(
                            fontSize = 14.sp,
                            fontFamily = CommissionerFontFamily,
                            fontWeight = FontWeight(400),
                            color = Color(0xFFCDCDCD),

                            )
                    )

                    Text(
                        text = name,
                        style = TextStyle(
                            fontSize = 16.sp,
                            fontFamily = CommissionerFontFamily,
                            fontWeight = FontWeight(600),
                            color = Color(0xFF161616),

                            )
                    )
                }
            }
        }
}