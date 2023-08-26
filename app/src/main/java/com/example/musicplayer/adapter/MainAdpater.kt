package com.example.musicplayer.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.musicplayer.databinding.ContentReyclerviewBinding
import com.example.musicplayer.models.MainModel

class MainAdpater(private val collection: List<MainModel>): RecyclerView.Adapter<MainAdpater.MyViewHolder>() {
    inner class MyViewHolder(val binding:ContentReyclerviewBinding):RecyclerView.ViewHolder(binding.root){

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return MyViewHolder(ContentReyclerviewBinding.inflate(LayoutInflater.from(parent.context),parent,false))
    }

    override fun getItemCount(): Int {
        return collection.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.binding.apply {
            val collection=collection[position]
            contentTitle.text=collection.title
            val songAdapter=SongAdpater(collection.songmodels)
            contentRecyclerView.adapter=songAdapter

        }
    }
}