/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.feature.splash.splash

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mifos.core.designsystem.component.MifosScaffold
import com.mifos.core.designsystem.theme.SummerSky
import com.mifos.feature.splash.R

@Composable
internal fun SplashScreen(
    navigatePasscode: () -> Unit,
    viewmodel: SplashScreenViewmodel = hiltViewModel(),
    navigateLogin: () -> Unit,
) {
    val state by viewmodel.isAuthenticated.collectAsStateWithLifecycle()

    SplashScreen(
        state = state,
        navigatePasscode = navigatePasscode,
        navigateLogin = navigateLogin,
    )
}

@Composable
internal fun SplashScreen(
    state: Boolean?,
    navigatePasscode: () -> Unit,
    navigateLogin: () -> Unit,
    modifier: Modifier = Modifier,
) {
    when (state) {
        false -> navigateLogin()
        true -> navigatePasscode()
        else -> {}
    }

    MifosScaffold(
        modifier = modifier,
        containerColor = SummerSky,
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Image(
                modifier = Modifier.size(100.dp),
                painter = painterResource(id = R.drawable.feature_splash_icon),
                contentDescription = null,
            )
        }
    }
}

@Preview
@Composable
private fun SplashScreenPreview() {
    SplashScreen(
        state = false,
        navigatePasscode = {},
        navigateLogin = {},
    )
}
