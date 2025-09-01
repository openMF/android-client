/*
 * Copyright 2025 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.feature.loan.groupLoanAccount

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import kotlinx.serialization.Serializable

@Serializable
data class GroupLoanScreenRoute(
    val groupId: Int,
)

fun NavController.navigateToGroupLoanScreen(groupId: Int) {
    navigate(GroupLoanScreenRoute(groupId))
}

fun NavGraphBuilder.groupLoanScreen(
    onBackPressed: () -> Unit,
) {
    composable<GroupLoanScreenRoute> {
        GroupLoanAccountScreen(
            onBackPressed = onBackPressed,
        )
    }
}
