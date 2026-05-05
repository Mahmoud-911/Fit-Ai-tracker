package com.fitai.tracker

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.fitai.tracker.ui.navigation.AppNavigation
import com.fitai.tracker.ui.navigation.BottomNavBar
import com.fitai.tracker.ui.theme.FitAiTrackerTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            FitAiTrackerTheme {
                val navController = rememberNavController()
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    bottomBar = { BottomNavBar(navController) },
                    containerColor = com.fitai.tracker.ui.theme.DarkBackground
                ) { paddingValues ->
                    AppNavigation(
                        navController = navController,
                    )
                }
            }
        }
    }
}
