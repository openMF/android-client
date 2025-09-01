/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.feature.individualCollectionSheet.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.navigation
import com.mifos.core.common.utils.Constants
import com.mifos.core.model.objects.account.loan.PaymentTypeOptions
import com.mifos.core.model.objects.collectionsheets.LoanAndClientName
import com.mifos.core.network.model.IndividualCollectionSheetPayload
import com.mifos.feature.individualCollectionSheet.generateCollectionSheet.GenerateCollectionSheetScreen
import com.mifos.feature.individualCollectionSheet.individualCollectionSheet.IndividualCollectionSheetScreen
import com.mifos.feature.individualCollectionSheet.individualCollectionSheetDetails.IndividualCollectionSheetDetailsScreen
import com.mifos.feature.individualCollectionSheet.paymentDetails.PaymentDetailsScreenRoute
import com.mifos.room.entities.collectionsheet.IndividualCollectionSheet
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

@Serializable
data object IndividualCollectionSheetNavGraph

fun NavGraphBuilder.individualCollectionSheetNavGraph(
    navController: NavController,
    onBackPressed: () -> Unit,
) {
    navigation<IndividualCollectionSheetNavGraph>(
        startDestination = IndividualCollectionSheetScreenRoute,
    ) {
        individualCollectionSheetScreen(
            onBackPressed = onBackPressed,
            onDetail = { _, sheet ->
                navController.navigateToIndividualCollectionSheetDetailScreen(sheet)
            },

        )

        individualCollectionSheetDetailScreen(
            onBackPressed = onBackPressed,
            submit = navController::navigateToPaymentDetailsScreen,
        )

        paymentDetailsScreen()
    }
}

@Serializable
data object IndividualCollectionSheetScreenRoute

private fun NavGraphBuilder.individualCollectionSheetScreen(
    onBackPressed: () -> Unit,
    onDetail: (String, IndividualCollectionSheet) -> Unit,
) {
    composable<IndividualCollectionSheetScreenRoute> {
        IndividualCollectionSheetScreen(
            onBackPressed = onBackPressed,
            onDetail = onDetail,
        )
    }
}

// TODO : change while implementing screens because this is not primitive
private fun NavGraphBuilder.individualCollectionSheetDetailScreen(
    onBackPressed: () -> Unit,
    submit: (Int, IndividualCollectionSheetPayload, List<String>, LoanAndClientName, List<PaymentTypeOptions>, Int) -> Unit,
) {
    composable(
        route = CollectionSheetScreens.IndividualCollectionSheetDetailScreen.route,
        arguments = listOf(
            navArgument(name = Constants.INDIVIDUAL_SHEET, builder = { NavType.StringType }),
        ),
    ) {
        IndividualCollectionSheetDetailsScreen(
            onBackPressed = onBackPressed,
            submit = submit,
        )
    }
}

@Serializable
data object GenerateCollectionSheetScreenRoute

fun NavGraphBuilder.generateCollectionSheetScreen(
    onBackPressed: () -> Unit,
) {
    composable<GenerateCollectionSheetScreenRoute> {
        GenerateCollectionSheetScreen(
            onBackPressed = onBackPressed,
        )
    }
}

// TODO:change while implementing screens because this is not primitive
fun NavGraphBuilder.paymentDetailsScreen() {
    composable(
        route = CollectionSheetScreens.PaymentDetailsScreen.route,
        arguments = listOf(
            navArgument(name = Constants.PAYMENT_DETAILS_ARGS) {
                type = NavType.StringType
            },
        ),
    ) {
        PaymentDetailsScreenRoute()
    }
}

fun NavController.navigateToIndividualCollectionSheetDetailScreen(sheet: IndividualCollectionSheet) {
    navigate(CollectionSheetScreens.IndividualCollectionSheetDetailScreen.argument(sheet))
}

fun NavController.navigateToIndividualCollectionSheetScreen() {
    navigate(IndividualCollectionSheetScreenRoute)
}

fun NavController.navigateToPaymentDetailsScreen(
    position: Int,
    payload: IndividualCollectionSheetPayload,
    paymentTypeOptionsName: List<String>,
    loansAndClientName: LoanAndClientName,
    paymentTypeOptions: List<PaymentTypeOptions>,
    clientId: Int,
) {
    val args = PaymentDetailsArgs(
        position = position,
        individualCollectionSheetPayload = payload,
        paymentTypeOptionsName = paymentTypeOptionsName,
        loanAndClientName = loansAndClientName,
        paymentTypeOptions = paymentTypeOptions,
        clientId = clientId,
    )
    val encoded = Json.encodeToString(args)
    navigate(CollectionSheetScreens.PaymentDetailsScreen.argument(encoded))
}

@Serializable
data class PaymentDetailsArgs(
    val position: Int,
    val individualCollectionSheetPayload: IndividualCollectionSheetPayload,
    val paymentTypeOptionsName: List<String>,
    val loanAndClientName: LoanAndClientName,
    val paymentTypeOptions: List<PaymentTypeOptions>,
    val clientId: Int,
)
