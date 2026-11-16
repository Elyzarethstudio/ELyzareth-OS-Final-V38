package com.example.ui.production

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material.icons.filled.Equalizer
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.MusicProductionEntity
import com.example.data.model.RegisteredAssetEntity
import com.example.data.model.VisualDirectionEntity
import com.example.ui.creative.ModuleHeaderBanner
import com.example.ui.creative.SuiteFeatureRow
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
import com.example.viewmodel.ElyzarethViewModel

/**
 * 5. MUSIC PRODUCTION CONSOLE (UI/UX Suite v1.0 Module 5)
 * Sections:
 * - Music Style Engineering, Suno / External Workflow, Audio Ingest & Stems,
 *   Arrangement & Mix, Production Notes, Production History
 */
@Composable
fun MusicProductionConsoleScreen(
    viewModel: ElyzarethViewModel,
    tracks: List<MusicProductionEntity>,
    modifier: Modifier = Modifier
) {
    var showAddForm by remember { mutableStateOf(false) }
    var songTitle by remember { mutableStateOf("") }
    var stylePrompt by remember { mutableStateOf("72 BPM, 4/4, dry felt piano, acoustic guitar, T60 0.42s, natural room air") }
    var stemsSummary by remember { mutableStateOf("01_LeadVocal_Dry.wav, 02_FeltPiano.wav, 03_AcousticGuitar.wav") }
    var mixNotes by remember { mutableStateOf("Preserve 1.8s P2 inter-stanza silence; -14 LUFS integrated.") }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(ObsidianBlack)
            .padding(horizontal = 16.dp)
            .testTag("music_production_screen"),
        contentPadding = PaddingValues(top = 14.dp, bottom = 32.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            ModuleHeaderBanner(
                badge = "MODULE 05 • PRODUCTION LAYER",
                title = "5. Music Production Console",
                subtitle = "Music Style Engineering • Suno / External Workflow • Stems & Mix",
                accent = BronzeGold
            )
        }

        item {
            Button(
                onClick = { showAddForm = !showAddForm },
                colors = ButtonDefaults.buttonColors(containerColor = BronzeGold),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.fillMaxWidth().testTag("ingest_production_track_btn")
            ) {
                Icon(Icons.Default.Equalizer, contentDescription = null, tint = ObsidianBlack, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Ingest New Production Stems & Style Prompt", color = ObsidianBlack, fontWeight = FontWeight.Bold, fontSize = 12.sp)
            }
        }

        if (showAddForm) {
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = SlateCard),
                    border = androidx.compose.foundation.BorderStroke(1.dp, BronzeGold),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        ProductionField("Song Title", songTitle) { songTitle = it }
                        ProductionField("Music Style Engineering Prompt", stylePrompt) { stylePrompt = it }
                        ProductionField("Audio Stems Bundle", stemsSummary) { stemsSummary = it }
                        ProductionField("Arrangement & Mix Notes", mixNotes) { mixNotes = it }
                        Button(
                            onClick = {
                                if (songTitle.isNotBlank()) {
                                    viewModel.addMusicProductionTrack(
                                        songTitle = songTitle,
                                        stylePrompt = stylePrompt,
                                        stemsSummary = stemsSummary,
                                        mixNotes = mixNotes,
                                        bpm = 72,
                                        meter = "4/4"
                                    )
                                    songTitle = ""
                                    showAddForm = false
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = StateParsed),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Save Production Session", color = ObsidianBlack, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        item {
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                SuiteFeatureRow("Music Style Engineering", "Envelope Class A • T60 0.42s Dry Wooden Room Calibration", BronzeGold)
                SuiteFeatureRow("Suno / External Workflow", "Offline-First Prompt Export & Stem Verification Bridge", BlueThread)
                SuiteFeatureRow("Audio Ingest & Stems", "24-bit/48kHz PCM Stem Validation (Vocal, Piano, Guitar, Room)", StateParsed)
                SuiteFeatureRow("Arrangement & Mix", "Mandatory P2 Inter-Stanza Negative Space Enforcement", BronzeGold)
            }
        }

        item {
            Text(
                text = "PRODUCTION HISTORY (${tracks.size} SESSIONS)",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = BronzeGold,
                fontFamily = FontFamily.Monospace
            )
        }

        items(tracks, key = { it.id }) { track ->
            Card(
                colors = CardDefaults.cardColors(containerColor = SlateDark),
                border = androidx.compose.foundation.BorderStroke(1.dp, SlateCardBorder),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(track.songTitle, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = WitnessParchment)
                        Text("${track.bpm} BPM • ${track.meter} • T60 ${track.t60DecaySeconds}s", fontSize = 10.sp, color = BronzeGold, fontFamily = FontFamily.Monospace)
                    }
                    Text("Style Prompt: ${track.styleEngineeringPrompt}", fontSize = 11.sp, color = BlueThread)
                    Text("Stems: ${track.stemsSummary}", fontSize = 11.sp, color = WitnessParchment.copy(alpha = 0.85f), fontFamily = FontFamily.Monospace)
                    Text("Mix Notes: ${track.arrangementAndMixNotes}", fontSize = 11.sp, color = MutedSlateText)
                }
            }
        }
    }
}

