/*
 * Copyright 2024 Mifos Initiative
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
