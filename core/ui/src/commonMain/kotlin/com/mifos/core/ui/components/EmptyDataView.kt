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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.mifos.core.designsystem.icon.MifosIcons
import com.mifos.core.designsystem.theme.MifosTheme
import com.mifos.core.ui.util.DevicePreview
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

@Composable
fun EmptyDataView(
    error: StringResource,
    modifier: Modifier = Modifier.fillMaxSize(),
    icon: ImageVector = MifosIcons.Error,
    errorString: String? = null,
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Icon(
            modifier = Modifier
                .size(100.dp)
                .padding(bottom = 12.dp),
            imageVector = icon,
            contentDescription = null,
        )

        Text(
            modifier = Modifier.padding(horizontal = 20.dp),
            text = errorString ?: stringResource(error),
            style = MaterialTheme.typography.labelSmall,
            textAlign = TextAlign.Center,
        )
    }
}

@Composable
fun EmptyDataView(
    error: StringResource,
    modifier: Modifier = Modifier.fillMaxSize(),
    image: DrawableResource? = null,
    errorString: String? = null,
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        image?.let {
            Icon(
                modifier = Modifier
                    .size(100.dp)
                    .padding(bottom = 12.dp),
                painter = painterResource(it),
                contentDescription = null,
            )
        }

        Text(
            modifier = Modifier.padding(horizontal = 20.dp),
            text = errorString ?: stringResource(error),
            style = MaterialTheme.typography.labelMedium,
            textAlign = TextAlign.Center,
        )
    }
}

@DevicePreview
@Composable
fun EmptyDataViewPreview(
    modifier: Modifier = Modifier,
) {
    MifosTheme {
        EmptyDataView(
            error = Res.string.core_ui_no_internet,
            modifier = modifier,
            image = null,
        )
    }
}
