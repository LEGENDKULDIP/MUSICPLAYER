package com.example.musicplayer.Fragment

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Color
import android.net.ConnectivityManager
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.SearchView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.musicplayer.R
import com.example.musicplayer.adapter.SearchAdapter
import com.example.musicplayer.databinding.FragmentSearchBinding
import com.example.musicplayer.models.SongModel
import com.example.musicplayer.utils.SampleData
import java.util.*

@Suppress("CANDIDATE_CHOSEN_USING_OVERLOAD_RESOLUTION_BY_LAMBDA_ANNOTATION")
class Search_Fragment : Fragment() {

    lateinit var binding:FragmentSearchBinding
    companion object{
        var searchSongList: ArrayList<SongModel.Songs>?=null
        private const val NETWORK_STATE_PERMISSION_CODE = 123
    }

    lateinit var searchAdapter: SearchAdapter
    private lateinit var connectivityManager:ConnectivityManager
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {


        val view = inflater.inflate(R.layout.fragment_search_, container, false)
        binding = FragmentSearchBinding.bind(view)

        connectivityManager=requireContext().getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        searchSongList =  listOf(SampleData.punjabiModel,SampleData.bollywoodModel,SampleData.hollywoodModels, SampleData.topPicks, SampleData.trendingArtist)
            .flatten()
            .flatMap { it.songs }
            .toCollection(kotlin.collections.ArrayList())


                searchAdapter = SearchAdapter(requireContext(), ArrayList(searchSongList ?: emptyList()))
                binding.recyclerView.adapter = searchAdapter

                val searchView: SearchView = view.findViewById(R.id.searchmusic)
                val id = searchView.context.resources.getIdentifier(
                    "android:id/search_src_text",
                    null,
                    null
                )
                val textView: TextView = searchView.findViewById(id) as TextView
                val textView2: EditText = searchView.findViewById(id) as EditText
                textView.setTextColor(Color.WHITE)
                textView2.setHintTextColor(Color.GRAY)

                binding.searchmusic.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                    override fun onQueryTextSubmit(query: String?): Boolean {
                        return false
                    }

                    override fun onQueryTextChange(newText: String?): Boolean {
                        if(isInternetConnected()){
                            filterSongs(newText.orEmpty())
                        }else{
                            Toast.makeText(requireContext(),"No internet Connection",Toast.LENGTH_SHORT).show()
                        }
                        return true
                    }
                })


                return view
            }

    private fun isInternetConnected(): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (requireContext().checkSelfPermission(Manifest.permission.ACCESS_NETWORK_STATE) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(
                    arrayOf(Manifest.permission.ACCESS_NETWORK_STATE),
                    NETWORK_STATE_PERMISSION_CODE
                )
                return false
            }
        }

        val networkInfo = connectivityManager.activeNetworkInfo
        return networkInfo != null && networkInfo.isConnected
    }


    private fun filterSongs(query: String) {
        val filteredList = if (query.isNotEmpty()) {
            searchSongList!!.filter {
                it.title.toLowerCase(Locale.getDefault()).contains(query.toLowerCase(Locale.getDefault())) ||
                        it.artist.toLowerCase(Locale.getDefault()).contains(query.toLowerCase(Locale.getDefault()))
            }
        } else {
            emptyList()

        }
        searchAdapter.filterSongs(filteredList)
    }
}