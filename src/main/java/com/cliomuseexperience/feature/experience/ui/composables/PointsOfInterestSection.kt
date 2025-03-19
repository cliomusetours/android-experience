package com.cliomuseexperience.feature.experience.ui.composables


import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cliomuseexperience.core.extensions.toPointsListImage
import com.cliomuseexperience.core.presentation.theme.CommissionerFontFamily
import com.cliomuseexperience.experiencecliomuse.R
import com.cliomuseexperience.feature.experience.domain.model.Item

@Composable
fun PointsOfInterestSection(
    interestPoints: List<Item>,
    withPlayer: Boolean
) {

    Column(
        modifier = Modifier
            .fillMaxWidth()

    ) {
        Text(
            modifier = Modifier.padding(horizontal = 16.dp),
            text = stringResource(id = R.string.points_of_interest),
            style = TextStyle(
                fontSize = 22.sp,
                fontFamily = CommissionerFontFamily,
                fontWeight = FontWeight(600),
                color = Color(0xFF161616)
            )
        )

        Spacer(modifier = Modifier.height(20.dp))

        LazyRow {
            itemsIndexed(interestPoints) { index, interestPoint ->
                if (withPlayer) {
                    PointOfInterestWithPlayerCard(
                        item = interestPoint,
                        index = index + 1, // converting 0-based index to 1-based index
                        isItemPlaying = true
                    )
                } else {
                    PointOfInterestCard(
                        item = interestPoint,
                        index = index + 1 // converting 0-based index to 1-based index
                    )
                }
            }
        }
    }
}

@Composable
fun PointOfInterestCard(
    modifier: Modifier = Modifier,
    item: Item,
    index: Int,
    textColor: Color = Color(0xFF161616),
    onClick: () -> Unit = {}
) {
    Column(
        modifier = Modifier
            .padding(8.dp)
            .width(130.dp)
            .clickable {
                onClick()
            },
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            modifier = modifier
                .clip(RoundedCornerShape(8.dp))
                .fillMaxWidth()
                .height(172.dp),
            bitmap = item.imageFile?.toPointsListImage() ?: return@Column,
            contentDescription = "$index. ${item.name}",
            contentScale = ContentScale.Crop
        )


        Spacer(modifier = Modifier.height(8.dp))

        Text(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp)
                .align(Alignment.Start),
            text = "${index}. ${item.name}",
            style = TextStyle(
                fontSize = 14.sp,
                fontFamily = CommissionerFontFamily,
                fontWeight = FontWeight(500),
                color = textColor
            ),
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
fun PointOfInterestWithPlayerCard(
    item: Item,
    index: Int,
    onClick: (Item) -> Unit = {},
    onPlayerClickListener: (Item?) -> Unit = {},
    isItemPlaying: Boolean,
) {

    val iconRes = if (isItemPlaying) {
        R.drawable.ic_player_pause_red
    } else {
        R.drawable.ic_player_play_red
    }

    Column(
        modifier = Modifier
            .padding(8.dp)
            .width(180.dp)
            .height(150.dp)
            .clickable {
                onClick(item)
            },
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(8.dp))
        ) {
            Image(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp),
                bitmap = item.imageFile?.toPointsListImage() ?: return@Column,
                contentDescription = "$index. ${item.name}",
                contentScale = ContentScale.Crop
            )

            OvalIconWithNumber(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(9.dp),
                number = item.stories?.size ?: 0
            )

            Image(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(9.dp)
                    .size(35.dp)
                    .clickable {
                        onPlayerClickListener(item)
                    },
                painter = painterResource(id = iconRes),
                contentDescription = if (isItemPlaying) "Pause" else "Play",
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp)
                .align(Alignment.Start),
            text = "${index}. ${item.name}",
            style = TextStyle(
                fontSize = 14.sp,
                fontFamily = CommissionerFontFamily,
                fontWeight = FontWeight(500),
                color = Color(0xFF161616)
            ),
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
fun OvalIconWithNumber(
    modifier: Modifier = Modifier,
    number: Int
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(50))
            .background(Color(0xFF1D2548)),
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier
                .padding(vertical = 9.dp, horizontal = 15.dp)
        ) {
            Image(
                modifier = Modifier
                    .size(16.dp)
                    .align(Alignment.CenterVertically),
                painter = painterResource(id = R.drawable.ic_stories),
                contentDescription = null,
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                modifier = Modifier
                    .align(Alignment.CenterVertically),
                text = number.toString(),
                style = TextStyle(
                    fontSize = 12.sp,
                    fontFamily = CommissionerFontFamily,
                    fontWeight = FontWeight(700),
                    color = Color(0xFFFFFFFF),

                    )
            )
        }
    }
}

@Preview
@Composable
fun OvalIconWithNumberPreview() {
    OvalIconWithNumber(number = 12)
}
