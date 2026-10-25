package com.example.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.RecordVoiceOver
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.Strings
import com.example.data.model.AspectRatioOption
import com.example.data.model.GenerationProject
import com.example.ui.StudioViewModel
import com.example.ui.theme.*
import java.io.File
import kotlin.math.sin

@Composable
fun AnimatedVideoPlayer(
    project: GenerationProject?,
    viewModel: StudioViewModel,
    modifier: Modifier = Modifier
) {
    var isPlaying by remember { mutableStateOf(true) }

    val infiniteTransition = rememberInfiniteTransition(label = "video_motion")
    val motionProgress by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 4000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "motion_progress"
    )

    val aspectRatio = project?.aspectRatio ?: AspectRatioOption.RATIO_16_9
    val ratioFloat = aspectRatio.widthRatio / aspectRatio.heightRatio

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = DarkSurface),
        border = androidx.compose.foundation.BorderStroke(1.dp, NeonCyan.copy(alpha = 0.3f)),
        modifier = modifier
            .fillMaxWidth()
            .testTag("video_player_card")
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(ratioFloat)
                    .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
                    .background(Color.Black),
                contentAlignment = Alignment.Center
            ) {
                // Background visual: local file or animated canvas
                val mediaFile = project?.mediaUrl?.let { File(it) }
                if (mediaFile != null && mediaFile.exists()) {
                    AsyncImage(
                        model = mediaFile,
                        contentDescription = "Project Frame",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .fillMaxSize()
                            .offset(
                                x = if (isPlaying) ((motionProgress - 0.5f) * 20).dp else 0.dp,
                                y = if (isPlaying) (sin(motionProgress * 3.14f) * 10).dp else 0.dp
                            )
                    )
                }

                // Dynamic Cinematic Motion Canvas Overlay
                Canvas(modifier = Modifier.fillMaxSize()) {
                    if (isPlaying) {
                        drawCinematicEffects(motionProgress)
                    }
                }

                // Subtitles / Dialogue Strip
                project?.dialogueScript?.let { dialogue ->
                    if (dialogue.isNotBlank()) {
                        Box(
                            modifier = Modifier
                                .align(Alignment.BottomCenter)
                                .fillMaxWidth()
                                .background(
                                    Brush.verticalGradient(
                                        colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.85f))
                                    )
                                )
                                .padding(horizontal = 12.dp, vertical = 8.dp)
                        ) {
                            Text(
                                text = dialogue,
                                color = TextPrimary,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium,
                                maxLines = 2
                            )
                        }
                    }
                }

                // Camera & Resolution Badge
                Row(
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(10.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color.Black.copy(alpha = 0.65f))
                        .padding(horizontal = 8.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(if (isPlaying) StudioRose else TextTertiary)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "${project?.resolution ?: "1080p"} • ${project?.fps ?: 60}fps",
                        color = TextPrimary,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                // Model & Aspect Ratio Badge
                Row(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(10.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(DarkSurfaceVariant.copy(alpha = 0.8f))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = project?.cameraMovement ?: "Cinematic Motion",
                        color = NeonCyanLight,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            // Playback and Audio Controls Strip
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = { isPlaying = !isPlaying },
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(NeonCyan)
                            .testTag("video_play_pause_button")
                    ) {
                        Icon(
                            imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                            contentDescription = if (isPlaying) "Pause" else "Play",
                            tint = Color.Black,
                            modifier = Modifier.size(22.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    Column {
                        Text(
                            text = project?.title ?: "Muse Studio Preview",
                            color = TextPrimary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            maxLines = 1
                        )
                        Text(
                            text = "Motion: ${if (isPlaying) "Active Dynamic" else "Paused"}",
                            color = TextSecondary,
                            fontSize = 11.sp
                        )
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Speak Dialogue Button (TTS)
                    project?.dialogueScript?.let { dialogue ->
                        IconButton(
                            onClick = { viewModel.speakDialogue(dialogue) },
                            modifier = Modifier.size(36.dp).testTag("speak_dialogue_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.RecordVoiceOver,
                                contentDescription = "Speak Dialogue",
                                tint = NeonCyan,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }

                    // Play Synthesized Audio Track
                    val audioFile = project?.audioPath?.let { File(it) }
                    if (audioFile != null && audioFile.exists()) {
                        IconButton(
                            onClick = { viewModel.toggleAudioPlayback(audioFile) },
                            modifier = Modifier.size(36.dp).testTag("play_audio_track_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.VolumeUp,
                                contentDescription = "Play Audio",
                                tint = StudioAmber,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

private fun DrawScope.drawCinematicEffects(progress: Float) {
    val w = size.width
    val h = size.height

    // Anamorphic horizontal light flare
    val flareY = h * 0.45f
    val flareX = w * (0.2f + 0.6f * progress)

    drawCircle(
        brush = Brush.radialGradient(
            colors = listOf(NeonCyan.copy(alpha = 0.35f), Color.Transparent),
            center = Offset(flareX, flareY),
            radius = 120f
        ),
        radius = 120f,
        center = Offset(flareX, flareY)
    )

    // Moving atmospheric particles
    for (i in 0..12) {
        val px = (w * ((i * 0.08f + progress * 0.3f) % 1f))
        val py = (h * ((i * 0.12f + progress * 0.2f) % 1f))
        val alpha = (0.2f + 0.3f * sin((progress + i) * 3f)).coerceIn(0.1f, 0.6f)
        drawCircle(
            color = StudioAmber.copy(alpha = alpha),
            radius = (2.5f + (i % 3)),
            center = Offset(px, py)
        )
    }
}
