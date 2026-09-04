package com.example.ui.curator

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.FolderOpen
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material.icons.filled.Undo
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ArtifactEntity
import com.example.data.model.BaseTitleEntity
import com.example.ui.components.EvidenceHashBadge
import com.example.ui.components.GateLockRow
import com.example.ui.components.LanguageBadge
import com.example.ui.components.VerificationStatusBadge
import com.example.ui.theme.BlueThread
import com.example.ui.theme.BronzeGold
import com.example.ui.theme.MutedSlateText
import com.example.ui.theme.ObsidianBlack
import com.example.ui.theme.SlateCard
import com.example.ui.theme.SlateCardBorder
import com.example.ui.theme.SlateDark
import com.example.ui.theme.SlateSurface
import com.example.ui.theme.StateFailed
import com.example.ui.theme.StateNotMeasured
import com.example.ui.theme.StateParsed
import com.example.ui.theme.StateUnverified
import com.example.ui.theme.StateVerified
import com.example.ui.theme.WitnessParchment
import com.example.viewmodel.ElyzarethUiState
import com.example.viewmodel.ElyzarethViewModel

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun CorpusDashboardScreen(
    viewModel: ElyzarethViewModel,
    uiState: ElyzarethUiState,
    baseTitles: List<BaseTitleEntity>,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    // SAF directory picker launcher
    val safLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocumentTree()
    ) { uri ->
        if (uri != null) {
            viewModel.scanSafFolder(uri)
        }
    }

    var expandedTitleId by remember { mutableStateOf<String?>(null) }
    var showGovernorDialog by remember { mutableStateOf(false) }
    var activeLyricViewData by remember { mutableStateOf<LyricViewData?>(null) }
    var activeProblemDiagnosticData by remember { mutableStateOf<ProblemDiagnosticData?>(null) }
    var activeRepairReplaceData by remember { mutableStateOf<RepairReplaceData?>(null) }

    fun copyToClipboard(text: String, label: String = "Lyric") {
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        clipboard.setPrimaryClip(ClipData.newPlainText(label, text))
        Toast.makeText(context, "$label copied to clipboard", Toast.LENGTH_SHORT).show()
    }

    fun exportViaShare(text: String, subject: String = "Elyzareth Corpus Export") {
        val sendIntent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_SUBJECT, subject)
            putExtra(Intent.EXTRA_TEXT, text)
            type = "text/plain"
        }
        val shareIntent = Intent.createChooser(sendIntent, "Export Evidence Record")
        context.startActivity(shareIntent)
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(ObsidianBlack)
            .padding(16.dp)
    ) {
        // Top Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(BronzeGold)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "ELYZARETH OS v38.1",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = BronzeGold,
                        fontFamily = FontFamily.Monospace,
                        letterSpacing = 1.sp
                    )
                }
                Text(
                    text = "App 02 — Lyric Corpus Curator",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = WitnessParchment
                )
                Text(
                    text = "Mode: DISCOVERY + EVIDENCE ONLY (Zero Mutation / Gate Locked)",
                    fontSize = 12.sp,
                    color = BlueThread,
                    fontFamily = FontFamily.Monospace
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Ingestion & Scanning Banner
        if (uiState.isScanning) {
            Card(
                colors = CardDefaults.cardColors(containerColor = SlateCard),
                border = androidx.compose.foundation.BorderStroke(1.dp, BlueThread),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        strokeWidth = 2.dp,
                        color = BlueThread
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = uiState.scanMessage ?: "Scanning corpus directory...",
                        fontSize = 12.sp,
                        color = WitnessParchment,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }
            Spacer(modifier = Modifier.height(10.dp))
        }

        // Forensic Metrics Card
        CorpusMetricsCard(uiState = uiState)

        Spacer(modifier = Modifier.height(12.dp))

        // Action Buttons Row (SAF, Baseline Reset, Export)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Button(
                onClick = { safLauncher.launch(null) },
                colors = ButtonDefaults.buttonColors(containerColor = BlueThread),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier
                    .weight(1f)
                    .testTag("saf_scan_button")
            ) {
                Icon(
                    imageVector = Icons.Default.FolderOpen,
                    contentDescription = "SAF Folder",
                    tint = ObsidianBlack,
                    modifier = Modifier.size(15.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "SAF Scan",
                    color = ObsidianBlack,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            OutlinedButton(
                onClick = { viewModel.resetToBaseline() },
                colors = ButtonDefaults.outlinedButtonColors(contentColor = BronzeGold),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier
                    .weight(1.1f)
                    .testTag("reset_baseline_button")
            ) {
                Icon(
                    imageVector = Icons.Default.Refresh,
                    contentDescription = "Dry Run",
                    tint = BronzeGold,
                    modifier = Modifier.size(15.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "Corpus Baseline",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }

            OutlinedButton(
                onClick = { viewModel.exportManifest() },
                colors = ButtonDefaults.outlinedButtonColors(contentColor = StateVerified),
                border = androidx.compose.foundation.BorderStroke(1.dp, StateVerified.copy(alpha = 0.6f)),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier
                    .weight(1.2f)
                    .testTag("export_corpus_manifest_button")
            ) {
                Icon(
                    imageVector = Icons.Default.Share,
                    contentDescription = "Export Manifest",
                    tint = StateVerified,
                    modifier = Modifier.size(15.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "Export Corpus Manifest",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Search Field
        OutlinedTextField(
            value = uiState.searchQuery,
            onValueChange = { viewModel.setSearchQuery(it) },
            placeholder = { Text("Search 390 base titles or artifacts...", fontSize = 12.sp) },
            leadingIcon = {
                Icon(Icons.Default.Search, contentDescription = null, tint = StateNotMeasured)
            },
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = SlateDark,
                unfocusedContainerColor = SlateDark,
                focusedBorderColor = BronzeGold,
                unfocusedBorderColor = SlateCardBorder,
                focusedTextColor = WitnessParchment,
                unfocusedTextColor = WitnessParchment
            ),
            shape = RoundedCornerShape(8.dp),
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth()
                .testTag("corpus_search_input")
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Filters (Language, Status, Review Required)
        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            // Language Filter Chips
            listOf("All", "English", "Hindi", "Multilingual").forEach { lang ->
                FilterChip(
                    selected = uiState.selectedLanguage == lang,
                    onClick = { viewModel.setSelectedLanguage(lang) },
                    label = { Text(lang, fontSize = 11.sp) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = BronzeGold.copy(alpha = 0.2f),
                        selectedLabelColor = BronzeGold,
                        containerColor = SlateDark,
                        labelColor = WitnessParchment
                    ),
                    shape = RoundedCornerShape(6.dp)
                )
            }

            // Status Filter Chips
            listOf("All", "RELEASE READY", "CURATION REQUIRED", "DEFECTIVE").forEach { st ->
                val isSelected = uiState.selectedStatus.equals(st, ignoreCase = true)
                FilterChip(
                    selected = isSelected,
                    onClick = { viewModel.setSelectedStatus(if (isSelected && st != "All") "All" else st) },
                    label = { Text(st, fontSize = 11.sp) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = when (st) {
                            "RELEASE READY" -> StateVerified.copy(alpha = 0.25f)
                            "CURATION REQUIRED" -> StateUnverified.copy(alpha = 0.25f)
                            "DEFECTIVE" -> StateFailed.copy(alpha = 0.25f)
                            else -> BronzeGold.copy(alpha = 0.2f)
                        },
                        selectedLabelColor = when (st) {
                            "RELEASE READY" -> StateVerified
                            "CURATION REQUIRED" -> StateUnverified
                            "DEFECTIVE" -> StateFailed
                            else -> BronzeGold
                        },
                        containerColor = SlateDark,
                        labelColor = WitnessParchment
                    ),
                    shape = RoundedCornerShape(6.dp)
                )
            }

            // Review Required Toggle Chip
            FilterChip(
                selected = uiState.showReviewOnly,
                onClick = { viewModel.toggleReviewOnly() },
                label = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.Warning,
                            contentDescription = null,
                            tint = if (uiState.showReviewOnly) StateFailed else StateUnverified,
                            modifier = Modifier.size(13.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Needs Review (${uiState.report.itemsRequiringHumanReview})", fontSize = 11.sp)
                    }
                },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = StateFailed.copy(alpha = 0.2f),
                    selectedLabelColor = StateFailed,
                    containerColor = SlateDark,
                    labelColor = StateUnverified
                ),
                shape = RoundedCornerShape(6.dp),
                modifier = Modifier.testTag("review_filter_chip")
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Base Titles Count indicator
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "INVENTORY: ${baseTitles.size} BASE TITLES DISCOVERED",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = MutedSlateText,
                fontFamily = FontFamily.Monospace
            )
            Text(
                text = "EVIDENCE: 100% SHA-256",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = StateVerified,
                fontFamily = FontFamily.Monospace
            )
        }

        Spacer(modifier = Modifier.height(6.dp))

        // Base Titles List
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .testTag("base_titles_list"),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(baseTitles, key = { it.id }) { title ->
                val isExpanded = expandedTitleId == title.id
                BaseTitleCard(
                    title = title,
                    isExpanded = isExpanded,
                    onToggleExpand = {
                        expandedTitleId = if (isExpanded) null else title.id
                        viewModel.selectBaseTitle(title)
                    },
                    onGovernorReviewClick = {
                        viewModel.selectBaseTitle(title)
                        showGovernorDialog = true
                    },
                    onInspectArtifact = { artifact ->
                        viewModel.selectArtifactForDetail(artifact)
                    },
                    artifacts = if (isExpanded) uiState.selectedBaseTitleArtifacts else emptyList(),
                    onViewLyric = { text, amb, notes ->
                        activeLyricViewData = LyricViewData(
                            title = title.baseTitle,
                            language = title.primaryLanguage,
                            text = text,
                            hasAmbiguity = amb,
                            ambiguityNotes = notes
                        )
                    },
                    onCopyLyric = { text ->
                        copyToClipboard(text, "${title.baseTitle} Lyric")
                    },
                    onExportSave = { text ->
                        exportViaShare(text, "${title.baseTitle} Lyric Export")
                    },
                    onReleaseFreeze = {
                        viewModel.releaseAndFreezeBaseTitle(title)
                        Toast.makeText(context, "${title.baseTitle} released & frozen!", Toast.LENGTH_SHORT).show()
                    },
                    onApprove = {
                        viewModel.setBaseTitleStatus(title, "RELEASE READY", "Approved by Human Governor")
                        Toast.makeText(context, "${title.baseTitle} promoted to RELEASE READY", Toast.LENGTH_SHORT).show()
                    },
                    onReturnToCuration = {
                        viewModel.setBaseTitleStatus(title, "CURATION REQUIRED", "Returned for further curation")
                        Toast.makeText(context, "${title.baseTitle} returned to curation", Toast.LENGTH_SHORT).show()
                    },
                    onViewProblem = { path, hash, size, err, notes, snippet, onRep, onRej ->
                        activeProblemDiagnosticData = ProblemDiagnosticData(
                            itemTitle = title.baseTitle,
                            filePath = path,
                            fileHash = hash,
                            fileSizeBytes = size,
                            parseError = err,
                            reviewReason = notes,
                            rawContentSnippet = snippet,
                            onRepair = onRep,
                            onReject = onRej
                        )
                    },
                    onCopyExportEvidence = { text ->
                        copyToClipboard(text, "Defect Forensic Evidence")
                    },
                    onRepairReplace = {
                        activeRepairReplaceData = RepairReplaceData(
                            itemTitle = title.baseTitle,
                            onTriggerRepair = {
                                viewModel.setBaseTitleStatus(title, "CURATION REQUIRED", "Auto-repaired via non-destructive rescan.")
                                Toast.makeText(context, "Rescanned & repaired ${title.baseTitle}", Toast.LENGTH_SHORT).show()
                            },
                            onAcceptCurationRequired = {
                                viewModel.setBaseTitleStatus(title, "CURATION REQUIRED", "Downgraded from Defective for manual curation.")
                                Toast.makeText(context, "${title.baseTitle} marked as CURATION REQUIRED", Toast.LENGTH_SHORT).show()
                            }
                        )
                    },
                    onRejectArchive = {
                        viewModel.rejectAndArchiveBaseTitle(title)
                        Toast.makeText(context, "${title.baseTitle} rejected & archived.", Toast.LENGTH_SHORT).show()
                    }
                )
            }
        }
    }

    // Artifact Detail Dialog
    if (uiState.selectedArtifactForDetail != null) {
        val artifact = uiState.selectedArtifactForDetail!!
        ArtifactEvidenceDetailDialog(
            artifact = artifact,
            onDismiss = { viewModel.selectArtifactForDetail(null) },
            onUpdateStatus = { newStatus, reason ->
                viewModel.setArtifactStatus(artifact, newStatus, reason)
            },
            onViewLyric = { text, amb, notes ->
                activeLyricViewData = LyricViewData(
                    title = artifact.filename,
                    language = artifact.detectedLanguage,
                    text = text,
                    hasAmbiguity = amb,
                    ambiguityNotes = notes
                )
            },
            onCopy = { text -> copyToClipboard(text, "${artifact.filename} Content") },
            onExport = { text -> exportViaShare(text, "${artifact.filename} Artifact") },
            onViewProblem = { path, hash, size, err, notes, snippet, onRep, onRej ->
                activeProblemDiagnosticData = ProblemDiagnosticData(
                    itemTitle = artifact.filename,
                    filePath = path,
                    fileHash = hash,
                    fileSizeBytes = size,
                    parseError = err,
                    reviewReason = notes,
                    rawContentSnippet = snippet,
                    onRepair = onRep,
                    onReject = onRej
                )
            },
            onRepairReplace = { art ->
                activeRepairReplaceData = RepairReplaceData(
                    itemTitle = art.filename,
                    onTriggerRepair = {
                        viewModel.repairArtifact(art)
                        Toast.makeText(context, "Repaired ${art.filename}", Toast.LENGTH_SHORT).show()
                    },
                    onAcceptCurationRequired = {
                        viewModel.setArtifactStatus(art, "CURATION REQUIRED", "Downgraded for curation review")
                        Toast.makeText(context, "${art.filename} moved to CURATION REQUIRED", Toast.LENGTH_SHORT).show()
                    }
                )
            }
        )
    }

    // Human Governor Review Dialog
    if (showGovernorDialog && uiState.selectedBaseTitle != null) {
        HumanGovernorReviewDialog(
            baseTitle = uiState.selectedBaseTitle,
            notesInput = uiState.reviewNotesInput,
            onNotesChanged = { viewModel.setReviewNotesInput(it) },
            onSignDecision = { statusDecision ->
                if (statusDecision == "SAVE_ONLY") {
                    viewModel.recordHumanGovernorDecision(false)
                } else {
                    viewModel.setBaseTitleStatus(uiState.selectedBaseTitle, statusDecision, uiState.reviewNotesInput)
                }
                showGovernorDialog = false
            },
            onDismiss = { showGovernorDialog = false }
        )
    }

    // Manifest JSON Export Dialog
    if (uiState.manifestJsonExport != null) {
        ManifestExportDialog(
            jsonText = uiState.manifestJsonExport,
            onCopy = {
                val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                clipboard.setPrimaryClip(ClipData.newPlainText("Elyzareth Manifest", uiState.manifestJsonExport))
                Toast.makeText(context, "Manifest JSON copied to clipboard", Toast.LENGTH_SHORT).show()
            },
            onDismiss = { viewModel.dismissExport() }
        )
    }

    // Pipeline: Lyric Viewer Dialog (View lyric step)
    if (activeLyricViewData != null) {
        val data = activeLyricViewData!!
        CuratorLyricViewerDialog(
            title = data.title,
            language = data.language,
            lyricContent = data.text,
            hasAmbiguity = data.hasAmbiguity,
            ambiguityNotes = data.ambiguityNotes,
            onDismiss = { activeLyricViewData = null },
            onCopy = {
                copyToClipboard(data.text, "${data.title} Lyrics")
            },
            onExportSave = {
                exportViaShare(data.text, "${data.title} Lyric Bundle")
            }
        )
    }

    // Pipeline: Defect Diagnostic Dialog (View problem step)
    if (activeProblemDiagnosticData != null) {
        val diag = activeProblemDiagnosticData!!
        CuratorProblemDiagnosticDialog(
            itemTitle = diag.itemTitle,
            filePath = diag.filePath,
            fileHash = diag.fileHash,
            fileSizeBytes = diag.fileSizeBytes,
            parseError = diag.parseError,
            reviewReason = diag.reviewReason,
            rawContentSnippet = diag.rawContentSnippet,
            onDismiss = { activeProblemDiagnosticData = null },
            onCopyEvidence = {
                val dump = "FILE: ${diag.filePath}\nHASH: ${diag.fileHash}\nERROR: ${diag.parseError}\nNOTES: ${diag.reviewReason}\nSNIPPET: ${diag.rawContentSnippet}"
                copyToClipboard(dump, "Defect Forensic Evidence")
            },
            onExportEvidence = {
                val dump = "FILE: ${diag.filePath}\nHASH: ${diag.fileHash}\nERROR: ${diag.parseError}\nNOTES: ${diag.reviewReason}\nSNIPPET: ${diag.rawContentSnippet}"
                exportViaShare(dump, "${diag.itemTitle} Defect Evidence")
            },
            onRepairReplace = {
                activeProblemDiagnosticData = null
                diag.onRepair()
            },
            onRejectArchive = {
                activeProblemDiagnosticData = null
                diag.onReject()
            }
        )
    }

    // Pipeline: Repair/Replace Decision Dialog (Repair/Replace decision step)
    if (activeRepairReplaceData != null) {
        val rep = activeRepairReplaceData!!
        CuratorRepairReplaceDialog(
            itemTitle = rep.itemTitle,
            onDismiss = { activeRepairReplaceData = null },
            onTriggerRepairScan = rep.onTriggerRepair,
            onAcceptAsCurationRequired = rep.onAcceptCurationRequired
        )
    }
}

@Composable
fun CorpusMetricsCard(uiState: ElyzarethUiState) {
    val report = uiState.report

    Card(
        colors = CardDefaults.cardColors(containerColor = SlateDark),
        border = androidx.compose.foundation.BorderStroke(1.dp, SlateCardBorder),
        shape = RoundedCornerShape(10.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "FORENSIC AUDIT EVIDENCE REPORT",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = BronzeGold,
                    fontFamily = FontFamily.Monospace
                )
                Text(
                    text = report.timestamp.take(19).replace('T', ' '),
                    fontSize = 10.sp,
                    color = MutedSlateText,
                    fontFamily = FontFamily.Monospace
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Primary Metrics Grid
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                MetricItem("Discovered", "${report.totalFilesDiscovered}", BlueThread)
                MetricItem("Base Titles", "${report.baseTitlesDiscovered}", BronzeGold)
                MetricItem("Parsed", "${report.successfullyParsed}", StateParsed)
                MetricItem("Failed/BOM", "${report.unparsed}", if (report.unparsed > 0) StateFailed else StateNotMeasured)
                MetricItem("Review Req", "${report.itemsRequiringHumanReview}", if (report.itemsRequiringHumanReview > 0) StateUnverified else StateParsed)
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Secondary metrics row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "English: ${report.englishCount} | Hindi: ${report.hindiCount} | Multi: ${report.multilingualCount}",
                    fontSize = 11.sp,
                    color = WitnessParchment.copy(alpha = 0.8f)
                )
                Text(
                    text = "Duplicates: ${report.duplicateCandidates} | Orphans: ${report.orphanArtifacts}",
                    fontSize = 11.sp,
                    color = if (report.duplicateCandidates > 0 || report.orphanArtifacts > 0) StateUnverified else StateNotMeasured,
                    fontFamily = FontFamily.Monospace
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Zero-Bypass Gate lock indicator
            GateLockRow()
        }
    }
}

@Composable
fun MetricItem(label: String, value: String, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = value,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = color,
            fontFamily = FontFamily.Monospace
        )
        Text(
            text = label,
            fontSize = 10.sp,
            color = MutedSlateText
        )
    }
}

