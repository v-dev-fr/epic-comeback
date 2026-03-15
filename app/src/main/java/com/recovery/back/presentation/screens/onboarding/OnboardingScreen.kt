import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.*
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.recovery.back.data.local.AppDatabase
import com.recovery.back.data.local.entity.IbsSeverity
import com.recovery.back.data.local.entity.UserProfileEntity
import com.recovery.back.presentation.ui.theme.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.LocalDate
import android.Manifest
import android.content.pm.PackageManager
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OnboardingScreen(
    onComplete: () -> Unit
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { onComplete() }
    )

    var currentStep by remember { mutableIntStateOf(1) }
    var name by remember { mutableStateOf("") }
    var height by remember { mutableStateOf("") }
    var startWeight by remember { mutableStateOf("") }
    var goalWeight by remember { mutableStateOf("") }
    var ibsSeverity by remember { mutableStateOf(IbsSeverity.NONE) }
    var showError by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Obsidian)
            .padding(24.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(48.dp))
        
        // Step Indicator
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            StepDot(active = currentStep >= 1)
            StepDot(active = currentStep >= 2)
        }
        
        Spacer(modifier = Modifier.height(32.dp))

        AnimatedContent(
            targetState = currentStep,
            transitionSpec = {
                fadeIn() togetherWith fadeOut()
            },
            label = "stepEnter"
        ) { step ->
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                if (step == 1) {
                    StepOne(
                        name = name,
                        height = height,
                        onNameChange = { name = it },
                        onHeightChange = { height = it }
                    )
                } else {
                    StepTwo(
                        startWeight = startWeight,
                        goalWeight = goalWeight,
                        ibsSeverity = ibsSeverity,
                        onStartWeightChange = { startWeight = it },
                        onGoalWeightChange = { goalWeight = it },
                        onIbsChange = { ibsSeverity = it }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        if (showError != null) {
            Text(showError!!, color = ErrorRed, style = MaterialTheme.typography.bodySmall)
            Spacer(modifier = Modifier.height(16.dp))
        }

        Button(
            onClick = {
                if (currentStep == 1) {
                    if (name.isBlank() || height.toIntOrNull() == null) {
                        showError = "Please fill in all details"
                    } else {
                        showError = null
                        currentStep = 2
                    }
                } else {
                    val h = height.toIntOrNull() ?: 0
                    val sw = startWeight.toFloatOrNull() ?: 0f
                    val gw = goalWeight.toFloatOrNull() ?: 0f

                    if (h !in 100..250) {
                        showError = "Height check failed"
                    } else if (sw !in 30f..300f) {
                        showError = "Weight must be 30-300kg"
                    } else if (gw >= sw) {
                        showError = "Goal weight must be less than current"
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
                                    } else { onComplete() }
                                } else { onComplete() }
                            }
                        }
                    }
                }
            },
            modifier = Modifier.fillMaxWidth().height(60.dp),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(containerColor = ElectricBlue, contentColor = Obsidian)
        ) {
            Text(if (currentStep == 1) "Continue" else "Start Journey", fontWeight = FontWeight.Bold)
            if (currentStep == 1) {
                Spacer(modifier = Modifier.width(8.dp))
                Icon(Icons.Default.ArrowForward, contentDescription = null, modifier = Modifier.size(18.dp))
            }
        }
        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
fun StepDot(active: Boolean) {
    Box(
        modifier = Modifier
            .size(8.dp)
            .background(if (active) ElectricBlue else Color.White.copy(0.1f), CircleShape)
    )
}

@Composable
fun StepOne(name: String, height: String, onNameChange: (String) -> Unit, onHeightChange: (String) -> Unit) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text("Personal Bio", style = MaterialTheme.typography.displaySmall, fontWeight = FontWeight.Black, color = TextPrimary)
        Text("Let's customize your recovery path", color = TextSecondary, style = MaterialTheme.typography.bodyMedium)
        Spacer(modifier = Modifier.height(48.dp))
        
        ModernTextField(value = name, onValueChange = onNameChange, label = "Full Name")
        Spacer(modifier = Modifier.height(20.dp))
        ModernTextField(value = height, onValueChange = onHeightChange, label = "Height (cm)", keyboardType = KeyboardType.Number)
    }
}

@Composable
fun StepTwo(
    startWeight: String, 
    goalWeight: String, 
    ibsSeverity: IbsSeverity,
    onStartWeightChange: (String) -> Unit,
    onGoalWeightChange: (String) -> Unit,
    onIbsChange: (IbsSeverity) -> Unit
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text("Health Profile", style = MaterialTheme.typography.displaySmall, fontWeight = FontWeight.Black, color = TextPrimary)
        Text("Crucial for tracking spine hygiene", color = TextSecondary, style = MaterialTheme.typography.bodyMedium)
        Spacer(modifier = Modifier.height(48.dp))
        
        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            ModernTextField(value = startWeight, onValueChange = onStartWeightChange, label = "Weight (kg)", keyboardType = KeyboardType.Number, modifier = Modifier.weight(1f))
            ModernTextField(value = goalWeight, onValueChange = onGoalWeightChange, label = "Goal (kg)", keyboardType = KeyboardType.Number, modifier = Modifier.weight(1f))
        }
        
        Spacer(modifier = Modifier.height(32.dp))
        Text("IBS Severity Filter", color = TextPrimary, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(16.dp))
        
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            IbsSeverity.values().forEach { severity ->
                val selected = ibsSeverity == severity
                Surface(
                    onClick = { onIbsChange(severity) },
                    modifier = Modifier.weight(1f).height(48.dp).border(1.dp, if(selected) ElectricBlue else Color.White.copy(0.05f), RoundedCornerShape(12.dp)),
                    color = if(selected) ElectricBlue.copy(0.1f) else SurfaceDark,
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(severity.name, style = MaterialTheme.typography.labelSmall, color = if(selected) ElectricBlue else TextSecondary)
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ModernTextField(value: String, onValueChange: (String) -> Unit, label: String, keyboardType: KeyboardType = KeyboardType.Text, modifier: Modifier = Modifier) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label, color = TextSecondary) },
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = TextFieldDefaults.outlinedTextFieldColors(
            focusedBorderColor = ElectricBlue,
            unfocusedBorderColor = Color.White.copy(0.1f),
            textColor = TextPrimary,
            cursorColor = ElectricBlue
        )
    )
}
