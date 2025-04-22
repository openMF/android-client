/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.feature.about.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.mifos.feature.about.AboutScreen

/**
 * Created by Pronay Sarker on 10/08/2024 (7:56 AM)
 */
fun NavGraphBuilder.aboutScreen(
    onBackPressed: () -> Unit,
) {
    composable(AboutScreens.AboutScreen.route) {
        AboutScreen(
            onBackPressed = onBackPressed,
        )
    }
}
