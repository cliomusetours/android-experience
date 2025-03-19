package com.cliomuseexperience.feature.map.ui

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.saveable
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import com.cliomuseexperience.core.api.TOUR_EXTRA
import com.cliomuseexperience.core.extensions.formatDuration
import com.cliomuseexperience.core.extensions.getLastLangId
import com.cliomuseexperience.core.extensions.getLastTourId
import com.cliomuseexperience.core.extensions.saveLastTourInfo
import com.cliomuseexperience.core.extensions.toAudioTour
import com.cliomuseexperience.core.extensions.toPointsListThumbImageUri
import com.cliomuseexperience.core.presentation.viewmodel.EventDelegate
import com.cliomuseexperience.core.presentation.viewmodel.EventDelegateViewModel
import com.cliomuseexperience.feature.experience.domain.model.Item
import com.cliomuseexperience.feature.experience.domain.model.Story
import com.cliomuseexperience.feature.map.domain.MapEvent
import com.cliomuseexperience.feature.map.domain.MapState
import com.cliomuseexperience.feature.map.domain.UIEvent
import com.cliomuseexperience.player.service.PlayerEvent
import com.cliomuseexperience.player.service.SimpleMediaService
import com.cliomuseexperience.player.service.SimpleMediaServiceHandler
import com.cliomuseexperience.player.service.SimpleMediaState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MapViewModel
@Inject constructor(
    private val simpleMediaServiceHandler: SimpleMediaServiceHandler,
    savedStateHandle: SavedStateHandle
) : ViewModel(), EventDelegate<MapEvent> by EventDelegateViewModel() {

    var viewState by mutableStateOf(MapState())

    private var currentStoryIndex: Int = 0

    var duration by savedStateHandle.saveable { mutableStateOf(0L) }
    var progress by savedStateHandle.saveable { mutableStateOf(0f) }
    var progressString by savedStateHandle.saveable { mutableStateOf("00:00") }
    var remainingTimeString by savedStateHandle.saveable { mutableStateOf("00:00") }
    var isPlaying by savedStateHandle.saveable { mutableStateOf(false) }
    var downloadProgress by mutableStateOf(0f)

    // Add new state flows
    private val _showImageDialog = MutableStateFlow(false)
    val showImageDialog: StateFlow<Boolean> get() = _showImageDialog

    private val _showVideoDialog = MutableStateFlow(false)
    val showVideoDialog: StateFlow<Boolean> get() = _showVideoDialog

    init {
        viewState = viewState.copy(
            tour = TOUR_EXTRA,
            touItemsList = TOUR_EXTRA?.items,
            itemToursList = TOUR_EXTRA?.items?.flatMap { it.stories ?: emptyList() },
        )
        viewModelScope.launch {
            simpleMediaServiceHandler.simpleMediaState.collect { mediaState ->
                when (mediaState) {
                    is SimpleMediaState.Buffering -> calculateProgressValues(mediaState.progress)
                    SimpleMediaState.Initial -> {}
                    is SimpleMediaState.Playing -> isPlaying = mediaState.isPlaying
                    is SimpleMediaState.Progress -> calculateProgressValues(mediaState.progress)
                    is SimpleMediaState.Ready -> duration = mediaState.duration
                }
            }
        }

        viewModelScope.launch {
            simpleMediaServiceHandler.onMediaItemChanged.collectLatest { (storyId, itemId) ->
                updateStoryAndItem(storyId, itemId)
            }
        }
        loadAllStoriesFromTour()
    }

    private fun updateStoryAndItem(storyId: Int, itemId: Int) {
        viewModelScope.launch {
            val newItem = viewState.touItemsList?.firstOrNull { it.id == itemId }
            val newStory = viewState.itemToursList?.firstOrNull { it.id == storyId }

            if (newItem != null && newStory != null) {
                currentStoryIndex = newItem.stories?.indexOf(newStory) ?: 0
                val nextStepList = getNextPointsList(viewState.tour?.items, newItem)
                viewState = viewState.copy(currentItem = newItem, currentStory = newStory, nextPoints = nextStepList)
                updateStory(newStory)
            }
        }
    }

    private fun closeDialogs() {
        viewModelScope.launch {
            _showImageDialog.value = false
            _showVideoDialog.value = false
        }
    }

    fun setShowImageDialog(show: Boolean) {
        viewModelScope.launch {
            _showImageDialog.value = show
        }
    }

    fun setShowVideoDialog(show: Boolean) {
        viewModelScope.launch {
            _showVideoDialog.value = show
        }
    }

    /* START PLAYER */

    fun onUIEvent(uiEvent: UIEvent) = viewModelScope.launch {
        when (uiEvent) {
            UIEvent.Backward -> simpleMediaServiceHandler.onPlayerEvent(PlayerEvent.Backward)
            UIEvent.Forward -> simpleMediaServiceHandler.onPlayerEvent(PlayerEvent.Forward)
            UIEvent.PlayPause -> simpleMediaServiceHandler.onPlayerEvent(PlayerEvent.PlayPause)
            UIEvent.Play -> simpleMediaServiceHandler.onPlayerEvent(PlayerEvent.Play)
            UIEvent.Pause -> simpleMediaServiceHandler.onPlayerEvent(PlayerEvent.Pause)
            UIEvent.SkipNext -> {
                simpleMediaServiceHandler.onPlayerEvent(PlayerEvent.SkipNext)
                playNextStory()
                closeDialogs()
            }

            UIEvent.SkipPrevious -> {
                simpleMediaServiceHandler.onPlayerEvent(PlayerEvent.SkipPrevious)
                playPreviousStory()
                closeDialogs()
            }

            UIEvent.TapOnAnnotation -> {}
            is UIEvent.UpdateProgress -> {
                progress = uiEvent.newProgress
                simpleMediaServiceHandler.onPlayerEvent(PlayerEvent.UpdateProgress(uiEvent.newProgress))
                if (uiEvent.newProgress >= 0.99f) {
                    simpleMediaServiceHandler.onPlayerEvent(PlayerEvent.SkipNext)
                    playNextStory()
                    closeDialogs()
                }
            }
        }
    }

    private fun calculateProgressValues(currentProgress: Long) {
        progress = if (currentProgress > 0) (currentProgress.toFloat() / duration) else 0f
        progressString = formatDuration(currentProgress)
        remainingTimeString = formatDuration(duration)
    }

    private fun loadAllStoriesFromTour() {
        viewModelScope.launch {
            val allMediaItems = viewState.tour?.items?.flatMap { item ->
                item.stories?.mapNotNull { story ->
                    story.audioFile?.toAudioTour()?.let { url ->
                        val artworkUri =
                            item.thumbFile?.toPointsListThumbImageUri() ?: Uri.parse("")
                        val index = item.index ?: 0
                        val name = item.name ?: ""
                        val albumTitleWithIndex = "$index.$name"

                        MediaItem.Builder()
                            .setUri(url)
                            .setMediaMetadata(
                                MediaMetadata.Builder()
                                    .setFolderType(MediaMetadata.FOLDER_TYPE_ALBUMS)
                                    .setArtworkUri(artworkUri)
                                    .setDisplayTitle(albumTitleWithIndex)
                                    .setAlbumTitle(story.title)
                                    .setExtras(Bundle().apply {
                                        putInt("story_id", story.id)  // Guardamos el ID único
                                        putInt("item_id", item.id)  // Guardamos el ID del item
                                    })
                                    .build()
                            ).build()
                    }
                } ?: emptyList()
            } ?: emptyList()

            if (allMediaItems.isNotEmpty()) {
                // Cargar todas las stories en ExoPlayer
                simpleMediaServiceHandler.addMediaItemList(allMediaItems)
            }
        }
    }

    fun clearPlayerIfNeeded(context: Context, newTourId: Int?, newLangId: Int?) {
        var lastTourId = getLastTourId(context)
        var lastLangId = getLastLangId(context)
        if (newTourId != null && newTourId != lastTourId || newLangId != null && newLangId != lastLangId) {
            clearPlayer(context)
            saveLastTourInfo(context, newTourId!!, newLangId!!)
        }
    }


    private fun clearPlayer(context: Context) {
        simpleMediaServiceHandler.resetPlayer()
        val intent = Intent(context, SimpleMediaService::class.java).apply {
            action = SimpleMediaService.ACTION_CLEAR_PLAYER
        }
        context.startService(intent)
    }

    /* END PLAYER */

    fun playNextStory() {
        viewModelScope.launch {
            val stories = viewState.currentItem?.stories ?: listOf()
            if (stories.isNotEmpty()) {
                if (currentStoryIndex < stories.size - 1) {
                    // Avanzamos en las stories del mismo Item
                    currentStoryIndex++
                    viewState = viewState.copy(currentStory = stories[currentStoryIndex])
                } else {
                    // Si ya estamos en la última story, avanzamos al siguiente Item
                    val currentItemIndex =
                        viewState.tour?.items?.indexOf(viewState.currentItem) ?: -1
                    if (currentItemIndex >= 0) {
                        val nextItemIndex = currentItemIndex + 1
                        val items = viewState.tour?.items ?: listOf()
                        if (nextItemIndex < items.size) {
                            viewState = viewState.copy(currentItem = items[nextItemIndex])
                            // Reiniciamos el índice para el nuevo Item
                            currentStoryIndex = 0
                            viewState = viewState.copy(
                                currentStory = viewState.currentItem?.stories?.getOrNull(0)
                            )
                        }
                    }
                }
                // Disparamos la acción en el reproductor
                simpleMediaServiceHandler.onPlayerEvent(PlayerEvent.SkipNext)
                closeDialogs()
            }
        }
    }

    fun playPreviousStory() {
        viewModelScope.launch {
            val stories = viewState.currentItem?.stories ?: listOf()
            if (stories.isNotEmpty()) {
                if (currentStoryIndex > 0) {
                    // Retrocedemos en las stories del mismo Item
                    currentStoryIndex--
                } else {
                    // Si ya estamos en la primera story, pasamos al Item anterior
                    val currentItemIndex =
                        viewState.tour?.items?.indexOf(viewState.currentItem) ?: -1
                    if (currentItemIndex > 0) {
                        val prevItemIndex = currentItemIndex - 1
                        val items = viewState.tour?.items ?: listOf()
                        viewState = viewState.copy(currentItem = items[prevItemIndex])
                        // Tomamos la última story del Item anterior
                        viewState.currentItem?.stories?.lastOrNull()?.let {
                            currentStoryIndex = viewState.currentItem?.stories?.indexOf(it) ?: 0
                            viewState = viewState.copy(currentStory = it)
                        }
                    }
                }
                simpleMediaServiceHandler.onPlayerEvent(PlayerEvent.SkipPrevious)
                closeDialogs()
            }
        }
    }

    private fun getNextPointsList(items: List<Item>?, currentItem: Item?): List<Item> {
        val nextPoints = mutableListOf<Item>()
        items?.let {
            val currentItemIndex = items.indexOf(currentItem)
            for (i in currentItemIndex + 1 until items.size) {
                nextPoints.add(items[i])
            }
        }
        return nextPoints
    }

    fun updateStory(story: Story?) {
        viewModelScope.launch {
            viewState.tour?.items?.forEach { item ->
                item.stories?.forEach { it.isSelected = false }
            }
            story?.isSelected = true
            viewState = viewState.copy(currentStory = story)
        }
    }

    fun clickBackButton() {
        viewModelScope.launch {
            sendEvent(MapEvent.BackButtonClicked)
        }
    }

    fun selectStory(story: Story?) {
        viewModelScope.launch {
            if (story == null) return@launch
            val storyIndex = viewState.itemToursList?.indexOfFirst { it.id == story.id } ?: -1

            if (storyIndex != -1) {
                viewState.touItemsList?.firstOrNull { it.stories?.contains(story) == true }?.let {
                    currentStoryIndex = it.stories?.indexOf(story) ?: 0
                    viewState = viewState.copy(currentItem = it, currentStory = story)
                    updateStory(story)
                    simpleMediaServiceHandler.player.seekTo(storyIndex, 0)
                    simpleMediaServiceHandler.onPlayerEvent(PlayerEvent.Play)
                }
            }
        }
    }

    fun selectItem(item: Item?) {
        viewModelScope.launch {
            val tmpItem = viewState.tour?.items?.firstOrNull { it.id == item?.id }
            val nextStepList = getNextPointsList(viewState.tour?.items, tmpItem)
            viewState = viewState.copy(
                currentItem = tmpItem,
                nextPoints = nextStepList
            )
            // Reiniciamos el índice y la story actual
            currentStoryIndex = 0
            tmpItem?.stories?.firstOrNull()?.let { firstStory ->
                selectStory(firstStory)
            }
        }
    }

}

