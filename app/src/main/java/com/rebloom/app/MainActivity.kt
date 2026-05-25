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

class MainActivity : ComponentActivity() {

    private var deepLinkUri by mutableStateOf<Uri?>(null)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        deepLinkUri = intent?.data
        enableEdgeToEdge()
        setContent {
            Plant_App_AndrTurtleSTheme {
                Surface(color = MaterialTheme.colorScheme.background) {
                    AppRoot(deepLinkUri = deepLinkUri)
                }
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        deepLinkUri = intent.data
    }
}
