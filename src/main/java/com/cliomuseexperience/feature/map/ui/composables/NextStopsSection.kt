package com.cliomuseexperience.feature.map.ui.composables

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cliomuseexperience.core.presentation.theme.CommissionerFontFamily
import com.cliomuseexperience.experiencecliomuse.R
import com.cliomuseexperience.feature.experience.domain.model.Item
import com.cliomuseexperience.feature.experience.ui.composables.PointOfInterestCard

@Composable
fun NextStopsSection(
    nextPoints: List<Item>?,
    onStoryClickListener: (Item) -> Unit
) {
    val lazyListState = rememberLazyListState()

    LaunchedEffect(nextPoints) {
        if (!nextPoints.isNullOrEmpty()) {
            lazyListState.animateScrollToItem(0)
        }
    }

    Column {
        Text(
            text = stringResource(id = R.string.jump_to_next_stops),
            style = TextStyle(
                fontSize = 20.sp,
                fontFamily = CommissionerFontFamily,
                fontWeight = FontWeight(700),
                color = Color(0xFFFFFFFF)
            )
        )

        Spacer(modifier = Modifier.height(16.dp))

        nextPoints?.let {
            LazyRow(state = lazyListState) {
                itemsIndexed(nextPoints) { index, nextPoint ->
                    PointOfInterestCard(
                        onClick = { onStoryClickListener(nextPoint) },
                        item = nextPoint,
                        index = nextPoint.index ?: (index + 1),
                        textColor = Color(0xFFFFFFFF)
                    )
                }
            }
        }
    }
}