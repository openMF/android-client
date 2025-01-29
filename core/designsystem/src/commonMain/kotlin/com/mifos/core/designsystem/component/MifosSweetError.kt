/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.core.designsystem.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import core.designsystem.generated.resources.Res
import core.designsystem.generated.resources.core_designsystem_ic_error_black_24dp
import core.designsystem.generated.resources.core_designsystem_try_again
import core.designsystem.generated.resources.core_designsystem_unable_to_load
import org.jetbrains.compose.resources.stringResource

@Composable
fun MifosSweetError(
    message: String,
    modifier: Modifier = Modifier
        .fillMaxSize()
        .padding(18.dp)
        .semantics { contentDescription = "MifosSweetError" },
    isRetryEnabled: Boolean = true,
    buttonText: String = stringResource(Res.string.core_designsystem_try_again),
    onclick: () -> Unit = {},
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        AsyncImage(
            modifier = Modifier.size(70.dp),
            model = Res.drawable.core_designsystem_ic_error_black_24dp,
            contentDescription = null,
            colorFilter = ColorFilter.tint(Color.Gray),
        )
        Spacer(modifier = Modifier.height(20.dp))
        Text(
            text = stringResource(Res.string.core_designsystem_unable_to_load),
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.secondary,
        )
        Text(
            text = message,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.secondary,
        )
        if (isRetryEnabled) {
            Spacer(modifier = Modifier.height(20.dp))
            Button(
                onClick = { onclick() },
                contentPadding = PaddingValues(),
            ) {
                Text(
                    modifier = Modifier.padding(start = 20.dp, end = 20.dp),
                    text = buttonText,
                    style = MaterialTheme.typography.labelMedium,
                )
            }
        }
    }
}

@Composable
fun MifosPaginationSweetError(
    modifier: Modifier = Modifier,
    onclick: () -> Unit,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(18.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Icon(
            imageVector = Icons.Default.Info,
            contentDescription = "Info Image",
        )
        Text(
            text = stringResource(Res.string.core_designsystem_unable_to_load),
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.secondary,
        )
        Button(
            onClick = { onclick() },
            contentPadding = PaddingValues(),
        ) {
            Text(
                modifier = Modifier
                    .padding(start = 20.dp, end = 20.dp),
                text = stringResource(Res.string.core_designsystem_try_again),
                style = MaterialTheme.typography.labelMedium,
            )
        }
    }
}
