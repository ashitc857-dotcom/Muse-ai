package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import com.example.data.model.StudioMediaType
import com.example.ui.StudioScreen
import com.example.ui.StudioViewModel
import com.example.ui.theme.*
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun ProjectsScreen(
    viewModel: StudioViewModel,
    modifier: Modifier = Modifier
) {
    val projects by viewModel.projectRepo.projects.collectAsState()
    var filterType by remember { mutableStateOf<StudioMediaType?>(null) }

    val filteredList = remember(projects, filterType) {
        if (filterType == null) projects else projects.filter { it.mediaType == filterType }
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(top = 10.dp, bottom = 24.dp)
    ) {
        // Title & Stats
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "PROJECT LIBRARY & DOWNLOADS",
                        color = TextPrimary,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                    Text(
                        text = "${projects.size} Creative Productions Rendered",
                        color = TextSecondary,
                        fontSize = 11.sp
                    )
                }

                TextButton(onClick = { viewModel.bannerMessage.value = "📥 Download queue up-to-date" }) {
                    Icon(imageVector = Icons.Default.FileDownloadDone, contentDescription = null, tint = NeonCyan, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Export All", color = NeonCyan, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        // Filter Chips
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterChip(
                    selected = filterType == null,
                    onClick = { filterType = null },
                    label = { Text("All (${projects.size})", fontSize = 11.sp) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = NeonCyan,
                        selectedLabelColor = Color.Black
                    )
                )

                StudioMediaType.values().forEach { type ->
                    val count = projects.count { it.mediaType == type }
                    FilterChip(
                        selected = filterType == type,
                        onClick = { filterType = if (filterType == type) null else type },
                        label = { Text("${type.label} ($count)", fontSize = 11.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = NeonCyan,
                            selectedLabelColor = Color.Black
                        )
                    )
                }
            }
        }

        // Project Items List
        items(filteredList) { project ->
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = DarkSurface),
                border = androidx.compose.foundation.BorderStroke(1.dp, DarkSurfaceBorder),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        viewModel.activeProject.value = project
                        if (project.mediaType == StudioMediaType.VIDEO) {
                            viewModel.navigateTo(StudioScreen.VIDEO_STUDIO)
                        } else if (project.mediaType == StudioMediaType.MUSIC) {
                            viewModel.navigateTo(StudioScreen.MUSIC_STUDIO)
                        } else if (project.mediaType == StudioMediaType.IMAGE) {
                            viewModel.navigateTo(StudioScreen.IMAGE_STUDIO)
                        }
                    }
                    .testTag("project_item_${project.id}")
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Thumbnail or Icon Box
                    val imgFile = project.mediaUrl?.let { File(it) }
                    if (imgFile != null && imgFile.exists()) {
                        AsyncImage(
                            model = imgFile,
                            contentDescription = null,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .size(64.dp)
                                .clip(RoundedCornerShape(10.dp))
                        )
                    } else {
                        Box(
                            modifier = Modifier
                                .size(64.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(DarkSurfaceVariant),
                            contentAlignment = Alignment.Center
                        ) {
                            val icon = when (project.mediaType) {
                                StudioMediaType.VIDEO -> Icons.Default.Movie
                                StudioMediaType.MUSIC -> Icons.Default.MusicNote
                                StudioMediaType.IMAGE -> Icons.Default.Image
                                StudioMediaType.SCRIPT -> Icons.Default.Description
                                StudioMediaType.THUMBNAIL -> Icons.Default.AspectRatio
                            }
                            Icon(imageVector = icon, contentDescription = null, tint = NeonCyan, modifier = Modifier.size(28.dp))
                        }
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = project.title,
                            color = TextPrimary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            maxLines = 1
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = project.prompt,
                            color = TextSecondary,
                            fontSize = 11.sp,
                            maxLines = 1
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "${project.mediaType.label} • ${project.resolution}",
                                color = NeonCyanLight,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = SimpleDateFormat("MMM d, HH:mm", Locale.US).format(Date(project.timestamp)),
                                color = TextTertiary,
                                fontSize = 10.sp
                            )
                        }
                    }

                    // Delete & Export
                    IconButton(
                        onClick = { viewModel.projectRepo.deleteProject(project.id) },
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(imageVector = Icons.Default.DeleteOutline, contentDescription = "Delete", tint = TextTertiary, modifier = Modifier.size(18.dp))
                    }
                }
            }
        }
    }
}
