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

import androidclient.core.ui.generated.resources.Res
import androidclient.core.ui.generated.resources.core_ui_ic_group_black_24dp
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.mifos.core.designsystem.theme.MifosTheme
import com.mifos.core.ui.util.DevicePreview
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource

@Composable
fun MifosRoundIcon(
    iconId: DrawableResource,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier
            .clip(CircleShape),
    ) {
        Image(
            modifier = Modifier.padding(all = 6.dp),
            painter = painterResource(iconId),
            contentDescription = "Icon",
        )
    }
}

@DevicePreview
@Composable
private fun MifosRoundIconPreview(
    modifier: Modifier = Modifier,
) {
    MifosTheme {
        MifosRoundIcon(
            iconId = Res.drawable.core_ui_ic_group_black_24dp,
            modifier = modifier,
        )
    }
}
