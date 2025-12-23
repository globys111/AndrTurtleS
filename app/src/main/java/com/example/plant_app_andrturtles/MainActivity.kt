package com.example.plant_app_andrturtles

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import com.example.plant_app_andrturtles.ui.app.AppRoot
import com.example.plant_app_andrturtles.ui.theme.Plant_App_AndrTurtleSTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            Plant_App_AndrTurtleSTheme {
                Surface(color = MaterialTheme.colorScheme.background) {
                    AppRoot()
                }
            }
        }
    }
}
