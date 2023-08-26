package com.example.musicplayer

import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.Drawable
import android.media.AudioManager
import android.media.MediaPlayer
import android.os.*
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat.*
import androidx.core.app.NotificationCompat
import androidx.navigation.NavDeepLinkBuilder
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.example.musicplayer.Fragment.NowPlaying_Fragment
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Suppress("KotlinConstantConditions")
class MusicService:Service() {
    companion object {
        var isOnline: Boolean = true
    }
    var mediaPlayer: MediaPlayer? = null
    private var myBinder = MyBinder()
    private lateinit var runnable: Runnable
    private lateinit var mediaSession:MediaSessionCompat
    lateinit var audioManager: AudioManager

    override fun onBind(intent: Intent?): IBinder? {
        mediaSession= MediaSessionCompat(baseContext,"My Music")
        return myBinder
    }
    inner class MyBinder: Binder(){
        fun currentService(): MusicService {
            return this@MusicService
        }
    }

    fun showOnlineNotification(playPauseBtn: Int) {
        val intent = NavDeepLinkBuilder(this)
            .setGraph(R.navigation.nav_view)
            .setDestination(R.id.mymusic_Fragment)
            .createPendingIntent()
        val flag = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            PendingIntent.FLAG_IMMUTABLE
        } else {
            PendingIntent.FLAG_UPDATE_CURRENT
        }
        isOnline=true

        val prevIntent = Intent(baseContext, NotificationReciever::class.java).setAction(ApplicationClass.previous)
        val prevPendingIntent = PendingIntent.getBroadcast(baseContext, 0, prevIntent, flag)

        val playIntent = Intent(baseContext, NotificationReciever::class.java).setAction(ApplicationClass.play)
        val playPendingIntent = PendingIntent.getBroadcast(baseContext, 0, playIntent, flag)

        val nextIntent = Intent(baseContext, NotificationReciever::class.java).setAction(ApplicationClass.next)
        val nextPendingIntent = PendingIntent.getBroadcast(baseContext, 0, nextIntent, flag)

        val exitIntent = Intent(baseContext, NotificationReciever::class.java).setAction(ApplicationClass.exit)
        val exitPendingIntent = PendingIntent.getBroadcast(baseContext, 0, exitIntent, flag)

