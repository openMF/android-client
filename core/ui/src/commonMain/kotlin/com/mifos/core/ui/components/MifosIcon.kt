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
import androidclient.core.ui.generated.resources.core_ui_ic_centers_24dp
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.mifos.core.designsystem.theme.MifosTheme
import com.mifos.core.ui.util.DevicePreview
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource

@Composable
fun MifosIcon(
    mobileIcon: DrawableResource,
    modifier: Modifier = Modifier,
) {
    Column(modifier) {
        Image(
            painter = painterResource(mobileIcon),
            contentDescription = "Mobile Icon",
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.CenterHorizontally)
                .padding(0.dp, 56.dp),
        )
    }
}

@DevicePreview
@Composable
private fun MifosMobileIconPreview(
    modifier: Modifier = Modifier,
) {
    MifosTheme {
        MifosIcon(
            mobileIcon = Res.drawable.core_ui_ic_centers_24dp,
            modifier = modifier,
        )
    }
}
