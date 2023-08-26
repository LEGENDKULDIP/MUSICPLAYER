package com.example.musicplayer.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.example.musicplayer.Music_player
import com.example.musicplayer.databinding.ListSongBinding
import com.example.musicplayer.models.SongModel

class SearchAdapter(private val context: Context, private var songList: ArrayList<SongModel.Songs>):RecyclerView.Adapter<SearchAdapter.ViewHolder>() {
    private var filteredSongList = mutableListOf<SongModel.Songs>()
    class ViewHolder(val binding:ListSongBinding):RecyclerView.ViewHolder(binding.root){
        val songName=binding.TvSongname
        val album=binding.TvSingername
        val image=binding.musicImage


        fun bind(song: SongModel.Songs) {
            songName.text = song.title
            album.text = song.artist
            image.load(song.Image)

        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchAdapter.ViewHolder {
        return ViewHolder(ListSongBinding.inflate(LayoutInflater.from(parent.context)))
    }
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if (filteredSongList.isNotEmpty()) {
            val song = filteredSongList[position]
            holder.bind(song)
            holder.itemView.visibility = View.VISIBLE
        } else {
            holder.itemView.visibility = View.GONE
        }
        holder.binding.root.setOnClickListener {
            val song = filteredSongList[position]
            val songIndexInOriginalList = songList.indexOf(song)
            val intent = Intent(holder.itemView.context, Music_player::class.java)
            intent.putExtra("index", songIndexInOriginalList)
            intent.putExtra("class", "SearchMusicAdapter")
            holder.itemView.context.startActivity(intent)
        }


    }

    override fun getItemCount(): Int {
        return filteredSongList.size
    }

    fun filterSongs(filteredList: List<SongModel.Songs>) {
        filteredSongList=filteredList.toMutableList()
        notifyDataSetChanged()
    }
}