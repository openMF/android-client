/*
 * Copyright 2025 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.core.ui.util

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle

data class TextUtil(
    val text: String = "",
    val style: TextStyle? = null,
    val color: Color? = null,
)
