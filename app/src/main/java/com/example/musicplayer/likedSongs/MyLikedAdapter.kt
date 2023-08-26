package com.example.musicplayer.likedSongs

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater.from
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.musicplayer.Music_player
import com.example.musicplayer.R
import com.example.musicplayer.databinding.ListSongBinding
import com.example.musicplayer.models.SongModel

class MyLikedAdapter(private val context: Context,private val musiclist:ArrayList<SongModel.Songs>):RecyclerView.Adapter<MyLikedAdapter.MyViewHolder>() {
    class MyViewHolder(binding:ListSongBinding):RecyclerView.ViewHolder(binding.root){
        val songName=binding.TvSongname
        val album=binding.TvSingername
        val image=binding.musicImage
        val root=binding.root

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return MyViewHolder(ListSongBinding.inflate(from(parent.context),parent,false))
    }

    override fun getItemCount(): Int {
        return musiclist.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.songName.text= musiclist[position].title
        holder.album.text=musiclist[position].artist
        Glide.with(context).load(musiclist[position].Image).apply(
            RequestOptions().placeholder(R.drawable.musical).centerCrop()).into(holder.image)
        holder.root.setOnClickListener {
            val intent= Intent(context, Music_player::class.java)
            intent.putExtra("index",position)
            intent.putExtra("class", "FavouriteAdapter")
            ContextCompat.startActivity(context,intent,null)
        }
    }
}