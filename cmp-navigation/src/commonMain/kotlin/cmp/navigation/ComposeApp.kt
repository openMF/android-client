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

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.rememberNavController
import cmp.navigation.navigation.RootNavGraph
import com.mifos.core.data.util.NetworkMonitor
import com.mifos.core.datastore.model.AppTheme
import com.mifos.core.designsystem.component.MifosCircularProgress
import com.mifos.core.designsystem.theme.MifosTheme
import org.koin.compose.koinInject
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun ComposeApp(
    modifier: Modifier = Modifier,
    networkMonitor: NetworkMonitor = koinInject(),
    viewModel: ComposeAppViewModel = koinViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    when (uiState) {
        is MainUiState.Loading -> {
            MifosCircularProgress(Modifier.fillMaxWidth())
        }
        is MainUiState.Success -> {
            val theme = (uiState as MainUiState.Success).appTheme
            val isDarkTheme=when(theme){
                AppTheme.SYSTEM -> isSystemInDarkTheme()
                AppTheme.LIGHT -> false
                AppTheme.DARK -> true
            }
            MifosTheme(isDarkTheme) {
                RootNavGraph(
                    networkMonitor = networkMonitor,
                    navHostController = rememberNavController(),
                    modifier = modifier,
                )
            }
        }
    }
}
