package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.ChatBubbleOutline
import androidx.compose.material.icons.filled.FolderSpecial
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.Strings
import com.example.ui.StudioScreen
import com.example.ui.StudioViewModel
import com.example.ui.theme.*

@Composable
fun StudioBottomBar(
    viewModel: StudioViewModel,
    modifier: Modifier = Modifier
) {
    val currentScreen by viewModel.currentScreen.collectAsState()

    Surface(
        color = DarkSurface,
        tonalElevation = 8.dp,
        border = androidx.compose.foundation.BorderStroke(1.dp, DarkSurfaceBorder.copy(alpha = 0.5f)),
        modifier = modifier
            .fillMaxWidth()
            .windowInsetsPadding(WindowInsets.navigationBars)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 6.dp, horizontal = 4.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            BottomNavItem(
                icon = Icons.Default.Home,
                label = Strings.get("nav_home"),
                isSelected = currentScreen == StudioScreen.HOME,
                tag = "nav_home_button",
                onClick = { viewModel.navigateTo(StudioScreen.HOME) }
            )

            BottomNavItem(
                icon = Icons.Default.Movie,
                label = Strings.get("nav_video"),
                isSelected = currentScreen == StudioScreen.VIDEO_STUDIO,
                tag = "nav_video_button",
                onClick = { viewModel.navigateTo(StudioScreen.VIDEO_STUDIO) }
            )

            BottomNavItem(
                icon = Icons.Default.MusicNote,
                label = Strings.get("nav_music"),
                isSelected = currentScreen == StudioScreen.MUSIC_STUDIO,
                tag = "nav_music_button",
                onClick = { viewModel.navigateTo(StudioScreen.MUSIC_STUDIO) }
            )

            BottomNavItem(
                icon = Icons.Default.Image,
                label = Strings.get("nav_image"),
                isSelected = currentScreen == StudioScreen.IMAGE_STUDIO,
                tag = "nav_image_button",
                onClick = { viewModel.navigateTo(StudioScreen.IMAGE_STUDIO) }
            )

            BottomNavItem(
                icon = Icons.Default.ChatBubbleOutline,
                label = Strings.get("nav_chat"),
                isSelected = currentScreen == StudioScreen.SCRIPT_CHAT,
                tag = "nav_chat_button",
                onClick = { viewModel.navigateTo(StudioScreen.SCRIPT_CHAT) }
            )

            BottomNavItem(
                icon = Icons.Default.FolderSpecial,
                label = Strings.get("nav_projects"),
                isSelected = currentScreen == StudioScreen.PROJECTS,
                tag = "nav_projects_button",
                onClick = { viewModel.navigateTo(StudioScreen.PROJECTS) }
            )
        }
    }
}

@Composable
private fun BottomNavItem(
    icon: ImageVector,
    label: String,
    isSelected: Boolean,
    tag: String,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .minimumInteractiveComponentSize()
            .clip(RoundedCornerShape(12.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 8.dp, vertical = 4.dp)
            .testTag(tag),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(if (isSelected) GlowCyan else Color.Transparent)
                    .padding(horizontal = 10.dp, vertical = 2.dp)
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = label,
                    tint = if (isSelected) NeonCyan else TextSecondary,
                    modifier = Modifier.size(22.dp)
                )
            }
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = label,
                color = if (isSelected) NeonCyan else TextTertiary,
                fontSize = 11.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                maxLines = 1
            )
        }
    }
}
