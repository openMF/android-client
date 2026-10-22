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

import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import kpt.core.designsystem.generated.resources.Res
import kpt.core.designsystem.generated.resources.inter_black
import kpt.core.designsystem.generated.resources.inter_bold
import kpt.core.designsystem.generated.resources.inter_extra_bold
import kpt.core.designsystem.generated.resources.inter_extra_light
import kpt.core.designsystem.generated.resources.inter_light
import kpt.core.designsystem.generated.resources.inter_medium
import kpt.core.designsystem.generated.resources.inter_regular
import kpt.core.designsystem.generated.resources.inter_semi_bold
import kpt.core.designsystem.generated.resources.inter_thin
import org.jetbrains.compose.resources.Font

/**
 * Inter — the typeface `6b66e8a43` ships and uses.
 *
 * All nine Inter weights were carried across with the rest of the design system and then went
 * unreferenced, because this accessor still pointed at the template's Outfit. The font files being
 * present is what makes this a one-word fix rather than an asset migration.
 */
val fontFamily: FontFamily
    @Composable get() = FontFamily(
        Font(Res.font.inter_black, FontWeight.Black),
        Font(Res.font.inter_bold, FontWeight.Bold),
        Font(Res.font.inter_semi_bold, FontWeight.SemiBold),
        Font(Res.font.inter_medium, FontWeight.Medium),
        Font(Res.font.inter_regular, FontWeight.Normal),
        Font(Res.font.inter_light, FontWeight.Light),
        Font(Res.font.inter_thin, FontWeight.Thin),
        Font(Res.font.inter_extra_light, FontWeight.ExtraLight),
        Font(Res.font.inter_extra_bold, FontWeight.ExtraBold),
    )
