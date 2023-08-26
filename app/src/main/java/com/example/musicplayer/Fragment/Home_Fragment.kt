package com.example.musicplayer.Fragment

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.musicplayer.R
import com.example.musicplayer.adapter.MainAdpater
import com.example.musicplayer.databinding.FragmentHomeBinding
import com.example.musicplayer.utils.SampleData


class Home_Fragment : Fragment() {
    lateinit var binding:FragmentHomeBinding

    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view=inflater.inflate(R.layout.fragment_home_, container, false)
        binding = FragmentHomeBinding.bind(view)
        binding.apply {
            HomeRecyclerView.adapter=MainAdpater(SampleData.collections)
        }
        binding.HomeRecyclerView.layoutManager=LinearLayoutManager(requireContext())
        binding.LikedSongIcon.setOnClickListener {
            val fragment = LikedSong_Fragment()
            val transaction = requireActivity().supportFragmentManager.beginTransaction()
            transaction.replace(R.id.mainfragment, fragment)
            transaction.addToBackStack(null)
            transaction.commit()
        }
        return view
    }

}