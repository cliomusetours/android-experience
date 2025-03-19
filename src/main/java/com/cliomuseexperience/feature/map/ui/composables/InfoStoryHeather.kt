package com.cliomuseexperience.feature.map.ui.composables


import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cliomuseexperience.core.presentation.theme.CommissionerFontFamily
import com.cliomuseexperience.experiencecliomuse.R

@Composable
fun InfoStoryHeather(
    itemTitle: String?,
    storyValue: Int?,
    storyTitle: String?,
    withCloseButton: Boolean = false,
    titleColor: Color,
    closeClickListener: () -> Unit = {}
) {
    Row(
        modifier = Modifier
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        storyValue?.let {
            Image(
                modifier = Modifier
                    .width(40.dp)
                    .height(40.dp)
                    .clip(RoundedCornerShape(4.dp)),
                painter = painterResource(
                    id = storyValue,
                    //        id = story.category?.values?.get(0)?.toIcon() ?: R.drawable.ic_directions
                ),
                contentScale = ContentScale.Crop,
                contentDescription = stringResource(id = R.string.ejemplo)
            )
        }

        Spacer(modifier = Modifier.width(16.dp))

        Column(
            modifier = Modifier.weight(1f)
        ) {
            storyTitle?.let { title ->
                Text(
                    modifier = Modifier
                        .fillMaxWidth(),
                    textAlign = if (withCloseButton) TextAlign.Center else TextAlign.Start,
                    text = title,
                    style = TextStyle(
                        fontSize = 14.sp,
                        fontFamily = CommissionerFontFamily,
                        fontWeight = FontWeight(700),
                        color = if (withCloseButton) Color.White else titleColor,
                        )
                )
            }
            itemTitle?.let { name ->
                Text(
                    modifier = Modifier
                        .fillMaxWidth(),
                    textAlign = if (withCloseButton) TextAlign.Center else TextAlign.Start,
                    text = name,
                    style = TextStyle(
                        fontSize = 12.sp,
                        fontFamily = CommissionerFontFamily,
                        fontWeight = FontWeight(500),
                        color = Color.White,

                        )
                )
            }

        }
        if (withCloseButton)
            Image(
                modifier = Modifier
                    .clickable { closeClickListener() },
                painter = painterResource(id = R.drawable.ic_close_more),
                contentDescription = stringResource(id = R.string.close),
            )
    }
}

@Preview
@Composable
fun InfoStoryHeatherPreview() {
    InfoStoryHeather(
        itemTitle = "Title",
        storyValue = R.drawable.ic_directions,
        storyTitle = "Story",
        withCloseButton = true,
        titleColor = Color.White
    )
}