        val notificationTarget = object : CustomTarget<Bitmap>() {
            override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                val notification = NotificationCompat.Builder(baseContext, ApplicationClass.Channel_Id)
                    .setContentIntent(intent)
                    .setContentTitle(Music_player.musicList[Music_player.songPosition].title)
                    .setContentText(Music_player.musicList[Music_player.songPosition].artist)
                    .setSmallIcon(R.drawable.musical)
                    .setLargeIcon(BitmapFactory.decodeResource(resources, R.drawable.musical))
                    .setStyle(androidx.media.app.NotificationCompat.MediaStyle()
                        .setMediaSession(mediaSession.sessionToken)
                    )
                    .setPriority(androidx.core.app.NotificationCompat.PRIORITY_HIGH)
                    .setVisibility(androidx.core.app.NotificationCompat.VISIBILITY_PUBLIC)
                    .setOnlyAlertOnce(true)
                    .setLargeIcon(resource)
                    .addAction(R.drawable.baseline_previous, "Previous", prevPendingIntent)
                    .addAction(playPauseBtn, "Play", playPendingIntent)
                    .addAction(R.drawable.baseline_next, "Next", nextPendingIntent)
                    .addAction(R.drawable.exit, "Exit", exitPendingIntent)
                    .build()


                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q){
                    val playbackSpeed = if(Music_player.isPlaying) 1F else 0F
                    mediaSession.setMetadata(
                        MediaMetadataCompat.Builder()
                        .putLong(MediaMetadataCompat.METADATA_KEY_DURATION, mediaPlayer!!.duration.toLong())
                        .build())
                    val playBackState = Builder()
                        .setState(STATE_PLAYING, mediaPlayer!!.currentPosition.toLong(), playbackSpeed)
                        .setActions(ACTION_SEEK_TO)
                        .build()
                    mediaSession.setPlaybackState(playBackState)
                    mediaSession.setCallback(object : MediaSessionCompat.Callback() {
                        override fun onMediaButtonEvent(mediaButtonEvent: Intent?): Boolean {
                            if(Music_player.isPlaying){
                                //pause music
                                Music_player.binding.playorpausebtn.setIconResource(R.drawable.baseline_play_arrow_24)
                                NowPlaying_Fragment.binding.playbtn.setImageResource(R.drawable.baseline_play_arrow_24)
                                Music_player.isPlaying = false
                                mediaPlayer!!.pause()
                                showOnlineNotification(R.drawable.baseline_play_arrow_24)
                            }else{
                                //play music
                                Music_player.binding.playorpausebtn.setIconResource(R.drawable.baseline_pause_24)
                                NowPlaying_Fragment.binding.playbtn.setImageResource(R.drawable.baseline_pause_24)
                                Music_player.isPlaying = true
                                mediaPlayer!!.start()
                                showOnlineNotification(R.drawable.baseline_pause_24)
                            }
                            return super.onMediaButtonEvent(mediaButtonEvent)
                        }

                        override fun onSeekTo(pos: Long) {
                            super.onSeekTo(pos)
                            mediaPlayer!!.seekTo(pos.toInt())
                            val playBackStateNew = Builder()
                                .setState(STATE_PLAYING, mediaPlayer!!.currentPosition.toLong(), playbackSpeed)
                                .setActions(ACTION_SEEK_TO)
                                .build()
                            mediaSession.setPlaybackState(playBackStateNew)
                        }
                    })

                }
                startForeground(11, notification)
            }

            override fun onLoadCleared(placeholder: Drawable?) {

            }
        }

        if (Music_player.songPosition >= 0 && Music_player.songPosition < Music_player.musicList.size) {
            val imageUrl = Music_player.musicList[Music_player.songPosition].Image
            Glide.with(this)
                .asBitmap()
                .load(imageUrl)
                .into(notificationTarget)
        } else {

        }

    }

    fun showOfflineNotification(playPauseBtn: Int) {
        val intent = NavDeepLinkBuilder(this)
            .setGraph(R.navigation.nav_view)
            .setDestination(R.id.mymusic_Fragment)
            .createPendingIntent()

        val flag = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            PendingIntent.FLAG_IMMUTABLE
        } else {
            PendingIntent.FLAG_UPDATE_CURRENT
        }

        isOnline=false
        val prevIntent = Intent(baseContext, NotificationReciever::class.java).setAction(ApplicationClass.previous)
        val prevPendingIntent = PendingIntent.getBroadcast(baseContext, 0, prevIntent, flag)

        val playIntent = Intent(baseContext, NotificationReciever::class.java).setAction(ApplicationClass.play)
        val playPendingIntent = PendingIntent.getBroadcast(baseContext, 0, playIntent, flag)

        val nextIntent = Intent(baseContext, NotificationReciever::class.java).setAction(ApplicationClass.next)
        val nextPendingIntent = PendingIntent.getBroadcast(baseContext, 0, nextIntent, flag)

        val exitIntent = Intent(baseContext, NotificationReciever::class.java).setAction(ApplicationClass.exit)
        val exitPendingIntent = PendingIntent.getBroadcast(baseContext, 0, exitIntent, flag)

        val imgArt = getImgArt(Music_player.musicList2[Music_player.songPosition].path)
        val image = if(imgArt != null){
            BitmapFactory.decodeByteArray(imgArt, 0, imgArt.size)
        }else{
            BitmapFactory.decodeResource(resources, R.drawable.musical)
        }

        val notificationIcon = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            R.drawable.musical
        } else {
            R.drawable.sounds
        }
        val notificationn = NotificationCompat.Builder(baseContext, ApplicationClass.Channel_Id)
            .setContentIntent(intent)
            .setContentTitle(Music_player.musicList2[Music_player.songPosition].songname)
            .setContentText(Music_player.musicList2[Music_player.songPosition].Artistname)
            .setSmallIcon(notificationIcon)
            .setLargeIcon(image)
            .setLargeIcon(BitmapFactory.decodeResource(resources, R.drawable.musical))
            .setStyle(
                androidx.media.app.NotificationCompat.MediaStyle()
                    .setMediaSession(mediaSession.sessionToken)
            )
            .setPriority(androidx.core.app.NotificationCompat.PRIORITY_HIGH)
            .setVisibility(androidx.core.app.NotificationCompat.VISIBILITY_PUBLIC)
            .setOnlyAlertOnce(true)
            .setLargeIcon(image)
            .addAction(R.drawable.baseline_previous, "Previous", prevPendingIntent)
            .addAction(playPauseBtn, "Play", playPendingIntent)
            .addAction(R.drawable.baseline_next, "Next", nextPendingIntent)
            .addAction(R.drawable.exit, "Exit", exitPendingIntent)
            .build()
        startForeground(11,notificationn)
    }
    suspend fun onlineMediaPlayer(){
        try{
            withContext(Dispatchers.IO) {
                if (Music_player.musicService!!.mediaPlayer == null) Music_player.musicService!!.mediaPlayer = MediaPlayer()
                Music_player.musicService!!.mediaPlayer!!.reset()
                Music_player.musicService!!.mediaPlayer!!.setDataSource(Music_player.musicList[Music_player.songPosition].songUrl)
                Music_player.musicService!!.mediaPlayer!!.prepare()
            }
            withContext(Dispatchers.Main){
                Music_player.binding.playorpausebtn.setIconResource(R.drawable.baseline_pause_24)
                Music_player.binding.Seekbarstarttime.text= songDuration(Music_player.musicService!!.mediaPlayer!!.currentPosition.toLong())
                Music_player.binding.Seekbarendtime.text= songDuration(Music_player.musicService!!.mediaPlayer!!.duration.toLong())
                Music_player.binding.seekbar.progress=0
                Music_player.binding.seekbar.max= Music_player.musicService!!.mediaPlayer!!.duration
                Music_player.musicService!!.showOnlineNotification(R.drawable.baseline_pause_24)
            }
        }catch (e:java.lang.Exception){

        }

    }

    fun offlineMediaPlayer2() {
        try{
            if (mediaPlayer == null) mediaPlayer = MediaPlayer()
            mediaPlayer!!.reset()
            mediaPlayer!!.setDataSource(Music_player.musicList2[Music_player.songPosition].path)
            mediaPlayer!!.prepare()
            showOfflineNotification(R.drawable.baseline_pause_24)
            Music_player.binding.Seekbarstarttime.text= songDuration(mediaPlayer!!.currentPosition.toLong())
            Music_player.binding.Seekbarendtime.text= songDuration(mediaPlayer!!.duration.toLong())
            Music_player.binding.seekbar.progress=0
            Music_player.binding.seekbar.max= mediaPlayer!!.duration
            Music_player.binding.playorpausebtn.setIconResource(R.drawable.baseline_pause_24)
            Music_player.nowPlayingId=Music_player.musicList2[Music_player.songPosition].id
        }catch (e:java.lang.Exception){
            return
        }

    }
    fun seekBarSetup(){
        runnable = Runnable {
            Music_player.binding.Seekbarstarttime.text = songDuration(mediaPlayer!!.currentPosition.toLong())
            Music_player.binding.seekbar.progress = mediaPlayer!!.currentPosition
            Handler(Looper.getMainLooper()).postDelayed(runnable, 200)
        }
        Handler(Looper.getMainLooper()).postDelayed(runnable, 0)
    }
    //for while call come
//    override fun onAudioFocusChange(focusChange: Int) {
//        if(focusChange <=0){
//            Music_player.binding.playorpausebtn.setIconResource(R.drawable.baseline_play_arrow_24)
//            NowPlaying_Fragment.binding.playbtn.setImageResource(R.drawable.baseline_play_arrow_24)
//            Music_player.isPlaying =false
//            mediaPlayer!!.pause()
//            showNotification(R.drawable.baseline_play_arrow_24)
//
//        }
//        else{
//            Music_player.binding.playorpausebtn.setIconResource(R.drawable.baseline_pause_24)
//            NowPlaying_Fragment.binding.playbtn.setImageResource(R.drawable.baseline_pause_24)
//            Music_player.isPlaying =true
//            mediaPlayer!!.start()
//            showNotification(R.drawable.baseline_pause_24)
//
//        }
//    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return START_STICKY
    }

}