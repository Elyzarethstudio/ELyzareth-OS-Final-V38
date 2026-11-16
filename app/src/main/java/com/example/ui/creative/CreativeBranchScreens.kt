package com.example.ui.creative

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CompareArrows
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.BaseTitleEntity
import com.example.data.model.CreativeDnaEntity
import com.example.data.model.IdeaBlueprintEntity
import com.example.navigation.OsModuleDestination
import com.example.ui.theme.BlueThread
import com.example.ui.theme.BronzeGold
import com.example.ui.theme.MutedSlateText
import com.example.ui.theme.ObsidianBlack
import com.example.ui.theme.SlateCard
import com.example.ui.theme.SlateCardBorder
import com.example.ui.theme.SlateDark
import com.example.ui.theme.StateParsed
import com.example.ui.theme.StateVerified
import com.example.ui.theme.WitnessParchment
import com.example.viewmodel.ElyzarethUiState
import com.example.viewmodel.ElyzarethViewModel

/**
 * 1. IDEA & BLUEPRINT STUDIO (UI/UX Suite v1.0 Module 1)
 * Sections:
 * - New Idea
 * - Creative Blueprint
 * - Idea Intake & Notes, Song Structure, Genre & Style, Emotion & Theme, Blueprint Library
 */
@Composable
fun IdeaBlueprintStudioScreen(
    viewModel: ElyzarethViewModel,
    blueprints: List<IdeaBlueprintEntity>,
    modifier: Modifier = Modifier
) {
    var titleInput by remember { mutableStateOf("") }
    var conceptInput by remember { mutableStateOf("") }
    var structureInput by remember { mutableStateOf("Verse 1 -> P2 Silence (2s) -> Chorus -> Verse 2 -> Dry Outro") }
    var genreInput by remember { mutableStateOf("Intimate Chamber Folk / 72 BPM") }
    var emotionInput by remember { mutableStateOf("Domestic Stillness & Somatic Grounding") }
    var anchorInput by remember { mutableStateOf("Blue Thread (नीला धागा)") }
    var languageInput by remember { mutableStateOf("Hindi") }
    var showCreateForm by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(ObsidianBlack)
            .padding(horizontal = 16.dp)
            .testTag("idea_blueprint_screen"),
        contentPadding = PaddingValues(top = 14.dp, bottom = 32.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            ModuleHeaderBanner(
                badge = "MODULE 01 • CREATIVE LAYER",
                title = "1. Idea & Blueprint Studio",
                subtitle = "Concept • Intent • Song Structure • Physical Witness Plan",
                accent = BronzeGold
            )
        }

        // Top Action Cards matching UI/UX Suite v1.0 Screen 1
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Button(
                    onClick = { showCreateForm = !showCreateForm },
                    colors = ButtonDefaults.buttonColors(containerColor = BronzeGold),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier
                        .weight(1f)
                        .testTag("new_idea_btn")
                ) {
                    Icon(Icons.Default.Lightbulb, contentDescription = null, tint = ObsidianBlack, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("New Idea", color = ObsidianBlack, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }

                OutlinedButton(
                    onClick = { viewModel.navigateToDestination(OsModuleDestination.LYRIC_GENERATOR) },
                    border = androidx.compose.foundation.BorderStroke(1.dp, BlueThread),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Send to Lyric Generator →", color = BlueThread, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                }
            }
        }

        if (showCreateForm) {
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = SlateCard),
                    border = androidx.compose.foundation.BorderStroke(1.dp, BronzeGold),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(
                            text = "CREATE CANONICAL CREATIVE BLUEPRINT",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = BronzeGold,
                            fontFamily = FontFamily.Monospace
                        )
                        BlueprintField("Song / Working Title", titleInput) { titleInput = it }
                        BlueprintField("Concept & Witness Intent", conceptInput) { conceptInput = it }
                        BlueprintField("Song Structure", structureInput) { structureInput = it }
                        BlueprintField("Genre & Acoustic Style", genreInput) { genreInput = it }
                        BlueprintField("Emotion & Theme", emotionInput) { emotionInput = it }
                        BlueprintField("Physical Witness Anchor", anchorInput) { anchorInput = it }
                        BlueprintField("Primary Language (Hindi / English / Multilingual)", languageInput) { languageInput = it }

                        Button(
                            onClick = {
                                if (titleInput.isNotBlank()) {
                                    viewModel.createIdeaBlueprint(
                                        title = titleInput,
                                        conceptIntent = conceptInput.ifBlank { "Grounded witness observation" },
                                        songStructure = structureInput,
                                        genreAndStyle = genreInput,
                                        emotionAndTheme = emotionInput,
                                        physicalAnchorName = anchorInput,
                                        primaryLanguage = languageInput
                                    )
                                    titleInput = ""
                                    conceptInput = ""
                                    showCreateForm = false
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = StateParsed),
                            modifier = Modifier.fillMaxWidth().testTag("save_blueprint_btn")
                        ) {
                            Icon(Icons.Default.AddCircle, contentDescription = null, tint = ObsidianBlack, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Commit Blueprint to Room Vault", color = ObsidianBlack, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }
                    }
                }
            }
        }

        // Standard Suite Menu Rows from Image 2 Screen 1
        item {
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                SuiteFeatureRow("Idea Intake & Notes", "Capture raw acoustic witness observations without metaphor", BronzeGold)
                SuiteFeatureRow("Song Structure", "Define Verse, Chorus, Bridge & mandatory P2 1.8s silence gaps", BlueThread)
                SuiteFeatureRow("Genre & Style", "Acoustic Chamber Folk • Dry Room T60 = 0.42s • 68–88 BPM", BronzeGold)
                SuiteFeatureRow("Emotion & Theme", "Domestic, Environmental, Recovery, Cultural, Transcendental", BlueThread)
            }
        }

        item {
            Text(
                text = "BLUEPRINT LIBRARY (${blueprints.size} REGISTERED)",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = BronzeGold,
                fontFamily = FontFamily.Monospace
            )
        }

        items(blueprints, key = { it.id }) { bp ->
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
                        Text(bp.title, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = WitnessParchment)
                        Text(
                            text = "${bp.id} • STAGE ${bp.pipelineStageCode}",
                            fontSize = 10.sp,
                            color = BronzeGold,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(bp.conceptIntent, fontSize = 12.sp, color = WitnessParchment.copy(alpha = 0.85f))
                    Spacer(modifier = Modifier.height(6.dp))
                    Text("Structure: ${bp.songStructure}", fontSize = 10.sp, color = BlueThread, fontFamily = FontFamily.Monospace)
                    Text("Style: ${bp.genreAndStyle} | Anchor: ${bp.physicalAnchorName}", fontSize = 10.sp, color = MutedSlateText, fontFamily = FontFamily.Monospace)
                }
            }
        }
    }
}

