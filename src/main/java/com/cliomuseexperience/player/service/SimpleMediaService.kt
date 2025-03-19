package com.cliomuseexperience.player.service

import android.app.Service
import android.content.Intent
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.session.MediaSession
import androidx.media3.session.MediaSessionService
import com.cliomuseexperience.player.service.notification.SimpleMediaNotificationManager
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import javax.inject.Named

@AndroidEntryPoint
class SimpleMediaService : MediaSessionService() {

    companion object {
        const val ACTION_CLEAR_PLAYER = "clear_player"
    }

    @Inject
    lateinit var simpleMediaServiceHandler: SimpleMediaServiceHandler
    @Inject
    @Named("sdk")
    lateinit var mediaSession: MediaSession

    @Inject
    @Named("sdk")
    lateinit var notificationManager: SimpleMediaNotificationManager

    @UnstableApi
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent?.action == ACTION_CLEAR_PLAYER) {
            clearPlayerAndNotification()
            return START_NOT_STICKY
        }
        notificationManager.startNotificationService(
            mediaSessionService = this,
            mediaSession = mediaSession
        )
        super.onStartCommand(intent, flags, startId)
        return START_NOT_STICKY
    }

    override fun onTaskRemoved(rootIntent: Intent?) {
        clearPlayerAndNotification()
        super.onTaskRemoved(rootIntent)
        stopSelf()
    }

    override fun onDestroy() {
        clearPlayerAndNotification()
        super.onDestroy()
        mediaSession.release()
        mediaSession.run {
            release()
            if (player.playbackState != Player.STATE_IDLE) {
                player.seekTo(0)
                player.playWhenReady = false
                player.stop()
            }
        }
    }

    private fun clearPlayerAndNotification() {
        simpleMediaServiceHandler.resetPlayer() // Reset your player.
        // Stop foreground mode and remove the notification.
        stopForeground(Service.STOP_FOREGROUND_REMOVE)
    }

    override fun onGetSession(controllerInfo: MediaSession.ControllerInfo): MediaSession =
        mediaSession
}
