package com.cliomuseexperience.player.service

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewmodel.compose.saveable
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Named

class SimpleMediaServiceHandler @Inject constructor(
    @Named("sdk") val player: ExoPlayer
) : Player.Listener {

    private val _simpleMediaState = MutableStateFlow<SimpleMediaState>(SimpleMediaState.Initial)
    val simpleMediaState: StateFlow<SimpleMediaState> = _simpleMediaState.asStateFlow()

    private var onCompletionListener: (() -> Unit)? = null

    private val _onLastStoryEnded = MutableStateFlow(false)
    val onLastStoryEnded: StateFlow<Boolean> = _onLastStoryEnded

    private val _onMediaItemChanged = MutableSharedFlow<Pair<Int, Int>>(replay = 1)
    val onMediaItemChanged: SharedFlow<Pair<Int, Int>> = _onMediaItemChanged


    private var job: Job? = null
    private var isSeeking = false
    private var isSkippingNext = false
    private var isSkippingPrevious = false
    private var isUpdatingProgress = false

    init {
        player.addListener(this)
        job = Job()
    }

    override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
        super.onMediaItemTransition(mediaItem, reason)

        mediaItem?.mediaMetadata?.extras?.let { extras ->
            val storyId = extras.getInt("story_id", -1)
            val itemId = extras.getInt("item_id", -1)

            if (storyId != -1 && itemId != -1) {
                _onMediaItemChanged.tryEmit(Pair(storyId, itemId))  // Emitimos el par correctamente
            }
        }
    }

    fun setOnCompletionListener(listener: () -> Unit) {
        onCompletionListener = listener
    }

//    fun addMediaItem(mediaItem: MediaItem) {
//        player.setMediaItem(mediaItem)
//        player.prepare()
//    }

    fun addMediaItemList(mediaItemList: List<MediaItem>) {
        player.setMediaItems(mediaItemList)
        player.prepare()
    }

    suspend fun onPlayerEvent(playerEvent: PlayerEvent) {
        when (playerEvent) {
            PlayerEvent.Backward -> {
                isSeeking = true
                val newPosition = (player.currentPosition - 10_000).coerceAtLeast(0)
                player.seekTo(newPosition)
            }

            PlayerEvent.Forward -> {
                isSeeking = true
                val newPosition = (player.currentPosition + 10_000).coerceAtMost(player.duration)
                player.seekTo(newPosition)
            }

            PlayerEvent.SkipNext -> {
                isSkippingNext = true
                player.seekToNext()
            }

            PlayerEvent.SkipPrevious -> {
                isSkippingPrevious = true
                player.seekToPrevious()
            }

            is PlayerEvent.PlayPause -> {
                if (player.isPlaying) {
                    player.pause()
                    stopProgressUpdate()
                } else {
                    player.play()
                    _simpleMediaState.value = SimpleMediaState.Playing(isPlaying = true)
                    Log.e(
                        "SimpleMediaServiceHandler",
                        "🔄 _simpleMediaState cambiado a Playing(true)"
                    )
                    startProgressUpdate()
                }
            }

            is PlayerEvent.Play -> {
                player.play()
                _simpleMediaState.value = SimpleMediaState.Playing(isPlaying = true)
                Log.e("SimpleMediaServiceHandler", "🔄 _simpleMediaState cambiado a Playing(true)")
                startProgressUpdate()
            }

            is PlayerEvent.Pause -> {
                player.pause()
                Log.e("SimpleMediaServiceHandler", "🔄 _simpleMediaState cambiado a Playing(false)")
                stopProgressUpdate()
            }

            PlayerEvent.Stop -> stopProgressUpdate()
            is PlayerEvent.UpdateProgress -> {
                isUpdatingProgress = true
                player.seekTo((player.duration * playerEvent.newProgress).toLong())
                // Reset flag immediately after seeking
                resetFlags()
            }
        }
    }

    override fun onPlaybackStateChanged(playbackState: Int) {
        when (playbackState) {
            ExoPlayer.STATE_BUFFERING -> _simpleMediaState.value =
                SimpleMediaState.Buffering(player.currentPosition)

            ExoPlayer.STATE_READY -> {
                _simpleMediaState.value = SimpleMediaState.Ready(player.duration)
                if (isSeeking || isSkippingNext || isSkippingPrevious || isUpdatingProgress) {
                    resetFlags()
                    _simpleMediaState.value = SimpleMediaState.Playing(isPlaying = player.isPlaying)
                }
            }

            ExoPlayer.STATE_ENDED -> {
                if (!player.hasNextMediaItem()) {
                    Log.e("MediaServiceHandler", "🎯 Emitiendo onLastStoryEnded")
                    CoroutineScope(Dispatchers.Main).launch {
                        _onLastStoryEnded.value = true
                    }
                }
                onCompletionListener?.invoke()
            }
        }
    }

    fun resetLastStoryEnded() {
        _onLastStoryEnded.value = false
    }

    @OptIn(DelicateCoroutinesApi::class)
    override fun onIsPlayingChanged(isPlaying: Boolean) {
        if (!isSeeking && !isSkippingNext && !isSkippingPrevious && !isUpdatingProgress) {
            _simpleMediaState.value = SimpleMediaState.Playing(isPlaying = isPlaying)
            if (isPlaying) {
                GlobalScope.launch(Dispatchers.Main) {
                    startProgressUpdate()
                }
            } else {
                stopProgressUpdate()
            }
        }
    }

    fun resetPlayer() {
        stopProgressUpdate()
        player.stop()
        player.clearMediaItems()
        resetFlags()
        _simpleMediaState.value = SimpleMediaState.Initial
    }


    private fun resetFlags() {
        isSeeking = false
        isSkippingNext = false
        isSkippingPrevious = false
        isUpdatingProgress = false
    }

    private suspend fun startProgressUpdate() = job.run {
        while (true) {
            delay(500)
            _simpleMediaState.value = SimpleMediaState.Progress(player.currentPosition)
        }
    }

    private fun stopProgressUpdate() {
        job?.cancel()
        _simpleMediaState.value = SimpleMediaState.Playing(isPlaying = false)
    }
}


sealed class PlayerEvent {
    object PlayPause : PlayerEvent()
    object Play : PlayerEvent()
    object Pause : PlayerEvent()
    object Backward : PlayerEvent()
    object Forward : PlayerEvent()
    object SkipNext : PlayerEvent()
    object SkipPrevious : PlayerEvent()
    object Stop : PlayerEvent()
    data class UpdateProgress(val newProgress: Float) :
        PlayerEvent() // Needs to carry progress value, hence the data class
}

sealed class SimpleMediaState {
    object Initial : SimpleMediaState()
    data class Ready(val duration: Long) : SimpleMediaState()
    data class Progress(val progress: Long) : SimpleMediaState()
    data class Buffering(val progress: Long) : SimpleMediaState()
    data class Playing(val isPlaying: Boolean) : SimpleMediaState()
}


