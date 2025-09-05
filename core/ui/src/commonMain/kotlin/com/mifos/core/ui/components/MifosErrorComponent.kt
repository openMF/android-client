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
import androidclient.core.ui.generated.resources.core_ui_no_data
import androidclient.core.ui.generated.resources.core_ui_no_internet
import androidclient.core.ui.generated.resources.core_ui_retry
import androidclient.core.ui.generated.resources.core_ui_something_went_wrong
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mifos.core.designsystem.icon.MifosIcons
import com.mifos.core.designsystem.theme.MifosTheme
import com.mifos.core.ui.util.DevicePreview
import org.jetbrains.compose.resources.stringResource

@Composable
fun MifosErrorComponent(
    modifier: Modifier = Modifier,
    isNetworkConnected: Boolean = true,
    message: String? = null,
    isEmptyData: Boolean = false,
    isRetryEnabled: Boolean = false,
    onRetry: () -> Unit = {},
) {
    when {
        !isNetworkConnected -> NoInternetComponent(isRetryEnabled = isRetryEnabled) { onRetry() }
        else -> EmptyDataComponent(
            modifier = modifier,
            isEmptyData = isEmptyData,
            message = message,
            isRetryEnabled = isRetryEnabled,
            onRetry = onRetry,
        )
    }
}

@Composable
fun NoInternetComponent(
    modifier: Modifier = Modifier,
    isRetryEnabled: Boolean = false,
    onRetry: () -> Unit = {},
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
            imageVector = MifosIcons.WifiOff,
            contentDescription = "Wifi Icon",
        )

        Text(
            text = stringResource(Res.string.core_ui_no_internet),
            style = TextStyle(fontSize = 20.sp),
        )

        Spacer(modifier = Modifier.height(12.dp))

        if (isRetryEnabled) {
            FilledTonalButton(onClick = { onRetry.invoke() }) {
                Text(text = stringResource(Res.string.core_ui_retry))
            }
        }
    }
}

@Composable
fun EmptyDataComponent(
    modifier: Modifier = Modifier,
    isEmptyData: Boolean = false,
    message: String? = null,
    isRetryEnabled: Boolean = false,
    onRetry: () -> Unit = {},
) {
    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Icon(
            modifier = Modifier
                .size(100.dp)
                .padding(bottom = 12.dp),
            imageVector = MifosIcons.Info,
            contentDescription = "Info Icon",
        )

        Text(
            modifier = Modifier.padding(horizontal = 20.dp),
            text = message ?: if (isEmptyData) {
                stringResource(Res.string.core_ui_no_data)
            } else {
                stringResource(Res.string.core_ui_something_went_wrong)
            },
            style = TextStyle(fontSize = 20.sp),
            textAlign = TextAlign.Center,
        )

        if (isRetryEnabled) {
            FilledTonalButton(
                modifier = Modifier.padding(top = 8.dp),
                onClick = { onRetry.invoke() },
            ) {
                Text(text = stringResource(Res.string.core_ui_retry))
            }
        }
    }
}

@Composable
fun EmptyDataComponentWithModifiedMessageAndIcon(
    message: String,
    icon: ImageVector,
    modifier: Modifier = Modifier,
    isEmptyData: Boolean = false,
) {
    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Icon(
            modifier = Modifier
                .size(100.dp)
                .padding(bottom = 12.dp),
            imageVector = if (isEmptyData) icon else MifosIcons.Info,
            contentDescription = "Info Icon",
        )

        Text(
            modifier = Modifier.padding(horizontal = 20.dp),
            text = if (isEmptyData) message else stringResource(Res.string.core_ui_something_went_wrong),
            style = TextStyle(fontSize = 20.sp),
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.error,

        )
    }
}

@DevicePreview
@Composable
private fun NoInternetPreview() {
    MifosTheme {
        NoInternetComponent()
    }
}

@DevicePreview
@Composable
private fun EmptyDataPreview() {
    MifosTheme {
        EmptyDataComponent()
    }
}

@DevicePreview
@Composable
private fun EmptyDataComponentWithModifiedMessageAndIconPreview(
    modifier: Modifier = Modifier,
) {
    MifosTheme {
        EmptyDataComponentWithModifiedMessageAndIcon(
            message = "No data found",
            icon = MifosIcons.Error,
            modifier = modifier,
            isEmptyData = true,
        )
    }
}
