package com.example.ui.protocol

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.GateLockRow
import com.example.ui.theme.BlueThread
import com.example.ui.theme.BronzeGold
import com.example.ui.theme.MutedSlateText
import com.example.ui.theme.ObsidianBlack
import com.example.ui.theme.SlateCard
import com.example.ui.theme.SlateCardBorder
import com.example.ui.theme.SlateDark
import com.example.ui.theme.StateFailed
import com.example.ui.theme.StateNotMeasured
import com.example.ui.theme.StateParsed
import com.example.ui.theme.StateVerified
import com.example.ui.theme.WitnessParchment
import com.example.viewmodel.ElyzarethUiState
import com.example.viewmodel.ElyzarethViewModel

@Composable
fun ProtocolConsoleScreen(
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
        item {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(StateVerified)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "GOVERNANCE & PROTOCOL CONSOLE",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = StateVerified,
                        fontFamily = FontFamily.Monospace,
                        letterSpacing = 1.sp
                    )
                }
                Text(
                    text = "3.2.1.0 Protocol & Gate Audit",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = WitnessParchment
                )
                Text(
                    text = "Listen (3) → Evaluate (2) → Decide (1) → Freeze (0)",
                    fontSize = 12.sp,
                    color = BronzeGold,
                    fontFamily = FontFamily.Monospace
                )
            }
        }

        // Stop condition rule banner
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = SlateDark),
                border = androidx.compose.foundation.BorderStroke(1.dp, BronzeGold),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Shield, contentDescription = null, tint = BronzeGold, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "CURRENT MANDATE: STOP CONDITION ACTIVE",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = BronzeGold,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "The first pass is DISCOVERY + EVIDENCE ONLY. App 02 has successfully halted post-ingestion. No auto-cure, no file modification, and G1–G5 scores remain strictly NOT MEASURED until Human Governor confirmation.",
                        fontSize = 11.sp,
                        color = WitnessParchment,
                        lineHeight = 16.sp
                    )
                }
            }
        }

        // Protocol Step Indicators
        item {
            Text(
                text = "3.2.1.0 PROTOCOL WORKFLOW STEPS",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = BronzeGold,
                fontFamily = FontFamily.Monospace
            )
        }

        val protocolSteps = listOf(
            Triple(3, "3 — LISTEN (Acoustic Morphology)", "Forensic auditory check: STFT spectral centroids, room acoustics (T60 decay 0.42s dry), phase coherence, zero synthetic cinematic swells."),
            Triple(2, "2 — EVALUATE (Witness Constraints)", "Physical Witness Check: Blue thread, dry salt, porcelain, iron key. Zero metaphor soup or pop melodrama. 4-State Verification Model verification."),
            Triple(1, "1 — DECIDE (Human Governor Authority)", "User retains 100% final authority. Review candidate duplicates, orphan metadata, and Hindi / English witness vault classifications."),
            Triple(0, "0 — FREEZE (Final Master Lock)", "Lock G5 state master into catalog. Binary hash frozen in persistent Room Database. Zero variable drift permitted.")
        )

        protocolSteps.forEach { (stepNumber, title, desc) ->
            item {
                val isActive = uiState.protocolStep == stepNumber
                val isPassed = uiState.protocolStep < stepNumber

                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = if (isActive) SlateDark else SlateCard
                    ),
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp,
                        when {
                            isActive -> BlueThread
                            isPassed -> StateParsed
                            else -> SlateCardBorder
                        }
                    ),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        Box(
                            modifier = Modifier
                                .size(28.dp)
                                .clip(CircleShape)
                                .background(
                                    when {
                                        isPassed -> StateParsed.copy(alpha = 0.25f)
                                        isActive -> BlueThread.copy(alpha = 0.25f)
                                        else -> SlateCardBorder
                                    }
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            if (isPassed) {
                                Icon(Icons.Default.Check, contentDescription = null, tint = StateParsed, modifier = Modifier.size(16.dp))
                            } else {
                                Text(
                                    text = "$stepNumber",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isActive) BlueThread else StateNotMeasured,
                                    fontFamily = FontFamily.Monospace
                                )
                            }
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = title,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isActive) WitnessParchment else WitnessParchment.copy(alpha = 0.7f)
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = desc,
                                fontSize = 11.sp,
                                color = MutedSlateText,
                                lineHeight = 16.sp
                            )

                            if (isActive) {
                                Spacer(modifier = Modifier.height(8.dp))
                                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    Button(
                                        onClick = {
                                            if (stepNumber > 0) {
                                                viewModel.advanceProtocolStep(stepNumber - 1)
                                            }
                                        },
                                        colors = ButtonDefaults.buttonColors(containerColor = BlueThread),
                                        shape = RoundedCornerShape(6.dp),
                                        modifier = Modifier.testTag("advance_step_${stepNumber}")
                                    ) {
                                        Text(
                                            text = if (stepNumber == 1) "Confirm & Engage Step 0 (Freeze)" else "Complete Step $stepNumber",
                                            color = ObsidianBlack,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // 4-State Verification Model Card
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = SlateCard),
                border = androidx.compose.foundation.BorderStroke(1.dp, SlateCardBorder),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text(
                        text = "4-STATE VERIFICATION MODEL",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = BronzeGold,
                        fontFamily = FontFamily.Monospace
                    )
                    Spacer(modifier = Modifier.height(6.dp))

                    VerificationStateRow("1. SPEC", "Functional contract & witness boundaries defined.")
                    VerificationStateRow("2. IMPLEMENTED / UNVERIFIED", "Corpus parsed & hashed; awaiting forensic human audit.")
                    VerificationStateRow("3. VERIFIED", "Confirmed physical witness anchor & SHA-256 match.")
                    VerificationStateRow("4. FAILED", "Encoding mismatch, BOM offset error, or trope violation.")
                }
            }
        }

        // Gate locks
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = SlateCard),
                border = androidx.compose.foundation.BorderStroke(1.dp, SlateCardBorder),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    GateLockRow()
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "G1: Witness Transformation | G2: Read-Only Evaluator | G3: Performance/T60 | G4: Integrity | G5: Final Master Lock",
                        fontSize = 10.sp,
                        color = MutedSlateText,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }
        }
    }
}

@Composable
fun VerificationStateRow(name: String, desc: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 3.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = name, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = WitnessParchment, fontFamily = FontFamily.Monospace)
        Text(text = desc, fontSize = 10.sp, color = MutedSlateText)
    }
}
