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

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.mifos.core.designsystem.theme.AppColors
import com.mifos.core.designsystem.theme.DesignToken
import com.mifos.core.designsystem.theme.MifosTypography
import org.jetbrains.compose.ui.tooling.preview.Preview

data class Step(
    val name: String,
    val content: @Composable () -> Unit,
)

@Composable
fun MifosStepper(
    steps: List<Step>,
    currentIndex: Int,
    onStepChange: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(
                vertical = DesignToken.padding.small,
                horizontal = DesignToken.padding.large
            ),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Row(
            modifier = Modifier
                .clip(shape = DesignToken.shapes.medium)
                .background(MaterialTheme.colorScheme.primary)
                .padding(vertical = DesignToken.padding.largeIncreasedExtra)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            steps.forEachIndexed { index, step ->
                Row(
                    verticalAlignment = Alignment.Top,
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier=Modifier.width(DesignToken.sizes.avatarMediumExtra)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(DesignToken.sizes.iconLarge)
                                .clip(CircleShape)
                                .background(
                                    when {
                                        index == currentIndex -> AppColors.customWhite
                                        else -> AppColors.stepperColor
                                    },
                                )
                                .clickable(enabled = index < currentIndex) {
                                    if (index < currentIndex) onStepChange(index)
                                },
                            contentAlignment = Alignment.Center,
                        ) {
                            Text(
                                text = (index + 1).toString(),
                                color = MaterialTheme.colorScheme.primary,
                            )
                        }

                        Spacer(modifier = Modifier.height(DesignToken.padding.small))
                        Text(
                            text=step.name,
                            color = AppColors.customWhite,
                            style = MifosTypography.labelSmall,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                    if (index != steps.lastIndex) {
                        Box(
                            modifier = Modifier
                                .padding(vertical = DesignToken.padding.large)
                                .width(8.dp)
                                .height(1.dp)
                                .background(AppColors.customWhite)
                        )
                    }
                }
            }
        }
        Spacer(Modifier.height(DesignToken.padding.largeIncreased))
        steps[currentIndex].content()
    }
}

@Preview
@Composable
fun MifosStepperDemo() {
    val steps = listOf(
        Step("Details") { Text("Step 1: Details Content") },
        Step("Terms") { Text("Step 2: Terms Content") },
        Step("Charges") { Text("Step 3: Charges Content") },
        Step("Schedule") { Text("Step 4: Schedule Content") },
        Step("Preview") { Text("Step 5: Preview Content") },
    )

    MifosStepper(
        steps = steps,
        currentIndex = 2,
        onStepChange = { },
        modifier = Modifier
            .fillMaxWidth(),
    )
}
