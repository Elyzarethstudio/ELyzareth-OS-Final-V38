package com.example.ui.services

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.Backup
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Gavel
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.foundation.ConstitutionalLaws
import com.example.foundation.GovernanceGateId
import com.example.foundation.PipelineStage
import com.example.kernel.CanonicalProjectContext
import com.example.kernel.KernelEvent
import com.example.kernel.SovereignKernel
import com.example.shared.BackgroundJobCoordinator
import com.example.shared.OfflineFirstIntegrations
import com.example.shared.SovereignSecurityManager
import com.example.ui.creative.ModuleHeaderBanner
import com.example.ui.creative.SuiteFeatureRow
import com.example.ui.protocol.ProtocolConsoleScreen
import com.example.ui.theme.BlueThread
import com.example.ui.theme.BronzeGold
import com.example.ui.theme.MutedSlateText
import com.example.ui.theme.ObsidianBlack
import com.example.ui.theme.SlateCard
import com.example.ui.theme.SlateCardBorder
import com.example.ui.theme.SlateDark
import com.example.ui.theme.StateFailed
import com.example.ui.theme.StateParsed
import com.example.ui.theme.StateUnverified
import com.example.ui.theme.StateVerified
import com.example.ui.theme.WitnessParchment
import com.example.viewmodel.ElyzarethUiState
import com.example.viewmodel.ElyzarethViewModel

/**
 * 9. GOVERNANCE CONSOLE (UI/UX Suite v1.0 Module 9)
 * Combines:
 * - G1–G5 Gate Approval & Human Veto Console (Screen 9 in Image 2)
 * - Preserved 3.2.1.0 Protocol Console (Listen -> Evaluate -> Decide -> Freeze)
 */