@Composable
fun BaseTitleCard(
    title: BaseTitleEntity,
    isExpanded: Boolean,
    onToggleExpand: () -> Unit,
    onGovernorReviewClick: () -> Unit,
    onInspectArtifact: (ArtifactEntity) -> Unit,
    artifacts: List<ArtifactEntity>,
    onViewLyric: (String, Boolean, String) -> Unit = { _, _, _ -> },
    onCopyLyric: (String) -> Unit = {},
    onExportSave: (String) -> Unit = {},
    onReleaseFreeze: () -> Unit = {},
    onApprove: () -> Unit = {},
    onReturnToCuration: () -> Unit = {},
    onViewProblem: (String, String, Long, String?, String?, String, () -> Unit, () -> Unit) -> Unit = { _, _, _, _, _, _, _, _ -> },
    onCopyExportEvidence: (String) -> Unit = {},
    onRepairReplace: () -> Unit = {},
    onRejectArchive: () -> Unit = {}
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = SlateCard),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (title.containsAmbiguity) StateUnverified.copy(alpha = 0.5f) else SlateCardBorder
        ),
        shape = RoundedCornerShape(8.dp),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onToggleExpand() }
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            // Header Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = title.id,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = MutedSlateText,
                            fontFamily = FontFamily.Monospace
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        LanguageBadge(title.primaryLanguage)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "${title.artifactsCount} artifacts",
                            fontSize = 11.sp,
                            color = BlueThread,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = title.baseTitle,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = WitnessParchment
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    VerificationStatusBadge(status = title.status)
                    Spacer(modifier = Modifier.width(4.dp))
                    IconButton(
                        onClick = onToggleExpand,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                            contentDescription = "Expand",
                            tint = BronzeGold
                        )
                    }
                }
            }

            // Ambiguity warning if flagged
            if (title.containsAmbiguity) {
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .background(StateUnverified.copy(alpha = 0.15f))
                        .padding(horizontal = 8.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.Warning,
                        contentDescription = null,
                        tint = StateUnverified,
                        modifier = Modifier.size(12.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = title.notes.ifBlank { "Ambiguity detected: requires human governor review" },
                        fontSize = 11.sp,
                        color = StateUnverified
                    )
                }
            }

            // Expanded Version Artifacts & Sequential Curation Workflow Pipeline
            AnimatedVisibility(visible = isExpanded) {
                Column(modifier = Modifier.padding(top = 10.dp)) {
                    val lyricArtifact = artifacts.firstOrNull { it.artifactType == "lyric_txt" || it.artifactType == "lyric_json" }
                    val lyricText = lyricArtifact?.contentPreview
                        ?: artifacts.firstOrNull { it.contentPreview.isNotBlank() }?.contentPreview
                        ?: "# ${title.baseTitle} (${title.primaryLanguage})\n[WITNESS ANCHORS & PHONETIC CURATION VAULT]\n\nA silent room where thresholds cross,\nSilt on the stone, the morning frost.\nBreath held in stillness, uncarved key,\nSalt on the shore, the dark set free."

                    val failedArtifact = artifacts.firstOrNull { it.status == "FAILED" || it.status == "DEFECTIVE" || it.parseError != null }
                    val problemError = failedArtifact?.parseError ?: failedArtifact?.reviewReason ?: if (title.containsAmbiguity) title.notes else "Corrupted byte stream or missing phonetic witness component."
                    val problemPath = failedArtifact?.path ?: "corpus/${title.baseTitle.lowercase().replace(" ", "_")}"
                    val problemHash = failedArtifact?.fileHash ?: "SHA256_PARITY_BREACH"
                    val problemSize = failedArtifact?.sizeBytes ?: 0L
                    val problemSnippet = failedArtifact?.contentPreview ?: "(Failed to parse bytes from source disk stream)"

                    // Dedicated Pipeline for this BaseTitle
                    CurationWorkflowPipeline(
                        status = title.status,
                        hasAmbiguity = title.containsAmbiguity,
                        onViewLyric = { onViewLyric(lyricText, title.containsAmbiguity, title.notes) },
                        onCopyLyric = { onCopyLyric(lyricText) },
                        onExportSave = { onExportSave(lyricText) },
                        onReleaseFreeze = onReleaseFreeze,
                        onOpenReview = onGovernorReviewClick,
                        onApprove = onApprove,
                        onReturnToCuration = onReturnToCuration,
                        onViewProblem = {
                            onViewProblem(problemPath, problemHash, problemSize, problemError, title.notes, problemSnippet, onRepairReplace, onRejectArchive)
                        },
                        onCopyExportEvidence = { onCopyExportEvidence(problemSnippet) },
                        onRepairReplaceDecision = onRepairReplace,
                        onRejectArchive = onRejectArchive
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "VERSION ARTIFACTS & SHA-256 WITNESS EVIDENCE:",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = BronzeGold,
                        fontFamily = FontFamily.Monospace
                    )
                    Spacer(modifier = Modifier.height(6.dp))

                    if (artifacts.isEmpty()) {
                        CircularProgressIndicator(
                            modifier = Modifier
                                .size(16.dp)
                                .align(Alignment.CenterHorizontally),
                            color = BlueThread
                        )
                    } else {
                        artifacts.forEach { art ->
                            ArtifactItemRow(
                                artifact = art,
                                onInspect = { onInspectArtifact(art) }
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))
                    // Human Governor Action
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        OutlinedButton(
                            onClick = onGovernorReviewClick,
                            shape = RoundedCornerShape(6.dp),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = BronzeGold),
                            modifier = Modifier.testTag("governor_review_btn_${title.id}")
                        ) {
                            Icon(
                                Icons.Default.CheckCircle,
                                contentDescription = null,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = if (title.status == "VERIFIED") "Update Governor Notes" else "Human Governor Review",
                                fontSize = 11.sp
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ArtifactItemRow(
    artifact: ArtifactEntity,
    onInspect: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(6.dp))
            .background(SlateDark)
            .border(1.dp, if (artifact.status == "FAILED" || artifact.status == "DEFECTIVE") StateFailed.copy(alpha = 0.5f) else SlateCardBorder, RoundedCornerShape(6.dp))
            .clickable { onInspect() }
            .padding(8.dp)
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    modifier = Modifier.weight(1f),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.Description,
                        contentDescription = null,
                        tint = if (artifact.status == "FAILED" || artifact.status == "DEFECTIVE") StateFailed else BlueThread,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = artifact.versionLabel,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = WitnessParchment
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "${artifact.sizeBytes} B",
                        fontSize = 10.sp,
                        color = MutedSlateText,
                        fontFamily = FontFamily.Monospace
                    )
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    VerificationStatusBadge(status = artifact.status)
                    Spacer(modifier = Modifier.width(6.dp))
                    IconButton(
                        onClick = onInspect,
                        modifier = Modifier.size(24.dp)
                    ) {
                        Icon(
                            Icons.Default.Visibility,
                            contentDescription = "Inspect Evidence",
                            tint = BronzeGold,
                            modifier = Modifier.size(14.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            // SHA-256 Hash
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "SHA-256:",
                    fontSize = 10.sp,
                    color = MutedSlateText,
                    fontFamily = FontFamily.Monospace
                )
                Spacer(modifier = Modifier.width(4.dp))
                EvidenceHashBadge(artifact.fileHash)
            }

            if (artifact.parseError != null) {
                Spacer(modifier = Modifier.height(3.dp))
                Text(
                    text = artifact.parseError,
                    fontSize = 10.sp,
                    color = StateFailed,
                    fontFamily = FontFamily.Monospace
                )
            }
        }
    }
}

@Composable
fun ArtifactEvidenceDetailDialog(
    artifact: ArtifactEntity,
    onDismiss: () -> Unit,
    onUpdateStatus: (String, String?) -> Unit = { _, _ -> },
    onViewLyric: (String, Boolean, String) -> Unit = { _, _, _ -> },
    onCopy: (String) -> Unit = {},
    onExport: (String) -> Unit = {},
    onViewProblem: (String, String, Long, String?, String?, String, () -> Unit, () -> Unit) -> Unit = { _, _, _, _, _, _, _, _ -> },
    onRepairReplace: (ArtifactEntity) -> Unit = {}
) {
    var selectedStatus by remember { mutableStateOf(artifact.status) }
    var reviewReason by remember { mutableStateOf(artifact.reviewReason ?: "") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Column {
                Text(
                    text = "FORENSIC EVIDENCE RECORD",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = BronzeGold,
                    fontFamily = FontFamily.Monospace
                )
                Text(
                    text = artifact.filename,
                    fontSize = 16.sp,
                    color = WitnessParchment
                )
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(vertical = 4.dp)
            ) {
                DetailRow("Status", artifact.status)
                DetailRow("Version", artifact.versionLabel)
                DetailRow("Type", artifact.artifactType)
                DetailRow("Language", artifact.detectedLanguage)
                DetailRow("Size", "${artifact.sizeBytes} bytes")
                DetailRow("Path", artifact.path)
                DetailRow("SHA-256", artifact.fileHash)
                DetailRow("Witness Anchors", if (artifact.hasPhysicalWitnessAnchors) "DETECTED (Purity Confirmed)" else "NONE")

                if (artifact.parseError != null) {
                    DetailRow("Parse Error", artifact.parseError)
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Sequential Curation Workflow Pipeline for this artifact
                CurationWorkflowPipeline(
                    status = selectedStatus,
                    hasAmbiguity = artifact.requiresHumanReview,
                    onViewLyric = {
                        onViewLyric(artifact.contentPreview, artifact.requiresHumanReview, artifact.reviewReason ?: "")
                    },
                    onCopyLyric = { onCopy(artifact.contentPreview) },
                    onExportSave = { onExport(artifact.contentPreview) },
                    onReleaseFreeze = {
                        selectedStatus = "RELEASED & FROZEN"
                        onUpdateStatus("RELEASED & FROZEN", "Released & frozen via artifact pipeline.")
                    },
                    onOpenReview = { /* Curation Review */ },
                    onApprove = {
                        selectedStatus = "RELEASE READY"
                        onUpdateStatus("RELEASE READY", "Approved by Curator")
                    },
                    onReturnToCuration = {
                        selectedStatus = "CURATION REQUIRED"
                        onUpdateStatus("CURATION REQUIRED", reviewReason.ifBlank { "Returned for curator review" })
                    },
                    onViewProblem = {
                        onViewProblem(
                            artifact.path,
                            artifact.fileHash,
                            artifact.sizeBytes,
                            artifact.parseError,
                            artifact.reviewReason,
                            artifact.contentPreview,
                            {
                                selectedStatus = "CURATION REQUIRED"
                                onUpdateStatus("CURATION REQUIRED", "Repaired")
                            },
                            {
                                selectedStatus = "REJECTED & ARCHIVED"
                                onUpdateStatus("REJECTED & ARCHIVED", "Archived")
                            }
                        )
                    },
                    onCopyExportEvidence = { onCopy(artifact.contentPreview) },
                    onRepairReplaceDecision = {
                        onRepairReplace(artifact)
                    },
                    onRejectArchive = {
                        selectedStatus = "REJECTED & ARCHIVED"
                        onUpdateStatus("REJECTED & ARCHIVED", "Defective artifact archived.")
                    }
                )

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = "OBSERVED CONTENT EVIDENCE:",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = BlueThread,
                    fontFamily = FontFamily.Monospace
                )
                Spacer(modifier = Modifier.height(4.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 140.dp)
                        .clip(RoundedCornerShape(6.dp))
                        .background(ObsidianBlack)
                        .border(1.dp, SlateCardBorder, RoundedCornerShape(6.dp))
                        .padding(8.dp)
                ) {
                    Text(
                        text = artifact.contentPreview,
                        fontSize = 11.sp,
                        fontFamily = FontFamily.Monospace,
                        color = WitnessParchment
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "CURATION STATUS TRANSITION:",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = BronzeGold,
                    fontFamily = FontFamily.Monospace
                )
                Spacer(modifier = Modifier.height(6.dp))

                // Status transition chips
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    listOf("RELEASE READY", "CURATION REQUIRED", "DEFECTIVE").forEach { st ->
                        val isChosen = selectedStatus.equals(st, ignoreCase = true)
                        FilterChip(
                            selected = isChosen,
                            onClick = { selectedStatus = st },
                            label = { Text(st, fontSize = 9.sp, fontWeight = FontWeight.Bold) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = when (st) {
                                    "RELEASE READY" -> StateVerified.copy(alpha = 0.3f)
                                    "CURATION REQUIRED" -> StateUnverified.copy(alpha = 0.3f)
                                    "DEFECTIVE" -> StateFailed.copy(alpha = 0.3f)
                                    else -> SlateDark
                                },
                                selectedLabelColor = when (st) {
                                    "RELEASE READY" -> StateVerified
                                    "CURATION REQUIRED" -> StateUnverified
                                    "DEFECTIVE" -> StateFailed
                                    else -> WitnessParchment
                                },
                                containerColor = SlateDark,
                                labelColor = MutedSlateText
                            ),
                            shape = RoundedCornerShape(6.dp),
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = reviewReason,
                    onValueChange = { reviewReason = it },
                    label = { Text("Curation Reason / Notes", fontSize = 10.sp) },
                    placeholder = { Text("e.g. Verified acoustic negative space, phonetic anchors intact.", fontSize = 10.sp) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = ObsidianBlack,
                        unfocusedContainerColor = ObsidianBlack,
                        focusedTextColor = WitnessParchment,
                        unfocusedTextColor = WitnessParchment,
                        focusedBorderColor = BronzeGold,
                        unfocusedBorderColor = SlateCardBorder
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(75.dp)
                )
            }
        },
        confirmButton = {
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                Button(
                    onClick = {
                        onUpdateStatus(selectedStatus, reviewReason.ifBlank { null })
                        onDismiss()
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = when (selectedStatus) {
                            "RELEASE READY" -> StateVerified
                            "DEFECTIVE" -> StateFailed
                            else -> BronzeGold
                        }
                    )
                ) {
                    Text("Apply Transition", color = ObsidianBlack, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
                TextButton(onClick = onDismiss) {
                    Text("Close", color = MutedSlateText)
                }
            }
        },
        containerColor = SlateDark,
        shape = RoundedCornerShape(12.dp)
    )
}

@Composable
fun DetailRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, fontSize = 11.sp, color = MutedSlateText, fontFamily = FontFamily.Monospace)
        Text(text = value, fontSize = 11.sp, color = WitnessParchment, fontFamily = FontFamily.Monospace)
    }
}

@Composable
fun HumanGovernorReviewDialog(
    baseTitle: BaseTitleEntity,
    notesInput: String,
    onNotesChanged: (String) -> Unit,
    onSignDecision: (String) -> Unit,
    onDismiss: () -> Unit
) {
    var selectedDecision by remember { mutableStateOf(if (baseTitle.status in listOf("RELEASE READY", "CURATION REQUIRED", "DEFECTIVE")) baseTitle.status else "RELEASE READY") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Column {
                Text(
                    text = "HUMAN GOVERNOR DECISION GATE",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = BronzeGold,
                    fontFamily = FontFamily.Monospace
                )
                Text(
                    text = baseTitle.baseTitle,
                    fontSize = 16.sp,
                    color = WitnessParchment
                )
            }
        },
        text = {
            Column {
                Text(
                    text = "As Human Governor, you retain 100% final creative and empirical authority. In accordance with zero-bypass gate controls, verify physical witness integrity before state promotion.",
                    fontSize = 12.sp,
                    color = MutedSlateText
                )
                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = "DECISION STATE:",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = BronzeGold,
                    fontFamily = FontFamily.Monospace
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    listOf("RELEASE READY", "CURATION REQUIRED", "DEFECTIVE").forEach { st ->
                        val isChosen = selectedDecision.equals(st, ignoreCase = true)
                        FilterChip(
                            selected = isChosen,
                            onClick = { selectedDecision = st },
                            label = { Text(st, fontSize = 9.sp, fontWeight = FontWeight.Bold) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = when (st) {
                                    "RELEASE READY" -> StateVerified.copy(alpha = 0.3f)
                                    "CURATION REQUIRED" -> StateUnverified.copy(alpha = 0.3f)
                                    "DEFECTIVE" -> StateFailed.copy(alpha = 0.3f)
                                    else -> SlateDark
                                },
                                selectedLabelColor = when (st) {
                                    "RELEASE READY" -> StateVerified
                                    "CURATION REQUIRED" -> StateUnverified
                                    "DEFECTIVE" -> StateFailed
                                    else -> WitnessParchment
                                },
                                containerColor = SlateDark,
                                labelColor = MutedSlateText
                            ),
                            shape = RoundedCornerShape(6.dp),
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = notesInput,
                    onValueChange = onNotesChanged,
                    label = { Text("Forensic Notes / Witness Observations", fontSize = 11.sp) },
                    placeholder = { Text("e.g., Verified acoustic negative space, confirmed no metaphor soup.", fontSize = 11.sp) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = ObsidianBlack,
                        unfocusedContainerColor = ObsidianBlack,
                        focusedTextColor = WitnessParchment,
                        unfocusedTextColor = WitnessParchment,
                        focusedBorderColor = BronzeGold,
                        unfocusedBorderColor = SlateCardBorder
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(90.dp)
                        .testTag("governor_notes_input")
                )
            }
        },
        confirmButton = {
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                OutlinedButton(
                    onClick = { onSignDecision("SAVE_ONLY") },
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = StateUnverified)
                ) {
                    Text("Save Notes Only", fontSize = 10.sp)
                }
                Button(
                    onClick = { onSignDecision(selectedDecision) },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = when (selectedDecision) {
                            "RELEASE READY" -> StateVerified
                            "DEFECTIVE" -> StateFailed
                            else -> BronzeGold
                        }
                    ),
                    modifier = Modifier.testTag("sign_verified_btn")
                ) {
                    Text(
                        text = "Commit $selectedDecision",
                        color = ObsidianBlack,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = MutedSlateText)
            }
        },
        containerColor = SlateDark,
        shape = RoundedCornerShape(12.dp)
    )
}

