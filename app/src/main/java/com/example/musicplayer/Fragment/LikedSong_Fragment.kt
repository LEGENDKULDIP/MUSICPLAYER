package com.example.musicplayer.Fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.musicplayer.R
import com.example.musicplayer.databinding.FragmentLikedSongBinding
import com.example.musicplayer.likedSongs.MyLikedAdapter
import com.example.musicplayer.models.SongModel


class LikedSong_Fragment : Fragment() {
    lateinit var binding:FragmentLikedSongBinding
    lateinit var myLikedAdapter: MyLikedAdapter

    companion object{
        var likedSong :ArrayList<SongModel.Songs> = ArrayList()
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view= inflater.inflate(R.layout.fragment_liked_song_, container, false)
        binding= FragmentLikedSongBinding.bind(view)
        binding.backbtn.setOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }
        binding.likedRecylerView.apply {
            adapter= MyLikedAdapter(requireContext(), likedSong)
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(requireContext())
        }
        return view
    }
}