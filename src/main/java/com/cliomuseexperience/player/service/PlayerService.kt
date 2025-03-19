package com.cliomuseexperience.player.service

import androidx.media3.exoplayer.ExoPlayer

interface PlayerService {
    fun getExoPlayer(): ExoPlayer
}