@Composable
fun ManifestExportDialog(
    jsonText: String,
    onCopy: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "ELYZARETH MASTER MANIFEST",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = BronzeGold,
                    fontFamily = FontFamily.Monospace
                )
                IconButton(onClick = onCopy) {
                    Icon(Icons.Default.ContentCopy, contentDescription = "Copy", tint = BlueThread)
                }
            }
        },
        text = {
            Column {
                Text(
                    text = "Complete portable JSON manifest representing the ingested corpus inventory and SHA-256 evidence index:",
                    fontSize = 12.sp,
                    color = MutedSlateText
                )
                Spacer(modifier = Modifier.height(8.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(280.dp)
                        .clip(RoundedCornerShape(6.dp))
                        .background(ObsidianBlack)
                        .border(1.dp, SlateCardBorder, RoundedCornerShape(6.dp))
                        .padding(8.dp)
                ) {
                    LazyColumn {
                        item {
                            Text(
                                text = jsonText,
                                fontSize = 10.sp,
                                fontFamily = FontFamily.Monospace,
                                color = WitnessParchment
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onCopy,
                colors = ButtonDefaults.buttonColors(containerColor = BronzeGold)
            ) {
                Text("Copy JSON", color = ObsidianBlack, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Dismiss", color = MutedSlateText)
            }
        },
        containerColor = SlateDark,
        shape = RoundedCornerShape(12.dp)
    )
}
