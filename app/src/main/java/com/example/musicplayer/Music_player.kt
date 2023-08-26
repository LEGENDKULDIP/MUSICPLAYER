package com.example.musicplayer

import android.annotation.SuppressLint
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.graphics.Color
import android.media.MediaPlayer
import android.media.audiofx.AudioEffect
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import android.view.View
import android.widget.ImageView
import android.widget.SeekBar
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.musicplayer.Fragment.LikedSong_Fragment
import com.example.musicplayer.Fragment.Mymusic_Fragment
import com.example.musicplayer.Fragment.OnlineSong_Fragment
import com.example.musicplayer.Fragment.Search_Fragment
import com.example.musicplayer.databinding.ActivityMusicPlayerBinding
import com.example.musicplayer.models.SongModel
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class Music_player : AppCompatActivity(),ServiceConnection,MediaPlayer.OnCompletionListener {
    private var offlineMediaPlayerJob: Job? = null
    private var updateSeekBarJob: Job? = null
    companion object{
        @SuppressLint("StaticFieldLeak")
        lateinit var binding:ActivityMusicPlayerBinding
        lateinit var musicList:ArrayList<SongModel.Songs>
        lateinit var musicList2:ArrayList<MusicList>
        var songPosition:Int=0
        var isOnline:Boolean=true
        var repeat:Boolean=false
        var musicService:MusicService?=null
        var isPlaying:Boolean=false
        var min10:Boolean=false
        var min20: Boolean = false
        var min30: Boolean = false
        var min45: Boolean = false
        var nowPlayingId:String=""
        var isFavourite:Boolean=false
        var fIndex:Int=1
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityMusicPlayerBinding.inflate(layoutInflater)
        setContentView(binding.root)
    lifecycleScope.launch{
        initilizelayout()
    }
    binding.playorpausebtn.setOnClickListener {
        if(isPlaying) stopMusic() else playMusic()
    }
    binding.previousbtn.setOnClickListener {
        lifecycleScope.launch{
            preNextSong(increment = false)
        }  }
    binding.nextbtn.setOnClickListener{
        lifecycleScope.launch {
            preNextSong(increment = true)

        }
    }
    binding.likeimgbtn.setOnClickListener {
        if(isFavourite){
            isFavourite=false
            binding.likeimgbtn.setImageResource(R.drawable.baseline_favorite_24)
            LikedSong_Fragment.likedSong.removeAt(fIndex)
        }
        else{
            isFavourite=true
            binding.likeimgbtn.setImageResource(R.drawable.baseline_redcolorlikebtn)
            LikedSong_Fragment.likedSong.add(musicList[songPosition])
        }
    }

    binding.seekbar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {

        override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
            if(fromUser) {
                musicService!!.mediaPlayer!!.seekTo(progress)
                if(isOnline){
                    musicService!!.showOnlineNotification(if (isPlaying) R.drawable.baseline_pause_24 else R.drawable.baseline_play_arrow_24)
                }else{
                    musicService!!.showOfflineNotification(if (isPlaying) R.drawable.baseline_pause_24 else R.drawable.baseline_play_arrow_24)
                }
            }


        }
        override fun onStartTrackingTouch(p0: SeekBar?) =Unit

        override fun onStopTrackingTouch(p0: SeekBar?) =Unit
    })
        binding.shufflebtn.setOnClickListener {
            if(!repeat){
                repeat=true
                Toast.makeText(this,"Repeat is on",Toast.LENGTH_SHORT).show()
                binding.shufflebtn.setColorFilter(ContextCompat.getColor(this,R.color.red))
            }else{
                repeat=false
                Toast.makeText(this,"Repeat is off",Toast.LENGTH_SHORT).show()
                binding.shufflebtn.setColorFilter(ContextCompat.getColor(this,R.color.white_smoke))
            }
        }
        binding.backbtn.setOnClickListener { finish() }
        binding.equilizerbtn.setOnClickListener {
           try{
               val eqIntent=Intent(AudioEffect.ACTION_DISPLAY_AUDIO_EFFECT_CONTROL_PANEL)
               eqIntent.putExtra(AudioEffect.EXTRA_AUDIO_SESSION, musicService!!.mediaPlayer!!.audioSessionId)
               eqIntent.putExtra(AudioEffect.EXTRA_PACKAGE_NAME,baseContext.packageName)
               eqIntent.putExtra(AudioEffect.EXTRA_CONTENT_TYPE, AudioEffect.CONTENT_TYPE_MUSIC)
               startActivityForResult(eqIntent,11)
           }catch (e:java.lang.Exception){
               Toast.makeText(this,"Something went wrong",Toast.LENGTH_SHORT).show()
           }
        }
        binding.timerbtn.setOnClickListener {
            val timer= min10 || min20 || min30 || min45
            if(!timer)showBottomSheetDialog()
            else{
                val builder=MaterialAlertDialogBuilder(this)
                builder.setTitle("Stop Timer")
                    .setMessage("Do you want to stop timer?")
                    .setPositiveButton("Yes"){ _, _ ->
                        min10=false
                        min20=false
                        min30=false
                        min45=false
                        binding.timerbtn.setColorFilter(ContextCompat.getColor(this,R.color.white_smoke))
                    }
                    .setNegativeButton("No"){dialog, _ ->
                        dialog.dismiss()
                    }
                val customDialog=builder.create()
                customDialog.show()
                customDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.RED)
                customDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.RED)

            }
        }
        binding.sharebtn.setOnClickListener {
            val intent=Intent()
            intent.action=Intent.ACTION_SEND
            intent.type="audio/*"
            if(isOnline){
                intent.putExtra(Intent.EXTRA_STREAM, Uri.parse(musicList[songPosition].songUrl))
            }else{
                intent.putExtra(Intent.EXTRA_STREAM, Uri.parse(musicList2[songPosition].path))
            }
            startActivity(Intent.createChooser(intent,"Share Music to"))
        }
    }
    fun setLayout(){
        fIndex = favouriteChecker(musicList[songPosition].title)
        Glide.with(this).load(musicList[songPosition].Image).apply(RequestOptions().placeholder(R.drawable.musical).centerCrop()).into(binding.musiceimage)
        binding.musicname.text= musicList[songPosition].title
        if(isFavourite) binding.likeimgbtn.setImageResource(R.drawable.baseline_redcolorlikebtn)
        else binding.likeimgbtn.setImageResource(R.drawable.baseline_favorite_24)
        if(repeat) binding.shufflebtn.setColorFilter(ContextCompat.getColor(this,R.color.red))
        if(min10 || min20 || min30 || min45) binding.timerbtn.setColorFilter(ContextCompat.getColor(this,R.color.blue))


    }
    fun setLayout2(){
        Glide.with(this).load(musicList2[songPosition].songImage).apply(RequestOptions().placeholder(R.drawable.musical).centerCrop()).into(binding.musiceimage)
        binding.musicname.text= musicList2[songPosition].songname
        if(repeat) binding.shufflebtn.setColorFilter(ContextCompat.getColor(this,R.color.red))
        if(min10 || min20 || min30 || min45) binding.timerbtn.setColorFilter(ContextCompat.getColor(this,R.color.blue))




    }
