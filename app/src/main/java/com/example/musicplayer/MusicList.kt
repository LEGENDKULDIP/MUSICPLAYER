package com.example.musicplayer

import android.media.MediaMetadataRetriever
import com.example.musicplayer.Fragment.LikedSong_Fragment
import java.util.concurrent.TimeUnit
import kotlin.system.exitProcess

data class MusicList(
    val id:String,
    val songname:String,
    val Artistname:String,
    val songImage:String,
    val duration:Long=0,
    val path:String
)

fun songDuration(duration: Long):String{
    val min=TimeUnit.MINUTES.convert(duration,TimeUnit.MILLISECONDS)
    val sec=(TimeUnit.SECONDS.convert(duration,TimeUnit.MILLISECONDS)) -
            min * TimeUnit.SECONDS.convert(1,TimeUnit.MINUTES)
    return String.format("%02d:%02d",min,sec)
}

fun getImgArt(path:String): ByteArray? {
    val retriever=MediaMetadataRetriever()
    retriever.setDataSource(path)
    return retriever.embeddedPicture

}
fun setSongPosition(
    increment: Boolean,
    className: String // Pass the class name as a parameter
) {
    if (Music_player.isOnline) {
        if (increment) {
            if (Music_player.musicList.size - 1 == Music_player.songPosition)
                Music_player.songPosition = 0
            else ++Music_player.songPosition
        } else {
            if (Music_player.songPosition == 0)
                Music_player.songPosition = Music_player.musicList.size - 1
            else --Music_player.songPosition
        }
    } else {
        if (increment) {
            if (Music_player.musicList2.size - 1 == Music_player.songPosition)
                Music_player.songPosition = 0
            else ++Music_player.songPosition
        } else {
            if (Music_player.songPosition == 0)
                Music_player.songPosition = Music_player.musicList2.size - 1
            else --Music_player.songPosition
        }
    }
}
fun favouriteChecker(id:String) : Int{
    Music_player.isFavourite=false
    LikedSong_Fragment.likedSong.forEachIndexed { index, songs ->
        if(id ==songs.title){
            Music_player.isFavourite=true
            return index
        }
    }
    return -1
}
fun exitApplication(){
    if(Music_player.musicService!=null){
//        Music_player.musicService!!.audioManager.abandonAudioFocus(Music_player.musicService)
        Music_player.musicService!!.stopForeground(true)
        Music_player.musicService!!.mediaPlayer!!.release()
        Music_player.musicService =null }
    exitProcess(11)
}
