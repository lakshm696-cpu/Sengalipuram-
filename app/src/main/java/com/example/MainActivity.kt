package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.room.Room
import com.example.data.AppDatabase
import com.example.data.ReportRepository
import com.example.data.UserRepository
import com.example.ui.MainAppNavHost
import com.example.ui.ReportViewModel
import com.example.ui.ReportViewModelFactory
import com.example.ui.auth.AuthViewModel
import com.example.ui.auth.AuthViewModelFactory
import com.example.ui.chat.ChatViewModel
import com.example.ui.chat.ChatViewModelFactory
import com.example.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {
    private val database by lazy {
        Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java,
            "village_reports.db"
        )
        .fallbackToDestructiveMigration() // Needed because I bumped the DB version
        .build()
    }

    private val reportRepository by lazy {
        ReportRepository(database.reportDao())
    }

    private val userRepository by lazy {
        UserRepository(database.userDao())
    }

    private val reportViewModel: ReportViewModel by viewModels {
        ReportViewModelFactory(reportRepository, userRepository)
    }

    private val authViewModel: AuthViewModel by viewModels {
        AuthViewModelFactory(userRepository)
    }

    private val chatViewModel: ChatViewModel by viewModels {
        ChatViewModelFactory(database.messageDao())
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                MainAppNavHost(
                    authViewModel = authViewModel, 
                    reportViewModel = reportViewModel,
                    chatViewModel = chatViewModel
                )
            }
        }
    }
}
