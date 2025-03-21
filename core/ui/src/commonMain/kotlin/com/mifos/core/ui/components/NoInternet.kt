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
import androidclient.core.ui.generated.resources.core_ui_no_internet
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mifos.core.designsystem.icon.MifosIcons
import com.mifos.core.designsystem.theme.MifosTheme
import com.mifos.core.ui.util.DevicePreview
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource

@Composable
fun NoInternet(
    error: StringResource,
    modifier: Modifier = Modifier,
    isRetryEnabled: Boolean = true,
    icon: ImageVector = MifosIcons.WifiOff,
    retry: () -> Unit = {},
) {
    Column(
        modifier = modifier
            .fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Icon(
            modifier = Modifier
                .size(100.dp)
                .padding(bottom = 12.dp),
            imageVector = icon,
            contentDescription = "No Internet Icon",
        )

        Text(
            text = stringResource(error),
            style = TextStyle(fontSize = 20.sp),
        )

        Spacer(modifier = Modifier.height(12.dp))
        if (isRetryEnabled) {
            FilledTonalButton(onClick = { retry.invoke() }) {
                Text(text = "Retry")
            }
        }
    }
}

@DevicePreview
@Composable
fun NoInternetPreview(
    modifier: Modifier = Modifier,
) {
    MifosTheme {
        NoInternet(
            error = Res.string.core_ui_no_internet,
            modifier = modifier,
            isRetryEnabled = true,
            retry = {},
        )
    }
}
