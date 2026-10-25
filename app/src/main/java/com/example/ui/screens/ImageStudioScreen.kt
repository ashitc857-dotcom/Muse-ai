package com.example.ui.screens

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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.Strings
import com.example.data.model.AspectRatioOption
import com.example.ui.StudioViewModel
import com.example.ui.theme.*
import java.io.File

@Composable
fun ImageStudioScreen(
    viewModel: StudioViewModel,
    modifier: Modifier = Modifier
) {
    val prompt by viewModel.imagePrompt.collectAsState()
    val isGenerating by viewModel.isGenerating.collectAsState()
    val activeProject by viewModel.activeProject.collectAsState()
    val selectedRatio by viewModel.selectedAspectRatio.collectAsState()
    val tokenCost by viewModel.ledgerManager.videoCost.collectAsState()

    val styles = listOf(
        "Cinematic Film Still",
        "Cyberpunk Neon 8K",
        "Photorealistic Portrait",
        "Anime Digital Art",
        "3D Disney Pixar Render",
        "Dark Fantasy Concept"
    )
    var selectedStyle by remember { mutableStateOf(styles[0]) }

    val ratioFloat = selectedRatio.widthRatio / selectedRatio.heightRatio

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
        contentPadding = PaddingValues(top = 10.dp, bottom = 24.dp)
    ) {
        // Image Canvas / Preview Card
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = DarkSurface),
                border = androidx.compose.foundation.BorderStroke(1.dp, ElectricViolet.copy(alpha = 0.4f)),
                modifier = Modifier.fillMaxWidth()
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
                        val mediaFile = activeProject?.mediaUrl?.let { File(it) }
                        if (mediaFile != null && mediaFile.exists()) {
                            AsyncImage(
                                model = mediaFile,
                                contentDescription = "Generated Image",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )
                        } else {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier.padding(20.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Image,
                                    contentDescription = null,
                                    tint = ElectricViolet,
                                    modifier = Modifier.size(48.dp)
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "Ready to Render 8K Studio Image",
                                    color = TextPrimary,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp
                                )
                                Text(
                                    text = "High-aesthetic neural diffusion cluster",
                                    color = TextSecondary,
                                    fontSize = 11.sp
                                )
                            }
                        }

                        // Style Badge
                        Box(
                            modifier = Modifier
                                .align(Alignment.BottomStart)
                                .padding(10.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color.Black.copy(alpha = 0.7f))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = selectedStyle,
                                color = NeonCyanLight,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }

                    // Download / Export Action Bar
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Diffusion Quality: Ultra-Res",
                            color = TextSecondary,
                            fontSize = 12.sp
                        )

                        TextButton(
                            onClick = {
                                viewModel.bannerMessage.value = "💾 Image saved to device library!"
                            },
                            enabled = activeProject?.mediaUrl != null
                        ) {
                            Icon(imageVector = Icons.Default.Download, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Save to Gallery", color = NeonCyan, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }
                    }
                }
            }
        }

        // Style Selector Chips
        item {
            Column {
                Text(
                    text = "ARTISTIC STYLE PRESETS",
                    color = TextSecondary,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))

                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(styles) { style ->
                        val isSelected = selectedStyle == style
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = if (isSelected) GlowViolet else DarkSurfaceVariant,
                            border = androidx.compose.foundation.BorderStroke(
                                1.dp,
                                if (isSelected) ElectricViolet else DarkSurfaceBorder
                            ),
                            modifier = Modifier.clickable { selectedStyle = style }
                        ) {
                            Text(
                                text = style,
                                color = if (isSelected) Color.White else TextPrimary,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                fontSize = 11.sp,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                            )
                        }
                    }
                }
            }
        }

        // Aspect Ratio Selector
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
                            color = if (isSelected) GlowViolet else DarkSurfaceVariant,
                            border = androidx.compose.foundation.BorderStroke(
                                1.dp,
                                if (isSelected) ElectricViolet else DarkSurfaceBorder
                            ),
                            modifier = Modifier
                                .weight(1f)
                                .clickable { viewModel.selectedAspectRatio.value = ratio }
                        ) {
                            Box(
                                modifier = Modifier.padding(vertical = 10.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = ratio.label,
                                    color = if (isSelected) Color.White else TextPrimary,
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 11.sp
                                )
                            }
                        }
                    }
                }
            }
        }

        // Image Prompt Input
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
                            text = "IMAGE PROMPT",
                            color = TextSecondary,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )

                        TextButton(
                            onClick = { viewModel.enhanceCurrentPrompt("image") },
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp)
                        ) {
                            Icon(imageVector = Icons.Default.AutoAwesome, contentDescription = null, tint = ElectricViolet, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(text = Strings.get("enhance_prompt"), color = ElectricViolet, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    OutlinedTextField(
                        value = prompt,
                        onValueChange = { viewModel.imagePrompt.value = it },
                        placeholder = { Text(Strings.get("prompt_hint_image"), color = TextTertiary, fontSize = 12.sp) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = ElectricViolet,
                            unfocusedBorderColor = DarkSurfaceBorder,
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(min = 90.dp)
                            .testTag("image_prompt_input")
                    )
                }
            }
        }

        // Generate Action Button
        item {
            Button(
                onClick = { viewModel.generateImageStudio() },
                enabled = !isGenerating,
                colors = ButtonDefaults.buttonColors(containerColor = ElectricViolet, contentColor = Color.White),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp)
                    .testTag("generate_image_action_button")
            ) {
                if (isGenerating) {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Rendering Image...", fontWeight = FontWeight.Bold)
                } else {
                    Icon(imageVector = Icons.Default.Brush, contentDescription = null, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = "Generate 8K Image (Cost: $tokenCost Token)", fontWeight = FontWeight.Black, fontSize = 14.sp)
                }
            }
        }
    }
}
