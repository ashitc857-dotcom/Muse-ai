package com.example.ui

import androidx.activity.compose.BackHandler
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.StudioBottomBar
import com.example.ui.components.StudioHeader
import com.example.ui.screens.*
import com.example.ui.theme.*

@Composable
fun StudioApp(
    viewModel: StudioViewModel,
    modifier: Modifier = Modifier
) {
    val currentScreen by viewModel.currentScreen.collectAsState()
    val bannerMessage by viewModel.bannerMessage.collectAsState()

    // Handle back button navigation
    BackHandler(enabled = currentScreen != StudioScreen.HOME) {
        viewModel.handleBack()
    }

    Scaffold(
        topBar = { StudioHeader(viewModel = viewModel) },
        bottomBar = { StudioBottomBar(viewModel = viewModel) },
        containerColor = DarkBackground,
        modifier = modifier.fillMaxSize()
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Main Content Area with Smooth Crossfade
            Crossfade(
                targetState = currentScreen,
                label = "screen_transition"
            ) { screen ->
                when (screen) {
                    StudioScreen.HOME -> HomeScreen(viewModel = viewModel)
                    StudioScreen.VIDEO_STUDIO -> VideoStudioScreen(viewModel = viewModel)
                    StudioScreen.MUSIC_STUDIO -> MusicStudioScreen(viewModel = viewModel)
                    StudioScreen.IMAGE_STUDIO -> ImageStudioScreen(viewModel = viewModel)
                    StudioScreen.SCRIPT_CHAT -> ScriptChatScreen(viewModel = viewModel)
                    StudioScreen.PROJECTS -> ProjectsScreen(viewModel = viewModel)
                    StudioScreen.TOKEN_LEDGER -> TokenLedgerScreen(viewModel = viewModel)
                    StudioScreen.SETTINGS_ADMIN -> AdminSettingsScreen(viewModel = viewModel)
                }
            }

            // Notification / Toast Banner
            bannerMessage?.let { msg ->
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = DarkSurface,
                    border = androidx.compose.foundation.BorderStroke(1.dp, NeonCyan),
                    shadowElevation = 8.dp,
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(16.dp)
                        .fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 14.dp, vertical = 10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = msg,
                            color = TextPrimary,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.weight(1f)
                        )

                        IconButton(
                            onClick = { viewModel.dismissBanner() },
                            modifier = Modifier.size(28.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Dismiss",
                                tint = TextSecondary,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}
