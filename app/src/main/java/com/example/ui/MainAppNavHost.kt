package com.example.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.ui.auth.AuthViewModel
import com.example.ui.auth.LoginScreen
import com.example.ui.auth.RegisterScreen
import com.example.ui.chat.ChatViewModel

@Composable
fun MainAppNavHost(
    authViewModel: AuthViewModel,
    reportViewModel: ReportViewModel,
    chatViewModel: ChatViewModel
) {
    val navController = rememberNavController()
    val currentUser by authViewModel.currentUser.collectAsStateWithLifecycle()

    val startDestination = if (currentUser == null) "login" else "main"

    NavHost(navController = navController, startDestination = startDestination) {
        composable("login") {
            LoginScreen(
                viewModel = authViewModel,
                onLoginSuccess = {
                    navController.navigate("main") {
                        popUpTo("login") { inclusive = true }
                    }
                },
                onNavigateToRegister = {
                    navController.navigate("register")
                }
            )
        }
        composable("register") {
            RegisterScreen(
                viewModel = authViewModel,
                onRegisterSuccess = {
                    navController.navigate("main") {
                        popUpTo("login") { inclusive = true }
                    }
                },
                onNavigateBack = {
                    navController.navigateUp()
                }
            )
        }
        composable("main") {
            VillageApp(
                viewModel = reportViewModel,
                chatViewModel = chatViewModel,
                user = currentUser,
                onLogout = {
                    authViewModel.logout()
                    navController.navigate("login") {
                        popUpTo("main") { inclusive = true }
                    }
                }
            )
        }
    }
}
