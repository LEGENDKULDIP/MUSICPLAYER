package com.example.musicplayer.likedSongs

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.example.musicplayer.Music_player
import com.example.musicplayer.databinding.ListSongBinding
import com.example.musicplayer.models.SongModel

class MyOnlineSongAdapter(private val context: Context, private val musiclist:ArrayList<SongModel.Songs>):RecyclerView.Adapter<MyOnlineSongAdapter.MyViewHolder>() {
    class MyViewHolder(val binding:ListSongBinding):RecyclerView.ViewHolder(binding.root){
        val songName=binding.TvSongname
        val album=binding.TvSingername
        val image=binding.musicImage

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return MyViewHolder(ListSongBinding.inflate(LayoutInflater.from(parent.context),parent,false))
    }

    override fun getItemCount(): Int {
        return musiclist.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val song = musiclist[position]
            holder.songName.text = song.title
            holder.album.text = song.artist
        holder.image.load(song.Image)

        holder.binding.root.setOnClickListener {
            val intent= Intent(context, Music_player::class.java)
            intent.putExtra("index", position)
            intent.putExtra("class", "MusicAdapter")
            context.startActivity(intent)
        }
    }
}