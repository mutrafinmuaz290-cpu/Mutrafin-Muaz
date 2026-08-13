package com.example.models

import java.util.UUID

data class Video(
    val id: String = UUID.randomUUID().toString(),
    val title: String = "",
    val description: String = "",
    val channelName: String = "",
    val channelAvatarUrl: String = "",
    val thumbnailUrl: String = "",
    val videoUrl: String = "", // Could be an actual video URL, we'll just mock it
    val viewCount: Long = 0L,
    val likes: Long = 0L,
    val category: String = "All",
    val tags: List<String> = emptyList(),
    val is4K: Boolean = false,
    val timestamp: Long = System.currentTimeMillis()
)

data class Comment(
    val id: String = UUID.randomUUID().toString(),
    val videoId: String = "",
    val userId: String = "",
    val userName: String = "",
    val text: String = "",
    val timestamp: Long = System.currentTimeMillis()
)

data class Playlist(
    val id: String = UUID.randomUUID().toString(),
    val userId: String = "",
    val name: String = "",
    val videoIds: List<String> = emptyList()
)

data class Report(
    val id: String = UUID.randomUUID().toString(),
    val contentId: String = "",
    val type: String = "video", // video, comment, user
    val reason: String = "",
    val timestamp: Long = System.currentTimeMillis(),
    val status: String = "pending"
)

