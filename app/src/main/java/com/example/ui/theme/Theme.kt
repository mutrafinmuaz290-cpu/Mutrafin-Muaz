package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme =
  darkColorScheme(
      primary = DarkPrimary,
      primaryContainer = DarkPrimaryContainer,
      onPrimaryContainer = DarkOnPrimaryContainer,
      secondaryContainer = DarkSecondaryContainer,
      onSecondaryContainer = DarkOnSecondaryContainer,
      background = DarkBackground,
      surface = DarkSurface,
      onBackground = LightText,
      onSurface = LightText,
      onSurfaceVariant = GrayText
  )

@Composable
fun MyApplicationTheme(
  // Always use dark theme for the cinematic MyTube look
  darkTheme: Boolean = true,
  // Disable dynamic color to maintain brand
  dynamicColor: Boolean = false,
  content: @Composable () -> Unit,
) {
  val colorScheme = DarkColorScheme

  MaterialTheme(colorScheme = colorScheme, typography = Typography, content = content)
}
