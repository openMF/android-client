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

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.contentColorFor
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.arkivanov.essenty.backhandler.BackCallback
import com.mifos.core.designsystem.theme.DesignToken
import com.mifos.core.designsystem.theme.MifosTypography
import kotlinx.coroutines.launch
import org.jetbrains.compose.ui.tooling.preview.Preview

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MifosBottomSheet(
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
    containerColor: Color = MaterialTheme.colorScheme.onPrimary,
    contentColor: Color = contentColorFor(containerColor),
    content: @Composable () -> Unit,
) {
    val coroutineScope = rememberCoroutineScope()
    val modalSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var showBottomSheet by remember { mutableStateOf(true) }

    fun dismissSheet() {
        coroutineScope.launch { modalSheetState.hide() }.invokeOnCompletion {
            if (!modalSheetState.isVisible) {
                showBottomSheet = false
            }
        }
        onDismiss.invoke()
    }

    BackCallback(modalSheetState.isVisible) {
        dismissSheet()
    }

    AnimatedVisibility(visible = showBottomSheet) {
        ModalBottomSheet(
            onDismissRequest = {
                showBottomSheet = false
                dismissSheet()
            },
            sheetState = modalSheetState,
            containerColor = containerColor,
            contentColor = contentColor,
            modifier = modifier,
        ) {
            content()
        }
    }
}

@Composable
fun MifosBottomSheetOptionItem(
    label: String,
    icon: ImageVector,
    iconBackgroundColor: Color = MaterialTheme.colorScheme.onPrimary,
    onClick: () -> Unit,
) {
    Column(
        modifier = Modifier
            .padding(DesignToken.padding.large)
            .clickable { onClick() },
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        MifosCard(
            colors = CardDefaults.cardColors(
                containerColor = iconBackgroundColor,
            ),
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier
                    .padding(DesignToken.padding.medium)
                    .size(DesignToken.sizes.iconMedium),
                tint = MaterialTheme.colorScheme.primary,
            )
        }
        Spacer(Modifier.height(DesignToken.padding.small))
        Text(
            text = label,
            style = MifosTypography.labelMedium,
        )
    }
}

@Composable
fun MifosBottomSheetOptionItem(
    label: String,
    icon: ImageVector,
    elevation: Dp = DesignToken.elevation.elevation,
    onClick: () -> Unit,
) {
    Column(
        modifier = Modifier
            .padding(DesignToken.padding.large)
            .clickable { onClick() },
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        MifosCard(
            elevation = elevation,
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier
                    .padding(DesignToken.padding.medium)
                    .size(DesignToken.sizes.iconAverage),
                tint = MaterialTheme.colorScheme.primary,
            )
        }
        Spacer(Modifier.height(DesignToken.padding.small))
        Text(
            text = label,
            style = MifosTypography.labelMedium,
        )
    }
}

@Composable
fun MifosBottomSheetOptionItem(
    label: String,
    icon: ImageVector,
    onClick: () -> Unit,
) {
    Column(
        modifier = Modifier
            .padding(DesignToken.padding.large)
            .clickable { onClick() },
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        MifosCard(
            elevation = DesignToken.padding.extraExtraSmall,
            modifier = Modifier
                .clickable(onClick = onClick),
            shape = DesignToken.shapes.small,
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.onPrimary,
                contentColor = MaterialTheme.colorScheme.primary,
            ),
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier
                    .padding(DesignToken.padding.medium)
                    .size(DesignToken.sizes.iconAverage),
            )
        }
        Spacer(Modifier.height(DesignToken.padding.small))
        Text(
            text = label,
            style = MifosTypography.labelMedium,
        )
    }
}

@Preview
@Composable
private fun MifosBottomSheetPreview() {
    MifosBottomSheet(
        content = {
            Box {
                Modifier.height(100.dp)
            }
        },
        onDismiss = {},
    )
}
