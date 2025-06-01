/*
 * Copyright 2025 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package cmp.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import cmp.navigation.navigation.RootNavGraph
import com.mifos.core.data.util.NetworkMonitor
import com.mifos.core.designsystem.theme.MifosTheme
import org.koin.compose.koinInject

@Composable
fun ComposeApp(
    modifier: Modifier = Modifier,
    networkMonitor: NetworkMonitor = koinInject(),
) {
    MifosTheme {
        RootNavGraph(
            networkMonitor = networkMonitor,
            navHostController = rememberNavController(),
            modifier = modifier,
        )
    }
}