/**
 * 6. VISUAL DIRECTION STUDIO (UI/UX Suite v1.0 Module 6)
 * Sections:
 * - Album Artwork, Thumbnail Studio, Lyric Backgrounds, Visual Storyboard,
 *   Promotional Assets, Visual Library
 */
@Composable
fun VisualDirectionStudioScreen(
    viewModel: ElyzarethViewModel,
    visualAssets: List<VisualDirectionEntity>,
    modifier: Modifier = Modifier
) {
    var showAddVisual by remember { mutableStateOf(false) }
    var songTitle by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("Album Artwork") }
    var concept by remember { mutableStateOf("Macro study of blue thread across weathered oak in morning light.") }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(ObsidianBlack)
            .padding(horizontal = 16.dp)
            .testTag("visual_direction_screen"),
        contentPadding = PaddingValues(top = 14.dp, bottom = 32.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            ModuleHeaderBanner(
                badge = "MODULE 06 • VISUAL DIRECTION STUDIO",
                title = "6. Visual Direction Studio",
                subtitle = "Album Artwork • Thumbnail Studio • Lyric Backgrounds • Storyboard",
                accent = Color(0xFFF472B6)
            )
        }

        item {
            Button(
                onClick = { showAddVisual = !showAddVisual },
                colors = ButtonDefaults.buttonColors(containerColor = BronzeGold),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Default.Palette, contentDescription = null, tint = ObsidianBlack, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Create Visual Direction Spec", color = ObsidianBlack, fontWeight = FontWeight.Bold, fontSize = 12.sp)
            }
        }

        if (showAddVisual) {
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = SlateCard),
                    border = androidx.compose.foundation.BorderStroke(1.dp, BronzeGold),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        ProductionField("Song Title", songTitle) { songTitle = it }
                        ProductionField("Category (Album Artwork / Thumbnail / Storyboard)", category) { category = it }
                        ProductionField("Visual Concept", concept) { concept = it }
                        Button(
                            onClick = {
                                if (songTitle.isNotBlank()) {
                                    viewModel.addVisualAsset(songTitle, category, concept, "1:1 Master (3000x3000px)")
                                    songTitle = ""
                                    showAddVisual = false
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = StateParsed),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Save Visual Asset Spec", color = ObsidianBlack, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        item {
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                SuiteFeatureRow("Album Artwork", "3000x3000px Lossless Master Cover • Tactile Physical Anchor Framing", BronzeGold)
                SuiteFeatureRow("Thumbnail Studio", "16:9 & 9:16 Release Thumbnails • Zero Synthetic Neon Clutter", BlueThread)
                SuiteFeatureRow("Lyric Backgrounds", "High-Contrast Obsidian & Parchment Canvas for Devanagari/Latin", BronzeGold)
                SuiteFeatureRow("Visual Storyboard & Promo", "Frame-by-Frame Shot List Aligned to P2 Silence Gaps", StateVerified)
            }
        }

        items(visualAssets, key = { it.id }) { asset ->
            Card(
                colors = CardDefaults.cardColors(containerColor = SlateDark),
                border = androidx.compose.foundation.BorderStroke(1.dp, SlateCardBorder),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(asset.songTitle, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = WitnessParchment)
                        Text(asset.assetCategory, fontSize = 10.sp, color = BronzeGold, fontFamily = FontFamily.Monospace)
                    }
                    Text(asset.visualConcept, fontSize = 12.sp, color = WitnessParchment.copy(alpha = 0.85f))
                    Text("Palette: ${asset.colorPaletteSpec} • ${asset.aspectRatio}", fontSize = 10.sp, color = BlueThread, fontFamily = FontFamily.Monospace)
                }
            }
        }
    }
}

