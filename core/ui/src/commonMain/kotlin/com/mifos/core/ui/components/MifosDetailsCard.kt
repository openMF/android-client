/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mifos-x-field-officer-app/blob/master/LICENSE.md
 */
package com.mifos.core.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import com.mifos.core.designsystem.component.MifosCard
import com.mifos.core.designsystem.theme.MifosTypography
import kpt.core.base.designsystem.KptTheme
import kpt.core.base.designsystem.theme.LocalKptSpacing

@Composable
fun MifosDetailsCard(
    details: Map<String, String?>,
    modifier: Modifier = Modifier,
) {
    MifosCard(
        modifier = modifier.fillMaxWidth(),
    ) {
        Column(modifier = Modifier.padding(LocalKptSpacing.current.md)) {
            details.forEach { (heading, value) ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = LocalKptSpacing.current.sm),
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    Text(
                        text = "$heading :",
                        style = MifosTypography.labelMediumEmphasized,
                    )
                    Text(
                        text = value ?: "",
                        style = MifosTypography.labelMedium,
                        textAlign = TextAlign.Right,
                    )
                }
            }
        }
    }
}
