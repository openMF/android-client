/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.core.designsystem.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightThemeColors = lightColorScheme(
    primary = BluePrimary,
    onPrimary = Color.White,
    error = Color.Red,
    background = Color.White,
    onSurface = BlueSecondary,
    onSecondary = Color.Gray,
    outlineVariant = Color.Gray,
    surfaceTint = BlueSecondary,
)

private val DarkThemeColors = darkColorScheme(
    primary = BluePrimaryDark,
    onPrimary = Color.White,
    secondary = Black,
    error = Color.Red,
    background = Color.Black,
    surface = BlueSecondary,
    onSurface = Color.White,
    onSecondary = Color.White,
    outlineVariant = Color.White,
    surfaceTint = BlueSecondary,
)

@Composable
fun MifosTheme(
    useDarkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    val colors = when {
        useDarkTheme -> DarkThemeColors
        else -> LightThemeColors
    }

    MaterialTheme(
        colorScheme = colors,
        content = content,
    )
}
