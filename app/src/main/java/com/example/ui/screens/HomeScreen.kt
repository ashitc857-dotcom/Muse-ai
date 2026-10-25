package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.Strings
import com.example.ui.StudioScreen
import com.example.ui.StudioViewModel
import com.example.ui.components.AnimatedVideoPlayer
import com.example.ui.theme.*
import java.text.NumberFormat
import java.util.Locale

@Composable
fun HomeScreen(
    viewModel: StudioViewModel,
    modifier: Modifier = Modifier
) {
    val account by viewModel.ledgerManager.account.collectAsState()
    val activeProject by viewModel.activeProject.collectAsState()
    val canClaimBonus = viewModel.ledgerManager.canClaimDailyBonus()

    val formattedTokens = remember(account.tokenBalance) {
        NumberFormat.getNumberInstance(Locale.US).format(account.tokenBalance)
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(top = 12.dp, bottom = 24.dp)
    ) {
        // Hero Token Balance & Daily Reward Card
        item {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = DarkSurface),
                border = androidx.compose.foundation.BorderStroke(
                    1.dp,
                    Brush.horizontalGradient(listOf(StudioAmber, ElectricViolet))
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("token_balance_hero_card")
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = Strings.get("tokens").uppercase(),
                                color = TextSecondary,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.sp
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = formattedTokens,
                                color = StudioAmberLight,
                                fontSize = 28.sp,
                                fontWeight = FontWeight.Black
                            )
                        }

                        // Daily Bonus Claim Button
                        Button(
                            onClick = { viewModel.claimDailyBonus() },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (canClaimBonus) StudioAmber else DarkSurfaceVariant,
                                contentColor = if (canClaimBonus) Color.Black else TextSecondary
                            ),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.testTag("claim_daily_bonus_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.CardGiftcard,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (canClaimBonus) "+10,000" else Strings.get("claimed"),
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                    HorizontalDivider(color = DarkSurfaceBorder.copy(alpha = 0.5f))
                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Production Cost: 1 Token / Video • Auto-Refund on Error",
                            color = NeonCyanLight,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium
                        )
                        Text(
                            text = "Ledger Active",
                            color = StudioEmerald,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        // Live Active Showcase Player
        item {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "STUDIO MOTION PREVIEW",
                        color = TextPrimary,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )

                    Text(
                        text = "1080p • 60 FPS",
                        color = NeonCyan,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))
                AnimatedVideoPlayer(project = activeProject, viewModel = viewModel)
            }
        }

        // Quick Launchpad Grid
        item {
            Text(
                text = "CREATIVE ENGINES",
                color = TextPrimary,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )
            Spacer(modifier = Modifier.height(10.dp))

            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    StudioEngineCard(
                        title = Strings.get("generate_video"),
                        subtitle = "Text / Image to Cinematic 4K",
                        icon = Icons.Default.MovieFilter,
                        accentColor = NeonCyan,
                        modifier = Modifier.weight(1f),
                        tag = "launch_video_engine",
                        onClick = { viewModel.navigateTo(StudioScreen.VIDEO_STUDIO) }
                    )

                    StudioEngineCard(
                        title = Strings.get("nav_music"),
                        subtitle = "AI Song, WAV Synth & TTS",
                        icon = Icons.Default.GraphicEq,
                        accentColor = StudioAmber,
                        modifier = Modifier.weight(1f),
                        tag = "launch_music_engine",
                        onClick = { viewModel.navigateTo(StudioScreen.MUSIC_STUDIO) }
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    StudioEngineCard(
                        title = Strings.get("nav_image"),
                        subtitle = "Diffusion & 8K Thumbnails",
                        icon = Icons.Default.Brush,
                        accentColor = ElectricViolet,
                        modifier = Modifier.weight(1f),
                        tag = "launch_image_engine",
                        onClick = { viewModel.navigateTo(StudioScreen.IMAGE_STUDIO) }
                    )

                    StudioEngineCard(
                        title = Strings.get("nav_chat"),
                        subtitle = "Screenplay, Dialogue & Voice",
                        icon = Icons.Default.SmartToy,
                        accentColor = StudioEmerald,
                        modifier = Modifier.weight(1f),
                        tag = "launch_agent_engine",
                        onClick = { viewModel.navigateTo(StudioScreen.SCRIPT_CHAT) }
                    )
                }
            }
        }

        // Live Provider & Backend Status Badge
        item {
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = DarkSurfaceVariant),
                border = androidx.compose.foundation.BorderStroke(1.dp, DarkSurfaceBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .clip(CircleShape)
                            .background(StudioEmerald)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = Strings.get("demo_vs_live"),
                            color = TextPrimary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                        Text(
                            text = "Gemini 3.5 Flash • Pollinations Diffusion • Procedural WAV Audio Synth • Android TTS",
                            color = TextSecondary,
                            fontSize = 10.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun StudioEngineCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    accentColor: Color,
    modifier: Modifier = Modifier,
    tag: String,
    onClick: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = DarkSurface),
        border = androidx.compose.foundation.BorderStroke(1.dp, DarkSurfaceBorder),
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .clickable(onClick = onClick)
            .testTag(tag)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(accentColor.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = accentColor,
                    modifier = Modifier.size(20.dp)
                )
            }
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = title,
                color = TextPrimary,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 1
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = subtitle,
                color = TextSecondary,
                fontSize = 10.sp,
                maxLines = 1
            )
        }
    }
}
