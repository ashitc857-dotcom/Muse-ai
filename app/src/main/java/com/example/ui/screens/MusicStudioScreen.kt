package com.example.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.Strings
import com.example.ui.StudioViewModel
import com.example.ui.theme.*
import java.io.File
import kotlin.math.sin

@Composable
fun MusicStudioScreen(
    viewModel: StudioViewModel,
    modifier: Modifier = Modifier
) {
    val musicPrompt by viewModel.musicPrompt.collectAsState()
    val isPlaying by viewModel.isAudioPlaying.collectAsState()
    val isRecording by viewModel.isRecordingVoice.collectAsState()
    val isGenerating by viewModel.isGenerating.collectAsState()
    val activeProject by viewModel.activeProject.collectAsState()
    val tokenCost by viewModel.ledgerManager.videoCost.collectAsState()

    var selectedTab by remember { mutableStateOf(0) }
    val tabs = listOf("WAV Synthesizer", "AI Song Lyrics", "Vocal & TTS", "Voice Recorder")

    val genres = listOf(
        "Cyberpunk Synthwave",
        "Cinematic Ambient",
        "Lofi Chill Beats",
        "Heroic Orchestral"
    )
    var selectedGenre by remember { mutableStateOf(genres[0]) }

    var ttsText by remember { mutableStateOf("Muse AI Studio transforms your words into living cinematic reality.") }
    var ttsPitch by remember { mutableStateOf(1.0f) }
    var ttsSpeed by remember { mutableStateOf(1.0f) }

    val infiniteTransition = rememberInfiniteTransition(label = "waveform")
    val waveOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 6.28f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "wave_offset"
    )

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
        contentPadding = PaddingValues(top = 10.dp, bottom = 24.dp)
    ) {
        // Tab Selector
        item {
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
                            .background(if (selectedTab == idx) StudioAmber else Color.Transparent)
                            .clickable { selectedTab = idx }
                            .padding(vertical = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = title,
                            color = if (selectedTab == idx) Color.Black else TextSecondary,
                            fontWeight = if (selectedTab == idx) FontWeight.Bold else FontWeight.Normal,
                            fontSize = 10.5.sp,
                            maxLines = 1
                        )
                    }
                }
            }
        }

        // Live Audio Waveform & Player Card
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = DarkSurface),
                border = androidx.compose.foundation.BorderStroke(1.dp, StudioAmber.copy(alpha = 0.4f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "PROCEDURAL 44.1kHz AUDIO ENGINE",
                                color = StudioAmberLight,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = if (isPlaying) "Playing: $selectedGenre Track" else "Ready to Synthesize",
                                color = TextPrimary,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }

                        // Play/Pause button
                        val currentAudio = activeProject?.audioPath?.let { File(it) }
                        IconButton(
                            onClick = {
                                if (currentAudio != null && currentAudio.exists()) {
                                    viewModel.toggleAudioPlayback(currentAudio)
                                } else {
                                    viewModel.generateMusicTrack(selectedGenre)
                                }
                            },
                            modifier = Modifier
                                .size(44.dp)
                                .clip(CircleShape)
                                .background(StudioAmber)
                                .testTag("play_music_button")
                        ) {
                            Icon(
                                imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                                contentDescription = if (isPlaying) "Pause" else "Play",
                                tint = Color.Black,
                                modifier = Modifier.size(26.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Waveform Visualizer Canvas
                    Canvas(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(60.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(DarkBackground)
                    ) {
                        val barCount = 28
                        val barWidth = size.width / (barCount * 1.5f)
                        val step = size.width / barCount

                        for (i in 0 until barCount) {
                            val factor = if (isPlaying) {
                                (0.3f + 0.65f * kotlin.math.abs(sin(waveOffset + i * 0.4f))).toFloat()
                            } else {
                                0.15f
                            }
                            val barHeight = size.height * factor
                            val topY = (size.height - barHeight) / 2f
                            drawRoundRect(
                                color = if (isPlaying) StudioAmber else DarkSurfaceBorder,
                                topLeft = Offset(i * step, topY),
                                size = Size(barWidth, barHeight),
                                cornerRadius = CornerRadius(4f, 4f)
                            )
                        }
                    }
                }
            }
        }

        // Tab Content: Synthesizer
        if (selectedTab == 0) {
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
                            text = "SELECT SOUND GENRE",
                            color = TextSecondary,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            genres.forEach { genre ->
                                val isSelected = selectedGenre == genre
                                Surface(
                                    shape = RoundedCornerShape(12.dp),
                                    color = if (isSelected) GlowAmber else DarkSurfaceVariant,
                                    border = androidx.compose.foundation.BorderStroke(
                                        1.dp,
                                        if (isSelected) StudioAmber else DarkSurfaceBorder
                                    ),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable { selectedGenre = genre }
                                ) {
                                    Row(
                                        modifier = Modifier.padding(12.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        RadioButton(
                                            selected = isSelected,
                                            onClick = { selectedGenre = genre },
                                            colors = RadioButtonDefaults.colors(selectedColor = StudioAmber)
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Column {
                                            Text(
                                                text = genre,
                                                color = if (isSelected) StudioAmberLight else TextPrimary,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 13.sp
                                            )
                                            Text(
                                                text = "Procedural 16-bit PCM stereo WAV master",
                                                color = TextSecondary,
                                                fontSize = 10.sp
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        Button(
                            onClick = { viewModel.generateMusicTrack(selectedGenre) },
                            enabled = !isGenerating,
                            colors = ButtonDefaults.buttonColors(containerColor = StudioAmber, contentColor = Color.Black),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp)
                                .testTag("synthesize_wav_button")
                        ) {
                            Icon(imageVector = Icons.Default.MusicNote, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Synthesize WAV Track (Cost: $tokenCost Token)",
                                fontWeight = FontWeight.Black,
                                fontSize = 13.sp
                            )
                        }
                    }
                }
            }
        }

        // Tab Content: AI Lyrics Generator
        if (selectedTab == 1) {
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
                            text = "AI SONG TOPIC & LYRICS PROMPT",
                            color = TextSecondary,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        OutlinedTextField(
                            value = musicPrompt,
                            onValueChange = { viewModel.musicPrompt.value = it },
                            placeholder = { Text("e.g. Cyberpunk rebellion under neon rain...", color = TextTertiary, fontSize = 12.sp) },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = StudioAmber,
                                unfocusedBorderColor = DarkSurfaceBorder,
                                focusedTextColor = TextPrimary,
                                unfocusedTextColor = TextPrimary
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .heightIn(min = 80.dp)
                                .testTag("lyrics_prompt_input")
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        Button(
                            onClick = { viewModel.generateLyrics() },
                            colors = ButtonDefaults.buttonColors(containerColor = StudioAmber, contentColor = Color.Black),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("generate_lyrics_button")
                        ) {
                            Icon(imageVector = Icons.Default.FormatQuote, contentDescription = null)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Compose Lyrics with Gemini AI", fontWeight = FontWeight.Bold)
                        }

                        // Display generated lyrics if active project has dialogue/lyrics
                        activeProject?.dialogueScript?.let { script ->
                            Spacer(modifier = Modifier.height(12.dp))
                            HorizontalDivider(color = DarkSurfaceBorder)
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "COMPOSED LYRICS:",
                                color = StudioAmberLight,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = script,
                                color = TextPrimary,
                                fontSize = 12.sp,
                                lineHeight = 18.sp
                            )
                        }
                    }
                }
            }
        }

        // Tab Content: Vocal & TTS
        if (selectedTab == 2) {
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
                            text = "TEXT-TO-SPEECH (TTS) & VOICE SCRIPT",
                            color = TextSecondary,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(
                            value = ttsText,
                            onValueChange = { ttsText = it },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = StudioAmber,
                                unfocusedBorderColor = DarkSurfaceBorder,
                                focusedTextColor = TextPrimary,
                                unfocusedTextColor = TextPrimary
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .heightIn(min = 90.dp)
                                .testTag("tts_text_input")
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        // Pitch Slider
                        Text(
                            text = "Pitch: ${(ttsPitch * 100).toInt()}%",
                            color = TextSecondary,
                            fontSize = 11.sp
                        )
                        Slider(
                            value = ttsPitch,
                            onValueChange = { ttsPitch = it },
                            valueRange = 0.5f..1.5f,
                            colors = SliderDefaults.colors(thumbColor = StudioAmber, activeTrackColor = StudioAmber)
                        )

                        // Speed Slider
                        Text(
                            text = "Speed: ${(ttsSpeed * 100).toInt()}%",
                            color = TextSecondary,
                            fontSize = 11.sp
                        )
                        Slider(
                            value = ttsSpeed,
                            onValueChange = { ttsSpeed = it },
                            valueRange = 0.5f..1.5f,
                            colors = SliderDefaults.colors(thumbColor = StudioAmber, activeTrackColor = StudioAmber)
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Button(
                            onClick = { viewModel.audioEngine.speak(ttsText, Strings.currentLanguage.code, ttsPitch, ttsSpeed) },
                            colors = ButtonDefaults.buttonColors(containerColor = StudioAmber, contentColor = Color.Black),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("speak_tts_button")
                        ) {
                            Icon(imageVector = Icons.Default.RecordVoiceOver, contentDescription = null)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Speak Line in ${Strings.currentLanguage.nativeName}", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        // Tab Content: Voice Recorder
        if (selectedTab == 3) {
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
                            .padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "DIRECTOR VOICE RECORDER",
                            color = TextSecondary,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(16.dp))

                        IconButton(
                            onClick = { viewModel.toggleVoiceRecording() },
                            modifier = Modifier
                                .size(70.dp)
                                .clip(CircleShape)
                                .background(if (isRecording) StudioRose else StudioAmber)
                                .testTag("voice_record_button")
                        ) {
                            Icon(
                                imageVector = if (isRecording) Icons.Default.Stop else Icons.Default.Mic,
                                contentDescription = if (isRecording) "Stop" else "Record",
                                tint = Color.Black,
                                modifier = Modifier.size(36.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = if (isRecording) "🔴 Recording Voiceover... Tap to stop" else "Tap microphone to record voiceover",
                            color = if (isRecording) StudioRose else TextPrimary,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 12.sp
                        )
                    }
                }
            }
        }
    }
}
