package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.Strings
import com.example.data.model.AspectRatioOption
import com.example.data.model.ModelTier
import com.example.ui.StudioViewModel
import com.example.ui.components.AnimatedVideoPlayer
import com.example.ui.theme.*

@Composable
fun VideoStudioScreen(
    viewModel: StudioViewModel,
    modifier: Modifier = Modifier
) {
    val prompt by viewModel.videoPrompt.collectAsState()
    val characterName by viewModel.characterName.collectAsState()
    val dialogueInput by viewModel.dialogueInput.collectAsState()
    val selectedTier by viewModel.selectedModelTier.collectAsState()
    val selectedRatio by viewModel.selectedAspectRatio.collectAsState()
    val selectedDuration by viewModel.selectedDuration.collectAsState()
    val selectedResolution by viewModel.selectedResolution.collectAsState()
    val selectedFps by viewModel.selectedFps.collectAsState()
    val selectedCamera by viewModel.selectedCameraMovement.collectAsState()

    val isGenerating by viewModel.isGenerating.collectAsState()
    val generationProgress by viewModel.generationProgress.collectAsState()
    val generationStatus by viewModel.generationStatus.collectAsState()
    val activeProject by viewModel.activeProject.collectAsState()
    val tokenCost by viewModel.ledgerManager.videoCost.collectAsState()

    var selectedModeTab by remember { mutableStateOf(0) }
    val modeTabs = listOf("Text-to-Video", "Image-to-Video", "Video-to-Video")

    val durationOptions = listOf(5, 10, 15, 30, 60, 300, 3600)
    val resolutionOptions = listOf("720p HD", "1080p FHD", "4K UHD")
    val fpsOptions = listOf(24, 30, 60)
    val cameraMovements = listOf("Cinematic Dolly Zoom", "Orbit 360°", "Drone Crane Dive", "Slow Pan Right", "Static Wide")

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
        contentPadding = PaddingValues(top = 10.dp, bottom = 24.dp)
    ) {
        // Mode Tabs
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(DarkSurfaceVariant)
                    .padding(4.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                modeTabs.forEachIndexed { index, title ->
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (selectedModeTab == index) NeonCyan else Color.Transparent)
                            .clickable { selectedModeTab = index }
                            .padding(vertical = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = title,
                            color = if (selectedModeTab == index) Color.Black else TextSecondary,
                            fontWeight = if (selectedModeTab == index) FontWeight.Bold else FontWeight.Normal,
                            fontSize = 11.sp
                        )
                    }
                }
            }
        }

        // Live Preview Player
        item {
            AnimatedVideoPlayer(project = activeProject, viewModel = viewModel)
        }

        // Generation Progress Overlay if active
        if (isGenerating) {
            item {
                Card(
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = DarkSurface),
                    border = androidx.compose.foundation.BorderStroke(1.dp, NeonCyan),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = generationStatus,
                                color = NeonCyanLight,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "${(generationProgress * 100).toInt()}%",
                                color = TextPrimary,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        LinearProgressIndicator(
                            progress = { generationProgress },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(6.dp)
                                .clip(RoundedCornerShape(3.dp)),
                            color = NeonCyan,
                            trackColor = DarkSurfaceVariant
                        )
                    }
                }
            }
        }

        // Prompt Input Field with Enhance Button
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
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "SCENE PROMPT",
                            color = TextSecondary,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )

                        // Enhance with Gemini Button
                        TextButton(
                            onClick = { viewModel.enhanceCurrentPrompt("video") },
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                            modifier = Modifier.testTag("enhance_prompt_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.AutoAwesome,
                                contentDescription = null,
                                tint = NeonCyan,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = Strings.get("enhance_prompt"),
                                color = NeonCyan,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    OutlinedTextField(
                        value = prompt,
                        onValueChange = { viewModel.videoPrompt.value = it },
                        placeholder = {
                            Text(
                                text = Strings.get("prompt_hint_video"),
                                color = TextTertiary,
                                fontSize = 12.sp
                            )
                        },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = NeonCyan,
                            unfocusedBorderColor = DarkSurfaceBorder,
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(min = 90.dp)
                            .testTag("video_prompt_input")
                    )
                }
            }
        }

        // Spoken Dialogue & Character Voiceover Input
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
                        text = "CHARACTER DIALOGUE & SPEECH SYNTHESIS",
                        color = TextSecondary,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = characterName,
                            onValueChange = { viewModel.characterName.value = it },
                            label = { Text("Character", fontSize = 10.sp) },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = NeonCyan,
                                unfocusedBorderColor = DarkSurfaceBorder,
                                focusedTextColor = TextPrimary,
                                unfocusedTextColor = TextPrimary
                            ),
                            modifier = Modifier
                                .weight(0.35f)
                                .testTag("character_name_input")
                        )

                        OutlinedTextField(
                            value = dialogueInput,
                            onValueChange = { viewModel.dialogueInput.value = it },
                            label = { Text("Spoken Dialogue Line", fontSize = 10.sp) },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = NeonCyan,
                                unfocusedBorderColor = DarkSurfaceBorder,
                                focusedTextColor = TextPrimary,
                                unfocusedTextColor = TextPrimary
                            ),
                            modifier = Modifier
                                .weight(0.65f)
                                .testTag("dialogue_line_input")
                        )
                    }
                }
            }
        }

        // Model Tier Selector (Fast, Pro, Advanced, Expert, Enterprise)
        item {
            Column {
                Text(
                    text = Strings.get("model_tier").uppercase(),
                    color = TextSecondary,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))

                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(ModelTier.values()) { tier ->
                        val isSelected = selectedTier == tier
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = if (isSelected) GlowCyan else DarkSurfaceVariant,
                            border = androidx.compose.foundation.BorderStroke(
                                1.dp,
                                if (isSelected) NeonCyan else DarkSurfaceBorder
                            ),
                            modifier = Modifier
                                .clickable { viewModel.selectedModelTier.value = tier }
                                .testTag("tier_${tier.name}")
                        ) {
                            Column(modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)) {
                                Text(
                                    text = tier.displayName,
                                    color = if (isSelected) NeonCyanLight else TextPrimary,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp
                                )
                                Text(
                                    text = tier.speed,
                                    color = TextSecondary,
                                    fontSize = 10.sp
                                )
                            }
                        }
                    }
                }
            }
        }

        // Aspect Ratio Selector (16:9, 9:16, 1:1)
        item {
            Column {
                Text(
                    text = Strings.get("aspect_ratio").uppercase(),
                    color = TextSecondary,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    AspectRatioOption.values().forEach { ratio ->
                        val isSelected = selectedRatio == ratio
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = if (isSelected) GlowCyan else DarkSurfaceVariant,
                            border = androidx.compose.foundation.BorderStroke(
                                1.dp,
                                if (isSelected) NeonCyan else DarkSurfaceBorder
                            ),
                            modifier = Modifier
                                .weight(1f)
                                .clickable { viewModel.selectedAspectRatio.value = ratio }
                                .testTag("ratio_${ratio.name}")
                        ) {
                            Box(
                                modifier = Modifier.padding(vertical = 10.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = ratio.label,
                                    color = if (isSelected) NeonCyanLight else TextPrimary,
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 11.sp
                                )
                            }
                        }
                    }
                }
            }
        }

        // Camera Motion & Resolution Options
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Resolution Selector
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "RESOLUTION",
                        color = TextSecondary,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = DarkSurfaceVariant,
                        border = androidx.compose.foundation.BorderStroke(1.dp, DarkSurfaceBorder),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(4.dp),
                            horizontalArrangement = Arrangement.SpaceAround
                        ) {
                            listOf("1080p", "4K").forEach { res ->
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(if (selectedResolution.startsWith(res)) NeonCyan else Color.Transparent)
                                        .clickable { viewModel.selectedResolution.value = res }
                                        .padding(horizontal = 14.dp, vertical = 6.dp)
                                ) {
                                    Text(
                                        text = res,
                                        color = if (selectedResolution.startsWith(res)) Color.Black else TextPrimary,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 11.sp
                                    )
                                }
                            }
                        }
                    }
                }

                // Frame Rate Selector
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "FPS",
                        color = TextSecondary,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = DarkSurfaceVariant,
                        border = androidx.compose.foundation.BorderStroke(1.dp, DarkSurfaceBorder),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(4.dp),
                            horizontalArrangement = Arrangement.SpaceAround
                        ) {
                            listOf(30, 60).forEach { fps ->
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(if (selectedFps == fps) NeonCyan else Color.Transparent)
                                        .clickable { viewModel.selectedFps.value = fps }
                                        .padding(horizontal = 14.dp, vertical = 6.dp)
                                ) {
                                    Text(
                                        text = "${fps}fps",
                                        color = if (selectedFps == fps) Color.Black else TextPrimary,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 11.sp
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // Camera Motion Selector Chips
        item {
            Column {
                Text(
                    text = "CINEMATIC CAMERA MOVEMENT",
                    color = TextSecondary,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(6.dp))
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(cameraMovements) { motion ->
                        val isSelected = selectedCamera == motion
                        Surface(
                            shape = RoundedCornerShape(20.dp),
                            color = if (isSelected) GlowCyan else DarkSurfaceVariant,
                            border = androidx.compose.foundation.BorderStroke(
                                1.dp,
                                if (isSelected) NeonCyan else DarkSurfaceBorder
                            ),
                            modifier = Modifier.clickable { viewModel.selectedCameraMovement.value = motion }
                        ) {
                            Text(
                                text = motion,
                                color = if (isSelected) NeonCyanLight else TextPrimary,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Medium,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                            )
                        }
                    }
                }
            }
        }

        // Prominent Create / Generate Action Button
        item {
            Button(
                onClick = { viewModel.generateVideo() },
                enabled = !isGenerating,
                colors = ButtonDefaults.buttonColors(
                    containerColor = NeonCyan,
                    contentColor = Color.Black
                ),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp)
                    .testTag("generate_video_action_button")
            ) {
                if (isGenerating) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = Color.Black,
                        strokeWidth = 2.dp
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = Strings.get("generating"),
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.Videocam,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Render AI Video (Cost: $tokenCost Token)",
                        fontWeight = FontWeight.Black,
                        fontSize = 14.sp
                    )
                }
            }
        }
    }
}
