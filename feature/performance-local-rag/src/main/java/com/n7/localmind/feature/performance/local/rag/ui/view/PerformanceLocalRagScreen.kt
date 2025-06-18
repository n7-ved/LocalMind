package com.n7.localmind.feature.performance.local.rag.ui.view

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.performance.local.rag.R
import com.n7.localmind.component.local.rag.domain.model.LocalRagDocument
import com.n7.localmind.component.local.rag.domain.model.LocalRagDocumentPerformance
import com.n7.localmind.core.common.file.CsvExporter
import com.n7.localmind.design.system.ConsumableEvent
import com.n7.localmind.design.system.composables.TopBar
import com.n7.localmind.feature.performance.local.rag.ui.state.DisplayState
import com.n7.localmind.feature.performance.local.rag.ui.state.PerformanceLocalRagScreenEvent
import com.n7.localmind.feature.performance.local.rag.ui.state.PerformanceLocalRagScreenState
import com.n7.localmind.feature.performance.local.rag.ui.viewmodel.PerformanceLocalRagViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

@Composable
internal fun PerformanceLocalRagScreen(
    performanceLocalRagViewModel: PerformanceLocalRagViewModel
){

    val screenState by performanceLocalRagViewModel.screenState.collectAsState()

    Scaffold(
        topBar = {
            TopBar(stringResource(R.string.performance_screen_title))
        },
        bottomBar = {
            Box(Modifier.size(0.dp))
        },
        content = { padding ->
            Box(
                modifier = Modifier
                    .background(MaterialTheme.colorScheme.background)
                    .fillMaxSize()
                    .padding(padding),
            ) {
                Body(performanceLocalRagViewModel, screenState)
            }
        }
    )
}

@Composable
private fun Body(
    performanceLocalRagViewModel: PerformanceLocalRagViewModel,
    screenState: PerformanceLocalRagScreenState,
) {

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFFFAF6FF),
                        Color(0xFFE1D7FF),
                        Color(0xFFD1F2FF)
                    )
                )
            )
    ) {

        when (screenState.displayState) {

            is DisplayState.PerformanceAnalysisProgress -> {
                val progressState = screenState.displayState as DisplayState.PerformanceAnalysisProgress
                Box(
                    modifier = Modifier
                        .fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Card(
                        modifier = Modifier
                            .padding(24.dp)
                            .fillMaxWidth(0.9f),
                        shape = RoundedCornerShape(24.dp),
                        elevation = CardDefaults.cardElevation(8.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = Color.White.copy(alpha = 0.98f)
                        )
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(32.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(48.dp),
                                color = Color(0xFF512DA8),
                                strokeWidth = 5.dp
                            )
                            Spacer(modifier = Modifier.height(24.dp))
                            Text(
                                text = "Analyzing configuration:",
                                style = MaterialTheme.typography.titleMedium,
                                color = Color(0xFF3949AB)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Chunk Size: ${progressState.currentChunkSize}",
                                style = MaterialTheme.typography.bodyLarge,
                                color = Color(0xFF512DA8)
                            )
                            Text(
                                text = "Overlap: ${progressState.currentOverlap}%",
                                style = MaterialTheme.typography.bodyLarge,
                                color = Color(0xFF512DA8)
                            )
                            Spacer(modifier = Modifier.height(24.dp))
                            LinearProgressIndicator(
                                progress = { progressState.completedConfigurations.toFloat() / progressState.totalConfigurations },
                                modifier = Modifier.fillMaxWidth(0.85f),
                                color = Color(0xFF00B8D4)
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = "${progressState.completedConfigurations} / ${progressState.totalConfigurations} configurations completed",
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color(0xFF3949AB)
                            )
                        }
                    }
                }
            }

            is DisplayState.PerformanceDefault -> {

                DefaultBodyContent(
                    screenState.localRagDocuments,
                    screenState.localRagDocumentsPerformance,
                    onPerformanceAnalysisClink = { documentId, minChunkSize, maxChunkSize, chunkSizeInterval ->
                        performanceLocalRagViewModel.runPerformanceAnalysis(documentId, minChunkSize, maxChunkSize, chunkSizeInterval)
                    }
                )
            }

            DisplayState.PerformanceWithRemoteLLMLoading -> TODO()
        }
    }

    ScreenEvent(performanceLocalRagViewModel)
}

