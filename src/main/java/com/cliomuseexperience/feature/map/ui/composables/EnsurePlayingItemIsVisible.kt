package com.cliomuseexperience.feature.map.ui.composables


import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import com.cliomuseexperience.feature.experience.domain.model.Item

@Composable
fun EnsurePlayingItemIsVisible(
    lazyListState: LazyListState,
    items: List<Item>,
    isPlaying: Boolean,
    currentItem: Item?
) {
    LaunchedEffect(isPlaying, items, currentItem) {
        if (isPlaying && currentItem != null) {
            val playingIndex = items.indexOfFirst { it.id == currentItem.id }
            if (playingIndex != -1) {
                lazyListState.animateScrollToItem(playingIndex)
            }
        }
    }
}

@Composable
fun OnAnnotationTapItemScroll(
    lazyListState: LazyListState,
    items: List<Item>,
    currentItem: Item?
) {
    LaunchedEffect(currentItem) {
        currentItem?.let { item ->
            val index = items.indexOfFirst { it.id == item.id }
            if (index != -1) {
                lazyListState.animateScrollToItem(index)
            }
        }
    }
}