/**
 * 11. ASSET REGISTRY (UI/UX Suite v1.0 Module 11)
 * Sections:
 * - Register Asset, Audio Assets, Lyric Assets, Artwork Assets,
 *   Metadata & Checksum, Asset Relationships
 */
@Composable
fun AssetRegistryScreen(
    viewModel: ElyzarethViewModel,
    registeredAssets: List<RegisteredAssetEntity>,
    modifier: Modifier = Modifier
) {
    var showRegisterForm by remember { mutableStateOf(false) }
    var assetTitle by remember { mutableStateOf("") }
    var assetCategory by remember { mutableStateOf("AUDIO_ASSET") }
    var linkedSong by remember { mutableStateOf("Neela Dhaaga (The Blue Thread)") }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(ObsidianBlack)
            .padding(horizontal = 16.dp)
            .testTag("asset_registry_screen"),
        contentPadding = PaddingValues(top = 14.dp, bottom = 32.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            ModuleHeaderBanner(
                badge = "MODULE 11 • PRODUCTION & PROVENANCE",
                title = "11. Asset Registry",
                subtitle = "Register Asset • Audio/Lyric/Artwork Assets • Checksum & Relationships",
                accent = StateVerified
            )
        }

        item {
            Button(
                onClick = { showRegisterForm = !showRegisterForm },
                colors = ButtonDefaults.buttonColors(containerColor = StateVerified),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.fillMaxWidth().testTag("register_new_asset_btn")
            ) {
                Icon(Icons.Default.Inventory2, contentDescription = null, tint = ObsidianBlack, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Register Asset + Compute SHA-256 Checksum", color = ObsidianBlack, fontWeight = FontWeight.Bold, fontSize = 12.sp)
            }
        }

        if (showRegisterForm) {
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = SlateCard),
                    border = androidx.compose.foundation.BorderStroke(1.dp, StateVerified),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        ProductionField("Asset Filename / Title", assetTitle) { assetTitle = it }
                        ProductionField("Category (AUDIO_ASSET / LYRIC_ASSET / ARTWORK_ASSET)", assetCategory) { assetCategory = it }
                        ProductionField("Linked Song Title", linkedSong) { linkedSong = it }
                        Button(
                            onClick = {
                                if (assetTitle.isNotBlank()) {
                                    viewModel.registerNewAsset(
                                        title = assetTitle,
                                        category = assetCategory,
                                        linkedSongTitle = linkedSong,
                                        relationshipGraph = "Song: $linkedSong -> Category: $assetCategory -> Evidence Vault"
                                    )
                                    assetTitle = ""
                                    showRegisterForm = false
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = BronzeGold),
                            modifier = Modifier.fillMaxWidth().testTag("commit_asset_btn")
                        ) {
                            Icon(Icons.Default.AddCircle, contentDescription = null, tint = ObsidianBlack, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Commit to Asset Registry & Evidence Ledger", color = ObsidianBlack, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        item {
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                SuiteFeatureRow("Audio Assets", "Lossless WAV Stems & Master Mixes Linked by SHA-256", BronzeGold)
                SuiteFeatureRow("Lyric Assets", "Curated Devanagari & English Witness Manuscripts", BlueThread)
                SuiteFeatureRow("Artwork Assets", "Master Cover Art, Thumbnails & Storyboard Plates", StateVerified)
                SuiteFeatureRow("Metadata & Checksum + Relationships", "Full Parent-Child Provenance Graph per Song", StateParsed)
            }
        }

        items(registeredAssets, key = { it.id }) { asset ->
            Card(
                colors = CardDefaults.cardColors(containerColor = SlateDark),
                border = androidx.compose.foundation.BorderStroke(1.dp, SlateCardBorder),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(asset.title, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = WitnessParchment)
                        Text(asset.assetCode, fontSize = 10.sp, color = BronzeGold, fontFamily = FontFamily.Monospace)
                    }
                    Text("Category: ${asset.assetCategory} • Linked Song: ${asset.linkedSongTitle}", fontSize = 11.sp, color = BlueThread)
                    Text("SHA-256: ${asset.checksumSha256.take(36)}...", fontSize = 10.sp, color = StateParsed, fontFamily = FontFamily.Monospace)
                    Text("Graph: ${asset.relationshipGraph}", fontSize = 10.sp, color = MutedSlateText, fontFamily = FontFamily.Monospace)
                }
            }
        }
    }
}

@Composable
private fun ProductionField(label: String, value: String, onValueChange: (String) -> Unit) {
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
