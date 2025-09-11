/*
 * Copyright 2025 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.core.ui.components

import androidx.compose.foundation.layout.height
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.attafitamim.krop.core.crop.AspectRatio
import com.attafitamim.krop.core.crop.CropState
import com.attafitamim.krop.core.crop.RectCropShape
import com.attafitamim.krop.core.crop.cropperStyle
import com.attafitamim.krop.ui.ImageCropperDialog
import com.mifos.core.designsystem.component.MifosTopBar
import com.mifos.core.designsystem.icon.MifosIcons
import com.mifos.core.designsystem.theme.DesignToken

@Composable
fun MifosImageCropperDialog(
    state: CropState,
) {
    ImageCropperDialog(
        state = state,
        style = cropperStyle(
            shapes = listOf(
                RectCropShape,
            ),
            aspects = listOf(
                AspectRatio(2, 1),
            ),
        ),
        topBar = {
            MifosTopBar(
                modifier = Modifier.height(DesignToken.sizes.topBarStandardHeight),
                topBarTitle = "",
                backPress = {
                    state.done(accept = false)
                },
                actions = {
                    IconButton(
                        onClick = {
                            state.reset()
                        },
                    ) {
                        Icon(MifosIcons.Refresh, null)
                    }
                    IconButton(
                        onClick = {
                            state.done(accept = true)
                        },
                        enabled = !state.accepted,
                    ) {
                        Icon(MifosIcons.Check, null)
                    }
                },
            )
        },
    )
}
