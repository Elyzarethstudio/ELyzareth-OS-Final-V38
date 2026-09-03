package com.example.ui.generator

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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.DEFAULT_PHYSICAL_ANCHORS
import com.example.data.model.WitnessPillar
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

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun LyricGeneratorScreen(
    viewModel: ElyzarethViewModel,
    uiState: ElyzarethUiState,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(ObsidianBlack)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // App Header
        item {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(BlueThread)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "ELYZARETH OS v2.0",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = BlueThread,
                        fontFamily = FontFamily.Monospace,
                        letterSpacing = 1.sp
                    )
                }
                Text(
                    text = "App 01 — Lyric Generator",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = WitnessParchment
                )
                Text(
                    text = "Grounded Physical Witness Anchors | 5 Pillars | Anti-Trope Forensic Validator",
                    fontSize = 11.sp,
                    color = BronzeGold,
                    fontFamily = FontFamily.Monospace
                )
            }
        }

        // Section 1: Physical Witness Anchor Selection
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = SlateCard),
                border = androidx.compose.foundation.BorderStroke(1.dp, SlateCardBorder),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text(
                        text = "1. SELECT PHYSICAL WITNESS ANCHOR",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = BronzeGold,
                        fontFamily = FontFamily.Monospace
                    )
                    Text(
                        text = "Absolute boundary: Every stanza must anchor in tangible physical objects.",
                        fontSize = 11.sp,
                        color = WitnessParchment.copy(alpha = 0.7f)
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    FlowRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        DEFAULT_PHYSICAL_ANCHORS.forEach { anchor ->
                            val isSelected = uiState.generatorSelectedAnchor.id == anchor.id
                            FilterChip(
                                selected = isSelected,
                                onClick = { viewModel.setGeneratorAnchor(anchor) },
                                label = {
                                    Column {
                                        Text(anchor.name, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                                        Text(anchor.hindiAlias, fontSize = 9.sp)
                                    }
                                },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = BlueThread.copy(alpha = 0.25f),
                                    selectedLabelColor = BlueThread,
                                    containerColor = SlateDark,
                                    labelColor = WitnessParchment
                                ),
                                shape = RoundedCornerShape(6.dp)
                            )
                        }
                    }
                }
            }
        }

        // Section 2: 5 Witness Pillars
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = SlateCard),
                border = androidx.compose.foundation.BorderStroke(1.dp, SlateCardBorder),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text(
                        text = "2. SELECT WITNESS PILLAR",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = BronzeGold,
                        fontFamily = FontFamily.Monospace
                    )
                    Spacer(modifier = Modifier.height(6.dp))

                    FlowRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        WitnessPillar.values().forEach { pillar ->
                            val isSelected = uiState.generatorSelectedPillar == pillar
                            FilterChip(
                                selected = isSelected,
                                onClick = { viewModel.setGeneratorPillar(pillar) },
                                label = { Text(pillar.title, fontSize = 11.sp) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = BronzeGold.copy(alpha = 0.25f),
                                    selectedLabelColor = BronzeGold,
                                    containerColor = SlateDark,
                                    labelColor = WitnessParchment
                                ),
                                shape = RoundedCornerShape(6.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = uiState.generatorSelectedPillar.description,
                        fontSize = 11.sp,
                        color = BlueThread
                    )
                }
            }
        }

        // Section 3: Meter, BPM & Seed Line
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = SlateCard),
                border = androidx.compose.foundation.BorderStroke(1.dp, SlateCardBorder),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text(
                        text = "3. TEMPO & ACOUSTIC METER",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = BronzeGold,
                        fontFamily = FontFamily.Monospace
                    )
                    Spacer(modifier = Modifier.height(6.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Meter Chips
                        listOf("4/4", "3/4", "6/8").forEach { meter ->
                            FilterChip(
                                selected = uiState.generatorMeter == meter,
                                onClick = { viewModel.setGeneratorMeter(meter) },
                                label = { Text(meter, fontSize = 11.sp) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = BlueThread.copy(alpha = 0.2f),
                                    selectedLabelColor = BlueThread,
                                    containerColor = SlateDark,
                                    labelColor = WitnessParchment
                                )
                            )
                        }

                        Spacer(modifier = Modifier.weight(1f))

                        // BPM Chips
                        listOf(68, 72, 80, 88).forEach { bpm ->
                            FilterChip(
                                selected = uiState.generatorBpm == bpm,
                                onClick = { viewModel.setGeneratorBpm(bpm) },
                                label = { Text("$bpm BPM", fontSize = 10.sp) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = BronzeGold.copy(alpha = 0.2f),
                                    selectedLabelColor = BronzeGold,
                                    containerColor = SlateDark,
                                    labelColor = WitnessParchment
                                )
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = uiState.generatorCustomSeed,
                        onValueChange = { viewModel.setGeneratorSeed(it) },
                        placeholder = { Text("Optional seed witness line (e.g. Salt the steps before dusk)", fontSize = 11.sp) },
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
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Button(
                        onClick = { viewModel.generateLyrics() },
                        colors = ButtonDefaults.buttonColors(containerColor = BronzeGold),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("generate_lyrics_btn")
                    ) {
                        Icon(
                            Icons.Default.AutoAwesome,
                            contentDescription = null,
                            tint = ObsidianBlack,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Synthesize Concrete Witness Stanzas",
                            color = ObsidianBlack,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                    }
                }
            }
        }

        // Section 4: Generated Concrete Stanzas
        item {
            Text(
                text = "CONCRETE WITNESS STANZAS (UNVARNISHED)",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = BronzeGold,
                fontFamily = FontFamily.Monospace
            )
        }

        items(uiState.generatedStanzas) { stanza ->
            Card(
                colors = CardDefaults.cardColors(containerColor = SlateDark),
                border = androidx.compose.foundation.BorderStroke(1.dp, SlateCardBorder),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "STANZA ${stanza.stanzaNumber} [ENVELOPE CLASS A / P2 GAP]",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = BronzeGold,
                            fontFamily = FontFamily.Monospace
                        )
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = StateParsed,
                                modifier = Modifier.size(13.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "WITNESS PURITY: ${stanza.witnessPurityScore}%",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = StateParsed,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    stanza.lines.forEach { line ->
                        Text(
                            text = line,
                            fontSize = 13.sp,
                            color = WitnessParchment,
                            fontFamily = FontFamily.Serif,
                            lineHeight = 20.sp,
                            modifier = Modifier.padding(vertical = 2.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Anchor: ${stanza.physicalAnchorUsed} | Prohibitions: Zero Tropes Detected",
                        fontSize = 10.sp,
                        color = MutedSlateText,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }
        }

        // Section 5: Real-Time Trope Scanner & Forensic Evaluator
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = SlateCard),
                border = androidx.compose.foundation.BorderStroke(1.dp, BlueThread.copy(alpha = 0.5f)),
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
                            text = "FORENSIC TROPE SCANNER (LIVE CHECK)",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = BlueThread,
                            fontFamily = FontFamily.Monospace
                        )
                        if (uiState.customLyricEvaluation != null) {
                            val (purity, violations) = uiState.customLyricEvaluation
                            val statusColor = if (violations.isEmpty()) StateParsed else StateFailed
                            Text(
                                text = "PURITY: $purity%",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = statusColor,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                    }

                    Text(
                        text = "Automated scanner actively detects prohibited 'metaphor soup', 'cinematic swells', or pop melodrama.",
                        fontSize = 11.sp,
                        color = WitnessParchment.copy(alpha = 0.7f)
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = uiState.customLyricInput,
                        onValueChange = { viewModel.setCustomLyricInput(it) },
                        placeholder = { Text("Paste or type lyric verse to run automated witness purity scan...", fontSize = 11.sp) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = ObsidianBlack,
                            unfocusedContainerColor = ObsidianBlack,
                            focusedBorderColor = BlueThread,
                            unfocusedBorderColor = SlateCardBorder,
                            focusedTextColor = WitnessParchment,
                            unfocusedTextColor = WitnessParchment
                        ),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(90.dp)
                            .testTag("custom_lyric_input")
                    )

                    if (uiState.customLyricEvaluation != null) {
                        val (_, violations) = uiState.customLyricEvaluation
                        Spacer(modifier = Modifier.height(8.dp))
                        if (violations.isEmpty()) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.CheckCircle, contentDescription = null, tint = StateParsed, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "PASS: Stanza strictly compliant with Elyzareth Witness Constraints.",
                                    fontSize = 11.sp,
                                    color = StateParsed,
                                    fontFamily = FontFamily.Monospace
                                )
                            }
                        } else {
                            violations.forEach { infraction ->
                                Row(
                                    modifier = Modifier.padding(vertical = 2.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(Icons.Default.Warning, contentDescription = null, tint = StateFailed, modifier = Modifier.size(13.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = infraction,
                                        fontSize = 11.sp,
                                        color = StateFailed,
                                        fontFamily = FontFamily.Monospace
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
