package com.example.musicplayer

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.musicplayer.Fragment.NowPlaying_Fragment
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.system.exitProcess


class NotificationReciever: BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        when(intent?.action){
            ApplicationClass.previous -> CoroutineScope(Dispatchers.Main).launch {
                prevNextSong(increment = false, context=context!! ) }
            ApplicationClass.play ->
                if(Music_player.isPlaying) pauseMusic() else playMusic()
            ApplicationClass.next ->   CoroutineScope(Dispatchers.Main).launch {
                prevNextSong(increment = true, context=context!! ) }
            ApplicationClass.exit -> {
                Music_player.musicService!!.stopForeground(true)
                Music_player.musicService =null
                exitProcess(1)
            }
        }
    }
    //for smooth playback
    private fun updateNotificationAndUI(context: Context) {
        Music_player.binding.playorpausebtn.setIconResource(R.drawable.baseline_pause_24)
        Glide.with(context).load(Music_player.musicList[Music_player.songPosition].Image).apply(RequestOptions().placeholder(R.drawable.musical).centerCrop()).into(Music_player.binding.musiceimage)
        Music_player.binding.musicname.text = Music_player.musicList[Music_player.songPosition].title
        Glide.with(context).load(Music_player.musicList[Music_player.songPosition].Image).apply(RequestOptions().placeholder(R.drawable.musical).centerCrop()).into(NowPlaying_Fragment.binding.musicImage)
        NowPlaying_Fragment.binding.TvSongname.text = Music_player.musicList[Music_player.songPosition].title
        NowPlaying_Fragment.binding.TvSingername.text = Music_player.musicList[Music_player.songPosition].artist
        Music_player.fIndex = favouriteChecker(Music_player.musicList[Music_player.songPosition].title)
        if (Music_player.isFavourite) Music_player.binding.likeimgbtn.setImageResource(R.drawable.baseline_redcolorlikebtn)
        else Music_player.binding.likeimgbtn.setImageResource(R.drawable.baseline_favorite_24)
        playMusic()

    }
    private fun playMusic() {
        Music_player.isPlaying = true
        Music_player.musicService!!.mediaPlayer!!.start()
        if (Music_player.isOnline) {
            Music_player.musicService!!.showOnlineNotification(R.drawable.baseline_pause_24)
        } else {
            Music_player.musicService!!.showOfflineNotification(R.drawable.baseline_pause_24)
        }
        Music_player.binding.playorpausebtn.setIconResource(R.drawable.baseline_pause_24)
        NowPlaying_Fragment.binding.playbtn.setImageResource(R.drawable.baseline_pause_24)
    }
    private fun pauseMusic() {
        Music_player.isPlaying = false
        Music_player.musicService!!.mediaPlayer!!.pause()
        if (Music_player.isOnline) {
            Music_player.musicService!!.showOnlineNotification(R.drawable.baseline_play_arrow_24)
        } else {
            Music_player.musicService!!.showOfflineNotification(R.drawable.baseline_play_arrow_24)
        }
        Music_player.binding.playorpausebtn.setIconResource(R.drawable.baseline_play_arrow_24)
        NowPlaying_Fragment.binding .playbtn.setImageResource(R.drawable.baseline_play_arrow_24)
    }

    private suspend fun prevNextSong(increment: Boolean, context: Context) {
        if(Music_player.isOnline){
            setSongPosition(increment = increment, className = "MusicAdapter")
            updateNotificationAndUI(context)
            Music_player.musicService!!.onlineMediaPlayer()
            playMusic()
        }else {
            setSongPosition(increment = increment, className = "MyMusicAdapter")
            Music_player.musicService!!.offlineMediaPlayer2()
            Music_player.binding.playorpausebtn.setIconResource(R.drawable.baseline_pause_24)
            Glide.with(context).load(Music_player.musicList2[Music_player.songPosition].songImage).apply(RequestOptions().placeholder(R.drawable.musical).centerCrop()).into(Music_player.binding.musiceimage)
            Music_player.binding.musicname.text = Music_player.musicList2[Music_player.songPosition].songname
            Glide.with(context).load(Music_player.musicList2[Music_player.songPosition].songImage).apply(RequestOptions().placeholder(R.drawable.musical).centerCrop()).into(NowPlaying_Fragment.binding.musicImage)
            NowPlaying_Fragment.binding.TvSongname.text= Music_player.musicList2[Music_player.songPosition].songname
            NowPlaying_Fragment.binding.TvSingername.text= Music_player.musicList2[Music_player.songPosition].Artistname
            playMusic()
        }
    }
}