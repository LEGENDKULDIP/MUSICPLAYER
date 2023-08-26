package com.example.musicplayer.models

data class Track(
    val id: String,
    val name: String,
    val artists: List<Artist>,
    val album: Album
)

data class Artist(
    val id: String,
    val name: String
)

data class Album(
    val id: String,
    val name: String,
    val images: List<Image>
)

data class Song(
    val title: String,
    val artist: String,
    val album: String,
    val imageUrl: String
)

data class Image(
    val url: String,
    val width: Int,
    val height: Int
)

data class AccessTokenResponse(
    val access_token: String,
    val token_type: String,
    val expires_in: Int,
    val refresh_token: String? = null
)