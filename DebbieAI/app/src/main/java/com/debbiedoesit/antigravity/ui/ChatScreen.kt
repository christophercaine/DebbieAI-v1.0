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
fun ChatScreen(viewModel: ChatViewModel = viewModel()) {
        var inputText by remember { mutableStateOf("") }
        val messages = viewModel.messages
        val isGenerating by viewModel.isGenerating.collectAsState()
        val isPreparingModel by viewModel.isPreparingModel.collectAsState()

        val avatarRes = if (isGenerating) R.drawable.deep_thought else R.drawable.neutral_listening
        val infiniteTransition = rememberInfiniteTransition(label = "pulse")
        val scale by
                infiniteTransition.animateFloat(
                        initialValue = 1f,
                        targetValue = if (isGenerating) 1.05f else 1f,
                        animationSpec =
                                infiniteRepeatable(
                                        animation = tween(800, easing = FastOutSlowInEasing),
                                        repeatMode = RepeatMode.Reverse
                                ),
                        label = "scale"
                )

        Column(modifier = Modifier.fillMaxSize().padding(16.dp).imePadding()) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                        Image(
                                painter = painterResource(id = avatarRes),
                                contentDescription = "Debbie AI",
                                modifier =
                                        Modifier.size(48.dp)
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
                        Text(
                                text = "Debbie AI Assistant",
                                style = MaterialTheme.typography.headlineMedium,
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.weight(1f)
                        )
                        if (isPreparingModel) {
                                CircularProgressIndicator(
                                        modifier = Modifier.size(24.dp),
                                        strokeWidth = 2.dp
                                )
                        }
                }

                if (isPreparingModel) {
                        Text(
                                text = "Preparing AI (first-time setup)...",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.secondary
                        )
                        LinearProgressIndicator(
                                modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
                        )
                }

                Spacer(modifier = Modifier.height(8.dp))

                LazyColumn(modifier = Modifier.weight(1f).fillMaxWidth(), reverseLayout = false) {
                        items(messages) { message -> ChatBubble(message) }
                }

                Spacer(modifier = Modifier.height(8.dp))

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
                                        )
                        )

                        if (inputText.isBlank()) {
                                IconButton(onClick = { /* TODO speech to text */}) {
                                        Icon(
                                                Icons.Default.Mic,
                                                contentDescription = "Speak",
                                                tint = MaterialTheme.colorScheme.primary
                                        )
                                }
                        } else {
                                IconButton(
                                        onClick = {
                                                viewModel.sendMessage(inputText)
                                                inputText = ""
                                        },
                                        enabled = !isGenerating
                                ) {
                                        Icon(
                                                Icons.Default.Send,
                                                contentDescription = "Send",
                                                tint = MaterialTheme.colorScheme.primary
                                        )
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
