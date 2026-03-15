package com.recovery.back

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.*
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.*
import com.recovery.back.presentation.screens.nutrition.DietScreen
import com.recovery.back.presentation.screens.nutrition.FoodListScreen
import com.recovery.back.presentation.MainViewModel
import com.recovery.back.presentation.screens.alarms.AlarmsScreen
import com.recovery.back.presentation.screens.exercises.ExercisesScreen
import com.recovery.back.presentation.screens.onboarding.OnboardingScreen
import com.recovery.back.presentation.screens.progress.ExportScreen
import com.recovery.back.presentation.screens.progress.ProgressScreen
import com.recovery.back.presentation.ui.theme.BackRecoveryTheme
import com.recovery.back.data.local.entity.UserProfileEntity
import com.recovery.back.data.local.entity.MealLogEntity
import com.recovery.back.data.local.entity.AlarmConfigEntity
import com.recovery.back.data.local.entity.DailyLogEntity
import com.recovery.back.data.local.entity.WaterLogEntity
import com.recovery.back.presentation.screens.dashboard.DashboardScreen

sealed class Screen(val route: String, val label: String, val icon: ImageVector) {
    object Dashboard : Screen("dashboard", "Home", Icons.Default.Home)
    object Exercises : Screen("exercises", "Routine", Icons.Default.PlayArrow)
    object Diet : Screen("diet", "Diet", Icons.Default.Restaurant)
    object Trends : Screen("trends", "Trends", Icons.Filled.Timeline)
    object Alarms : Screen("alarms", "Alarms", Icons.Default.Notifications)
}

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
                    val navBackStackEntry by navController.currentBackStackEntryAsState()
                    val currentDestination = navBackStackEntry?.destination
                    
                    val items = listOf(
                        Screen.Dashboard,
                        Screen.Exercises,
                        Screen.Diet,
                        Screen.Trends,
                        Screen.Alarms
                    )

                    val showBottomBar = currentDestination?.route != "onboarding" && 
                                      currentDestination?.route != "food_list"

                    Scaffold(
                        bottomBar = {
                            if (showBottomBar) {
                                NavigationBar(
                                    containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.95f),
                                    tonalElevation = 0.dp
                                ) {
                                    items.forEach { screen ->
                                        NavigationBarItem(
                                            icon = { Icon(screen.icon, contentDescription = null) },
                                            label = { Text(screen.label) },
                                            selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true,
                                            onClick = {
                                                navController.navigate(screen.route) {
                                                    popUpTo(navController.graph.findStartDestination().id) {
                                                        saveState = true
                                                    }
                                                    launchSingleTop = true
                                                    restoreState = true
                                                }
                                            }
                                        )
                                    }
                                }
                            }
                        }
                    ) { innerPadding ->
                        NavHost(
                            navController = navController, 
                            startDestination = if (userProfile != null) "dashboard" else "onboarding",
                            modifier = Modifier.padding(innerPadding)
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
                            composable(Screen.Dashboard.route) {
                                val profile = userProfile
                                val waterLog by viewModel.waterLog.collectAsState()
                                val dailyLog by viewModel.dailyLog.collectAsState()
                                DashboardScreen(
                                    currentPhase = profile?.currentPhase ?: 1,
                                    xp = profile?.xp ?: 0,
                                    level = profile?.level ?: 1,
                                    completionPercentage = dailyLog?.completionPercentage ?: 0f,
                                    isRestDay = dailyLog?.restDay ?: false,
                                    waterCount = waterLog?.glassCount ?: 0,
                                    onLogPainClick = { back, sciatic -> viewModel.logPain(back, sciatic) },
                                    onAddWaterClick = { viewModel.addWater() },
                                    onLogWeightClick = { weight -> viewModel.logWeight(weight) },
                                    onRestDayToggle = { viewModel.toggleRestDay(it) }
                                )
                            }
                            composable(Screen.Exercises.route) {
                                val profile = userProfile
                                ExercisesScreen(
                                    currentPhase = profile?.currentPhase ?: 1,
                                    onExerciseComplete = { id: String, reps: Int, phase: Int ->
                                        viewModel.logExerciseCompletion(id, reps, phase)
                                    }
                                )
                            }
                            composable(Screen.Diet.route) {
                                val meals by viewModel.todayMeals.collectAsState()
                                DietScreen(
                                    meals = meals,
                                    onFoodGuideClick = { navController.navigate("food_list") },
                                    onAddMealClick = { name, cals -> viewModel.logMeal(name, cals) }
                                )
                            }
                            composable("food_list") {
                                FoodListScreen(onBackClick = { navController.popBackStack() })
                            }
                            composable(Screen.Trends.route) {
                                ProgressScreen(onExportClick = { navController.navigate("export") })
                            }
                            composable(Screen.Alarms.route) {
                                val alarms by viewModel.alarms.collectAsState()
                                AlarmsScreen(
                                    alarms = alarms,
                                    onToggleAlarm = { alarm: AlarmConfigEntity, checked: Boolean -> viewModel.updateAlarm(alarm, checked) }
                                )
                            }
                            composable("export") {
                                ExportScreen(onBackClick = { navController.popBackStack() })
                            }
                        }
                    }
                }
            }
        }
    }
}
