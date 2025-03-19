package com.cliomuseexperience.core.presentation.composables

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.cliomuseexperience.feature.experience.domain.model.Item
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.rememberPagerState


@OptIn(ExperimentalFoundationApi::class, ExperimentalPagerApi::class)
@Composable
fun HorizontalPagerContent(
    items: List<Item>,
    modifier: Modifier = Modifier,
    initialItem: Item,
    pageListener: (Boolean, Boolean, Item) -> Unit = { _, _, _ -> },
    itemContent: @Composable (Item) -> Unit
) {
    // Determine the initial page based on the initialItem's index
    val initialPage = items.indexOfFirst { it.id == initialItem.id }.takeIf { it >= 0 } ?: 0
    val pagerState = rememberPagerState(initialPage = initialPage)

    LaunchedEffect(initialPage) {
        if (pagerState.currentPage != initialPage) {
            pagerState.animateScrollToPage(initialPage)
        }
    }

    // Listen to page changes and notify via pageListener
    LaunchedEffect(pagerState) {
        snapshotFlow { pagerState.currentPage }
            .collect { page ->
                val isBetween = page in 1 until (items.size - 1)
                val isLast = page == (items.size - 1)
                pageListener(isBetween, isLast, items[page])
            }
    }

    HorizontalPager(
        state = pagerState,
        count = items.size,
        contentPadding = PaddingValues(horizontal = 70.dp),
        itemSpacing = 25.dp,
        modifier = modifier
    ) { page ->
        itemContent(items[page])
    }
}


