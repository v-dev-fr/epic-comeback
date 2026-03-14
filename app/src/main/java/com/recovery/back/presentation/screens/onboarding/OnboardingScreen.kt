package com.recovery.back.presentation.screens.onboarding

import android.os.Build
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.recovery.back.data.local.entity.IbsSeverity
import java.time.LocalDate
import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import com.recovery.back.data.local.AppDatabase
import com.recovery.back.data.local.entity.UserProfileEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OnboardingScreen(
    onComplete: () -> Unit
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = {
            onComplete()
        }
    )

    var name by remember { mutableStateOf("") }
    var height by remember { mutableStateOf("") }
    var startWeight by remember { mutableStateOf("") }
    var goalWeight by remember { mutableStateOf("") }
    var ibsSeverity by remember { mutableStateOf(IbsSeverity.NONE) }
    
    // Validation flags
    var showError by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("Welcome to Back Recovery", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(32.dp))

        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Name") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = height,
            onValueChange = { height = it },
            label = { Text("Height (cm)") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            OutlinedTextField(
                value = startWeight,
                onValueChange = { startWeight = it },
                label = { Text("Current Weight (kg)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.weight(1f)
            )
            OutlinedTextField(
                value = goalWeight,
                onValueChange = { goalWeight = it },
                label = { Text("Goal (kg)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.weight(1f)
            )
        }
        Spacer(modifier = Modifier.height(16.dp))
        
        Text("IBS Severity", style = MaterialTheme.typography.titleMedium)
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
            IbsSeverity.values().forEach { severity ->
                FilterChip(
                    selected = ibsSeverity == severity,
                    onClick = { ibsSeverity = severity },
                    label = { Text(severity.name) }
                )
            }
        }
        
        Spacer(modifier = Modifier.height(32.dp))

        if (showError != null) {
            Text(showError!!, color = MaterialTheme.colorScheme.error)
            Spacer(modifier = Modifier.height(16.dp))
        }

        Button(
            onClick = {
                val h = height.toIntOrNull() ?: 0
                val sw = startWeight.toFloatOrNull() ?: 0f
                val gw = goalWeight.toFloatOrNull() ?: 0f

                if (name.isBlank()) {
                    showError = "Name cannot be empty"
                } else if (h !in 100..250) {
                    showError = "Height must be between 100 and 250 cm"
                } else if (sw !in 30f..300f) {
                    showError = "Weight must be between 30 and 300 kg"
                } else if (gw >= sw) {
                    showError = "Goal weight must be less than current weight"
                } else {
                    showError = null
                    
                    coroutineScope.launch(Dispatchers.IO) {
                        val db = AppDatabase.getDatabase(context)
                        db.appDao().insertUserProfile(
                            UserProfileEntity(
                                name = name,
                                heightCm = h,
                                startWeightKg = sw,
                                goalWeightKg = gw,
                                injuryDateEpochDay = LocalDate.now().toEpochDay(),
                                ibsSeverity = ibsSeverity
                            )
                        )
                        
                        launch(Dispatchers.Main) {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                                if (ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                                    permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                                } else {
                                    onComplete()
                                }
                            } else {
                                onComplete()
                            }
                        }
                    }
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Start Journey")
        }
    }
}
