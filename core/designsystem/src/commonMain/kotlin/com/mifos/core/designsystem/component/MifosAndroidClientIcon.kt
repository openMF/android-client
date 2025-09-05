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

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Android
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.unit.dp
import com.mifos.core.designsystem.theme.MifosTheme
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun MifosAndroidClientIcon(imageVector: Painter, modifier: Modifier = Modifier) {
    Image(
        painter = imageVector,
        contentDescription = null,
        modifier = modifier.then(Modifier.size(200.dp, 100.dp)),
    )
}

@Preview
@Composable
private fun MifosAndroidClientIconPreview() {
    MifosTheme {
        MifosAndroidClientIcon(
            imageVector = rememberVectorPainter(Icons.Default.Android),
            modifier = Modifier.padding(16.dp),
        )
    }
}
