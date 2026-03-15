import android.content.Context
import android.content.Intent
import android.graphics.pdf.PdfDocument
import android.os.Environment
import android.provider.MediaStore
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.recovery.back.presentation.ui.theme.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExportScreen(
    onBackClick: () -> Unit
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    var selectedRange by remember { mutableStateOf("1 Week") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Obsidian)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(24.dp))
        Text(
            text = "Health Export",
            style = MaterialTheme.typography.displaySmall,
            fontWeight = FontWeight.Black,
            color = TextPrimary
        )
        Text(
            text = "Generate a PDF for your therapist",
            style = MaterialTheme.typography.bodyMedium,
            color = TextSecondary
        )
        
        Spacer(modifier = Modifier.height(48.dp))

        // Date Range Selection
        Text(
            "Select Duration",
            style = MaterialTheme.typography.titleSmall,
            color = TextPrimary,
            modifier = Modifier.align(Alignment.Start)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
            listOf("1 Week", "1 Month", "All Time").forEach { range ->
                val selected = selectedRange == range
                Surface(
                    onClick = { selectedRange = range },
                    modifier = Modifier.weight(1f).height(48.dp).border(1.dp, if(selected) ElectricBlue else Color.White.copy(0.05f), RoundedCornerShape(12.dp)),
                    color = if(selected) ElectricBlue.copy(0.1f) else SurfaceDark,
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(range, style = MaterialTheme.typography.labelSmall, color = if(selected) ElectricBlue else TextSecondary)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        Surface(
            color = SurfaceDark,
            shape = RoundedCornerShape(24.dp),
            modifier = Modifier.fillMaxWidth().border(1.dp, Color.White.copy(alpha = 0.05f), RoundedCornerShape(24.dp))
        ) {
            Column(modifier = Modifier.padding(24.dp)) {
                Text("Report Includes:", fontWeight = FontWeight.Bold, color = TextPrimary)
                Spacer(modifier = Modifier.height(16.dp))
                val items = listOf("Average Pain Score", "Routine Completion %", "IBS Flare Analysis", "Weight Progression")
                items.forEach { item ->
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(modifier = Modifier.size(6.dp).background(ElectricBlue, androidx.compose.foundation.shape.CircleShape))
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(item, style = MaterialTheme.typography.bodySmall, color = TextSecondary)
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        Button(
            onClick = {
                coroutineScope.launch {
                    val uri = generateAndSavePdf(context)
                    if (uri != null) {
                        sharePdf(context, uri)
                    }
                }
            },
            modifier = Modifier.fillMaxWidth().height(60.dp),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(containerColor = ElectricBlue, contentColor = Obsidian)
        ) {
            Text("Download Health Summary", fontWeight = FontWeight.Bold)
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        TextButton(onClick = onBackClick) {
            Text("Cancel", color = TextSecondary)
        }
        Spacer(modifier = Modifier.height(24.dp))
    }
}

// Implementation for MediaStore PDF generation
private suspend fun generateAndSavePdf(context: Context): android.net.Uri? = withContext(Dispatchers.IO) {
    try {
        val pdfDocument = PdfDocument()
        val pageInfo = PdfDocument.PageInfo.Builder(595, 842, 1).create() // A4 size
        val page = pdfDocument.startPage(pageInfo)
        
        val canvas = page.canvas
        val paint = android.graphics.Paint()
        paint.textSize = 18f
        paint.isFakeBoldText = true
        canvas.drawText("Back Recovery Health Report", 50f, 50f, paint)
        
        paint.textSize = 12f
        paint.isFakeBoldText = false
        canvas.drawText("Generated on: ${java.time.LocalDate.now()}", 50f, 80f, paint)
        canvas.drawText("App Identity: com.recovery.back", 50f, 100f, paint)
        
        canvas.drawText("• Average Pain Score: 3.2 (Mock)", 50f, 140f, paint)
        canvas.drawText("• Routine Completion: 85%", 50f, 165f, paint)
        canvas.drawText("• IBS Severity: Moderate Correlation", 50f, 190f, paint)
        
        pdfDocument.finishPage(page)

        val fileName = "BackRecovery_Report_${System.currentTimeMillis()}.pdf"
        val contentValues = android.content.ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, fileName)
            put(MediaStore.MediaColumns.MIME_TYPE, "application/pdf")
            put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS)
        }

        val resolver = context.contentResolver
        val uri = resolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, contentValues)
        
        uri?.let {
            resolver.openOutputStream(it).use { outputStream ->
                pdfDocument.writeTo(outputStream)
            }
        }
        
        pdfDocument.close()
        return@withContext uri
    } catch (e: Exception) {
        e.printStackTrace()
        return@withContext null
    }
}

private fun sharePdf(context: Context, uri: android.net.Uri) {
    val shareIntent = Intent(Intent.ACTION_SEND).apply {
        type = "application/pdf"
        putExtra(Intent.EXTRA_STREAM, uri)
        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
    }
    context.startActivity(Intent.createChooser(shareIntent, "Share Report to Doctor"))
}
