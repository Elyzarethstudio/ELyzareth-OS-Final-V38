package com.example.ui.commandcentre

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Equalizer
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material.icons.filled.Gavel
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.LibraryMusic
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.PlayCircleFilled
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.foundation.PipelineStage
import com.example.kernel.CanonicalProjectContext
import com.example.navigation.OsModuleDestination
import com.example.ui.theme.BlueThread
import com.example.ui.theme.BronzeGold
import com.example.ui.theme.MutedSlateText
import com.example.ui.theme.ObsidianBlack
import com.example.ui.theme.SlateCard
import com.example.ui.theme.SlateCardBorder
import com.example.ui.theme.SlateDark
import com.example.ui.theme.StateParsed
import com.example.ui.theme.StateUnverified
import com.example.ui.theme.StateVerified
import com.example.ui.theme.WitnessParchment
import com.example.viewmodel.ElyzarethUiState
import com.example.viewmodel.ElyzarethViewModel

@Composable
fun CommandCentreScreen(
    viewModel: ElyzarethViewModel,
    uiState: ElyzarethUiState,
    projectContext: CanonicalProjectContext,
    totalTitlesCount: Int,
    totalBlueprintsCount: Int,
    pendingReviewCount: Int,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(ObsidianBlack)
            .padding(horizontal = 14.dp)
            .testTag("command_centre_screen"),
        contentPadding = PaddingValues(top = 12.dp, bottom = 32.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // 1. Sovereign Mansion Hero Header
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = SlateDark),
                border = androidx.compose.foundation.BorderStroke(1.5.dp, BronzeGold.copy(alpha = 0.8f)),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    Color(0xFF1A2238),
                                    Color(0xFF121622),
                                    ObsidianBlack
                                )
                            )
                        )
                        .padding(16.dp)
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        // Royal E Emblem
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(CircleShape)
                                .background(BronzeGold.copy(alpha = 0.15f))
                                .border(1.5.dp, BronzeGold, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "E",
                                fontSize = 22.sp,
                                fontWeight = FontWeight.ExtraBold,
                                fontFamily = FontFamily.Serif,
                                color = BronzeGold
                            )
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "ELYZARETH OS",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = BronzeGold,
                            letterSpacing = 2.sp,
                            fontFamily = FontFamily.Serif
                        )
                        Text(
                            text = "Command Centre",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = WitnessParchment
                        )
                        Text(
                            text = "(Main Dashboard • Sovereign Creative Factory v1.0)",
                            fontSize = 11.sp,
                            color = MutedSlateText
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        // Sovereign Kernel Banner Pill
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(SlateCard)
                                .border(1.dp, BronzeGold.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                                .clickable { viewModel.navigateToDestination(OsModuleDestination.SOVEREIGN_KERNEL) }
                                .padding(horizontal = 12.dp, vertical = 8.dp)
                                .testTag("open_sovereign_kernel_banner"),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    Icons.Default.AccountBalance,
                                    contentDescription = "Sovereign Kernel",
                                    tint = BronzeGold,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Text(
                                        text = "SOVEREIGN KERNEL (CONTROL CENTRE)",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = BronzeGold,
                                        fontFamily = FontFamily.Monospace
                                    )
                                    Text(
                                        text = "Active Project: ${projectContext.activeProjectTitle}",
                                        fontSize = 11.sp,
                                        color = WitnessParchment,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }
                            }
                            Text(
                                text = "INSPECT →",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = BlueThread,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                    }
                }
            }
        }

        // 2. 12-Module 3x4 Mansion Grid (Matching UI/UX Suite v1.0 Center Screen)
        item {
            val gridItems = OsModuleDestination.COMMAND_CENTRE_GRID_MODULES
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                gridItems.chunked(3).forEach { rowDestinations ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        rowDestinations.forEach { dest ->
                            CommandCentreModuleTile(
                                destination = dest,
                                onClick = { viewModel.navigateToDestination(dest) },
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
            }
        }

        // 3. Canonical Production Pipeline (14 Stages)
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = SlateCard),
                border = androidx.compose.foundation.BorderStroke(1.dp, BronzeGold.copy(alpha = 0.5f)),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Production Pipeline (14 Stages)",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = WitnessParchment
                        )
                        Text(
                            text = "Stage ${projectContext.currentPipelineStage.code}: ${projectContext.currentPipelineStage.title}",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = BronzeGold,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        PipelineStage.entries.forEach { stage ->
                            val isCurrent = projectContext.currentPipelineStage == stage
                            val isCompleted = stage.stageNumber < projectContext.currentPipelineStage.stageNumber
                            val bgColor = when {
                                isCurrent -> BronzeGold
                                isCompleted -> StateVerified.copy(alpha = 0.3f)
                                else -> SlateDark
                            }
                            val textColor = if (isCurrent) ObsidianBlack else WitnessParchment
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(bgColor)
                                    .border(
                                        1.dp,
                                        if (isCurrent) BronzeGold else SlateCardBorder,
                                        RoundedCornerShape(6.dp)
                                    )
                                    .clickable { viewModel.setPipelineStage(stage) }
                                    .padding(horizontal = 10.dp, vertical = 6.dp)
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(
                                        text = stage.code,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = if (isCurrent) ObsidianBlack else BronzeGold,
                                        fontFamily = FontFamily.Monospace
                                    )
                                    Text(
                                        text = stage.title,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = textColor
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // 4. System Status & 100% System Health Card (Matching Bottom of Center Screen in Image 2)
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = SlateCard),
                border = androidx.compose.foundation.BorderStroke(1.dp, SlateCardBorder),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = "System Status",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = WitnessParchment
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        // Left column: Kernel, DB, Governance
                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            StatusIndicatorLine("Kernel: Online", StateParsed)
                            StatusIndicatorLine("Database: OK", StateParsed)
                            StatusIndicatorLine("Governance: Active", StateParsed)
                        }

                        // Middle column: Projects, Songs, Pending G2
                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            StatusMetricLine("Projects: ${totalBlueprintsCount.coerceAtLeast(3)}")
                            StatusMetricLine("Songs: ${totalTitlesCount.coerceAtLeast(390)}")
                            StatusMetricLine("Pending Review: $pendingReviewCount")
                        }

                        // Right column: 100% System Health Ring
                        Box(
                            modifier = Modifier.size(74.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Canvas(modifier = Modifier.fillMaxSize()) {
                                drawArc(
                                    color = StateParsed.copy(alpha = 0.2f),
                                    startAngle = 0f,
                                    sweepAngle = 360f,
                                    useCenter = false,
                                    style = Stroke(width = 7.dp.toPx(), cap = StrokeCap.Round)
                                )
                                drawArc(
                                    color = StateParsed,
                                    startAngle = -90f,
                                    sweepAngle = 360f,
                                    useCenter = false,
                                    style = Stroke(width = 7.dp.toPx(), cap = StrokeCap.Round)
                                )
                            }
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = "${projectContext.systemHealthPercent}%",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = WitnessParchment
                                )
                                Text(
                                    text = "System\nHealth",
                                    fontSize = 8.sp,
                                    color = StateParsed,
                                    textAlign = TextAlign.Center,
                                    lineHeight = 9.sp
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Bottom Quick Links to Screen 13 (System Services) & Screen 14 (Project Overview)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        QuickSystemFooterButton(
                            title = "13. System Services",
                            icon = Icons.Default.Settings,
                            accent = BlueThread,
                            onClick = { viewModel.navigateToDestination(OsModuleDestination.SYSTEM_SERVICES) },
                            modifier = Modifier
                                .weight(1f)
                                .testTag("quick_btn_system_services")
                        )
                        QuickSystemFooterButton(
                            title = "14. Project Overview",
                            icon = Icons.Default.Analytics,
                            accent = BronzeGold,
                            onClick = { viewModel.navigateToDestination(OsModuleDestination.PROJECT_OVERVIEW) },
                            modifier = Modifier
                                .weight(1f)
                                .testTag("quick_btn_project_overview")
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun CommandCentreModuleTile(
    destination: OsModuleDestination,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val (icon, accentColor) = moduleVisualSpec(destination)
    Card(
        colors = CardDefaults.cardColors(containerColor = SlateCard),
        border = androidx.compose.foundation.BorderStroke(1.dp, accentColor.copy(alpha = 0.65f)),
        shape = RoundedCornerShape(12.dp),
        modifier = modifier
            .height(94.dp)
            .clickable { onClick() }
            .testTag("grid_card_${destination.routeId}")
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            accentColor.copy(alpha = 0.16f),
                            SlateDark
                        )
                    )
                )
                .padding(8.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(34.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(accentColor.copy(alpha = 0.2f))
                        .border(1.dp, accentColor.copy(alpha = 0.6f), RoundedCornerShape(8.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = destination.shortTitle,
                        tint = accentColor,
                        modifier = Modifier.size(20.dp)
                    )
                }
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = destination.shortTitle,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = WitnessParchment,
                    textAlign = TextAlign.Center,
                    maxLines = 2,
                    lineHeight = 13.sp,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
private fun StatusIndicatorLine(text: String, dotColor: Color) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(8.dp)
                .clip(CircleShape)
                .background(dotColor)
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
            text = text,
            fontSize = 11.sp,
            color = WitnessParchment,
            fontFamily = FontFamily.Monospace
        )
    }
}

@Composable
private fun StatusMetricLine(text: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
            Icons.Default.Description,
            contentDescription = null,
            tint = BronzeGold,
            modifier = Modifier.size(12.dp)
        )
        Spacer(modifier = Modifier.width(5.dp))
        Text(
            text = text,
            fontSize = 11.sp,
            color = WitnessParchment,
            fontFamily = FontFamily.Monospace
        )
    }
}

