package com.example.ui.curator

import android.content.Context
import android.content.Intent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Archive
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.RateReview
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Undo
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ArtifactEntity
import com.example.data.model.BaseTitleEntity
import com.example.ui.theme.BlueThread
import com.example.ui.theme.BronzeGold
import com.example.ui.theme.MutedSlateText
import com.example.ui.theme.ObsidianBlack
import com.example.ui.theme.SlateCard
import com.example.ui.theme.SlateCardBorder
import com.example.ui.theme.SlateDark
import com.example.ui.theme.StateFailed
import com.example.ui.theme.StateUnverified
import com.example.ui.theme.StateVerified
import com.example.ui.theme.WitnessParchment

data class LyricViewData(
    val title: String,
    val language: String,
    val text: String,
    val hasAmbiguity: Boolean = false,
    val ambiguityNotes: String = ""
)

data class ProblemDiagnosticData(
    val itemTitle: String,
    val filePath: String,
    val fileHash: String,
    val fileSizeBytes: Long,
    val parseError: String?,
    val reviewReason: String?,
    val rawContentSnippet: String,
    val onRepair: () -> Unit,
    val onReject: () -> Unit
)

data class RepairReplaceData(
    val itemTitle: String,
    val onTriggerRepair: () -> Unit,
    val onAcceptCurationRequired: () -> Unit
)

enum class WorkflowCategory {
    RELEASE_READY,
    CURATION_REQUIRED,
    DEFECTIVE
}

fun resolveWorkflowCategory(status: String, hasAmbiguity: Boolean = false): WorkflowCategory {
    val upper = status.uppercase()
    return when {
        upper in listOf("DEFECTIVE", "FAILED", "REJECTED & ARCHIVED", "REJECTED", "ARCHIVED") ->
            WorkflowCategory.DEFECTIVE
        upper in listOf("RELEASE READY", "RELEASE_READY", "RELEASED & FROZEN", "FROZEN", "VERIFIED") && !hasAmbiguity ->
            WorkflowCategory.RELEASE_READY
        else ->
            WorkflowCategory.CURATION_REQUIRED
    }
}

/**
 * Sequential Curator Workflow Pipeline Header and Interactive Step Tracker
 * Displays the 3 mandated workflows:
 * 🟢 Release Ready: View lyric → Copy → Export/Save → Release/Freeze
 * 🟡 Curation Required: View lyric → Copy → Review → Approve / Return to Curation
 * 🔴 Defective: View problem → Copy/export evidence → Repair/Replace decision → Reject/Archive
 */
