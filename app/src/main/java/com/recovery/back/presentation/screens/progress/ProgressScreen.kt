import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.recovery.back.presentation.ui.theme.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue

@Composable
fun ProgressScreen(
    hasData: Boolean = false,
    onExportClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Obsidian)
            .padding(horizontal = 20.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Spacer(modifier = Modifier.height(24.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Recovery Trends",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Black,
                    color = TextPrimary
                )
                Text(
                    text = "Historical health data",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary
                )
            }
            IconButton(
                onClick = onExportClick,
                modifier = Modifier
                    .background(Color.White.copy(0.05f), RoundedCornerShape(12.dp))
                    .border(1.dp, Color.White.copy(0.1f), RoundedCornerShape(12.dp))
            ) {
                Icon(androidx.compose.material.icons.Icons.Default.Share, contentDescription = "Export")
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        if (!hasData) {
            Surface(
                color = SurfaceDark,
                shape = RoundedCornerShape(24.dp),
                modifier = Modifier.fillMaxWidth().height(200.dp)
            ) {
                Box(contentAlignment = Alignment.Center, modifier = Modifier.padding(24.dp)) {
                    Text(
                        text = "Complete your first day to unlock deep insights.",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                }
            }
        } else {
            TrendCard("Pain vs IBS Severity", "[ Dual-Axis Chart ]")
            Spacer(modifier = Modifier.height(20.dp))
            TrendCard("Weight Trend", "[ Weight Progression Chart ]")
        }
        
        Spacer(modifier = Modifier.height(40.dp))
    }
}

@Composable
fun TrendCard(title: String, placeholder: String) {
    Surface(
        color = SurfaceDark,
        shape = RoundedCornerShape(24.dp),
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, Color.White.copy(alpha = 0.05f), RoundedCornerShape(24.dp))
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
            Spacer(modifier = Modifier.height(24.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .background(Color.White.copy(0.02f), RoundedCornerShape(16.dp))
                    .border(1.dp, Color.White.copy(0.05f), RoundedCornerShape(16.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text(text = placeholder, color = ElectricBlue, style = MaterialTheme.typography.labelSmall)
            }
        }
    }
}