@Composable
private fun DefaultBodyContent(
    localRagDocuments: List<LocalRagDocument>,
    localRagDocumentsPerformance: List<LocalRagDocumentPerformance>,
    onPerformanceAnalysisClink: (Long, Int, Int, Int) -> Unit
) {
    // State to control dialog visibility and which document is being analyzed
    var showDialogForDocumentId by remember { mutableStateOf<Long?>(null) }
    var minChunkSize by remember { mutableStateOf("100") }
    var maxChunkSize by remember { mutableStateOf("500") }
    var chunkSizeInterval by remember { mutableStateOf("5") }

    // Centralized export state
    val context = LocalContext.current
    var showExportForDocumentId by remember { mutableStateOf<Long?>(null) }
    var csvStringToExport by rememberSaveable { mutableStateOf<String?>(null) }
    var showExportError by remember { mutableStateOf(false) }

    val createCsvLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("text/csv")
    ) { uri: Uri? ->
        uri?.let { safeUri ->
            csvStringToExport?.let { csv ->
                try {
                    context.contentResolver.openOutputStream(safeUri)?.use { output ->
                        output.write(csv.toByteArray())
                    }
                } catch (e: Exception) {
                    showExportError = true
                }
            }
        }
        showExportForDocumentId = null
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        if (localRagDocuments.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Please upload a document first in the Document screen to perform analysis",
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(localRagDocuments) { document ->
                    val performanceData = localRagDocumentsPerformance.filter { it.documentId == document.documentId }
                    DocumentCard(
                        document = document,
                        performanceData = performanceData,
                        onPerformanceAnalysisClick = {
                            showDialogForDocumentId = document.documentId
                            minChunkSize = "100"
                            maxChunkSize = "500"
                            chunkSizeInterval = "5"
                        },
                        onExportClick = {
                            if (performanceData.isNotEmpty()) {
                                csvStringToExport = constructCsvExportString(performanceData)
                                showExportForDocumentId = document.documentId
                                createCsvLauncher.launch("performance_${document.documentName}_${System.currentTimeMillis()}.csv")
                            }
                        }
                    )
                }
            }
        }
    }

    // Show dialog if a document is selected for analysis
    if (showDialogForDocumentId != null) {
        ChunkParameterDialog(
            minChunkSize = minChunkSize,
            maxChunkSize = maxChunkSize,
            chunkSizeInterval = chunkSizeInterval,
            onMinChunkSizeChange = { minChunkSize = it.filter { c -> c.isDigit() } },
            onMaxChunkSizeChange = { maxChunkSize = it.filter { c -> c.isDigit() } },
            onChunkSizeIntervalChange = { chunkSizeInterval = it.filter { c -> c.isDigit() } },
            onConfirm = {
                val min = minChunkSize.toIntOrNull() ?: 100
                val max = maxChunkSize.toIntOrNull() ?: 500
                val interval = chunkSizeInterval.toIntOrNull() ?: 5
                showDialogForDocumentId?.let { docId ->
                    onPerformanceAnalysisClink(docId, min, max, interval)
                }
                showDialogForDocumentId = null
            },
            onDismiss = { showDialogForDocumentId = null }
        )
    }

    // Show export error dialog if needed
    if (showExportError) {
        AlertDialog(
            onDismissRequest = { showExportError = false },
            title = { Text("Export Failed", color = Color(0xFFD32F2F)) },
            text = {
                Text(
                    "Failed to export performance data. Please try again.",
                    style = MaterialTheme.typography.bodyMedium
                )
            },
            confirmButton = {
                TextButton(onClick = { showExportError = false }) {
                    Text("OK")
                }
            },
            containerColor = Color(0xFFFFEBEE),
            shape = RoundedCornerShape(20.dp)
        )
    }
}

