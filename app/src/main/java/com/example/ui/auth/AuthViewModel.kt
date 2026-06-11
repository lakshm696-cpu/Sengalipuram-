package com.example.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.User
import com.example.data.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed interface AuthState {
    data object Idle : AuthState
    data object Loading : AuthState
    data class Success(val user: User) : AuthState
    data class Error(val message: String) : AuthState
}

class AuthViewModel(private val repository: UserRepository) : ViewModel() {
    private val _authState = MutableStateFlow<AuthState>(AuthState.Idle)
    val authState: StateFlow<AuthState> = _authState.asStateFlow()

    private val _currentUser = MutableStateFlow<User?>(null)
    val currentUser: StateFlow<User?> = _currentUser.asStateFlow()

    fun login(username: String, passwordHash: String) {
        if (username.isBlank() || passwordHash.isBlank()) {
            _authState.value = AuthState.Error("Fields cannot be empty")
            return
        }
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            val user = repository.login(username, passwordHash)
            if (user != null) {
                _currentUser.value = user
                _authState.value = AuthState.Success(user)
            } else {
                _authState.value = AuthState.Error("Invalid username or password")
            }
        }
    }

    fun register(username: String, passwordHash: String) {
        if (username.isBlank() || passwordHash.isBlank()) {
            _authState.value = AuthState.Error("Fields cannot be empty")
            return
        }
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            val user = repository.register(username, passwordHash)
            if (user != null) {
                _currentUser.value = user
                _authState.value = AuthState.Success(user)
            } else {
                _authState.value = AuthState.Error("Username already exists")
            }
        }
    }

    fun logout() {
        _currentUser.value = null
        _authState.value = AuthState.Idle
    }

    fun resetState() {
        _authState.value = AuthState.Idle
    }
}

class AuthViewModelFactory(private val repository: UserRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AuthViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return AuthViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
