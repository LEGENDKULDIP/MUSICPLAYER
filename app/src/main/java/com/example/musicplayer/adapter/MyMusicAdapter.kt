package com.example.musicplayer.adapter


import android.app.ActivityOptions
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.musicplayer.MusicList
import com.example.musicplayer.Music_player
import com.example.musicplayer.R
import com.example.musicplayer.databinding.ListSongBinding

class MyMusicAdapter(private val context: Context, private var musiclist:ArrayList<MusicList>):RecyclerView.Adapter<MyMusicAdapter.MyViewHolder>() {
    class MyViewHolder(val binding:ListSongBinding):RecyclerView.ViewHolder(binding.root){
        val songName=binding.TvSongname
        val album=binding.TvSingername
        val image=binding.musicImage
        val root=binding.root
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return MyViewHolder(ListSongBinding.inflate(LayoutInflater.from(context),parent,false))

    }

    override fun getItemCount(): Int {
       return musiclist.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val data= musiclist[position]
        holder.apply {
            songName.text=data.songname
            album.text=data.Artistname
            Glide.with(context).load(data.songImage).apply(RequestOptions().placeholder(R.drawable.musical).centerCrop()).into(holder.image)
            root.setOnClickListener {
                val intent= Intent(context,Music_player::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                intent.putExtra("index",position)
                intent.putExtra("class","MyMusicAdapter")
                ContextCompat.startActivity(context,intent,getSlideUpAnimation())
            }
        }
    }

    fun getSlideUpAnimation(): Bundle? {
        val bundle=ActivityOptions.makeCustomAnimation(
            context,
            R.anim.slid_up,0
        ).toBundle()
        return bundle
    }
    fun updateMusicList(searchList:ArrayList<MusicList>){
        musiclist= ArrayList()
        musiclist.addAll(searchList)
        notifyDataSetChanged()
    }

}
