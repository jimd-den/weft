package com.example.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.SourceEntity
import com.example.data.remote.ChatMessage
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AgentSheet(
    chatHistory: List<ChatMessage>,
    isSending: Boolean,
    onSendMessage: (String) -> Unit,
    onDismiss: () -> Unit
) {
    var text by remember { mutableStateOf("") }
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = LcarsBg2,
        modifier = Modifier.fillMaxHeight(0.9f)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp)
                .navigationBarsPadding(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "AGENT TERMINAL",
                style = MaterialTheme.typography.labelMedium.copy(letterSpacing = 2.sp),
                color = LcarsSurvey
            )
            
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(chatHistory) { msg ->
                    if (msg.role != "system") {
                        val isUser = msg.role == "user"
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start
                        ) {
                            Box(
                                modifier = Modifier
                                    .background(
                                        if (isUser) LcarsSurvey else Color(0xFF2A2A34),
                                        RoundedCornerShape(8.dp)
                                    )
                                    .padding(12.dp)
                                    .widthIn(max = 300.dp)
                            ) {
                                Text(
                                    text = msg.content,
                                    color = if (isUser) LcarsBlack else LcarsText,
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                        }
                    }
                }
                if (isSending) {
                    item {
                        Text("Thinking...", color = LcarsDim, style = MaterialTheme.typography.labelSmall)
                    }
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = text,
                    onValueChange = { text = it },
                    modifier = Modifier.weight(1f),
                    textStyle = MaterialTheme.typography.bodyMedium,
                    placeholder = { Text("Command the agent...", color = LcarsDim) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = LcarsSurvey,
                        unfocusedBorderColor = Color(0xFF2A2A34),
                        focusedTextColor = LcarsText,
                        unfocusedTextColor = LcarsText
                    )
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "SND",
                    style = MaterialTheme.typography.labelSmall,
                    color = LcarsBlack,
                    modifier = Modifier
                        .background(LcarsSurvey, RoundedCornerShape(6.dp))
                        .clickable {
                            if (text.isNotBlank() && !isSending) {
                                onSendMessage(text)
                                text = ""
                            }
                        }
                        .padding(horizontal = 16.dp, vertical = 14.dp)
                )
            }
        }
    }
}


