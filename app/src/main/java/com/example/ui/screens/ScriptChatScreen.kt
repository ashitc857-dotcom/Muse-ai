package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.Strings
import com.example.ui.StudioViewModel
import com.example.ui.theme.*

@Composable
fun ScriptChatScreen(
    viewModel: StudioViewModel,
    modifier: Modifier = Modifier
) {
    val chatMessages by viewModel.chatMessages.collectAsState()
    val chatInput by viewModel.chatInput.collectAsState()

    var selectedTab by remember { mutableStateOf(0) }
    val tabs = listOf("AI Agent Chat", "Screenplay Generator")

    var scriptTopic by remember { mutableStateOf("Neon Cyberpunk Heist in Neo-Dhaka") }
    var generatedScript by remember { mutableStateOf("") }
    var isWritingScript by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 10.dp)
    ) {
        // Mode Tabs
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(DarkSurfaceVariant)
                .padding(4.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            tabs.forEachIndexed { idx, title ->
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (selectedTab == idx) StudioEmerald else Color.Transparent)
                        .clickable { selectedTab = idx }
                        .padding(vertical = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = title,
                        color = if (selectedTab == idx) Color.Black else TextSecondary,
                        fontWeight = if (selectedTab == idx) FontWeight.Bold else FontWeight.Normal,
                        fontSize = 12.sp
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        if (selectedTab == 0) {
            // Chat Assistant Mode
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                contentPadding = PaddingValues(bottom = 8.dp)
            ) {
                items(chatMessages) { msg ->
                    val isUser = msg.sender == "user"
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start
                    ) {
                        Surface(
                            shape = RoundedCornerShape(
                                topStart = 14.dp,
                                topEnd = 14.dp,
                                bottomStart = if (isUser) 14.dp else 2.dp,
                                bottomEnd = if (isUser) 2.dp else 14.dp
                            ),
                            color = if (isUser) NeonCyan else DarkSurface,
                            border = if (!isUser) androidx.compose.foundation.BorderStroke(1.dp, DarkSurfaceBorder) else null,
                            modifier = Modifier.widthIn(max = 300.dp)
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text(
                                    text = if (isUser) "You" else "Muse AI Director",
                                    color = if (isUser) Color.Black.copy(alpha = 0.7f) else StudioEmerald,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = msg.text,
                                    color = if (isUser) Color.Black else TextPrimary,
                                    fontSize = 13.sp,
                                    lineHeight = 18.sp
                                )

                                if (!isUser) {
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.End
                                    ) {
                                        IconButton(
                                            onClick = { viewModel.speakDialogue(msg.text) },
                                            modifier = Modifier.size(26.dp)
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.VolumeUp,
                                                contentDescription = "Read Aloud",
                                                tint = NeonCyan,
                                                modifier = Modifier.size(16.dp)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Chat Input Field
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = chatInput,
                    onValueChange = { viewModel.chatInput.value = it },
                    placeholder = { Text("Ask Muse AI anything about story, scene, prompt...", color = TextTertiary, fontSize = 12.sp) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = NeonCyan,
                        unfocusedBorderColor = DarkSurfaceBorder,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary
                    ),
                    modifier = Modifier
                        .weight(1f)
                        .testTag("chat_input_field")
                )

                Spacer(modifier = Modifier.width(8.dp))

                IconButton(
                    onClick = { viewModel.sendChatMessage() },
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(NeonCyan)
                        .testTag("send_chat_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Send,
                        contentDescription = "Send",
                        tint = Color.Black,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        } else {
            // Screenplay Generator Mode
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item {
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = DarkSurface),
                        border = androidx.compose.foundation.BorderStroke(1.dp, DarkSurfaceBorder),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp)
                        ) {
                            Text(
                                text = "SCREENPLAY TOPIC & MOVIE PREMISE",
                                color = TextSecondary,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            OutlinedTextField(
                                value = scriptTopic,
                                onValueChange = { scriptTopic = it },
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = StudioEmerald,
                                    unfocusedBorderColor = DarkSurfaceBorder,
                                    focusedTextColor = TextPrimary,
                                    unfocusedTextColor = TextPrimary
                                ),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .heightIn(min = 70.dp)
                            )

                            Spacer(modifier = Modifier.height(10.dp))

                            Button(
                                onClick = {
                                    isWritingScript = true
                                    viewModel.draftScreenplay(scriptTopic) { result ->
                                        generatedScript = result
                                        isWritingScript = false
                                    }
                                },
                                enabled = !isWritingScript,
                                colors = ButtonDefaults.buttonColors(containerColor = StudioEmerald, contentColor = Color.Black),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("generate_script_button")
                            ) {
                                if (isWritingScript) {
                                    CircularProgressIndicator(color = Color.Black, modifier = Modifier.size(18.dp), strokeWidth = 2.dp)
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Drafting Screenplay...")
                                } else {
                                    Icon(imageVector = Icons.Default.EditNote, contentDescription = null)
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Draft Screenplay with Gemini", fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }

                if (generatedScript.isNotBlank()) {
                    item {
                        Card(
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = DarkSurface),
                            border = androidx.compose.foundation.BorderStroke(1.dp, StudioEmerald.copy(alpha = 0.5f)),
                            modifier = Modifier.fillMaxWidth()
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
                                        text = "GENERATED SCREENPLAY",
                                        color = StudioEmerald,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold
                                    )

                                    IconButton(
                                        onClick = { viewModel.speakDialogue(generatedScript.take(200)) },
                                        modifier = Modifier.size(30.dp)
                                    ) {
                                        Icon(imageVector = Icons.Default.VolumeUp, contentDescription = "Read", tint = StudioEmerald, modifier = Modifier.size(18.dp))
                                    }
                                }
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = generatedScript,
                                    color = TextPrimary,
                                    fontSize = 12.sp,
                                    lineHeight = 18.sp
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
