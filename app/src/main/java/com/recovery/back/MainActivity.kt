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
import androidx.lifecycle.viewmodel.compose.viewModel
import com.recovery.back.presentation.MainViewModel
import androidx.compose.runtime.collectAsState
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
                    val viewModel: MainViewModel = viewModel()
                    val userProfile by viewModel.userProfile.collectAsState()
                    val waterCount by viewModel.waterCount.collectAsState()
                    
                    NavHost(
                        navController = navController, 
                        startDestination = if (userProfile != null) "dashboard" else "onboarding"
                    ) {
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
                            val profile = userProfile
                            com.recovery.back.presentation.screens.dashboard.DashboardScreen(
                                currentPhase = profile?.currentPhase ?: 1,
                                xp = profile?.xp ?: 0,
                                level = profile?.level ?: 1,
                                onLogPainClick = { /* Navigate to pain log */ },
                                onAddWaterClick = { viewModel.addWater() },
                                onLogWeightClick = { /* Open weight dialog */ },
                                onRestDayToggle = { /* Toggle in VM */ }
                            )
                        }
                    }
                }
            }
        }
    }
}
