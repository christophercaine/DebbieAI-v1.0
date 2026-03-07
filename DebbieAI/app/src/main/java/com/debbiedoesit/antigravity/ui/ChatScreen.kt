package com.debbiedoesit.antigravity.ui

import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.debbiedoesit.antigravity.R
import com.debbiedoesit.antigravity.viewmodel.ChatMessage
import com.debbiedoesit.antigravity.viewmodel.ChatViewModel

@Composable
fun ChatScreen(viewModel: ChatViewModel = viewModel(), onBack: () -> Unit = {}) {
        var inputText by remember { mutableStateOf("") }
        val messages = viewModel.messages
        val isGenerating by viewModel.isGenerating.collectAsState()
        val isPreparingModel by viewModel.isPreparingModel.collectAsState()
        val expression by viewModel.expression.collectAsState()
        val sttText by viewModel.sttText.collectAsState()

        // Sync input text with STT result
        LaunchedEffect(sttText) {
                if (sttText.isNotEmpty() && sttText != "Listening...") {
                        inputText = sttText
                        viewModel.clearSttText()
                }
        }

        val infiniteTransition = rememberInfiniteTransition(label = "pulse")
        val scale by
                infiniteTransition.animateFloat(
                        initialValue = 1f,
                        targetValue = if (isGenerating) 1.08f else 1.02f,
                        animationSpec =
                                infiniteRepeatable(
                                        animation = tween(1200, easing = LinearOutSlowInEasing),
                                        repeatMode = RepeatMode.Reverse
                                ),
                        label = "scale"
                )

        Box(modifier = Modifier.fillMaxSize().padding(16.dp).imePadding()) {
                Column(modifier = Modifier.fillMaxSize()) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                                IconButton(onClick = onBack) {
                                        Icon(
                                                Icons.Default.ArrowBack,
                                                contentDescription = "Back",
                                                tint = MaterialTheme.colorScheme.primary
                                        )
                                }
                                Spacer(modifier = Modifier.width(4.dp))
                                Image(
                                        painter = painterResource(id = expression),
                                        contentDescription = "Debbie AI",
                                        modifier =
                                                Modifier.size(64.dp)
                                                        .scale(scale)
                                                        .clip(CircleShape)
                                                        .border(
                                                                2.dp,
                                                                MaterialTheme.colorScheme.primary,
                                                                CircleShape
                                                        ),
                                        contentScale = ContentScale.Crop
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                                text = "Debbie AI",
                                                style = MaterialTheme.typography.headlineSmall,
                                                color = MaterialTheme.colorScheme.primary
                                        )
                                        Text(
                                                text =
                                                        if (isGenerating) "Explaining..."
                                                        else "Active",
                                                style = MaterialTheme.typography.labelSmall,
                                                color =
                                                        if (isGenerating)
                                                                MaterialTheme.colorScheme.secondary
                                                        else Color(0xFF4CAF50)
                                        )
                                }
                                if (isPreparingModel) {
                                        CircularProgressIndicator(
                                                modifier = Modifier.size(24.dp),
                                                strokeWidth = 2.dp
                                        )
                                }
                        }

                        if (isPreparingModel) {
                                Text(
                                        text = "Preparing AI (initial setup)...",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.secondary,
                                        modifier = Modifier.padding(top = 8.dp)
                                )
                                LinearProgressIndicator(
                                        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
                                )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        if (messages.isEmpty() && !isPreparingModel) {
                                Column(
                                        modifier = Modifier.weight(1f).fillMaxWidth(),
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        verticalArrangement = Arrangement.Center
                                ) {
                                        Image(
                                                painter =
                                                        painterResource(
                                                                id = R.drawable.debbieai_logo
                                                        ),
                                                contentDescription = "Debbie AI Logo",
                                                modifier =
                                                        Modifier.fillMaxHeight(
                                                                        0.5f
                                                                ) // Takes 1/3 of the screen roughly
                                                                // when combined with other
                                                                // elements
                                                                .scale(scale)
                                                                .clip(CircleShape),
                                                contentScale = ContentScale.Fit
                                        )
                                        Spacer(modifier = Modifier.height(24.dp))
                                        Text(
                                                "Ask Debbie AI anything about your projects.",
                                                color = Color.Gray,
                                                style = MaterialTheme.typography.bodyMedium
                                        )
                                }
                        } else {
                                LazyColumn(
                                        modifier = Modifier.weight(1f).fillMaxWidth(),
                                        reverseLayout = false,
                                        contentPadding = PaddingValues(bottom = 8.dp)
                                ) { items(messages) { message -> ChatBubble(message) } }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        if (sttText == "Listening...") {
                                Text(
                                        text = "Listening...",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.padding(start = 16.dp, bottom = 4.dp)
                                )
                        }

                        Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                        ) {
                                TextField(
                                        value = inputText,
                                        onValueChange = { inputText = it },
                                        modifier = Modifier.weight(1f),
                                        placeholder = { Text("Ask Debbie...") },
                                        shape = RoundedCornerShape(24.dp),
                                        colors =
                                                TextFieldDefaults.colors(
                                                        focusedIndicatorColor = Color.Transparent,
                                                        unfocusedIndicatorColor = Color.Transparent
                                                ),
                                        maxLines = 4
                                )

                                Spacer(modifier = Modifier.width(4.dp))

                                if (inputText.isBlank()) {
                                        IconButton(
                                                onClick = { viewModel.startSpeechToText() },
                                                colors =
                                                        IconButtonDefaults.iconButtonColors(
                                                                containerColor =
                                                                        MaterialTheme.colorScheme
                                                                                .primaryContainer
                                                        )
                                        ) {
                                                Icon(
                                                        Icons.Default.Mic,
                                                        contentDescription = "Speak",
                                                        tint =
                                                                MaterialTheme.colorScheme
                                                                        .onPrimaryContainer
                                                )
                                        }
                                } else {
                                        IconButton(
                                                onClick = {
                                                        viewModel.sendMessage(inputText)
                                                        inputText = ""
                                                },
                                                enabled = !isGenerating,
                                                colors =
                                                        IconButtonDefaults.iconButtonColors(
                                                                containerColor =
                                                                        MaterialTheme.colorScheme
                                                                                .primary
                                                        )
                                        ) {
                                                Icon(
                                                        Icons.Default.Send,
                                                        contentDescription = "Send",
                                                        tint = MaterialTheme.colorScheme.onPrimary
                                                )
                                        }
                                }
                        }
                }
        }
}

@Composable
fun ChatBubble(message: ChatMessage) {
        val alignment = if (message.isUser) Alignment.End else Alignment.Start
        val bgColor =
                if (message.isUser) MaterialTheme.colorScheme.primary
                else MaterialTheme.colorScheme.surfaceVariant
        val textColor =
                if (message.isUser) MaterialTheme.colorScheme.onPrimary
                else MaterialTheme.colorScheme.onSurfaceVariant
        val bubbleShape =
                if (message.isUser) RoundedCornerShape(16.dp, 16.dp, 0.dp, 16.dp)
                else RoundedCornerShape(16.dp, 16.dp, 16.dp, 0.dp)

        Column(
                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                horizontalAlignment = alignment
        ) {
                Box(modifier = Modifier.clip(bubbleShape).background(bgColor).padding(12.dp)) {
                        Text(text = message.text, color = textColor)
                }
        }
}