@Composable
private fun ChunkParameterDialog(
    minChunkSize: String,
    maxChunkSize: String,
    chunkSizeInterval: String,
    onMinChunkSizeChange: (String) -> Unit,
    onMaxChunkSizeChange: (String) -> Unit,
    onChunkSizeIntervalChange: (String) -> Unit,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Enter Chunk Parameters") },
        text = {
            Column {
                OutlinedTextField(
                    value = minChunkSize,
                    onValueChange = onMinChunkSizeChange,
                    label = { Text("Min Chunk Size") }
                )
                OutlinedTextField(
                    value = maxChunkSize,
                    onValueChange = onMaxChunkSizeChange,
                    label = { Text("Max Chunk Size") }
                )
                OutlinedTextField(
                    value = chunkSizeInterval,
                    onValueChange = onChunkSizeIntervalChange,
                    label = { Text("Chunk Intervals") }
                )
            }
        },
        confirmButton = {
            TextButton(onClick = onConfirm) { Text("Run Analysis") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}

@Composable
private fun DocumentCard(
    document: LocalRagDocument,
    performanceData: List<LocalRagDocumentPerformance>,
    onPerformanceAnalysisClick: () -> Unit,
    onExportClick: () -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(8.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = document.documentName,
                    style = MaterialTheme.typography.titleMedium.copy(
                        color = MaterialTheme.colorScheme.primary
                    ),
                    modifier = Modifier
                        .weight(1f)
                        .clickable { expanded = !expanded }
                        .padding(end = 8.dp)
                )
                Button(
                    onClick = onPerformanceAnalysisClick,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    ),
                    modifier = Modifier
                        .defaultMinSize(minWidth = 80.dp)
                ) {
                    Text("Analyze")
                }
            }
            AnimatedVisibility(visible = expanded) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    PerformanceResultsSection(performanceData, onExportClick)
                }
            }
        }
    }
}

@Composable
private fun PerformanceResultsSection(
    performanceData: List<LocalRagDocumentPerformance>,
    onExportClick: () -> Unit
) {
    if (performanceData.isNotEmpty()) {
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Performance Results",
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.onSurface
        )
        Spacer(modifier = Modifier.height(8.dp))
        performanceData.forEach { performance ->
            PerformanceItem(performance)
        }
        Spacer(modifier = Modifier.height(8.dp))
        Button(
            onClick = onExportClick,
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.secondary
            )
        ) {
            Text("Export Results as CSV")
        }
    }
}

@Composable
private fun PerformanceItem(performance: LocalRagDocumentPerformance) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            Text(
                text = "Chunk Size: ${performance.chunkSize}, Overlap: ${performance.chunkOverlapPercentage}%",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Total Time: ${performance.totalTimeS} s",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = "Memory Usage: ${performance.totalMemoryUsageMB}MB",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun ScreenEvent(
    performanceLocalRagViewModel: PerformanceLocalRagViewModel
) {
    val context = LocalContext.current
    var showExportError by remember { mutableStateOf(false) }
    var csvStringToExport by rememberSaveable { mutableStateOf<String?>(null) }

    val createCsvLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("text/csv")
    ) { uri: Uri? ->
        uri?.let { safeUri ->
            csvStringToExport?.let { csv ->
                context.contentResolver.openOutputStream(safeUri)?.use { output ->
                    output.write(csv.toByteArray())
                }
            }
        }
    }

    val noConsumableEvent = ConsumableEvent<PerformanceLocalRagScreenEvent>(null)
    var showDialogEvent by remember { mutableStateOf(noConsumableEvent) }

    LaunchedEffect(Unit) {
        performanceLocalRagViewModel.viewEvent.onEach { event ->
            showDialogEvent = when(event) {
                is PerformanceLocalRagScreenEvent.PerformanceComplete -> {
                    ConsumableEvent(event)
                }
                is PerformanceLocalRagScreenEvent.PerformanceError -> {
                    ConsumableEvent(event)
                }
            }
        }.launchIn(this)
    }

    // Export Error Dialog
    if (showExportError) {
        AlertDialog(
            onDismissRequest = { showExportError = false },
            title = { Text("Export Failed", color = Color(0xFFD32F2F)) },
            text = { 
                Text(
                    "Failed to export performance data. Please try again.",
                    style = MaterialTheme.typography.bodyMedium
                )
            },
            confirmButton = {
                TextButton(onClick = { showExportError = false }) {
                    Text("OK")
                }
            },
            containerColor = Color(0xFFFFEBEE),
            shape = RoundedCornerShape(20.dp)
        )
    }

    when(val eventContent = showDialogEvent.event) {

        is PerformanceLocalRagScreenEvent.PerformanceComplete -> {
            AlertDialog(
                onDismissRequest = { showDialogEvent = noConsumableEvent },
                title = { Text("Performance Analysis Complete", color = Color(0xFF388E3C)) },
                text = {
                    Column {
                        Text(
                            "Number of performance configurations: ${eventContent.localRagDocumentsPerformance.size}",
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            "Would you like to export the results to CSV?",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            csvStringToExport = constructCsvExportString(eventContent.localRagDocumentsPerformance)
                            createCsvLauncher.launch("performance_analysis_${System.currentTimeMillis()}.csv")
                            showDialogEvent = noConsumableEvent
                        }
                    ) {
                        Text("Export")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showDialogEvent = noConsumableEvent }) {
                        Text("Close")
                    }
                },
                containerColor = Color(0xFFE8F5E9),
                shape = RoundedCornerShape(20.dp)
            )
        }

        is PerformanceLocalRagScreenEvent.PerformanceError -> {
            AlertDialog(
                onDismissRequest = { showDialogEvent = noConsumableEvent },
                title = { Text("Analysis Failed", color = Color(0xFFD32F2F)) },
                text = { 
                    Text(
                        "Performance Analysis Error. Please try again.",
                        style = MaterialTheme.typography.bodyMedium
                    )
                },
                confirmButton = {
                    TextButton(onClick = { showDialogEvent = noConsumableEvent }) {
                        Text("OK")
                    }
                },
                containerColor = Color(0xFFFFEBEE),
                shape = RoundedCornerShape(20.dp)
            )
        }

        else -> {}
    }
}

