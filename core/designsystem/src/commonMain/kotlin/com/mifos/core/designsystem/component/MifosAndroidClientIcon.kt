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
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp

/**
 * Created by Aditya Gupta on 21/02/24.
 */

@Composable
fun MifosAndroidClientIcon(id: Int, modifier: Modifier = Modifier) {
    Image(
        painter = painterResource(id = id),
        contentDescription = null,
        modifier = modifier.then(Modifier.size(200.dp, 100.dp)),
    )
}
