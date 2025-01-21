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

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.sp

val aboutItemTextStyle = TextStyle(
    color = Color.Black,
    textAlign = TextAlign.Center,
    fontSize = 16.sp,
    fontWeight = FontWeight.Normal,
)

val aboutItemTextStyleBold = TextStyle(
    color = Color.Black,
    textAlign = TextAlign.Center,
    fontSize = 24.sp,
    fontWeight = FontWeight.SemiBold,
)

val identifierTextStyleDark = TextStyle(
    color = Color.Black,
    textAlign = TextAlign.Start,
    fontSize = 16.sp,
    fontWeight = FontWeight.Normal,
)

val identifierTextStyleLight = TextStyle(
    color = DarkGray,
    textAlign = TextAlign.Start,
    fontSize = 16.sp,
    fontWeight = FontWeight.Normal,
)

val styleMedium16sp =
    TextStyle(
        fontSize = 16.sp,
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Medium,
        color = Color.Black,
    )

val styleNormal18sp =
    TextStyle(
        fontSize = 18.sp,
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Normal,
        color = Color.Black,
    )

val styleMedium30sp =
    TextStyle(
        fontSize = 30.sp,
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Medium,
        color = Color.Black,
    )

val styleMifosTopBar =
    TextStyle(
        color = Color(0xFF212121),
        fontWeight = FontWeight.SemiBold,
        fontSize = 20.sp,
    )

val styleSettingsButton = TextStyle(color = Color.White, textAlign = TextAlign.Center)
val historyItemTextStyle = TextStyle(color = Color.Black, fontSize = 16.sp)