@Composable
fun BridgeScreen(
    sources: List<SourceEntity>,
    onSourceClick: (String) -> Unit,
    onCaptureClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Text(
            text = "BRIDGE // ACTIVE THREADS",
            style = MaterialTheme.typography.labelMedium.copy(letterSpacing = 3.sp),
            color = LcarsDim,
            modifier = Modifier.padding(bottom = 14.dp)
        )
        
        if (sources.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .background(LcarsBg2, RoundedCornerShape(14.dp))
                    .border(1.dp, Color(0xFF1C1C26), RoundedCornerShape(14.dp))
                    .clickable { onCaptureClick() },
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    Text(
                        text = "⊕ CAPTURE YOUR FIRST SOURCE",
                        style = MaterialTheme.typography.bodyLarge.copy(fontSize = 19.sp, lineHeight = 26.sp),
                        color = Color(0xFFE6E3DB)
                    )
                    Text(
                        text = "Paste text to start the weave",
                        style = MaterialTheme.typography.labelSmall,
                        color = LcarsDim
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(sources) { source ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(LcarsBg2, RoundedCornerShape(topEnd = 6.dp, bottomEnd = 16.dp, topStart = 6.dp, bottomStart = 6.dp))
                            .clickable { onSourceClick(source.id) }
                            .padding(16.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .padding(end = 16.dp)
                                .width(4.dp)
                                .height(44.dp)
                                .background(LcarsSurvey)
                        )
                        Column {
                            Text(
                                text = "SOURCE ${source.id} · ${source.yearTag}",
                                style = MaterialTheme.typography.labelSmall,
                                color = LcarsDim,
                                modifier = Modifier.padding(bottom = 8.dp)
                            )
                            Text(
                                text = source.title.ifBlank { "Untitled Source" },
                                style = MaterialTheme.typography.bodyLarge,
                                color = Color(0xFFDDD9D1)
                            )
                        }
                    }
                }
            }
            
            // Capture Button floating at bottom
            Box(
                modifier = Modifier.padding(top = 16.dp).fillMaxWidth()
            ) {
                Text(
                    text = "⊕ CAPTURE NEW",
                    style = MaterialTheme.typography.labelMedium,
                    color = LcarsBlack,
                    modifier = Modifier
                        .background(LcarsSurvey, RoundedCornerShape(999.dp))
                        .clickable { onCaptureClick() }
                        .padding(horizontal = 24.dp, vertical = 12.dp)
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CaptureSheet(
    onDismiss: () -> Unit,
    onSubmit: (String) -> Unit
) {
    var text by remember { mutableStateOf("") }
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = LcarsBg2
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp)
                .navigationBarsPadding(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "CAPTURE SOURCE",
                style = MaterialTheme.typography.labelMedium.copy(letterSpacing = 2.sp),
                color = LcarsSurvey
            )
            OutlinedTextField(
                value = text,
                onValueChange = { text = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp),
                textStyle = MaterialTheme.typography.bodyLarge,
                placeholder = { Text("Paste text here...", color = LcarsDim) },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = LcarsSurvey,
                    unfocusedBorderColor = Color(0xFF2A2A34),
                    focusedTextColor = LcarsText,
                    unfocusedTextColor = LcarsText
                )
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                Text(
                    text = "SAVE",
                    style = MaterialTheme.typography.labelSmall,
                    color = LcarsBlack,
                    modifier = Modifier
                        .background(LcarsSurvey, RoundedCornerShape(6.dp))
                        .clickable {
                            if (text.isNotBlank()) onSubmit(text)
                        }
                        .padding(horizontal = 24.dp, vertical = 12.dp)
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsSheet(
    apiKey: String,
    selectedAgent: String,
    onDismiss: () -> Unit,
    onSave: (String, String) -> Unit
) {
    var keyState by remember { mutableStateOf(apiKey) }
    var agentState by remember { mutableStateOf(selectedAgent) }
    
    val agents = listOf("google/gemini-2.5-flash", "google/gemini-2.5-pro", "anthropic/claude-3-haiku", "anthropic/claude-3-5-sonnet")

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = LcarsBg2
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp)
                .navigationBarsPadding(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "SYSTEM SETTINGS",
                style = MaterialTheme.typography.labelMedium.copy(letterSpacing = 2.sp),
                color = LcarsReflect
            )
            
            Text("OpenRouter API Key", color = LcarsDim, style = MaterialTheme.typography.labelSmall)
            OutlinedTextField(
                value = keyState,
                onValueChange = { keyState = it },
                modifier = Modifier.fillMaxWidth(),
                textStyle = MaterialTheme.typography.bodyLarge,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = LcarsReflect,
                    unfocusedBorderColor = Color(0xFF2A2A34),
                    focusedTextColor = LcarsText,
                    unfocusedTextColor = LcarsText
                )
            )
            
            Text("AI Agent Model", color = LcarsDim, style = MaterialTheme.typography.labelSmall)
            
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                agents.forEach { a ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { agentState = a }
                            .padding(8.dp)
                    ) {
                        RadioButton(
                            selected = (a == agentState),
                            onClick = { agentState = a },
                            colors = RadioButtonDefaults.colors(selectedColor = LcarsReflect, unselectedColor = LcarsDim)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = a, color = LcarsText, style = MaterialTheme.typography.bodyMedium)
                    }
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                Text(
                    text = "SAVE CONFIG",
                    style = MaterialTheme.typography.labelSmall,
                    color = LcarsBlack,
                    modifier = Modifier
                        .background(LcarsReflect, RoundedCornerShape(6.dp))
                        .clickable { onSave(keyState, agentState) }
                        .padding(horizontal = 24.dp, vertical = 12.dp)
                )
            }
        }
    }
}
