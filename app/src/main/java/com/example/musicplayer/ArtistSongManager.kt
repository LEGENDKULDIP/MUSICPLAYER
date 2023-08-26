package com.example.musicplayer

class ArtistSongManager {
    private val artistSongs: ArrayList<Pair<String, String>> = ArrayList()

    fun addArtistSong(artist: String, song: String) {
        artistSongs.add(Pair(artist, song))
    }

    fun getArtistSongs(): List<Pair<String, String>> = artistSongs


}