package com.example.musicplayer.adapter

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.example.musicplayer.Fragment.OnlineSong_Fragment
import com.example.musicplayer.R
import com.example.musicplayer.databinding.ContentItemBinding
import com.example.musicplayer.models.SongModel

class SongAdpater( private var songModel:List<SongModel>):RecyclerView.Adapter<SongAdpater.MyViewHolder>() {
    private var filteredSongModel: List<SongModel> = songModel
    inner class MyViewHolder(val binding:ContentItemBinding):RecyclerView.ViewHolder(binding.root){

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return MyViewHolder(ContentItemBinding.inflate(LayoutInflater.from(parent.context),parent,false))
    }

    override fun getItemCount(): Int {
        return songModel.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val data=songModel[position]
        val context=holder.itemView.context
        val songs=data.songs
        holder.binding.apply {
            singerimage.load(songModel[position].songImage)
            singername.text=data.artisName
        }
        holder.binding.root.setOnClickListener {
            val onlineSongFragment = OnlineSong_Fragment().apply {
                arguments= Bundle().apply {
                    putString("userPhoto",data.songImage)
                    putParcelableArrayList("songList", ArrayList(songs))
                    putString("imageList", data.songs[position].Image)

                }
            }

            val fragmentManager = (context as AppCompatActivity).supportFragmentManager
            fragmentManager.beginTransaction()
                .replace(R.id.mainfragment, onlineSongFragment)
                .addToBackStack(null)
                .commit()
        }
    }
}