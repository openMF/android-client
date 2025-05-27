/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.feature.settings.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.mifos.feature.settings.settings.SettingsScreen

/**
 * Created by Pronay Sarker on 10/08/2024 (7:52 AM)
 */
fun NavGraphBuilder.settingsScreen(
    navigateBack: () -> Unit,
    navigateToLoginScreen: () -> Unit,
    changePasscode: (String) -> Unit,
    languageChanged: () -> Unit,
) {
    composable(
        route = SettingsScreens.SettingsScreen.route,
    ) {
        SettingsScreen(
            onBackPressed = navigateBack,
            navigateToLoginScreen = navigateToLoginScreen,
            languageChanged = languageChanged,
            changePasscode = changePasscode,
        )
    }
}
