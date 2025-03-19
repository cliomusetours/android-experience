package com.cliomuseexperience.player.di


import android.content.Context
import androidx.media3.common.AudioAttributes
import androidx.media3.common.C
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.trackselection.DefaultTrackSelector
import androidx.media3.session.MediaSession
import com.cliomuseexperience.player.service.PlayerService
import com.cliomuseexperience.player.service.SimpleMediaServiceHandler
import com.cliomuseexperience.player.service.notification.SimpleMediaNotificationManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class SimpleMediaModule {


    @Named("sdk")
    @Provides
    @Singleton
    fun provideAudioAttributes(): AudioAttributes =
        AudioAttributes.Builder()
            .setContentType(C.AUDIO_CONTENT_TYPE_MOVIE)
            .setUsage(C.USAGE_MEDIA)
            .build()


    @Named("sdk")
    @Provides
    @Singleton
    @UnstableApi
    fun providePlayer(
        @ApplicationContext context: Context,
        @Named("sdk") audioAttributes: AudioAttributes
    ): ExoPlayer =
        ExoPlayer.Builder(context)
            .setAudioAttributes(audioAttributes, true)
            .setHandleAudioBecomingNoisy(true)
            .setTrackSelector(DefaultTrackSelector(context))
            .setSeekForwardIncrementMs(10_000)
            .setSeekBackIncrementMs(10_000)
            .build()


    @Named("sdk")
    @Provides
    @Singleton
    fun provideNotificationManager(
        @ApplicationContext context: Context,
        @Named("sdk") player: ExoPlayer
    ): SimpleMediaNotificationManager =
        SimpleMediaNotificationManager(
            context = context,
            player = player
        )


    @Named("sdk")
    @Provides
    @Singleton
    fun provideMediaSession(
        @ApplicationContext context: Context,
        @Named("sdk") player: ExoPlayer
    ): MediaSession =
        MediaSession.Builder(context, player).build()


    @Named("sdk")
    @Provides
    @Singleton
    fun provideServiceHandler(
        @Named("sdk") player: ExoPlayer
    ): SimpleMediaServiceHandler =
        SimpleMediaServiceHandler(
            player = player
        )


    @Named("sdk")
    @Module
    @InstallIn(SingletonComponent::class)
    object PlayerServiceModule {

        @Provides
        @Singleton
        fun providePlayerService(@ApplicationContext context: Context): PlayerService {
            return object : PlayerService {
                private val exoPlayer = ExoPlayer.Builder(context).build()

                override fun getExoPlayer(): ExoPlayer = exoPlayer
            }
        }
    }


}