@Composable
fun CurationWorkflowPipeline(
    status: String,
    hasAmbiguity: Boolean = false,
    onViewLyric: () -> Unit,
    onCopyLyric: () -> Unit,
    onExportSave: () -> Unit,
    onReleaseFreeze: () -> Unit,
    onOpenReview: () -> Unit,
    onApprove: () -> Unit,
    onReturnToCuration: () -> Unit,
    onViewProblem: () -> Unit,
    onCopyExportEvidence: () -> Unit,
    onRepairReplaceDecision: () -> Unit,
    onRejectArchive: () -> Unit,
    modifier: Modifier = Modifier
) {
    val category = resolveWorkflowCategory(status, hasAmbiguity)

    Card(
        colors = CardDefaults.cardColors(
            containerColor = when (category) {
                WorkflowCategory.RELEASE_READY -> StateVerified.copy(alpha = 0.08f)
                WorkflowCategory.CURATION_REQUIRED -> StateUnverified.copy(alpha = 0.08f)
                WorkflowCategory.DEFECTIVE -> StateFailed.copy(alpha = 0.08f)
            }
        ),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            when (category) {
                WorkflowCategory.RELEASE_READY -> StateVerified.copy(alpha = 0.4f)
                WorkflowCategory.CURATION_REQUIRED -> StateUnverified.copy(alpha = 0.4f)
                WorkflowCategory.DEFECTIVE -> StateFailed.copy(alpha = 0.4f)
            }
        ),
        shape = RoundedCornerShape(8.dp),
        modifier = modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(10.dp)) {
            // Pipeline Label Banner
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(
                                when (category) {
                                    WorkflowCategory.RELEASE_READY -> StateVerified
                                    WorkflowCategory.CURATION_REQUIRED -> StateUnverified
                                    WorkflowCategory.DEFECTIVE -> StateFailed
                                }
                            )
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = when (category) {
                            WorkflowCategory.RELEASE_READY -> "RELEASE READY PIPELINE"
                            WorkflowCategory.CURATION_REQUIRED -> "CURATION REQUIRED PIPELINE"
                            WorkflowCategory.DEFECTIVE -> "DEFECTIVE ARTIFACT PIPELINE"
                        },
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = when (category) {
                            WorkflowCategory.RELEASE_READY -> StateVerified
                            WorkflowCategory.CURATION_REQUIRED -> StateUnverified
                            WorkflowCategory.DEFECTIVE -> StateFailed
                        },
                        fontFamily = FontFamily.Monospace
                    )
                }

                Text(
                    text = "STEP-BY-STEP GATE",
                    fontSize = 9.sp,
                    color = MutedSlateText,
                    fontFamily = FontFamily.Monospace
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Pipeline Horizontal Sequence of Step Buttons
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                when (category) {
                    WorkflowCategory.RELEASE_READY -> {
                        // 🟢 Release Ready: View lyric → Copy → Export/Save → Release/Freeze
                        WorkflowStepButton(
                            stepNumber = "1",
                            label = "View lyric",
                            icon = Icons.Default.Visibility,
                            color = StateVerified,
                            onClick = onViewLyric,
                            testTag = "workflow_view_lyric"
                        )
                        PipelineArrow(color = StateVerified)
                        WorkflowStepButton(
                            stepNumber = "2",
                            label = "Copy",
                            icon = Icons.Default.ContentCopy,
                            color = StateVerified,
                            onClick = onCopyLyric,
                            testTag = "workflow_copy"
                        )
                        PipelineArrow(color = StateVerified)
                        WorkflowStepButton(
                            stepNumber = "3",
                            label = "Export/Save",
                            icon = Icons.Default.Share,
                            color = StateVerified,
                            onClick = onExportSave,
                            testTag = "workflow_export_save"
                        )
                        PipelineArrow(color = StateVerified)
                        WorkflowStepButton(
                            stepNumber = "4",
                            label = if (status.uppercase().contains("FROZEN")) "Frozen" else "Release/Freeze",
                            icon = Icons.Default.Lock,
                            color = StateVerified,
                            onClick = onReleaseFreeze,
                            testTag = "workflow_release_freeze",
                            isPrimary = true
                        )
                    }

                    WorkflowCategory.CURATION_REQUIRED -> {
                        // 🟡 Curation Required: View lyric → Copy → Review → Approve / Return to Curation
                        WorkflowStepButton(
                            stepNumber = "1",
                            label = "View lyric",
                            icon = Icons.Default.Visibility,
                            color = StateUnverified,
                            onClick = onViewLyric,
                            testTag = "workflow_view_lyric"
                        )
                        PipelineArrow(color = StateUnverified)
                        WorkflowStepButton(
                            stepNumber = "2",
                            label = "Copy",
                            icon = Icons.Default.ContentCopy,
                            color = StateUnverified,
                            onClick = onCopyLyric,
                            testTag = "workflow_copy"
                        )
                        PipelineArrow(color = StateUnverified)
                        WorkflowStepButton(
                            stepNumber = "3",
                            label = "Review",
                            icon = Icons.Default.RateReview,
                            color = StateUnverified,
                            onClick = onOpenReview,
                            testTag = "workflow_review"
                        )
                        PipelineArrow(color = StateUnverified)
                        WorkflowStepButton(
                            stepNumber = "4a",
                            label = "Approve",
                            icon = Icons.Default.CheckCircle,
                            color = StateVerified,
                            onClick = onApprove,
                            testTag = "workflow_approve",
                            isPrimary = true
                        )
                        WorkflowStepButton(
                            stepNumber = "4b",
                            label = "Return to Curation",
                            icon = Icons.Default.Undo,
                            color = StateUnverified,
                            onClick = onReturnToCuration,
                            testTag = "workflow_return_curation"
                        )
                    }

                    WorkflowCategory.DEFECTIVE -> {
                        // 🔴 Defective: View problem → Copy/export evidence → Repair/Replace decision → Reject/Archive
                        WorkflowStepButton(
                            stepNumber = "1",
                            label = "View problem",
                            icon = Icons.Default.Warning,
                            color = StateFailed,
                            onClick = onViewProblem,
                            testTag = "workflow_view_problem"
                        )
                        PipelineArrow(color = StateFailed)
                        WorkflowStepButton(
                            stepNumber = "2",
                            label = "Copy/export evidence",
                            icon = Icons.Default.ContentCopy,
                            color = StateFailed,
                            onClick = onCopyExportEvidence,
                            testTag = "workflow_copy_export_evidence"
                        )
                        PipelineArrow(color = StateFailed)
                        WorkflowStepButton(
                            stepNumber = "3",
                            label = "Repair/Replace",
                            icon = Icons.Default.Build,
                            color = BronzeGold,
                            onClick = onRepairReplaceDecision,
                            testTag = "workflow_repair_replace"
                        )
                        PipelineArrow(color = StateFailed)
                        WorkflowStepButton(
                            stepNumber = "4",
                            label = "Reject/Archive",
                            icon = Icons.Default.Archive,
                            color = StateFailed,
                            onClick = onRejectArchive,
                            testTag = "workflow_reject_archive",
                            isPrimary = true
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun WorkflowStepButton(
    stepNumber: String,
    label: String,
    icon: ImageVector,
    color: Color,
    onClick: () -> Unit,
    testTag: String,
    isPrimary: Boolean = false
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(6.dp))
            .background(if (isPrimary) color.copy(alpha = 0.25f) else SlateDark)
            .border(1.dp, color.copy(alpha = if (isPrimary) 0.8f else 0.4f), RoundedCornerShape(6.dp))
            .clickable { onClick() }
            .padding(horizontal = 8.dp, vertical = 6.dp)
            .testTag(testTag)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(16.dp)
                    .clip(CircleShape)
                    .background(color.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = stepNumber,
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold,
                    color = color,
                    fontFamily = FontFamily.Monospace
                )
            }
            Spacer(modifier = Modifier.width(5.dp))
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = color,
                modifier = Modifier.size(12.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = label,
                fontSize = 11.sp,
                fontWeight = if (isPrimary) FontWeight.Bold else FontWeight.Medium,
                color = if (isPrimary) WitnessParchment else color
            )
        }
    }
}

