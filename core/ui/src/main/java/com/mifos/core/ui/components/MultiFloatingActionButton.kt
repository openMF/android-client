/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.core.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp

enum class FabType {
    CLIENT,
    CENTER,
    GROUP,
}

sealed class FabButtonState {
    data object Collapsed : FabButtonState()

    data object Expand : FabButtonState()

    fun isExpanded() = this == Expand

    fun toggleValue() = if (isExpanded()) {
        Collapsed
    } else {
        Expand
    }
}

data class FabButton(
    val fabType: FabType,
    val iconRes: Int,
)

@Composable
fun FabItem(
    fabButton: FabButton,
    modifier: Modifier = Modifier,
    onFabClick: (FabType) -> Unit,
) {
    FloatingActionButton(
        onClick = {
            onFabClick(fabButton.fabType)
        },
        modifier = modifier
            .size(48.dp),
    ) {
        Icon(
            painter = painterResource(id = fabButton.iconRes),
            contentDescription = fabButton.fabType.name,
        )
    }
}

@Composable
fun MultiFloatingActionButton(
    fabButtons: List<FabButton>,
    fabButtonState: FabButtonState,
    onFabButtonStateChange: (FabButtonState) -> Unit,
    modifier: Modifier = Modifier,
    onFabClick: (FabType) -> Unit,
) {
    val rotation by animateFloatAsState(
        if (fabButtonState.isExpanded()) {
            45f
        } else {
            0f
        },
        label = "mainFabRotation",
    )

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        AnimatedVisibility(
            visible = fabButtonState.isExpanded(),
            enter = fadeIn() + expandVertically(),
            exit = fadeOut() + shrinkVertically(),
        ) {
            Column {
                fabButtons.forEach {
                    FabItem(
                        fabButton = it,
                        onFabClick = onFabClick,
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                }
            }
        }

        FloatingActionButton(
            onClick = {
                onFabButtonStateChange(fabButtonState.toggleValue())
            },
            modifier = Modifier
                .rotate(rotation),
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "mainFabIcon",
            )
        }
    }
}
