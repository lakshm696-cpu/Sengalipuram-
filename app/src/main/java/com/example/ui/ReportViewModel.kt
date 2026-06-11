package com.example.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.Report
import com.example.data.ReportRepository
import com.example.data.UserRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ReportViewModel(
    private val reportRepository: ReportRepository,
    private val userRepository: UserRepository
) : ViewModel() {
    val uiState: StateFlow<List<Report>> = reportRepository.allReports
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    private val _currentUserId = MutableStateFlow<Int?>(null)

    @OptIn(ExperimentalCoroutinesApi::class)
    val followedUserIds: StateFlow<Set<Int>> = _currentUserId.flatMapLatest { userId ->
        if (userId == null) flowOf(emptySet())
        else {
            userRepository.getFollowedIds(userId).map { it.toSet() }
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptySet()
    )

    fun setCurrentUser(userId: Int?) {
        _currentUserId.value = userId
    }

    fun toggleFollow(authorId: Int) {
        val currentUserId = _currentUserId.value ?: return
        if (currentUserId == authorId) return // Cannot follow self
        
        viewModelScope.launch {
            if (followedUserIds.value.contains(authorId)) {
                userRepository.unfollowUser(currentUserId, authorId)
            } else {
                userRepository.followUser(currentUserId, authorId)
            }
        }
    }

    fun addReport(userId: Int, username: String, title: String, description: String, location: String, mediaUri: String?, isVideo: Boolean, latitude: Double? = null, longitude: Double? = null) {
        viewModelScope.launch {
            reportRepository.insert(
                Report(userId = userId, username = username, title = title, description = description, location = location, mediaUri = mediaUri, isVideo = isVideo, latitude = latitude, longitude = longitude)
            )
        }
    }

    fun deleteReport(id: Int) {
        viewModelScope.launch {
            reportRepository.deleteById(id)
        }
    }
}

class ReportViewModelFactory(
    private val reportRepository: ReportRepository,
    private val userRepository: UserRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ReportViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ReportViewModel(reportRepository, userRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
