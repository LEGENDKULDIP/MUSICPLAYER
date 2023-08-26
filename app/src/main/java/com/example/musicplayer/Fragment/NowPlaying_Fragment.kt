package com.example.musicplayer.Fragment

import android.annotation.SuppressLint
import android.app.ActivityOptions
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.musicplayer.Music_player
import com.example.musicplayer.R
import com.example.musicplayer.databinding.FragmentNowPlayingBinding
//import com.example.musicplayer.likedSongs.setSongPosition
import com.example.musicplayer.setSongPosition
import kotlinx.coroutines.launch

class NowPlaying_Fragment : Fragment() {

    companion object{
        @SuppressLint("StaticFieldLeak")
        lateinit var binding:FragmentNowPlayingBinding
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view=inflater.inflate(R.layout.fragment_now_playing_, container, false)
        binding= FragmentNowPlayingBinding.bind(view)
        binding.root.visibility=View.INVISIBLE
        binding.playbtn.setOnClickListener {
            if(Music_player.isPlaying)pauseMusic() else playMusic()
        }
        lifecycleScope.launch {
            binding.nextbtn.setOnClickListener {
                lifecycleScope.launch {
                    if (Music_player.isOnline) {
                        setSongPosition(increment = true, className = "MusicAdapter")
                        updateNowPlayingAndUi(requireContext())
                        Music_player.musicService!!.onlineMediaPlayer()
                        playMusic()
                    } else {
                        setSongPosition(increment = true, className = "MyMusicAdapter")
                        updateNowPlayingAndUi2(requireContext())
                        Music_player.musicService!!.offlineMediaPlayer2()
                        playMusic()
                    }
                }
            }
        }
        binding.root.setOnClickListener {
            Music_player.binding.likeimgbtn.visibility=View.GONE
            val intent= Intent(requireContext(), Music_player::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            intent.putExtra("index",Music_player.songPosition)
            intent.putExtra("class","NowPlaying")
            ContextCompat.startActivity(requireContext(),intent,getSlideUpAnimation())
        }
        return view

    }

    override fun onResume() {
        super.onResume()
        if(Music_player.musicService !=null){
            binding.root.visibility=View.VISIBLE
            binding.TvSongname.isSelected=true
            if (Music_player.isOnline){
                val musicItem = Music_player.musicList[Music_player.songPosition]
                Glide.with(this).load(musicItem.Image).apply(RequestOptions().placeholder(R.drawable.musical).centerCrop()).into(binding.musicImage)
                binding.TvSongname.text= Music_player.musicList[Music_player.songPosition].title
                binding.TvSingername.text= Music_player.musicList[Music_player.songPosition].artist
            }else{
                val musicItem2 = Music_player.musicList2[Music_player.songPosition]
                Glide.with(this).load(musicItem2.songImage).apply(RequestOptions().placeholder(R.drawable.musical).centerCrop()).into(binding.musicImage)
                binding.TvSongname.text= Music_player.musicList2[Music_player.songPosition].songname
                binding.TvSingername.text= Music_player.musicList2[Music_player.songPosition].Artistname
            }
            if(Music_player.isPlaying) binding.playbtn.setImageResource(R.drawable.baseline_pause_24)
            else binding.playbtn.setImageResource(R.drawable.baseline_play_arrow_24)

        }
//        Log.d("music","class$selectedClass , index$index")
    }
    private fun playMusic(){
        Music_player.musicService!!.mediaPlayer!!.start()
        binding.playbtn.setImageResource(R.drawable.baseline_pause_24)
        if(Music_player.isOnline){
            Music_player.musicService!!.showOnlineNotification(R.drawable.baseline_pause_24)
        }else{
            Music_player.musicService!!.showOfflineNotification(R.drawable.baseline_pause_24)
        }
        Music_player.isPlaying=true
    }
    private fun pauseMusic(){
        Music_player.musicService!!.mediaPlayer!!.pause()
        binding.playbtn.setImageResource(R.drawable.baseline_play_arrow_24)
        if(Music_player.isOnline){
            Music_player.musicService!!.showOnlineNotification(R.drawable.baseline_play_arrow_24)
        }else{
            Music_player.musicService!!.showOfflineNotification(R.drawable.baseline_play_arrow_24)
        }
        Music_player.isPlaying=false
    }
    fun getSlideUpAnimation(): Bundle? {
        val bundle= ActivityOptions.makeCustomAnimation(
            context,
            R.anim.slid_up,0
        ).toBundle()
        return bundle
    }
    //for smooth playback
    private fun updateNowPlayingAndUi(context: Context){
        Glide.with(this@NowPlaying_Fragment).load(Music_player.musicList[Music_player.songPosition].Image).apply(RequestOptions().placeholder(R.drawable.musical).centerCrop()).into(binding.musicImage)
        binding.TvSongname.text = Music_player.musicList[Music_player.songPosition].title
        binding.TvSingername.text = Music_player.musicList[Music_player.songPosition].artist
        Music_player.musicService!!.showOnlineNotification(R.drawable.baseline_pause_24)
        playMusic()
    }

    private fun updateNowPlayingAndUi2(context: Context){
        Glide.with(this@NowPlaying_Fragment).load(Music_player.musicList2[Music_player.songPosition].songImage).apply(RequestOptions().placeholder(R.drawable.musical).centerCrop()).into(binding.musicImage)
        binding.TvSongname.text = Music_player.musicList2[Music_player.songPosition].songname
        binding.TvSingername.text = Music_player.musicList2[Music_player.songPosition].Artistname
        Music_player.musicService!!.showOfflineNotification(R.drawable.baseline_pause_24)
        playMusic()
    }
}