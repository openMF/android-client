/*
 * Copyright 2025 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See See https://github.com/openMF/kmp-project-template/blob/main/LICENSE
 */
package kpt.core.designsystem.theme

import androidx.compose.ui.graphics.Color

/**
 * The Mifos brand palette, ported verbatim from
 * `6b66e8a43:core/designsystem/.../theme/Color.kt`.
 *
 * These 70 names existed already — with the TEMPLATE's indigo values (`primaryLight` was
 * `0xFF4338CA`). The names overlapping is exactly why the gap survived the first migration pass:
 * `AppColor.kt` deliberately ported only the 140 NON-overlapping contrast variants, because
 * re-declaring an overlapping name in the same package is an overload-resolution ambiguity rather
 * than an override. So the brand half landed and the base half silently kept the template's.
 *
 * Nothing referenced the wrong colour by name, every gate passed, and the app simply rendered
 * lavender instead of Mifos blue — visible only by looking at it.
 *
 * `KptTheme` builds `lightScheme`/`darkScheme` from these, so correcting the VALUES re-themes every
 * surface at once; no call site changes and no second theme to switch to.
 */

// ── Light scheme ─────────────────────────────────────────────────────────────
// Primary — Trust Indigo (fintech-grade, more saturated than stock M3 indigo).
val primaryLight = Color(0xFF445E91)
val onPrimaryLight = Color(0xFFFFFFFF)
val primaryContainerLight = Color(0xFFD8E2FF)
val onPrimaryContainerLight = Color(0xFF2B4678)

// Secondary — Emerald (financial growth, positive deltas, primary CTAs).
val secondaryLight = Color(0xFF575E71)
val onSecondaryLight = Color(0xFFFFFFFF)
val secondaryContainerLight = Color(0xFFDBE2F9)
val onSecondaryContainerLight = Color(0xFF3F4759)

// Tertiary — Warm Amber (highlights, badges, warnings-but-not-errors).
val tertiaryLight = Color(0xFF715573)
val onTertiaryLight = Color(0xFFFFFFFF)
val tertiaryContainerLight = Color(0xFFFBD7FC)
val onTertiaryContainerLight = Color(0xFF583E5B)

// Error — Light red-orange (warmer than saturated red; still reads as urgent).
val errorLight = Color(0xFFBA1A1A)
val onErrorLight = Color(0xFFFFFFFF)
val errorContainerLight = Color(0xFFFFDAD6)
val onErrorContainerLight = Color(0xFF93000A)

// Background + surface — warm-cool neutral hierarchy (M3 surfaceContainer ladder).
val backgroundLight = Color(0xFFF9F9FF)
val onBackgroundLight = Color(0xFF1A1B20)
val surfaceLight = Color(0xFFF9F9FF)
val onSurfaceLight = Color(0xFF1A1B20)
val surfaceVariantLight = Color(0xFFE1E2EC)
val onSurfaceVariantLight = Color(0xFF44474F)
val outlineLight = Color(0xFF74777F)
val outlineVariantLight = Color(0xFFC4C6D0)
val scrimLight = Color(0xFF000000)
val inverseSurfaceLight = Color(0xFF2F3036)
val inverseOnSurfaceLight = Color(0xFFF0F0F7)
val inversePrimaryLight = Color(0xFFADC6FF)

// Surface tonal ladder — used by AppCard / HeroCard to feel lifted.
val surfaceDimLight = Color(0xFFD9D9E0)
val surfaceBrightLight = Color(0xFFF9F9FF)
val surfaceContainerLowestLight = Color(0xFFFFFFFF)
val surfaceContainerLowLight = Color(0xFFF3F3FA)
val surfaceContainerLight = Color(0xFFEDEDF4)
val surfaceContainerHighLight = Color(0xFFE8E7EE)
val surfaceContainerHighestLight = Color(0xFFE2E2E9)

// ── Dark scheme ──────────────────────────────────────────────────────────────
val primaryDark = Color(0xFFADC6FF)
val onPrimaryDark = Color(0xFF102F60)
val primaryContainerDark = Color(0xFF2B4678)
val onPrimaryContainerDark = Color(0xFFD8E2FF)

val secondaryDark = Color(0xFFBFC6DC)
val onSecondaryDark = Color(0xFF293041)
val secondaryContainerDark = Color(0xFF3F4759)
val onSecondaryContainerDark = Color(0xFFDBE2F9)

val tertiaryDark = Color(0xFFDEBCDF)
val onTertiaryDark = Color(0xFF402843)
val tertiaryContainerDark = Color(0xFF583E5B)
val onTertiaryContainerDark = Color(0xFFFBD7FC)

val errorDark = Color(0xFFFFB4AB)
val onErrorDark = Color(0xFF690005)
val errorContainerDark = Color(0xFF93000A)
val onErrorContainerDark = Color(0xFFFFDAD6)

val backgroundDark = Color(0xFF111318)
val onBackgroundDark = Color(0xFFE2E2E9)
val surfaceDark = Color(0xFF111318)
val onSurfaceDark = Color(0xFFE2E2E9)
val surfaceVariantDark = Color(0xFF44474F)
val onSurfaceVariantDark = Color(0xFFC4C6D0)
val outlineDark = Color(0xFF8E9099)
val outlineVariantDark = Color(0xFF44474F)
val scrimDark = Color(0xFF000000)
val inverseSurfaceDark = Color(0xFFE2E2E9)
val inverseOnSurfaceDark = Color(0xFF2F3036)
val inversePrimaryDark = Color(0xFF445E91)

val surfaceDimDark = Color(0xFF111318)
val surfaceBrightDark = Color(0xFF37393E)
val surfaceContainerLowestDark = Color(0xFF0C0E13)
val surfaceContainerLowDark = Color(0xFF1A1B20)
val surfaceContainerDark = Color(0xFF1E1F25)
val surfaceContainerHighDark = Color(0xFF282A2F)
val surfaceContainerHighestDark = Color(0xFF33353A)
