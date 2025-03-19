package com.cliomuseapp.cliomuseapp.feature.tour

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Divider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cliomuseexperience.core.presentation.composables.HorizontalPagerContent
import com.cliomuseexperience.core.presentation.theme.CommissionerFontFamily
import com.cliomuseexperience.feature.experience.domain.model.Item
import com.cliomuseexperience.feature.experience.domain.model.Story
import com.cliomuseapp.cliomuseapp.feature.tour.composables.GoBackMapSection
import com.cliomuseapp.cliomuseapp.feature.tour.composables.StoriesListItem
import com.cliomuseapp.cliomuseapp.feature.tour.composables.TourImageSection

@Composable
fun TourDetailScreen(
    itemsList: List<Item>,
    selectedItem: Item?,
    onItemChangeListener: (Item?) -> Unit,
    onStoryClickListener: (Story?) -> Unit,
    onBackClickListener: () -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(color = Color.White)
    ) {
        item { Spacer(modifier = Modifier.height(16.dp)) }

        item {
            GoBackMapSection(
                onBackClickListener = onBackClickListener,
                goMapClickListener = onBackClickListener
            )
        }

        item { Spacer(modifier = Modifier.height(16.dp)) }

        item {
            HorizontalPagerContent(
                items = itemsList,
                initialItem = selectedItem!!, // Ensure selectedItem is not null
                pageListener = { isBetween, isLast, currentItem ->
                    // Notify the parent about the page change
                    onItemChangeListener(currentItem)
                }
            ) { item ->
                TourImageSection(
                    item = item,
                    selectedItemState = selectedItem
                )
            }
        }

        item { Spacer(modifier = Modifier.height(20.dp)) }

        item {
            // This guarantees sequential numbering regardless of what the API sends.
            val displayIndex = itemsList.indexOf(selectedItem).plus(1)

            Text(
                modifier = Modifier.padding(horizontal = 30.dp),
                text = "$displayIndex. ${selectedItem?.name ?: ""}",
                style = TextStyle(
                    fontSize = 20.sp,
                    fontFamily = CommissionerFontFamily,
                    fontWeight = FontWeight(700),
                    color = Color(0xFF161616)
                )
            )
        }


        item { Spacer(modifier = Modifier.height(10.dp)) }

        item {
            Text(
                modifier = Modifier.padding(horizontal = 30.dp),
                text = selectedItem?.secret ?: "",
                style = TextStyle(
                    fontSize = 14.sp,
                    lineHeight = 20.sp,
                    fontFamily = CommissionerFontFamily,
                    fontWeight = FontWeight(400),
                    color = Color(0xFF161616)
                )
            )
        }

        item { Spacer(modifier = Modifier.height(20.dp)) }

        selectedItem?.stories?.let { stories ->
            itemsIndexed(stories) { index, story ->
                StoriesListItem(
                    modifier = Modifier.padding(horizontal = 12.dp),
                    index = index + 1,
                    story = story,
                    onClick = { onStoryClickListener(story) }
                )
            }
        }

        item { Spacer(modifier = Modifier.height(110.dp)) }
    }
}
