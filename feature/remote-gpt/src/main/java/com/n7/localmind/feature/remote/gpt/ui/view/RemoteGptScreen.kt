package com.n7.localmind.feature.remote.gpt.ui.view

import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.n7.localmind.component.remote.gpt.domain.model.RemoteGptChatMessage
import com.n7.localmind.design.system.BuildConfig
import com.n7.localmind.design.system.composables.TopBar
import com.n7.localmind.feature.remote.gpt.R
import com.n7.localmind.feature.remote.gpt.ui.state.ContentDisplayState
import com.n7.localmind.feature.remote.gpt.ui.state.DisplayState
import com.n7.localmind.feature.remote.gpt.ui.state.RemoteGptScreenState
import com.n7.localmind.feature.remote.gpt.ui.viewmodel.RemoteGptViewModel
import java.text.SimpleDateFormat
import java.util.Locale

@Composable
fun RemoteGptScreen(
    remoteGptViewModel: RemoteGptViewModel
) {

    val screenState by remoteGptViewModel.screenState.collectAsState()

    Scaffold(
        topBar = {
            TopBar(stringResource(R.string.remote_gpt_screen_title))
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

                if(BuildConfig.DEBUG) {
                    SideEffect {
                        Log.d("Nick-RecomposeCheck", "RemoteGptScreen recomposed")
                    }
                }

                Body(remoteGptViewModel, screenState)
            }
        }
    )
}

@Composable
private fun Body(
    remoteGptViewModel: RemoteGptViewModel,
    screenState: RemoteGptScreenState,
) {

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
        when(screenState.displayState) {

            is DisplayState.Error -> {
                TODO("Implement an error view or full-screen view or dialog to convey that we can't chat anymore")
            }

            is DisplayState.Content -> {
                BodyContent(
                    screenState.displayState,
                    onSendClick = { userMessage ->
                        remoteGptViewModel.getChatMessageFromRemoteGpt(userMessage)
                    }
                )
            }
        }
    }
}

@Composable
private fun BodyContent(
    content: DisplayState.Content,
    onSendClick: (String) -> Unit
) {
    var input by remember { mutableStateOf("") }
    val isLoading = content.contentDisplayState is ContentDisplayState.ContentLoading

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.background,
                        MaterialTheme.colorScheme.primary.copy(alpha = 0.05f),
                        MaterialTheme.colorScheme.secondary.copy(alpha = 0.05f)
                    )
                )
            )
    ) {
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 8.dp, vertical = 8.dp),
            reverseLayout = true
        ) {
            items(content.remoteGptChatMessages.size) { index ->
                val message = content.remoteGptChatMessages[content.remoteGptChatMessages.size - 1 - index]
                val isUser = message.role == RemoteGptChatMessage.Role.USER

                // Gradient bubble backgrounds
                val bubbleBrush = if (isUser) {
                    Brush.horizontalGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.surfaceVariant,
                            MaterialTheme.colorScheme.secondaryContainer
                        )
                    )
                } else {
                    Brush.horizontalGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.primary,
                            MaterialTheme.colorScheme.tertiary
                        )
                    )
                }

                val textColor = if (isUser) {
                    MaterialTheme.colorScheme.onSurfaceVariant
                } else {
                    MaterialTheme.colorScheme.onPrimary
                }

                val avatarColor = if (isUser) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondary
                val avatarInitial = if (isUser) "U" else "A"

                AnimatedVisibility(
                    visible = true,
                    enter = fadeIn() + slideInVertically(initialOffsetY = { it / 2 })
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start,
                        verticalAlignment = Alignment.Bottom
                    ) {
                        if (!isUser) {
                            // Assistant avatar
                            Box(
                                modifier = Modifier
                                    .size(width = 32.dp, height = 48.dp)
                                    .padding(bottom = 16.dp)
                                    .clip(MaterialTheme.shapes.small)
                                    .background(avatarColor.copy(alpha = 0.8f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = avatarInitial,
                                    color = MaterialTheme.colorScheme.onSecondary,
                                    fontWeight = FontWeight.Bold

                                )
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                        }
                        Surface(
                            shape = MaterialTheme.shapes.large,
                            tonalElevation = 6.dp,
                            shadowElevation = 8.dp,
                            modifier = if (isUser) {
                                Modifier
                                    .padding(start = 48.dp, end = 4.dp)
                                    .shadow(8.dp, MaterialTheme.shapes.large)
                            } else {
                                Modifier
                                    .padding(start = 4.dp, end = 48.dp, bottom = 16.dp)
                                    .shadow(8.dp, MaterialTheme.shapes.large)
                            },
                            color = Color.Transparent
                        ) {
                            Box(
                                modifier = Modifier
                                    .background(bubbleBrush)
                                    .padding(horizontal = 16.dp, vertical = 12.dp)
                            ) {
                                Column(horizontalAlignment = if (isUser) Alignment.End else Alignment.Start) {
                                    Text(
                                        text = message.content,
                                        color = textColor,
                                        style = MaterialTheme.typography.bodyLarge,
                                        fontWeight = FontWeight.Medium
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            text = SimpleDateFormat("dd MMM, hh:mm a", Locale.getDefault())
                                                .format(System.currentTimeMillis()),
                                            style = MaterialTheme.typography.labelSmall,
                                            color = textColor.copy(alpha = 0.6f)
                                        )
                                        if (isUser) {
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Icon(
                                                imageVector = Icons.Default.Check,
                                                contentDescription = "Sent",
                                                tint = textColor.copy(alpha = 0.7f),
                                                modifier = Modifier.size(14.dp)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                        if (isUser) {
                            Spacer(modifier = Modifier.width(8.dp))
                            // User avatar
                            Box(
                                modifier = Modifier
                                    .size(32.dp)
                                    .clip(MaterialTheme.shapes.small)
                                    .background(avatarColor.copy(alpha = 0.8f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = avatarInitial,
                                    color = MaterialTheme.colorScheme.onPrimary,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }
        }
        // Enhanced input bar
        Surface(
            tonalElevation = 8.dp,
            shadowElevation = 12.dp,
            shape = MaterialTheme.shapes.large,
            modifier = Modifier
                .padding(8.dp)
                .fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .padding(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = input,
                    onValueChange = { input = it },
                    modifier = Modifier
                        .weight(1f)
                        .background(MaterialTheme.colorScheme.background, MaterialTheme.shapes.medium),
                    placeholder = {
                        Text(
                            text = "Type a message ...".uppercase(),
                            style = MaterialTheme.typography.bodyMedium .copy(
                                fontWeight = FontWeight.Medium,
                            )
                        )
                    },
                    enabled = !isLoading,
                    shape = MaterialTheme.shapes.medium,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline
                    )
                )
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier
                            .padding(start = 8.dp)
                            .size(32.dp),
                        strokeWidth = 3.dp
                    )
                } else {
                    Button(
                        onClick = {
                            if (input.isNotBlank()) {
                                onSendClick(input)
                                input = ""
                            }
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary
                        ),
                        shape = MaterialTheme.shapes.medium,
                        modifier = Modifier
                            .padding(start = 8.dp)
                            .size(48.dp),
                        contentPadding = PaddingValues(0.dp)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.Send,
                            contentDescription = "Send",
                            tint = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                }
            }
        }
    }
}