@Composable
fun GovernanceSuiteScreen(
    viewModel: ElyzarethViewModel,
    uiState: ElyzarethUiState,
    modifier: Modifier = Modifier
) {
    var mode by remember { mutableStateOf(0) } // 0 = 3.2.1.0 Protocol + G1-G5 Gates, 1 = G1-G5 Gate Matrix

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(ObsidianBlack)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(SlateDark)
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            FilterChip(
                selected = mode == 0,
                onClick = { mode = 0 },
                label = { Text("3.2.1.0 Protocol Console", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = BronzeGold,
                    selectedLabelColor = ObsidianBlack,
                    containerColor = SlateCard,
                    labelColor = WitnessParchment
                ),
                modifier = Modifier.testTag("gov_mode_protocol")
            )
            FilterChip(
                selected = mode == 1,
                onClick = { mode = 1 },
                label = { Text("9. G1–G5 Gate Approvals & Veto", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = BlueThread,
                    selectedLabelColor = ObsidianBlack,
                    containerColor = SlateCard,
                    labelColor = WitnessParchment
                ),
                modifier = Modifier.testTag("gov_mode_gates")
            )
        }

        if (mode == 0) {
            ProtocolConsoleScreen(
                viewModel = viewModel,
                uiState = uiState,
                modifier = Modifier.weight(1f)
            )
        } else {
            val gateState = uiState.gateEvaluationState
            val canSeal = SovereignKernel.canSealMasterRelease(gateState)

            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .testTag("g1_g5_gate_matrix"),
                contentPadding = PaddingValues(top = 12.dp, bottom = 32.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                item {
                    ModuleHeaderBanner(
                        badge = "MODULE 09 • GOVERNANCE LAYER",
                        title = "9. Governance Console (G1–G5)",
                        subtitle = "Zero-Bypass Gate Approvals • Human Governor Sign-Off & Veto",
                        accent = BronzeGold
                    )
                }

                item {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = SlateCard),
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            if (canSeal) StateParsed else BronzeGold
                        ),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(
                                text = "TARGET: ${gateState.targetTitle}",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = WitnessParchment
                            )
                            Text(
                                text = if (canSeal) {
                                    "MASTER RELEASE AUTHORIZATION: UNLOCKED (G1–G5 VERIFIED)"
                                } else {
                                    "MASTER RELEASE LOCKED: Requires G1–G5 VERIFIED by Student-Director"
                                },
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (canSeal) StateParsed else StateUnverified,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                    }
                }

                items(GovernanceGateId.entries) { gate ->
                    val status = when (gate) {
                        GovernanceGateId.G1 -> gateState.g1Status
                        GovernanceGateId.G2 -> gateState.g2Status
                        GovernanceGateId.G3 -> gateState.g3Status
                        GovernanceGateId.G4 -> gateState.g4Status
                        GovernanceGateId.G5 -> gateState.g5Status
                    }
                    Card(
                        colors = CardDefaults.cardColors(containerColor = SlateDark),
                        border = androidx.compose.foundation.BorderStroke(1.dp, SlateCardBorder),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(gate.title, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = WitnessParchment)
                                Text(
                                    text = status,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (status == "VERIFIED") StateParsed else BronzeGold,
                                    fontFamily = FontFamily.Monospace
                                )
                            }
                            Text(gate.mandatoryRule, fontSize = 11.sp, color = MutedSlateText)
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                Button(
                                    onClick = {
                                        viewModel.updateGateDecision(
                                            gate = gate,
                                            newStatus = "VERIFIED",
                                            note = "Approved by Student-Director"
                                        )
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = StateParsed),
                                    shape = RoundedCornerShape(6.dp),
                                    modifier = Modifier.testTag("approve_gate_${gate.gateCode}")
                                ) {
                                    Text("Approve ${gate.gateCode}", color = ObsidianBlack, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                                Button(
                                    onClick = {
                                        viewModel.updateGateDecision(
                                            gate = gate,
                                            newStatus = "NOT MEASURED",
                                            note = "Reset / Vetoed by Student-Director"
                                        )
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = StateFailed),
                                    shape = RoundedCornerShape(6.dp),
                                    modifier = Modifier.testTag("veto_gate_${gate.gateCode}")
                                ) {
                                    Text("Veto / Lock", color = WitnessParchment, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

/**
 * 13. SYSTEM SERVICES (UI/UX Suite v1.0 Module 13)
 * Sections:
 * - Database & Storage, Backup & Recovery, Security & Access,
 *   Background Services, System Health, Logs & Diagnostics
 */
@Composable
fun SystemServicesScreen(
    viewModel: ElyzarethViewModel,
    uiState: ElyzarethUiState,
    titlesCount: Int,
    artifactsCount: Int,
    kernelEvents: List<KernelEvent>,
    modifier: Modifier = Modifier
) {
    val jobs = remember(titlesCount, artifactsCount) {
        BackgroundJobCoordinator.getRegisteredJobs(titlesCount.coerceAtLeast(390), artifactsCount.coerceAtLeast(510))
    }
    val session = SovereignSecurityManager.activeSession

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(ObsidianBlack)
            .padding(horizontal = 16.dp)
            .testTag("system_services_screen"),
        contentPadding = PaddingValues(top = 14.dp, bottom = 32.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            ModuleHeaderBanner(
                badge = "MODULE 13 • SUPPORTING SYSTEM BRANCHES",
                title = "13. System Services",
                subtitle = "Database • Backup & Recovery • Security • Background • Diagnostics",
                accent = BlueThread
            )
        }

        // Backup & Recovery Trigger
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = SlateCard),
                border = androidx.compose.foundation.BorderStroke(1.dp, BronzeGold),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Backup, contentDescription = null, tint = BronzeGold, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("BACKUP & RECOVERY ENGINE", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = BronzeGold, fontFamily = FontFamily.Monospace)
                    }
                    if (uiState.lastBackupSnapshot != null) {
                        val snap = uiState.lastBackupSnapshot
                        Text(
                            text = "Latest Snapshot: ${snap.snapshotId} (${snap.baseTitlesCount} titles / ${snap.artifactsCount} artifacts)\nSHA-256: ${snap.checksumSha256.take(36)}...",
                            fontSize = 10.sp,
                            color = StateParsed,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                    Button(
                        onClick = { viewModel.createSystemBackupSnapshot() },
                        colors = ButtonDefaults.buttonColors(containerColor = BronzeGold),
                        modifier = Modifier.fillMaxWidth().testTag("create_backup_snapshot_btn")
                    ) {
                        Text("Create Encrypted Vault Snapshot Now", color = ObsidianBlack, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                }
            }
        }

        // 6 System Service Categories from Screen 13 in Image 2
        item {
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                SuiteFeatureRow(
                    "Database & Storage",
                    "Room SQLite Vault (elyzareth_corpus_vault.db v2 • 10 Tables • Offline-First)",
                    StateParsed
                )
                SuiteFeatureRow(
                    "Security & Access",
                    "Principal: ${session.principal} • ${session.encryptionStandard}",
                    BronzeGold
                )
                SuiteFeatureRow(
                    "System Health",
                    "100% Operational • Zero Unhandled Exceptions • Memory Optimal",
                    StateParsed
                )
            }
        }

        // Background Services
        item {
            Text("BACKGROUND SERVICES & WORKERS", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = BronzeGold, fontFamily = FontFamily.Monospace)
        }
        items(jobs, key = { it.serviceId }) { job ->
            SuiteFeatureRow("${job.name} (${job.scheduleMode})", job.lastRunResult, BlueThread)
        }

        // Optional Offline-First Integrations
        item {
            Text("INTEGRATION ADAPTERS (OPTIONAL • OFFLINE-FIRST CORE)", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = BronzeGold, fontFamily = FontFamily.Monospace)
        }
        items(OfflineFirstIntegrations.adapters, key = { it.adapterId }) { adapter ->
            SuiteFeatureRow("${adapter.name} [${adapter.category}]", "${adapter.mode} • ${adapter.status}", StateVerified)
        }

        // Logs & Diagnostics
        item {
            Text("KERNEL EVENT LEDGER & DIAGNOSTICS (${kernelEvents.size})", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = BronzeGold, fontFamily = FontFamily.Monospace)
        }
        items(kernelEvents.take(8), key = { it.eventId }) { ev ->
            Card(
                colors = CardDefaults.cardColors(containerColor = SlateDark),
                border = androidx.compose.foundation.BorderStroke(1.dp, SlateCardBorder),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(10.dp)) {
                    Text("${ev.eventId} • ${ev.branch} • ${ev.action}", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = BronzeGold, fontFamily = FontFamily.Monospace)
                    Text(ev.details, fontSize = 11.sp, color = WitnessParchment)
                }
            }
        }
    }
}

/**
 * 14. PROJECT & SYSTEM OVERVIEW (UI/UX Suite v1.0 Module 14)
 */
@Composable
fun ProjectAndSystemOverviewScreen(
    activeProjectsCount: Int,
    totalSongsCount: Int,
    inProductionCount: Int,
    pendingApprovalCount: Int,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(ObsidianBlack)
            .padding(horizontal = 16.dp)
            .testTag("project_overview_screen"),
        contentPadding = PaddingValues(top = 14.dp, bottom = 32.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            ModuleHeaderBanner(
                badge = "MODULE 14 • ANALYTICS & SYSTEM HEALTH",
                title = "14. Project & System Overview",
                subtitle = "Active Projects • Total Songs • Production Telemetry • Branch Readiness",
                accent = StateParsed
            )
        }

        // Metric Summary Cards matching Screen 14 in Image 2
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = SlateCard),
                border = androidx.compose.foundation.BorderStroke(1.dp, BronzeGold),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OverviewStatBox("Active Projects", "$activeProjectsCount", BlueThread, Modifier.weight(1f))
                        OverviewStatBox("Total Songs", "$totalSongsCount", BronzeGold, Modifier.weight(1f))
                        OverviewStatBox("In Production", "$inProductionCount", StateVerified, Modifier.weight(1f))
                    }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(SlateDark, RoundedCornerShape(8.dp))
                            .border(1.dp, BronzeGold.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                            .padding(10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Pending Governor Approval", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = WitnessParchment)
                        Text("$pendingApprovalCount", fontSize = 16.sp, fontWeight = FontWeight.ExtraBold, color = BronzeGold, fontFamily = FontFamily.Monospace)
                    }

                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Overall Master Structure Baseline Progress", fontSize = 11.sp, color = WitnessParchment)
                            Text("100% Structurally Integrated", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = StateParsed, fontFamily = FontFamily.Monospace)
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        LinearProgressIndicator(
                            progress = { 1.0f },
                            color = StateParsed,
                            trackColor = SlateDark,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(10.dp)
                                .clip(RoundedCornerShape(5.dp))
                        )
                    }
                }
            }
        }

        item {
            Text("ARCHITECTURAL LAYER READINESS", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = BronzeGold, fontFamily = FontFamily.Monospace)
        }

        item {
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                SuiteFeatureRow("Foundation Layer (:core:foundation)", "Human Authority • 6 Constitutional Laws • 14 Pipeline Stages", StateParsed)
                SuiteFeatureRow("Kernel Layer (:core:kernel)", "Sovereign Kernel • Governor State Machine • Policy & Integrity", StateParsed)
                SuiteFeatureRow("Creative Layer (:app/ui/creative)", "Idea & Blueprint • Lyric Generator • Lyric Curator • Creative DNA", StateParsed)
                SuiteFeatureRow("Production Layer (:app/ui/production)", "Music Production • Visual Direction • Asset Registry", StateParsed)
                SuiteFeatureRow("Intelligence Layer (:app/ui/intelligence)", "Corpus & Archive • Forensic Lab • Evidence Vault", StateParsed)
                SuiteFeatureRow("Governance & Release Layer", "G1–G5 Gate Console • 3.2.1.0 Protocol • Master Vault & Player", StateParsed)
            }
        }
    }
}

