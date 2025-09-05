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
import androidclient.core.ui.generated.resources.core_ui_click_here_to_view_filled_state
import androidclient.core.ui.generated.resources.core_ui_no_item_found
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.mifos.core.designsystem.theme.DesignToken
import com.mifos.core.designsystem.theme.MifosTheme
import com.mifos.core.designsystem.theme.MifosTypography
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun MifosEmptyCard(
    msg: String = stringResource(Res.string.core_ui_click_here_to_view_filled_state),
    modifier: Modifier = Modifier,
) {
    MifosListingComponentOutline {
        Column(modifier.fillMaxWidth()) {
            Text(
                text = stringResource(Res.string.core_ui_no_item_found),
                style = MifosTypography.titleSmallEmphasized,
            )
            Spacer(Modifier.height((DesignToken.padding.medium)))
            Text(
                text = msg,
                style = MifosTypography.bodySmall,
            )
        }
    }
}

@Preview
@Composable
private fun MifosEmptyCardPreview() {
    MifosTheme {
        MifosEmptyCard("Add any to show")
    }
}
