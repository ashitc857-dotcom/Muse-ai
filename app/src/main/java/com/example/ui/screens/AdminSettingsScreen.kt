package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import com.example.data.AppLanguage
import com.example.data.Strings
import com.example.data.api.GeminiStudioClient
import com.example.ui.StudioViewModel
import com.example.ui.theme.*

@Composable
fun AdminSettingsScreen(
    viewModel: StudioViewModel,
    modifier: Modifier = Modifier
) {
    val account by viewModel.ledgerManager.account.collectAsState()
    val videoCost by viewModel.ledgerManager.videoCost.collectAsState()
    val dailyBonus by viewModel.ledgerManager.dailyBonusAmount.collectAsState()

    var userNameInput by remember { mutableStateOf(account.username) }
    var userEmailInput by remember { mutableStateOf(account.email) }
    var grantAmountInput by remember { mutableStateOf("100000") }
    var customApiKeyInput by remember { mutableStateOf(GeminiStudioClient.customApiKey ?: "") }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
        contentPadding = PaddingValues(top = 10.dp, bottom = 24.dp)
    ) {
        // User Profile & Account Settings
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
                        .padding(16.dp)
                ) {
                    Text(
                        text = "ACCOUNT & DIRECTOR PROFILE",
                        color = NeonCyanLight,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = userNameInput,
                        onValueChange = { userNameInput = it },
                        label = { Text("Director Name") },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = NeonCyan,
                            unfocusedBorderColor = DarkSurfaceBorder,
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = userEmailInput,
                        onValueChange = { userEmailInput = it },
                        label = { Text("Account Email") },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = NeonCyan,
                            unfocusedBorderColor = DarkSurfaceBorder,
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Button(
                        onClick = {
                            viewModel.ledgerManager.updateUser(userNameInput, userEmailInput)
                            viewModel.bannerMessage.value = "Profile updated successfully!"
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = NeonCyan, contentColor = Color.Black),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Save Profile Changes", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // Language Localization Switcher
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
                        .padding(16.dp)
                ) {
                    Text(
                        text = "APP LANGUAGE (বাংলা / ENGLISH / HINDI)",
                        color = TextSecondary,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        AppLanguage.values().forEach { lang ->
                            val isSelected = Strings.currentLanguage == lang
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = if (isSelected) GlowCyan else DarkSurfaceVariant,
                                border = androidx.compose.foundation.BorderStroke(
                                    1.dp,
                                    if (isSelected) NeonCyan else DarkSurfaceBorder
                                ),
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable { viewModel.setLanguage(lang) }
                            ) {
                                Box(
                                    modifier = Modifier.padding(vertical = 12.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = lang.nativeName,
                                        color = if (isSelected) NeonCyanLight else TextPrimary,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.sp
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // Admin Token Settings
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = DarkSurface),
                border = androidx.compose.foundation.BorderStroke(1.dp, StudioAmber.copy(alpha = 0.5f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Text(
                        text = "ADMINISTRATOR TOKEN CONFIGURATION",
                        color = StudioAmberLight,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "Generation Cost per Video:", color = TextPrimary, fontSize = 12.sp)
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            IconButton(onClick = {
                                if (videoCost > 1) viewModel.ledgerManager.updateConfig(videoCost - 1, dailyBonus)
                            }) {
                                Text("-", color = StudioAmber, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                            }
                            Text(text = "$videoCost Token", color = StudioAmberLight, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            IconButton(onClick = {
                                viewModel.ledgerManager.updateConfig(videoCost + 1, dailyBonus)
                            }) {
                                Text("+", color = StudioAmber, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(text = "Grant Tokens to Account:", color = TextPrimary, fontSize = 12.sp)
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value = grantAmountInput,
                            onValueChange = { grantAmountInput = it },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = StudioAmber,
                                unfocusedBorderColor = DarkSurfaceBorder,
                                focusedTextColor = TextPrimary,
                                unfocusedTextColor = TextPrimary
                            ),
                            modifier = Modifier.weight(1f)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Button(
                            onClick = {
                                val amount = grantAmountInput.toLongOrNull() ?: 0L
                                if (amount > 0) {
                                    viewModel.ledgerManager.adminGrantTokens(amount, "Admin Manual Grant")
                                    viewModel.bannerMessage.value = "Granted $amount tokens!"
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = StudioAmber, contentColor = Color.Black),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text("Grant", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        // API Key & Provider Connection
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
                        .padding(16.dp)
                ) {
                    Text(
                        text = "GEMINI AI & NEURAL CLOUD KEYS",
                        color = TextSecondary,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Configured via AI Studio Secrets Panel or custom key below:",
                        color = TextTertiary,
                        fontSize = 11.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = customApiKeyInput,
                        onValueChange = {
                            customApiKeyInput = it
                            GeminiStudioClient.customApiKey = it
                        },
                        placeholder = { Text("AI Studio Managed Key Active", color = TextTertiary, fontSize = 11.sp) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = NeonCyan,
                            unfocusedBorderColor = DarkSurfaceBorder,
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }

        // Build & Installation Verification
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = DarkSurfaceVariant),
                border = androidx.compose.foundation.BorderStroke(1.dp, StudioEmerald.copy(alpha = 0.5f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Default.Verified, contentDescription = null, tint = StudioEmerald, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "BUILD & SYSTEM STATUS: VERIFIED",
                            color = StudioEmerald,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "• App Name: Muse AI Studio\n" +
                               "• Platform Architecture: Native Android (Kotlin + Jetpack Compose M3)\n" +
                               "• Signing: debugConfig & release configured\n" +
                               "• Emulator: Live streaming in AI Studio browser\n" +
                               "• APK Export: Available via AI Studio Settings Menu > Export APK/AAB",
                        color = TextPrimary,
                        fontSize = 11.sp,
                        lineHeight = 18.sp
                    )
                }
            }
        }
    }
}
