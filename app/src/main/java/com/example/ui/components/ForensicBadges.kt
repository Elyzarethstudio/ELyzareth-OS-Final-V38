package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.BlueThread
import com.example.ui.theme.BronzeGold
import com.example.ui.theme.ObsidianBlack
import com.example.ui.theme.SlateCard
import com.example.ui.theme.SlateCardBorder
import com.example.ui.theme.StateDiscovered
import com.example.ui.theme.StateFailed
import com.example.ui.theme.StateNotMeasured
import com.example.ui.theme.StateParsed
import com.example.ui.theme.StateUnverified
import com.example.ui.theme.StateVerified
import com.example.ui.theme.WitnessParchment

@Composable
fun VerificationStatusBadge(
    status: String,
    modifier: Modifier = Modifier
) {
    val (bgColor, textColor) = when (status.uppercase()) {
        "PARSED" -> Pair(StateParsed.copy(alpha = 0.2f), StateParsed)
        "VERIFIED" -> Pair(StateVerified.copy(alpha = 0.2f), StateVerified)
        "FAILED" -> Pair(StateFailed.copy(alpha = 0.25f), StateFailed)
        "UNVERIFIED" -> Pair(StateUnverified.copy(alpha = 0.2f), StateUnverified)
        "DISCOVERED" -> Pair(StateDiscovered.copy(alpha = 0.2f), StateDiscovered)
        else -> Pair(StateNotMeasured.copy(alpha = 0.2f), StateNotMeasured)
    }

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(6.dp))
            .background(bgColor)
            .border(1.dp, textColor.copy(alpha = 0.5f), RoundedCornerShape(6.dp))
            .padding(horizontal = 8.dp, vertical = 3.dp)
    ) {
        Text(
            text = status.uppercase(),
            color = textColor,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 0.8.sp,
            fontFamily = FontFamily.Monospace
        )
    }
}

@Composable
fun LanguageBadge(
    language: String,
    modifier: Modifier = Modifier
) {
    val badgeColor = when (language) {
        "Hindi" -> BronzeGold
        "Multilingual" -> BlueThread
        else -> WitnessParchment.copy(alpha = 0.8f)
    }

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(4.dp))
            .background(badgeColor.copy(alpha = 0.15f))
            .border(1.dp, badgeColor.copy(alpha = 0.35f), RoundedCornerShape(4.dp))
            .padding(horizontal = 6.dp, vertical = 2.dp)
    ) {
        Text(
            text = language,
            color = badgeColor,
            fontSize = 11.sp,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
fun GateLockRow(
    g1: String = "NOT MEASURED",
    g2: String = "NOT MEASURED",
    g3: String = "NOT MEASURED",
    g4: String = "NOT MEASURED",
    g5: String = "NOT MEASURED",
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Text(
            text = "ZERO-BYPASS GATE PROGRESSION (G1–G5)",
            fontSize = 10.sp,
            fontWeight = FontWeight.SemiBold,
            color = StateNotMeasured,
            fontFamily = FontFamily.Monospace,
            letterSpacing = 0.5.sp,
            modifier = Modifier.padding(bottom = 4.dp)
        )
        Row(
            modifier = Modifier.horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            listOf("G1" to g1, "G2" to g2, "G3" to g3, "G4" to g4, "G5" to g5).forEach { (gate, score) ->
                val isLocked = score == "NOT MEASURED"
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .background(if (isLocked) SlateCard else StateParsed.copy(alpha = 0.2f))
                        .border(1.dp, if (isLocked) SlateCardBorder else StateParsed, RoundedCornerShape(4.dp))
                        .padding(horizontal = 6.dp, vertical = 3.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "$gate:",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = BronzeGold,
                            fontFamily = FontFamily.Monospace
                        )
                        Text(
                            text = " $score",
                            fontSize = 9.sp,
                            color = if (isLocked) StateNotMeasured else StateParsed,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun EvidenceHashBadge(
    hash: String,
    modifier: Modifier = Modifier
) {
    val displayHash = if (hash.length > 20) "${hash.take(12)}...${hash.takeLast(6)}" else hash
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(4.dp))
            .background(ObsidianBlack)
            .border(1.dp, SlateCardBorder, RoundedCornerShape(4.dp))
            .padding(horizontal = 6.dp, vertical = 2.dp)
    ) {
        Text(
            text = displayHash,
            fontSize = 10.sp,
            fontFamily = FontFamily.Monospace,
            color = BlueThread
        )
    }
}
