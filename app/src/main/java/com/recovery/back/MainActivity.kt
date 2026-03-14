package com.recovery.back

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.recovery.back.presentation.screens.onboarding.OnboardingScreen
import com.recovery.back.presentation.ui.theme.BackRecoveryTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            BackRecoveryTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()
                    
                    NavHost(navController = navController, startDestination = "onboarding") {
                        composable("onboarding") {
                            OnboardingScreen(
                                onComplete = { 
                                    navController.navigate("dashboard") {
                                        popUpTo("onboarding") { inclusive = true }
                                    }
                                }
                            )
                        }
                        composable("dashboard") {
                            com.recovery.back.presentation.screens.dashboard.DashboardScreen(
                                onLogPainClick = {},
                                onRestDayToggle = {}
                            )
                        }
                    }
                }
            }
        }
    }
}
