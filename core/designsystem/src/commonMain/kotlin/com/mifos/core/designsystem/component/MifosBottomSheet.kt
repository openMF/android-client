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
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
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
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MifosBottomSheet(
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colorScheme.onPrimary,
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
            containerColor = color,
            sheetState = modalSheetState,
            modifier = modifier,
        ) {
            content()
        }
    }
}

@Composable
fun MifosBottomSheetOptionItem(
    label: String,
    icon: DrawableResource,
    onClick: () -> Unit,
) {
    Column(
        modifier = Modifier
            .padding(DesignToken.padding.large)
            .clickable { onClick() },
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        MifosCard {
            Icon(
                painter = painterResource(icon),
                contentDescription = null,
                modifier = Modifier
                    .padding(DesignToken.padding.small)
                    .size(DesignToken.sizes.iconLarge),
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
        MifosCard (
            elevation = 4.dp,
            modifier = Modifier
                .clickable(onClick = onClick),
            shape = DesignToken.shapes.small,
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