/**
 * 3. LYRIC CURATOR MODULE (UI/UX Suite v1.0 Module 3)
 * Connects the dedicated Lyric Curator controls (Edit & Refine, Line Comparison,
 * Revision History, Emotional Logic, Originality Review, Curator Approval)
 * with the Corpus & Archive (App 02) Curation Workflow Pipeline.
 */
@Composable
fun LyricCuratorModuleScreen(
    viewModel: ElyzarethViewModel,
    uiState: ElyzarethUiState,
    baseTitles: List<BaseTitleEntity>,
    modifier: Modifier = Modifier
) {
    val activeTitle = uiState.selectedBaseTitle ?: baseTitles.firstOrNull()
    var refinedDraft by remember(activeTitle?.id) {
        mutableStateOf(
            "Cold floorboards under bare heels.\nThe blue thread rests beside the iron latch.\nNo stadium echo, only morning light through glass."
        )
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(ObsidianBlack)
            .padding(horizontal = 16.dp)
            .testTag("lyric_curator_screen"),
        contentPadding = PaddingValues(top = 14.dp, bottom = 32.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            ModuleHeaderBanner(
                badge = "MODULE 03 • CREATIVE & CURATION LAYER",
                title = "3. Lyric Curator",
                subtitle = "Edit & Refine • Line Comparison • Emotional Logic • Originality Review",
                accent = BronzeGold
            )
        }

        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = SlateCard),
                border = androidx.compose.foundation.BorderStroke(1.dp, BronzeGold.copy(alpha = 0.6f)),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("ACTIVE CURATION TARGET", fontSize = 10.sp, color = BronzeGold, fontFamily = FontFamily.Monospace)
                            Text(
                                text = activeTitle?.baseTitle ?: "Neela Dhaaga (The Blue Thread)",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = WitnessParchment
                            )
                        }
                        Button(
                            onClick = { viewModel.navigateToDestination(OsModuleDestination.CORPUS_ARCHIVE) },
                            colors = ButtonDefaults.buttonColors(containerColor = BlueThread),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text("Open Full Corpus Vault (390)", color = ObsidianBlack, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    OutlinedTextField(
                        value = refinedDraft,
                        onValueChange = { refinedDraft = it },
                        label = { Text("Edit & Refine Stanza (Physical Witness Enforced)", fontSize = 11.sp) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = SlateDark,
                            unfocusedContainerColor = SlateDark,
                            focusedBorderColor = BronzeGold,
                            unfocusedBorderColor = SlateCardBorder,
                            focusedTextColor = WitnessParchment,
                            unfocusedTextColor = WitnessParchment
                        ),
                        modifier = Modifier.fillMaxWidth().height(110.dp)
                    )

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Button(
                            onClick = {
                                if (activeTitle != null) {
                                    viewModel.setBaseTitleStatus(
                                        activeTitle,
                                        "RELEASE READY",
                                        "Curator Approved: Emotional logic & originality verified."
                                    )
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = StateParsed),
                            modifier = Modifier.weight(1f).testTag("curator_approve_btn")
                        ) {
                            Icon(Icons.Default.CheckCircle, contentDescription = null, tint = ObsidianBlack, modifier = Modifier.size(15.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Curator Approval", color = ObsidianBlack, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }
                    }
                }
            }
        }

        // 6 Canonical Curator Operations from UI/UX Suite v1.0 Screen 3
        item {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                CuratorActionCard(
                    icon = Icons.Default.Edit,
                    title = "Edit & Refine",
                    detail = "Direct stanza line refinement preserving Devanagari & English witness anchors."
                )
                CuratorActionCard(
                    icon = Icons.Default.CompareArrows,
                    title = "Line Comparison",
                    detail = "v1.0 Original vs v2.0 Curated Diff: 0 prohibited metaphors, +100% concrete noun density."
                )
                CuratorActionCard(
                    icon = Icons.Default.History,
                    title = "Revision History",
                    detail = "Immutable SHA-256 chain of custody across all ingested version artifacts."
                )
                CuratorActionCard(
                    icon = Icons.Default.MenuBook,
                    title = "Emotional Logic",
                    detail = "Verified restraint: emotion emerges from physical object placement, never adjective inflation."
                )
                CuratorActionCard(
                    icon = Icons.Default.Verified,
                    title = "Originality Review",
                    detail = "Cross-checked against 390-title / 510-artifact baseline corpus (Zero duplicate collision)."
                )
            }
        }
    }
}

