package com.n7.localmind.feature.document.local.rag.ui.view

import android.content.Intent
import android.provider.OpenableColumns
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.n7.localmind.component.local.rag.domain.model.LocalRagDocument
import com.n7.localmind.design.system.ConsumableEvent
import com.n7.localmind.design.system.composables.FullScreenLoadingV2
import com.n7.localmind.design.system.composables.TopBar
import com.n7.localmind.feature.document.local.rag.R
import com.n7.localmind.feature.document.local.rag.ui.state.DisplayState
import com.n7.localmind.feature.document.local.rag.ui.state.DocumentLocalRagScreenEvent
import com.n7.localmind.feature.document.local.rag.ui.state.DocumentLocalRagScreenState
import com.n7.localmind.feature.document.local.rag.ui.viewmodel.DocumentLocalRagViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

@Composable
internal fun DocumentLocalRagScreen(
    documentLocalRagViewModel: DocumentLocalRagViewModel,
) {

    val screenState by documentLocalRagViewModel.screenState.collectAsState()
    
    Scaffold(
        topBar = {
            TopBar(stringResource(R.string.document_screen_title))
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
                Body(documentLocalRagViewModel, screenState)
            }
        }
    )
}


@Composable
private fun Body(
    documentLocalRagViewModel: DocumentLocalRagViewModel,
    screenState: DocumentLocalRagScreenState,
) {
    val context = LocalContext.current
    
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        result.data?.data?.let { uri ->
            var docFileName = ""
            context.contentResolver.query(uri, null, null, null, null)?.use { cursor ->
                val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                cursor.moveToFirst()
                docFileName = cursor.getString(nameIndex)
            }

            context.contentResolver.openInputStream(uri)?.let { inputStream ->
                documentLocalRagViewModel.addDocumentToLocalRag(inputStream, docFileName)
            }
        }
    }

    // Gradient background for the whole screen
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

            is DisplayState.Loading -> {
                FullScreenLoadingV2()
            }

            is DisplayState.Default -> {

                DefaultBodyContent(
                    screenState.localRagDocuments,
                    onUploadClick = {
                        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
                            addCategory(Intent.CATEGORY_OPENABLE)
                            type = "application/pdf"
                        }
                        launcher.launch(intent)
                    }
                )
            }
        }
    }

    ScreenEvent(documentLocalRagViewModel)
}

@Composable
private fun ScreenEvent(
    documentLocalRagViewModel: DocumentLocalRagViewModel
) {

    val noConsumableEvent = ConsumableEvent<DocumentLocalRagScreenEvent>(null)
    var showDialogEvent by remember { mutableStateOf(noConsumableEvent) }

    LaunchedEffect(Unit) {
        documentLocalRagViewModel.viewEvent.onEach { event ->
            showDialogEvent = when(event){
                is DocumentLocalRagScreenEvent.DocumentUploadSuccess -> {
                    ConsumableEvent(event)
                }

                is DocumentLocalRagScreenEvent.DocumentUploadFailure -> {
                    ConsumableEvent(event)
                }
            }
        }.launchIn(this)
    }

    when(val eventContent = showDialogEvent.event) {

        is DocumentLocalRagScreenEvent.DocumentUploadSuccess -> {
            AlertDialog(
                onDismissRequest = { showDialogEvent = noConsumableEvent },
                title = { Text("Upload Successful!", color = Color(0xFF388E3C)) },
                text = {
                    Text(
                        "Document Name: ${eventContent.localRagDocument.documentName}\n" +
                        "Chunks: ${eventContent.localRagDocument.numberOfDocumentChunks}\n" +
                        "Embedding IDs: ${eventContent.localRagDocument.numberOfDocumentChuckEmbeddings}",
                        style = MaterialTheme.typography.bodyMedium
                    )
                },
                confirmButton = {
                    TextButton(onClick = { showDialogEvent = noConsumableEvent }) {
                        Text("OK")
                    }
                },
                containerColor = Color(0xFFE8F5E9),
                shape = RoundedCornerShape(20.dp)
            )
        }

        is DocumentLocalRagScreenEvent.DocumentUploadFailure -> {
            AlertDialog(
                onDismissRequest = { showDialogEvent = noConsumableEvent },
                title = { Text("Upload Failed", color = Color(0xFFD32F2F)) },
                text = { Text("Document upload failed. Please try again.", style = MaterialTheme.typography.bodyMedium) },
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

@Preview(showSystemUi = true, showBackground = true)
@Composable
private fun DefaultContentPreview(
) {
    val localRagDocument1 = LocalRagDocument(1L, "Doc_1", "", 110, 150)
    val localRagDocument2 = LocalRagDocument(2L, "Doc_2", "", 220, 250)
    val localRagDocument3 = LocalRagDocument(3L, "Doc_3", "", 330, 350)
    val dummyLocalRagDocuments = listOf(localRagDocument1, localRagDocument2, localRagDocument3)
    DefaultBodyContent(dummyLocalRagDocuments){  }
}

@Composable
private fun DefaultBodyContent(
    localRagDocuments: List<LocalRagDocument>,
    onUploadClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {

        Box(modifier = Modifier.weight(0.8f).fillMaxSize()) {
            if (localRagDocuments.isEmpty()) {

                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Please upload a document first to activate the Local RAG system",
                        style = MaterialTheme.typography.bodyLarge,
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                ) {
                    localRagDocuments.forEach { doc ->
                        ElevatedCard(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp),
                            elevation = CardDefaults.elevatedCardElevation(8.dp),
                            shape = MaterialTheme.shapes.extraLarge,
                            colors = CardDefaults.elevatedCardColors()
                        ) {
                            ListItem(
                                headlineContent = {
                                    Text(
                                        text = doc.documentName,
                                        style = MaterialTheme.typography.titleMedium.copy(
                                            color = MaterialTheme.colorScheme.primary
                                        )
                                    )
                                },
                                supportingContent = {
                                    Column {
                                        Text(
                                            text = "Chunks: ${doc.numberOfDocumentChunks}",
                                            style = MaterialTheme.typography.bodyMedium
                                        )
                                        Text(
                                            text = "Embedding IDs: ${doc.numberOfDocumentChuckEmbeddings}",
                                            style = MaterialTheme.typography.bodyMedium
                                        )
                                    }
                                },
                                leadingContent = {
                                    Icon(
                                        imageVector = Icons.Default.CheckCircle,
                                        contentDescription = "Document",
                                        tint = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.size(32.dp)
                                    )
                                }
                            )
                        }
                    }
                }
            }
        }
        Box(modifier = Modifier.weight(0.2f).fillMaxSize()) {
            ExtendedFloatingActionButton(
                text = { Text("Upload") },
                icon = { Icon(Icons.Default.Add, contentDescription = "Upload") },
                onClick = onUploadClick,
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(8.dp),
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = Color.White,
                elevation = FloatingActionButtonDefaults.elevation(8.dp)
            )
        }
    }
}
