package com.example.musicplayer.Fragment

import android.content.Context.MODE_PRIVATE
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import coil.load
import com.example.musicplayer.R
import com.example.musicplayer.databinding.FragmentOnlineSongBinding
import com.example.musicplayer.likedSongs.MyOnlineSongAdapter
import com.example.musicplayer.models.SongModel
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken

class OnlineSong_Fragment : Fragment() {
  lateinit var binding:FragmentOnlineSongBinding
  lateinit var myOnlineSongAdapter: MyOnlineSongAdapter
    private var userPhoto: String? = null
    companion object{
        var songList: ArrayList<SongModel.Songs>? = null

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            userPhoto=it.getString("userPhoto")
            songList=it.getParcelableArrayList("songList")
        }
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view= inflater.inflate(R.layout.fragment_online_song_, container, false)
        binding= FragmentOnlineSongBinding.bind(view)
        binding.onlineBackBtn.setOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }
        val imageView = view.findViewById<ImageView>(R.id.artistImage)

        initRecyclerView()

        //for retrieving liked data using shared preferences
        LikedSong_Fragment.likedSong= ArrayList()
        val editor=requireContext().getSharedPreferences("LIKED", MODE_PRIVATE)
        val jsonString=editor.getString("LikedSongs", null)
        val typeToken=object : TypeToken<ArrayList<SongModel.Songs>>(){}.type
        if(jsonString !=null){
            val data : ArrayList<SongModel.Songs> = GsonBuilder().create().fromJson(jsonString, typeToken)
            LikedSong_Fragment.likedSong.addAll(data)
        }


        userPhoto?.let {
            imageView.load(it)
        }
        return view
    }

    private fun initRecyclerView() {
        val songs:ArrayList<SongModel.Songs> = songList?.map { it -> SongModel.Songs(it.title,it.artist,it.Image,it.songUrl) } as? ArrayList<SongModel.Songs>?:ArrayList()
        myOnlineSongAdapter=MyOnlineSongAdapter(requireContext(),songs)
        binding.recyclerView.apply {
            setHasFixedSize(true)
            layoutManager=LinearLayoutManager(requireContext())
            adapter=myOnlineSongAdapter
        }
    }
//    override fun onDestroy() {
//        super.onDestroy()
//        if(!Music_player.isPlaying && Music_player.musicService !=null){
//            exitApplication()
//        }
//    }

    override fun onResume() {
        super.onResume()
        //for storing liked data using shared preferences
        val editor=requireContext().getSharedPreferences("LIKED", MODE_PRIVATE).edit()
        val jsonString= GsonBuilder().create().toJson(LikedSong_Fragment.likedSong)
        editor.putString("LikedSongs", jsonString)
        editor.apply()
    }


}