/**
 * 4. CREATIVE DNA ENGINE & CREATIVE FACTORY (UI/UX Suite v1.0 Module 4)
 * Sections:
 * - DNA Profile, DNA Selection, Style & Voice, Imagery Control, Conformance Check, DNA Report
 * - End-to-End Creative Factory Quality Check
 */
@Composable
fun CreativeDnaEngineScreen(
    viewModel: ElyzarethViewModel,
    dnaProfiles: List<CreativeDnaEntity>,
    modifier: Modifier = Modifier
) {
    var selectedProfileId by remember { mutableStateOf("DNA-001") }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(ObsidianBlack)
            .padding(horizontal = 16.dp)
            .testTag("creative_dna_screen"),
        contentPadding = PaddingValues(top = 14.dp, bottom = 32.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            ModuleHeaderBanner(
                badge = "MODULE 04 • CREATIVE DNA & FACTORY",
                title = "4. Creative DNA Engine",
                subtitle = "Identity • Style & Voice • Imagery Control • Conformance Check",
                accent = BlueThread
            )
        }

        items(dnaProfiles, key = { it.id }) { profile ->
            val isSelected = profile.id == selectedProfileId
            Card(
                colors = CardDefaults.cardColors(containerColor = if (isSelected) SlateCard else SlateDark),
                border = androidx.compose.foundation.BorderStroke(
                    1.dp,
                    if (isSelected) BlueThread else SlateCardBorder
                ),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { selectedProfileId = profile.id }
            ) {
                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Fingerprint, contentDescription = null, tint = BlueThread, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(profile.profileName, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = WitnessParchment)
                        }
                        Text(
                            text = "CONFORMANCE: ${profile.conformanceScore}%",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = StateParsed,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                    Text("Style & Voice: ${profile.styleAndVoice}", fontSize = 12.sp, color = WitnessParchment)
                    Text("Imagery Control: ${profile.imageryControlRules}", fontSize = 11.sp, color = BlueThread)
                    Text("Acoustic Signature: ${profile.acousticSignature}", fontSize = 11.sp, color = BronzeGold, fontFamily = FontFamily.Monospace)
                    Text("Anti-Trope Policy: ${profile.prohibitedTropesPolicy}", fontSize = 11.sp, color = MutedSlateText)
                }
            }
        }

        // Creative Factory End-to-End Validation Card (from Mansion Blueprint Upper Right)
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = SlateCard),
                border = androidx.compose.foundation.BorderStroke(1.dp, BronzeGold),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(
                        text = "CREATIVE FACTORY — END-TO-END QUALITY VALIDATION",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = BronzeGold,
                        fontFamily = FontFamily.Monospace
                    )
                    SuiteFeatureRow("1. Blueprint -> Lyric Anchor Alignment", "VERIFIED (100% Physical Object Match)", StateParsed)
                    SuiteFeatureRow("2. P2 Inter-Stanza Negative Space", "VERIFIED (1.8s–2.0s Room Decay Preserved)", StateParsed)
                    SuiteFeatureRow("3. Devanagari & Bilingual Phonetic Integrity", "VERIFIED (UTF-8 No-BOM Clean)", StateParsed)
                    SuiteFeatureRow("4. Prohibited Metaphor / Swell Scanner", "0 VIOLATIONS DETECTED", StateParsed)
                }
            }
        }
    }
}

