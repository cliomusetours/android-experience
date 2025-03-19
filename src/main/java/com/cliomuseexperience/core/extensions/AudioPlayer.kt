package com.cliomuseexperience.core.extensions

import android.content.Context
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer

class AudioPlayer(context: Context) {
    private val exoPlayer: ExoPlayer = ExoPlayer.Builder(context).build()

    fun play(url: String) {
        val mediaItem = MediaItem.fromUri(url)
        exoPlayer.setMediaItem(mediaItem)
        exoPlayer.prepare()
        exoPlayer.play()
    }

    fun pause() {
        exoPlayer.pause()
    }

    fun release() {
        exoPlayer.release()
    }

    fun getPlayer() = exoPlayer

    fun seekForward() {
        exoPlayer.seekTo(exoPlayer.currentPosition + 10000) // Avanza 10 segundos
    }

    fun seekBackward() {
        exoPlayer.seekTo(exoPlayer.currentPosition - 10000) // Retrocede 10 segundos
    }

    fun getCurrentPosition() = exoPlayer.currentPosition
    fun getDuration() = exoPlayer.duration

    fun resume() {
        exoPlayer.play()
    }
}