@Composable
fun PipelineArrow(color: Color) {
    Text(
        text = "→",
        fontSize = 12.sp,
        fontWeight = FontWeight.Bold,
        color = color.copy(alpha = 0.6f)
    )
}

/**
 * Step 1: Curator Lyric Viewer Dialog
 * Displays lyrics with phonetic anchor markers, word count, line count, language, and quick copy/export buttons.
 */
@Composable
fun CuratorLyricViewerDialog(
    title: String,
    language: String,
    lyricContent: String,
    hasAmbiguity: Boolean = false,
    ambiguityNotes: String = "",
    onDismiss: () -> Unit,
    onCopy: () -> Unit,
    onExportSave: () -> Unit
) {
    val lines = lyricContent.lines().filter { it.isNotBlank() }
    val wordCount = lyricContent.split(Regex("\\s+")).filter { it.isNotBlank() }.size

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "LYRIC VIEWER & WITNESS ANCHORS",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = BronzeGold,
                        fontFamily = FontFamily.Monospace
                    )
                    Text(
                        text = "$language • ${lines.size} lines • $wordCount words",
                        fontSize = 10.sp,
                        color = BlueThread,
                        fontFamily = FontFamily.Monospace
                    )
                }
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = title,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = WitnessParchment
                )
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
            ) {
                if (hasAmbiguity && ambiguityNotes.isNotBlank()) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(4.dp))
                            .background(StateUnverified.copy(alpha = 0.15f))
                            .padding(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.Warning,
                            contentDescription = null,
                            tint = StateUnverified,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "AMBIGUITY NOTICE: $ambiguityNotes",
                            fontSize = 11.sp,
                            color = StateUnverified
                        )
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                }

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 180.dp, max = 340.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(ObsidianBlack)
                        .border(1.dp, SlateCardBorder, RoundedCornerShape(8.dp))
                        .padding(12.dp)
                ) {
                    Text(
                        text = lyricContent.ifBlank { "(No lyric text discovered for this item)" },
                        fontSize = 13.sp,
                        lineHeight = 20.sp,
                        color = WitnessParchment,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }
        },
        confirmButton = {
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                Button(
                    onClick = onCopy,
                    colors = ButtonDefaults.buttonColors(containerColor = BronzeGold),
                    modifier = Modifier.testTag("lyric_viewer_copy_btn")
                ) {
                    Icon(Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(13.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Copy", color = ObsidianBlack, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
                OutlinedButton(
                    onClick = onExportSave,
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = BlueThread)
                ) {
                    Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(13.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Export/Save", fontSize = 11.sp)
                }
                TextButton(onClick = onDismiss) {
                    Text("Close", color = MutedSlateText, fontSize = 11.sp)
                }
            }
        },
        containerColor = SlateDark,
        shape = RoundedCornerShape(12.dp)
    )
}

