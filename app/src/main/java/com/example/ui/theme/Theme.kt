package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val ElyzarethColorScheme = darkColorScheme(
  primary = BronzeGold,
  onPrimary = ObsidianBlack,
  primaryContainer = BronzeGoldMuted,
  onPrimaryContainer = WitnessParchment,
  secondary = BlueThread,
  onSecondary = ObsidianBlack,
  secondaryContainer = BlueThreadMuted,
  onSecondaryContainer = WitnessParchment,
  tertiary = StateParsed,
  background = ObsidianBlack,
  onBackground = WitnessParchment,
  surface = SlateDark,
  onSurface = WitnessParchment,
  surfaceVariant = SlateCard,
  onSurfaceVariant = MutedSlateText,
  outline = SlateCardBorder
)

@Composable
fun MyApplicationTheme(
  darkTheme: Boolean = isSystemInDarkTheme(),
  dynamicColor: Boolean = false,
  content: @Composable () -> Unit,
) {
  MaterialTheme(
    colorScheme = ElyzarethColorScheme,
    typography = Typography,
    content = content
  )
}

