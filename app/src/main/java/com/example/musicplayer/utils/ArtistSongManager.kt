package com.example.musicplayer.utils

import com.example.musicplayer.adapter.Song

object ArtistSongManager {
    val artistSongsMap: HashMap<String, ArrayList<Song>> = HashMap()

    fun addArtistSong(artistName: String, song: Song) {
        if (artistSongsMap.containsKey(artistName)) {
            artistSongsMap[artistName]?.add(song)
        } else {
            val artistSongs: ArrayList<Song> = ArrayList()
            artistSongs.add(song)
            artistSongsMap[artistName] = artistSongs
        }
    }

    fun getArtistSongs(artistName: String): ArrayList<Song>? {
        return artistSongsMap[artistName]
    }
}