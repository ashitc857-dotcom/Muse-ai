package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Stars
import androidx.compose.material.icons.filled.Token
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
import com.example.data.AppLanguage
import com.example.data.Strings
import com.example.ui.StudioScreen
import com.example.ui.StudioViewModel
import com.example.ui.theme.*
import java.text.NumberFormat
import java.util.Locale

@Composable
fun StudioHeader(
    viewModel: StudioViewModel,
    modifier: Modifier = Modifier
) {
    val account by viewModel.ledgerManager.account.collectAsState()
    var showLanguageDropdown by remember { mutableStateOf(false) }

    val formattedTokens = remember(account.tokenBalance) {
        NumberFormat.getNumberInstance(Locale.US).format(account.tokenBalance)
    }

    Surface(
        color = DarkSurface.copy(alpha = 0.95f),
        modifier = modifier
            .fillMaxWidth()
            .statusBarsPadding(),
        tonalElevation = 4.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Logo & Title
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clickable { viewModel.navigateTo(StudioScreen.HOME) }
                        .testTag("app_logo_title")
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(
                                Brush.linearGradient(
                                    colors = listOf(NeonCyan, ElectricViolet)
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "M",
                            color = Color.White,
                            fontWeight = FontWeight.Black,
                            fontSize = 20.sp
                        )
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    Column {
                        Text(
                            text = Strings.get("app_title"),
                            color = TextPrimary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 17.sp
                        )
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(6.dp)
                                    .clip(CircleShape)
                                    .background(StudioEmerald)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "STUDIO V2.5 PRO",
                                color = NeonCyanLight,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 10.sp
                            )
                        }
                    }
                }

                // Actions: Language Toggle, Token Badge & Settings
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Language Switcher
                    Box {
                        Surface(
                            shape = RoundedCornerShape(20.dp),
                            color = DarkSurfaceVariant,
                            border = androidx.compose.foundation.BorderStroke(1.dp, DarkSurfaceBorder),
                            modifier = Modifier
                                .clickable { showLanguageDropdown = !showLanguageDropdown }
                                .testTag("language_toggle_button")
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Language,
                                    contentDescription = "Language",
                                    tint = NeonCyan,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = Strings.currentLanguage.nativeName,
                                    color = TextPrimary,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }

                        DropdownMenu(
                            expanded = showLanguageDropdown,
                            onDismissRequest = { showLanguageDropdown = false },
                            modifier = Modifier.background(DarkSurfaceVariant)
                        ) {
                            AppLanguage.values().forEach { lang ->
                                DropdownMenuItem(
                                    text = {
                                        Text(
                                            text = "${lang.nativeName} (${lang.label})",
                                            color = if (Strings.currentLanguage == lang) NeonCyan else TextPrimary,
                                            fontWeight = if (Strings.currentLanguage == lang) FontWeight.Bold else FontWeight.Normal
                                        )
                                    },
                                    onClick = {
                                        viewModel.setLanguage(lang)
                                        showLanguageDropdown = false
                                    }
                                )
                            }
                        }
                    }

                    // Token Pill Button
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = DarkSurfaceVariant,
                        border = androidx.compose.foundation.BorderStroke(1.dp, StudioAmber.copy(alpha = 0.5f)),
                        modifier = Modifier
                            .clickable { viewModel.navigateTo(StudioScreen.TOKEN_LEDGER) }
                            .testTag("token_pill_button")
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Stars,
                                contentDescription = "Tokens",
                                tint = StudioAmber,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = formattedTokens,
                                color = StudioAmberLight,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp
                            )
                        }
                    }

                    // Admin / Settings
                    IconButton(
                        onClick = { viewModel.navigateTo(StudioScreen.SETTINGS_ADMIN) },
                        modifier = Modifier
                            .size(34.dp)
                            .testTag("admin_settings_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.AdminPanelSettings,
                            contentDescription = "Admin Settings",
                            tint = TextSecondary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }
    }
}