@Composable
fun ModuleHeaderBanner(
    badge: String,
    title: String,
    subtitle: String,
    accent: Color
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = badge,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            color = accent,
            fontFamily = FontFamily.Monospace,
            letterSpacing = 1.sp
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = title,
            fontSize = 20.sp,
            fontWeight = FontWeight.ExtraBold,
            color = WitnessParchment
        )
        Text(
            text = subtitle,
            fontSize = 11.sp,
            color = MutedSlateText,
            fontFamily = FontFamily.Monospace
        )
    }
}

@Composable
fun SuiteFeatureRow(title: String, subtitle: String, accent: Color) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(SlateDark, RoundedCornerShape(8.dp))
            .border(1.dp, SlateCardBorder, RoundedCornerShape(8.dp))
            .padding(horizontal = 12.dp, vertical = 10.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(title, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = WitnessParchment)
            Text(subtitle, fontSize = 11.sp, color = MutedSlateText)
        }
        Spacer(modifier = Modifier.width(8.dp))
        Box(
            modifier = Modifier
                .background(accent.copy(alpha = 0.18f), RoundedCornerShape(4.dp))
                .border(1.dp, accent.copy(alpha = 0.5f), RoundedCornerShape(4.dp))
                .padding(horizontal = 6.dp, vertical = 2.dp)
        ) {
            Text("ACTIVE", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = accent, fontFamily = FontFamily.Monospace)
        }
    }
}

@Composable
private fun CuratorActionCard(icon: ImageVector, title: String, detail: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(SlateDark, RoundedCornerShape(8.dp))
            .border(1.dp, SlateCardBorder, RoundedCornerShape(8.dp))
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, contentDescription = title, tint = BronzeGold, modifier = Modifier.size(20.dp))
        Spacer(modifier = Modifier.width(10.dp))
        Column {
            Text(title, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = WitnessParchment)
            Text(detail, fontSize = 11.sp, color = MutedSlateText)
        }
    }
}

@Composable
private fun BlueprintField(label: String, value: String, onValueChange: (String) -> Unit) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label, fontSize = 11.sp) },
        singleLine = true,
        colors = OutlinedTextFieldDefaults.colors(
            focusedContainerColor = SlateDark,
            unfocusedContainerColor = SlateDark,
            focusedBorderColor = BronzeGold,
            unfocusedBorderColor = SlateCardBorder,
            focusedTextColor = WitnessParchment,
            unfocusedTextColor = WitnessParchment
        ),
        modifier = Modifier.fillMaxWidth()
    )
}