/**
 * Step 1: Defective Problem Diagnostics Dialog
 * Displays defect analysis, corrupt bytes/parse error, SHA-256 mismatch, and evidence logs.
 */
@Composable
fun CuratorProblemDiagnosticDialog(
    itemTitle: String,
    filePath: String,
    fileHash: String,
    fileSizeBytes: Long,
    parseError: String?,
    reviewReason: String?,
    rawContentSnippet: String,
    onDismiss: () -> Unit,
    onCopyEvidence: () -> Unit,
    onExportEvidence: () -> Unit,
    onRepairReplace: () -> Unit,
    onRejectArchive: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "DEFECT FORENSIC DIAGNOSTIC",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = StateFailed,
                        fontFamily = FontFamily.Monospace
                    )
                    Text(
                        text = "PARITY / SYNTAX FAILURE",
                        fontSize = 9.sp,
                        color = StateFailed,
                        fontFamily = FontFamily.Monospace
                    )
                }
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = itemTitle,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold,
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
                // Defect Callout
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(6.dp))
                        .background(StateFailed.copy(alpha = 0.15f))
                        .border(1.dp, StateFailed.copy(alpha = 0.4f), RoundedCornerShape(6.dp))
                        .padding(10.dp)
                ) {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Warning, contentDescription = null, tint = StateFailed, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "IDENTIFIED PROBLEM:",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = StateFailed,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = parseError ?: reviewReason ?: "Unspecified schema parity breach or corrupted byte boundary.",
                            fontSize = 12.sp,
                            color = WitnessParchment
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = "FORENSIC EVIDENCE PARAMETERS:",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = BronzeGold,
                    fontFamily = FontFamily.Monospace
                )
                Spacer(modifier = Modifier.height(4.dp))

                DetailRow("Path", filePath)
                DetailRow("SHA-256", fileHash.ifBlank { "NOT COMPUTED (UNREADABLE)" })
                DetailRow("Size", "$fileSizeBytes bytes")

                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = "OBSERVED CORRUPT / PARSED BYTES SNIPPET:",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = BlueThread,
                    fontFamily = FontFamily.Monospace
                )
                Spacer(modifier = Modifier.height(4.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 120.dp)
                        .clip(RoundedCornerShape(6.dp))
                        .background(ObsidianBlack)
                        .border(1.dp, SlateCardBorder, RoundedCornerShape(6.dp))
                        .padding(8.dp)
                ) {
                    Text(
                        text = rawContentSnippet.ifBlank { "(Empty or zero-byte payload encountered)" },
                        fontSize = 11.sp,
                        fontFamily = FontFamily.Monospace,
                        color = WitnessParchment
                    )
                }
            }
        },
        confirmButton = {
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                Button(
                    onClick = onRepairReplace,
                    colors = ButtonDefaults.buttonColors(containerColor = BronzeGold),
                    modifier = Modifier.testTag("diag_repair_replace_btn")
                ) {
                    Icon(Icons.Default.Build, contentDescription = null, modifier = Modifier.size(12.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Repair/Replace", color = ObsidianBlack, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                }
                Button(
                    onClick = onRejectArchive,
                    colors = ButtonDefaults.buttonColors(containerColor = StateFailed),
                    modifier = Modifier.testTag("diag_reject_archive_btn")
                ) {
                    Icon(Icons.Default.Archive, contentDescription = null, modifier = Modifier.size(12.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Reject/Archive", color = ObsidianBlack, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                }
                OutlinedButton(
                    onClick = onCopyEvidence,
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = BlueThread)
                ) {
                    Text("Copy Evidence", fontSize = 10.sp)
                }
                TextButton(onClick = onDismiss) {
                    Text("Close", color = MutedSlateText, fontSize = 10.sp)
                }
            }
        },
        containerColor = SlateDark,
        shape = RoundedCornerShape(12.dp)
    )
}

/**
 * Step 3: Repair / Replace Decision Dialog
 */
@Composable
fun CuratorRepairReplaceDialog(
    itemTitle: String,
    onDismiss: () -> Unit,
    onTriggerRepairScan: () -> Unit,
    onAcceptAsCurationRequired: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "REPAIR / REPLACE DECISION GATE",
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = BronzeGold,
                fontFamily = FontFamily.Monospace
            )
        },
        text = {
            Column {
                Text(
                    text = "Select an empirical remediation strategy for '$itemTitle':",
                    fontSize = 12.sp,
                    color = WitnessParchment
                )
                Spacer(modifier = Modifier.height(12.dp))

                Card(
                    colors = CardDefaults.cardColors(containerColor = ObsidianBlack),
                    border = androidx.compose.foundation.BorderStroke(1.dp, SlateCardBorder),
                    shape = RoundedCornerShape(6.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            onTriggerRepairScan()
                            onDismiss()
                        }
                ) {
                    Row(
                        modifier = Modifier.padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Refresh, contentDescription = null, tint = StateVerified, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text("Produce New Artifact & Run Validation Pipeline", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = WitnessParchment)
                            Text("Generates new replacement artifact → Computes SHA-256 parity → Runs PCM / phonetic validation → Promotes to CURATION REQUIRED for Human Governor review.", fontSize = 10.sp, color = MutedSlateText)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Card(
                    colors = CardDefaults.cardColors(containerColor = ObsidianBlack),
                    border = androidx.compose.foundation.BorderStroke(1.dp, SlateCardBorder),
                    shape = RoundedCornerShape(6.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            onAcceptAsCurationRequired()
                            onDismiss()
                        }
                ) {
                    Row(
                        modifier = Modifier.padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Undo, contentDescription = null, tint = StateUnverified, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text("Downgrade to Curation Required", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = WitnessParchment)
                            Text("Allows manual human governor intervention and line-by-line lyric recovery.", fontSize = 10.sp, color = MutedSlateText)
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = MutedSlateText)
            }
        },
        containerColor = SlateDark,
        shape = RoundedCornerShape(12.dp)
    )
}