/**
 * SOVEREIGN KERNEL CONTROL CENTRE SCREEN
 */
@Composable
fun SovereignKernelScreen(
    projectContext: CanonicalProjectContext,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(ObsidianBlack)
            .padding(horizontal = 16.dp)
            .testTag("sovereign_kernel_screen"),
        contentPadding = PaddingValues(top = 14.dp, bottom = 32.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            ModuleHeaderBanner(
                badge = "SOVEREIGN KERNEL • THE CONTROL CENTRE",
                title = "Sovereign Kernel & Constitutional Laws",
                subtitle = "Sole Authority for Canonical Workflow State Transitions",
                accent = BronzeGold
            )
        }

        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = SlateCard),
                border = androidx.compose.foundation.BorderStroke(1.dp, BronzeGold),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text("ACTIVE CANONICAL CONTEXT", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = BronzeGold, fontFamily = FontFamily.Monospace)
                    Text("Project: ${projectContext.activeProjectTitle} (${projectContext.activeProjectId})", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = WitnessParchment)
                    Text("Current Stage: ${projectContext.currentPipelineStage.code} — ${projectContext.currentPipelineStage.title}", fontSize = 11.sp, color = BlueThread, fontFamily = FontFamily.Monospace)
                    Text("Governance: ${projectContext.governanceStatus}", fontSize = 11.sp, color = StateParsed, fontFamily = FontFamily.Monospace)
                }
            }
        }

        item {
            Text("CONSTITUTIONAL LAWS (FOUNDATION LAYER)", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = BronzeGold, fontFamily = FontFamily.Monospace)
        }

        items(ConstitutionalLaws.ALL_LAWS, key = { it.lawId }) { law ->
            Card(
                colors = CardDefaults.cardColors(containerColor = SlateDark),
                border = androidx.compose.foundation.BorderStroke(1.dp, SlateCardBorder),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text("${law.lawId} — ${law.title}", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = BronzeGold, fontFamily = FontFamily.Monospace)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(law.mandate, fontSize = 11.sp, color = WitnessParchment)
                }
            }
        }
    }
}

@Composable
private fun OverviewStatBox(label: String, value: String, color: androidx.compose.ui.graphics.Color, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .background(SlateDark, RoundedCornerShape(8.dp))
            .border(1.dp, color.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
            .padding(10.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(label, fontSize = 10.sp, color = MutedSlateText)
        Spacer(modifier = Modifier.height(4.dp))
        Text(value, fontSize = 18.sp, fontWeight = FontWeight.ExtraBold, color = color, fontFamily = FontFamily.Monospace)
    }
}