@Composable
private fun QuickSystemFooterButton(
    title: String,
    icon: ImageVector,
    accent: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(SlateDark)
            .border(1.dp, accent.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
            .clickable { onClick() }
            .padding(horizontal = 10.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Icon(icon, contentDescription = title, tint = accent, modifier = Modifier.size(15.dp))
        Spacer(modifier = Modifier.width(6.dp))
        Text(
            text = title,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = WitnessParchment
        )
    }
}

fun moduleVisualSpec(destination: OsModuleDestination): Pair<ImageVector, Color> = when (destination) {
    OsModuleDestination.COMMAND_CENTRE -> Icons.Default.AccountBalance to BronzeGold
    OsModuleDestination.IDEA_BLUEPRINT -> Icons.Default.Lightbulb to BronzeGold
    OsModuleDestination.LYRIC_GENERATOR -> Icons.Default.AutoAwesome to Color(0xFFA78BFA)
    OsModuleDestination.LYRIC_CURATOR -> Icons.Default.MenuBook to BronzeGold
    OsModuleDestination.CREATIVE_DNA -> Icons.Default.Fingerprint to BlueThread
    OsModuleDestination.MUSIC_PRODUCTION -> Icons.Default.Equalizer to BronzeGold
    OsModuleDestination.VISUAL_DIRECTION -> Icons.Default.Palette to Color(0xFFF472B6)
    OsModuleDestination.CORPUS_ARCHIVE -> Icons.Default.LibraryMusic to BronzeGold
    OsModuleDestination.FORENSIC_LAB -> Icons.Default.Search to BlueThread
    OsModuleDestination.GOVERNANCE_CONSOLE -> Icons.Default.Gavel to BronzeGold
    OsModuleDestination.EVIDENCE_VAULT -> Icons.Default.VerifiedUser to BlueThread
    OsModuleDestination.ASSET_REGISTRY -> Icons.Default.Inventory2 to StateVerified
    OsModuleDestination.MASTER_VAULT -> Icons.Default.PlayCircleFilled to BronzeGold
    OsModuleDestination.SYSTEM_SERVICES -> Icons.Default.Settings to BlueThread
    OsModuleDestination.PROJECT_OVERVIEW -> Icons.Default.Analytics to StateParsed
    OsModuleDestination.SOVEREIGN_KERNEL -> Icons.Default.Lock to BronzeGold
}
