/*
 * Copyright 2025 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mifos-x-field-officer-app/blob/master/LICENSE.md
 */
package com.mifos.feature.client.shareAccounts

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.mifos.feature.client.createShareAccount.navigateToCreateShareAccountRoute
import kotlinx.serialization.Serializable

@Serializable
data class ShareAccountsRoute(
    val clientId: Int = -1,
)

fun NavGraphBuilder.shareAccountsDestination(
    navController: NavController,
    navigateToViewAccount: (Int) -> Unit,
) {
    composable<ShareAccountsRoute> {
        ShareAccountsScreen(
            navController = navController,
            viewAccount = navigateToViewAccount,
            createAccount = { clientId ->
                navController.navigateToCreateShareAccountRoute(clientId = clientId)
            },
        )
    }
}

fun NavController.navigateToShareAccountsScreen(
    clientId: Int,
) {
    this.navigate(ShareAccountsRoute(clientId = clientId))
}
