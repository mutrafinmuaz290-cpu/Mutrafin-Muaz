package com.example.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.models.Comment
import com.example.models.Video
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class MyTubeViewModel : ViewModel() {
    private val firestore = Firebase.firestore
    private val auth = Firebase.auth

    private val _videos = MutableStateFlow<List<Video>>(emptyList())
    val videos: StateFlow<List<Video>> = _videos.asStateFlow()
    
    private val _trendingVideos = MutableStateFlow<List<Video>>(emptyList())
    val trendingVideos: StateFlow<List<Video>> = _trendingVideos.asStateFlow()

    private val _comments = MutableStateFlow<Map<String, List<Comment>>>(emptyMap())
    val comments: StateFlow<Map<String, List<Comment>>> = _comments.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()
    
    private val _selectedCategory = MutableStateFlow("All")
    val selectedCategory: StateFlow<String> = _selectedCategory.asStateFlow()

    private val _subscribedChannels = MutableStateFlow<Set<String>>(emptySet())
    val subscribedChannels: StateFlow<Set<String>> = _subscribedChannels.asStateFlow()

    private val _downloadedVideos = MutableStateFlow<List<Video>>(emptyList())
    val downloadedVideos: StateFlow<List<Video>> = _downloadedVideos.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    init {
        loadMockDataFallback()
        fetchVideos()
    }

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
        filterVideos()
    }

    fun selectCategory(category: String) {
        _selectedCategory.value = category
        filterVideos()
    }
    
    private fun filterVideos() {
        var filtered = _videos.value
        val query = _searchQuery.value
        val category = _selectedCategory.value
        
        if (query.isNotBlank()) {
            filtered = filtered.filter { 
                it.title.contains(query, ignoreCase = true) || 
                it.channelName.contains(query, ignoreCase = true) ||
                it.tags.any { tag -> tag.contains(query, ignoreCase = true) }
            }
        }
        
        if (category != "All") {
            filtered = filtered.filter { it.category == category }
        }
        
        _trendingVideos.value = filtered.sortedByDescending { it.viewCount }
    }

    fun subscribeToChannel(channelName: String) {
        val current = _subscribedChannels.value.toMutableSet()
        if (current.contains(channelName)) {
            current.remove(channelName)
        } else {
            current.add(channelName)
        }
        _subscribedChannels.value = current
    }

    fun reportVideo(videoId: String, reason: String) {
        // Mock reporting to admin backend
        viewModelScope.launch {
            // firestore.collection("reports").add(Report(contentId = videoId, type = "video", reason = reason))
        }
    }
    
    fun addToPlaylist(videoId: String, playlistName: String) {
        // Mock adding to personal playlist
        viewModelScope.launch {
            // firestore.collection("playlists").document("my_playlist").update(...)
        }
    }
    
    fun downloadVideo(videoId: String) {
        val video = _videos.value.find { it.id == videoId }
        if (video != null) {
            val current = _downloadedVideos.value.toMutableList()
            if (current.any { it.id == videoId }) {
                current.removeAll { it.id == videoId }
            } else {
                current.add(video)
            }
            _downloadedVideos.value = current
        }
    }

    private fun loadMockDataFallback() {
        val mocks = listOf(
            Video(
                id = "v1",
                title = "Building a YouTube Clone in Jetpack Compose (100 Days of Code)",
                channelName = "Android Mastery",
                thumbnailUrl = "https://images.unsplash.com/photo-1611162617474-5b21e879e113?w=800&q=80",
                viewCount = 120500,
                likes = 15400,
                category = "Education",
                tags = listOf("Android", "Kotlin", "Compose"),
                is4K = true
            ),
            Video(
                id = "v2",
                title = "Cinematic Travel Video - Switzerland 4K Drone Footage",
                channelName = "Travel Vibes",
                thumbnailUrl = "https://images.unsplash.com/photo-1530122037265-a5f1f91d3b99?w=800&q=80",
                viewCount = 2050000,
                likes = 450000,
                category = "Travel",
                tags = listOf("Drone", "Switzerland", "4K"),
                is4K = true
            ),
            Video(
                id = "v3",
                title = "10 Tips for Better Sleep & Productivity",
                channelName = "Health & Wellness",
                thumbnailUrl = "https://images.unsplash.com/photo-1511295742362-92c96b5ade36?w=800&q=80",
                viewCount = 45000,
                likes = 3200,
                category = "Health",
                tags = listOf("Sleep", "Productivity"),
                is4K = false
            ),
            Video(
                id = "v4",
                title = "AI in 2026: What's Next for Gemini?",
                channelName = "Tech Insights",
                thumbnailUrl = "https://images.unsplash.com/photo-1620712943543-bcc4688e7485?w=800&q=80",
                viewCount = 950000,
                likes = 125000,
                category = "Technology",
                tags = listOf("AI", "Gemini", "Future"),
                is4K = true
            )
        )
        _videos.value = mocks
        _trendingVideos.value = mocks
    }

    fun fetchVideos() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val snapshot = firestore.collection("videos")
                    .orderBy("timestamp", com.google.firebase.firestore.Query.Direction.DESCENDING)
                    .get()
                    .await()
                
                val fetched = snapshot.toObjects(Video::class.java)
                if (fetched.isNotEmpty()) {
                    _videos.value = fetched
                    _trendingVideos.value = fetched.sortedByDescending { it.viewCount }
                }
            } catch (e: Exception) {
                // Fallback to mock data already loaded
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun fetchComments(videoId: String) {
        viewModelScope.launch {
            try {
                val snapshot = firestore.collection("videos").document(videoId)
                    .collection("comments")
                    .orderBy("timestamp", com.google.firebase.firestore.Query.Direction.DESCENDING)
                    .get()
                    .await()
                
                val fetched = snapshot.toObjects(Comment::class.java)
                val current = _comments.value.toMutableMap()
                current[videoId] = fetched
                _comments.value = current
            } catch (e: Exception) {
                // Ignore or show error
            }
        }
    }

    fun addComment(videoId: String, text: String) {
        val user = auth.currentUser
        val comment = Comment(
            videoId = videoId,
            userId = user?.uid ?: "anonymous",
            userName = user?.displayName ?: "Anonymous",
            text = text
        )
        viewModelScope.launch {
            try {
                firestore.collection("videos").document(videoId)
                    .collection("comments")
                    .document(comment.id)
                    .set(comment)
                    .await()
                fetchComments(videoId)
            } catch (e: Exception) {
                // Add to local state anyway for immediate feedback if offline
                val current = _comments.value.toMutableMap()
                val list = current[videoId]?.toMutableList() ?: mutableListOf()
                list.add(0, comment)
                current[videoId] = list
                _comments.value = current
            }
        }
    }

    fun likeVideo(videoId: String) {
        viewModelScope.launch {
            try {
                firestore.collection("videos").document(videoId)
                    .update("likes", com.google.firebase.firestore.FieldValue.increment(1))
                    .await()
                fetchVideos()
            } catch (e: Exception) {
                // Local optimistic update
                _videos.value = _videos.value.map {
                    if (it.id == videoId) it.copy(likes = it.likes + 1) else it
                }
            }
        }
    }
}
