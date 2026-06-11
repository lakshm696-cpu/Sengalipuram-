package com.example.ui.chat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.Message
import com.example.data.MessageDao
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ChatViewModel(private val messageDao: MessageDao) : ViewModel() {
    val messages: StateFlow<List<Message>> = messageDao.getAllMessages()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun sendMessage(userId: Int, username: String, text: String) {
        if (text.isBlank()) return
        viewModelScope.launch {
            messageDao.insertMessage(Message(userId = userId, username = username, text = text))
        }
    }
}

class ChatViewModelFactory(private val messageDao: MessageDao) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ChatViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ChatViewModel(messageDao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