suspend fun onlineMediaPlayer(){
    binding.likeimgbtn.visibility= View.VISIBLE
    try{
        // Show the notification first
        withContext(Dispatchers.Main){
            binding.playorpausebtn.setIconResource(R.drawable.baseline_pause_24)
            binding.loadingProgressBar.visibility = View.VISIBLE
            binding.playorpausebtn.visibility = View.GONE
            binding.Seekbarstarttime.text = songDuration(0) // Start time at 0
            binding.Seekbarendtime.text = songDuration(0)   // End time at 0
            binding.seekbar.progress = 0
            binding.seekbar.max = 0                        // Max progress at 0 initially
            musicService!!.showOnlineNotification(R.drawable.baseline_pause_24)
        }

        // Prepare and start playback
        withContext(Dispatchers.IO) {
            if (musicService!!.mediaPlayer == null) musicService!!.mediaPlayer = MediaPlayer()
            musicService!!.mediaPlayer!!.reset()
            musicService!!.mediaPlayer!!.setDataSource(musicList[songPosition].songUrl)
            musicService!!.mediaPlayer!!.prepare()
            musicService!!.mediaPlayer!!.start()
        }

        // Update UI after playback starts
        withContext(Dispatchers.Main){
            isPlaying=true
            isOnline=true
            binding.playorpausebtn.visibility = View.VISIBLE
            binding.loadingProgressBar.visibility=View.GONE
            binding.playorpausebtn.setIconResource(R.drawable.baseline_pause_24)
            updateSeekBarJob = lifecycleScope.launch {
                binding.Seekbarstarttime.text =
                    songDuration(musicService!!.mediaPlayer!!.currentPosition.toLong())
                binding.Seekbarendtime.text =
                    songDuration(musicService!!.mediaPlayer!!.duration.toLong())
                binding.seekbar.progress = 0
                binding.seekbar.max = musicService!!.mediaPlayer!!.duration
//                musicService!!.mediaPlayer!!.setOnCompletionListener(this@Music_player)

            }
        }

    } catch (e:java.lang.Exception){
        // Handle exceptions
    }
}

    private suspend fun offlineMediaPlayer2() {
        offlineMediaPlayerJob?.cancel()
        withContext(Dispatchers.IO) {
            if (musicService!!.mediaPlayer == null) musicService!!.mediaPlayer = MediaPlayer()


            if (musicService!!.mediaPlayer!!.isPlaying) {
                musicService!!.mediaPlayer!!.pause()
            }

            try {
                musicService!!.mediaPlayer!!.reset()
                musicService!!.mediaPlayer!!.setDataSource(musicList2[songPosition].path)
                musicService!!.mediaPlayer!!.prepare()
                musicService!!.showOfflineNotification(R.drawable.baseline_pause_24)
                musicService!!.mediaPlayer!!.start()

            } catch (e: Exception) {
                // Handle the exception, e.g., log or show an error message
            }
        }


        withContext(Dispatchers.Main) {
            isPlaying = true
            isOnline=false
            binding.playorpausebtn.setIconResource(R.drawable.baseline_pause_24)
            updateSeekBarJob = lifecycleScope.launch {
                binding.Seekbarstarttime.text =
                    songDuration(musicService!!.mediaPlayer!!.currentPosition.toLong())
                binding.Seekbarendtime.text =
                    songDuration(musicService!!.mediaPlayer!!.duration.toLong())
                binding.seekbar.progress = 0
                binding.seekbar.max = musicService!!.mediaPlayer!!.duration
//                musicService!!.mediaPlayer!!.setOnCompletionListener(this@Music_player)

            }
        }
    }
    private fun initilizelayout(){
        songPosition=intent.getIntExtra("index",0)
        when(intent.getStringExtra("class")){
            "NowPlaying" ->{
                if(Music_player.isOnline){
                    setLayout()
                    binding.likeimgbtn.visibility=View.VISIBLE
                    binding.Seekbarstarttime.text= songDuration(musicService!!.mediaPlayer!!.currentPosition.toLong())
                    binding.Seekbarendtime.text= songDuration(musicService!!.mediaPlayer!!.duration.toLong())
                    binding.seekbar.progress= musicService!!.mediaPlayer!!.currentPosition
                    binding.seekbar.max= musicService!!.mediaPlayer!!.duration
                } else{
                    setLayout2()
                    binding.Seekbarstarttime.text= songDuration(musicService!!.mediaPlayer!!.currentPosition.toLong())
                    binding.Seekbarendtime.text= songDuration(musicService!!.mediaPlayer!!.duration.toLong())
                    binding.seekbar.progress= musicService!!.mediaPlayer!!.currentPosition
                    binding.seekbar.max= musicService!!.mediaPlayer!!.duration
                }
                if(isPlaying) binding.playorpausebtn.setIconResource(R.drawable.baseline_pause_24)
                else binding.playorpausebtn.setIconResource(R.drawable.baseline_play_arrow_24)

            }
            "FavouriteAdapter" -> {
                val intent= Intent(this, MusicService::class.java)
                bindService(intent,this, Context.BIND_AUTO_CREATE)
                startService(intent)
                musicList= ArrayList()
                binding.Seekbarstarttime.text= songDuration(musicService!!.mediaPlayer!!.currentPosition.toLong())
                binding.Seekbarendtime.text= songDuration(musicService!!.mediaPlayer!!.duration.toLong())
                binding.seekbar.progress= musicService!!.mediaPlayer!!.currentPosition
                binding.seekbar.max= musicService!!.mediaPlayer!!.duration
                musicList.addAll(LikedSong_Fragment.likedSong)
                setLayout()
            }
            "MusicAdapter" ->{
                val intent= Intent(this, MusicService::class.java)
                bindService(intent,this, Context.BIND_AUTO_CREATE)
                startService(intent)
                musicList= ArrayList()
                OnlineSong_Fragment.songList?.let { musicList.addAll(it) }
                setLayout()
            }
            "MyMusicAdapter" ->{
                val intent= Intent(this, MusicService::class.java)
                bindService(intent,this, Context.BIND_AUTO_CREATE)
                startService(intent)
                musicList2= ArrayList()
                musicList2.addAll(Mymusic_Fragment.Musiclistt)
                setLayout2()
            }
            "SearchMusicAdapter" ->{
                val intent=Intent(this,MusicService::class.java)
                bindService(intent,this, Context.BIND_AUTO_CREATE)
                startService(intent)
                musicList= ArrayList()
                musicList.addAll(Search_Fragment?.searchSongList!!)
                setLayout()
            }
        }
    }

    private fun playMusic(){
    binding.playorpausebtn.setIconResource(R.drawable.baseline_pause_24)
    isPlaying=true
    musicService!!.mediaPlayer!!.start()
    if(isOnline){
        musicService!!.showOnlineNotification(R.drawable.baseline_pause_24)
    } else{
        musicService!!.showOfflineNotification(R.drawable.baseline_pause_24)
    }
}
    private fun stopMusic(){
        binding.playorpausebtn.setIconResource(R.drawable.baseline_play_arrow_24)
        isPlaying=false
        musicService!!.mediaPlayer!!.pause()
        if(isOnline){
            musicService!!.showOnlineNotification(R.drawable.baseline_play_arrow_24)
        } else{
            musicService!!.showOfflineNotification(R.drawable.baseline_play_arrow_24)
        }



    }
    suspend fun preNextSong(increment: Boolean) {
        if(Music_player.isOnline){
            if(increment){
                setSongPosition(increment = true, className = "MusicAdapter")
                setLayout()
                onlineMediaPlayer()
            }else{
                setSongPosition(increment = false, className = "MusicAdapter")
                setLayout()
                onlineMediaPlayer()
            }
        }else{
            if(increment){
                setSongPosition(increment = true, className = "MyMusicAdapter")
                setLayout2()
                offlineMediaPlayer2()
            }else{
                setSongPosition(increment = false, className = "MyMusicAdapter")
                setLayout2()
                offlineMediaPlayer2()
            }
        }

    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
        val binder=service as MusicService.MyBinder
        musicService =binder.currentService()
        val selectedClass = intent.getStringExtra("class")
        lifecycleScope.launch {
            if (selectedClass == "MusicAdapter" || selectedClass =="SearchMusicAdapter" || selectedClass=="FavouriteAdapter" || selectedClass=="SearchMusicAdapter") {
                onlineMediaPlayer()
                musicService!!.seekBarSetup()

            } else if (selectedClass == "MyMusicAdapter") {
                offlineMediaPlayer2()
                musicService!!.seekBarSetup()
           }
        }


    }

    override fun onServiceDisconnected(p0: ComponentName?) {
        musicService=null
}

    override fun onCompletion(mediaPlayer: MediaPlayer?) {
//        if(Music_player.isOnline){
//            setSongPosition(true, className = "MusicAdapter")
//            lifecycleScope.launch {  onlineMediaPlayer() }
//            setLayout()
//
//        }else{
//            setSongPosition(true, className = "MyMusicAdapter" )
//            lifecycleScope.launch {  offlineMediaPlayer2() }
//            setLayout2()
        }

        fun showBottomSheetDialog(){
        val bottomsheet= BottomSheetDialog(this,R.style.BottomSheetStyle)
        bottomsheet.setContentView(R.layout.bottom_sheet_dialog)
        val img1 =bottomsheet.findViewById<ImageView>(R.id.IVclock1)
        bottomsheet.apply {
            findViewById<TextView>(R.id.tv10mintimer)?.setOnClickListener {
                binding.timerbtn.setColorFilter(ContextCompat.getColor(context,R.color.blue))
                img1?.setColorFilter(ContextCompat.getColor(this@Music_player,R.color.red))
                Toast.makeText(baseContext, "Music will stop after 10min", Toast.LENGTH_SHORT).show()
                min10=true
                Thread{Thread.sleep(10 * 60 * 1000)
                    if(min10) exitApplication() }.start()
                bottomsheet.dismiss()
            }
            findViewById<TextView>(R.id.tv20mintimer)?.setOnClickListener {
                binding.timerbtn.setColorFilter(ContextCompat.getColor(context,R.color.blue))
                Toast.makeText(baseContext, "Music will stop after 20min", Toast.LENGTH_SHORT).show()
                min20=true
                Thread{Thread.sleep(20 * 60 * 1000)
                    if(min20) exitApplication() }.start()
                bottomsheet.dismiss()
            }
            findViewById<TextView>(R.id.tv30mintimer)?.setOnClickListener {
                binding.timerbtn.setColorFilter(ContextCompat.getColor(context,R.color.blue))
                Toast.makeText(baseContext, "Music will stop after 30min", Toast.LENGTH_SHORT).show()
                min30=true
                Thread{Thread.sleep(30 * 60 * 1000)
                    if(min30) exitApplication() }.start()
                bottomsheet.dismiss()
            }
            findViewById<TextView>(R.id.tv45mintimer)?.setOnClickListener {
                binding.timerbtn.setColorFilter(ContextCompat.getColor(context,R.color.blue))
                Toast.makeText(baseContext, "Music will stop after 45min", Toast.LENGTH_SHORT).show()
                min45=true
                Thread{Thread.sleep(45 * 60 * 1000)
                    if(min45) exitApplication() }.start()
                bottomsheet.dismiss()
            }
        }
        bottomsheet.show()
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode ==11 || resultCode== RESULT_OK)
            return
    }

}