/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.feature.about

import androidx.compose.ui.graphics.Color
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.StringResource

data class AboutItem(
    val icon: DrawableResource? = null,
    val title: StringResource,
    val subtitle: StringResource? = null,
    val color: Color? = null,
    val id: AboutItems,
)

enum class AboutItems {
    CONTRIBUTIONS,
    APP_VERSION,
    OFFICIAL_WEBSITE,
    TWITTER,
    SOURCE_CODE,
    LICENSE,
}
