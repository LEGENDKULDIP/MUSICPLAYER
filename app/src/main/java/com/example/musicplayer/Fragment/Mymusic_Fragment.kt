package com.example.musicplayer.Fragment

import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.*
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.musicplayer.MusicList
import com.example.musicplayer.Music_player
import com.example.musicplayer.adapter.MyMusicAdapter
import com.example.musicplayer.databinding.FragmentMymusicBinding
import com.example.musicplayer.exitApplication
import java.io.File


@Suppress("DEPRECATION")
class Mymusic_Fragment : Fragment() {
    lateinit var binding:FragmentMymusicBinding
    lateinit var myMusicAdapter: MyMusicAdapter

    companion object{
        var Musiclistt:ArrayList<MusicList> = ArrayList()
        var sortOrder: Int = 0
        val sortingList = arrayOf(MediaStore.Audio.Media.DATE_ADDED + " DESC", MediaStore.Audio.Media.TITLE,
            MediaStore.Audio.Media.SIZE + " DESC")
        lateinit var musicListSearch: ArrayList<MusicList>
        var search:Boolean=false
    }
    @RequiresApi(Build.VERSION_CODES.R)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding= FragmentMymusicBinding.inflate(layoutInflater,container,false)
        (requireActivity() as AppCompatActivity).setSupportActionBar(binding.toolbar)


        // Enable options menu in the fragment
        setHasOptionsMenu(true)
        if(requestPermission())
            initRecyclerView()
        setHasOptionsMenu(true)

        binding.swipeRefreshLayout.setOnRefreshListener {
            refreshRecyclerViewData()
        }
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.R)
    private fun refreshRecyclerViewData() {
        search=false
        Musiclistt=getAllAudio()
        setAdapter()
        StopRefreshing()
    }

    @RequiresApi(Build.VERSION_CODES.R)
    @SuppressLint("SetTextI18n")
    private fun setAdapter() {
        myMusicAdapter = context?.let { MyMusicAdapter(it.applicationContext, Musiclistt) }!!

        binding.myMusicRecyclerView.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(requireContext())
            adapter = myMusicAdapter
            binding.tvtotalsongs.text = "Total songs: ${myMusicAdapter.itemCount}"
            StopRefreshing()
        }
    }

    @RequiresApi(Build.VERSION_CODES.R)
    @SuppressLint("SetTextI18n")
    private fun initRecyclerView() {
        search=false
        Musiclistt =getAllAudio()
        setAdapter()
        StopRefreshing()
    }

    private fun StopRefreshing() {
        binding.swipeRefreshLayout.isRefreshing=false
    }

    @SuppressLint("Range")
    @RequiresApi(Build.VERSION_CODES.R)
    private fun getAllAudio():ArrayList<MusicList>{
        StopRefreshing()
        val tempList=ArrayList<MusicList>()
        val selection=MediaStore.Audio.Media.IS_MUSIC + " !=0"
        val projection = arrayOf(MediaStore.Audio.Media._ID,MediaStore.Audio.Media.TITLE,MediaStore.Audio.Media.ALBUM,
            MediaStore.Audio.Media.ARTIST,MediaStore.Audio.Media.DURATION,MediaStore.Audio.Media.DATE_ADDED,
            MediaStore.Audio.Media.DATA, MediaStore.Audio.Media.ALBUM_ID)

        val cursor = this.requireContext().contentResolver.query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, projection,selection,null,
            sortingList[sortOrder], null)
        if(cursor != null){
            if(cursor.moveToFirst()){
                do {
                    val titleC = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE))?:"Unknown"
                    val idC = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media._ID))?:"Unknown"
                    val albumC = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM))?:"Unknown"
                    val artistC = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST))?:"Unknown"
                    val pathC = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA))
                    val durationC = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.DURATION))
                    val albumIdC = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID)).toString()
                    val uri = Uri.parse("content://media/external/audio/albumart")
                    val artUriC = Uri.withAppendedPath(uri, albumIdC).toString()
                    val music = MusicList(id = idC, songname = titleC, Artistname = artistC, path = pathC, duration = durationC, songImage = artUriC
                        )
                    val file = File(music.path)
                    if(file.exists())
                        tempList.add(music)
                }while (cursor.moveToNext())
            }
            cursor.close()
        }
        return tempList
    }
    private fun requestPermission() :Boolean {
        if(ActivityCompat.checkSelfPermission(requireActivity(),android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(requireActivity(), arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE),11)
            return false
        }
        return true
    }

    @Deprecated("Deprecated in Java")
    @RequiresApi(Build.VERSION_CODES.R)
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 11) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(context, "Permission Granted", Toast.LENGTH_SHORT).show()
                initRecyclerView()
                StopRefreshing()

            }
            else
                ActivityCompat.requestPermissions(
                    requireActivity(),
                    arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE),
                    11
                )

        }
    }
    override fun onDestroy() {
        super.onDestroy()
        if(!Music_player.isPlaying && Music_player.musicService !=null){
            exitApplication()
        }
    }


}