private fun constructCsvExportString(
    localRagDocumentsPerformance: List<LocalRagDocumentPerformance>
) : String {

    val headers = listOf(
        "documentId","chunkSize", "chunkOverlapPercentage", "numberOfDocumentChunks",
        "numberOfDocumentChuckEmbeddings",
        "FirstSimilarContext", "SecondSimilarContext", "ThirdSimilarContext", "remoteLLMResponse",
        "chunkingTimeS", "embeddingTimeS", "vectorStorageTimeS", "vectorRetrievalTimeS",
        "totalTimeS", "memoryUsageAfterChunking", "memoryUsageAfterEmbedding",
        "memoryUsageAfterRetrieval", "chunkingMemoryDelta", "embeddingAndStorageMemoryDelta",
        "retrievalMemoryDelta", "totalMemoryUsageMB"
    )

    return CsvExporter.exportListToCSV(
        data = localRagDocumentsPerformance,
        headers = headers
    ) { perf ->
        listOf(
            perf.documentId.toString(),
            perf.chunkSize.toString(),
            perf.chunkOverlapPercentage.toString(),
            perf.numberOfDocumentChunks.toString(),
            perf.numberOfDocumentChuckEmbeddings.toString(),

            // Unpack the first 3 elements from localRagSimilarContextList
            perf.localRagSimilarContextList.getOrNull(0) ?: "",
            perf.localRagSimilarContextList.getOrNull(1) ?: "",
            perf.localRagSimilarContextList.getOrNull(2) ?: "",

            perf.remoteLLMResponse.toString(),
            perf.chunkingTimeS.toString(),
            perf.embeddingTimeS.toString(),
            perf.vectorStorageTimeS.toString(),
            perf.vectorRetrievalTimeS.toString(),
            perf.totalTimeS.toString(),
            perf.memoryUsageAfterChunkingMB.toString(),
            perf.memoryUsageAfterEmbeddingMB.toString(),
            perf.memoryUsageAfterRetrievalMB.toString(),
            perf.chunkingMemoryDeltaMB.toString(),
            perf.embeddingAndStorageMemoryDeltaMB.toString(),
            perf.retrievalMemoryDeltaMB.toString(),
            perf.totalMemoryUsageMB.toString()
        )
    }
}
