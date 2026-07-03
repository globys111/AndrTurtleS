package com.rebloom.app

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import com.rebloom.app.ui.navigation.AppRoot
import com.rebloom.app.ui.theme.Plant_App_AndrTurtleSTheme
import androidx.activity.viewModels
import com.rebloom.app.ui.screens.auth.AuthViewModel

class MainActivity : ComponentActivity() {
    private val authViewModel: AuthViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        intent?.data?.let { uri -> authViewModel.handleDeepLink(uri) }

        setContent {
            Plant_App_AndrTurtleSTheme {
                AppRoot(authViewModel = authViewModel)
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        // Обработать deep link, если приложение уже было открыто (singleTask)
        intent.data?.let { uri -> authViewModel.handleDeepLink(uri) }
    }
}