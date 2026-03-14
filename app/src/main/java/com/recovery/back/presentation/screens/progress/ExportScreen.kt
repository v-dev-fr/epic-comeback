package com.recovery.back.presentation.screens.progress

import android.content.Context
import android.content.Intent
import android.graphics.pdf.PdfDocument
import android.os.Environment
import android.provider.MediaStore
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

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
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Export Health Report", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(32.dp))

        // Date Range Selection
        Text("Select Date Range", style = MaterialTheme.typography.titleMedium)
        Row(horizontalArrangement = Arrangement.SpaceEvenly, modifier = Modifier.fillMaxWidth()) {
            listOf("1 Week", "1 Month", "All Time").forEach { range ->
                FilterChip(
                    selected = selectedRange == range,
                    onClick = { selectedRange = range },
                    label = { Text(range) }
                )
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        Card(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Report Preview:", fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(8.dp))
                Text("• Average Pain per week\n• Exercise Completion %\n• IBS Flare Count\n• Weight Trend Snapshot")
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = {
                coroutineScope.launch {
                    val uri = generateAndSavePdf(context)
                    if (uri != null) {
                        sharePdf(context, uri)
                    }
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Generate & Share PDF")
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        TextButton(onClick = onBackClick) {
            Text("Cancel")
        }
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
