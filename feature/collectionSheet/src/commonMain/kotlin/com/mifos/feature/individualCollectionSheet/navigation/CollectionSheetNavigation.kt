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
import com.mifos.feature.individualCollectionSheet.individualCollectionSheetDetail.IndividualCollectionSheetDetailScreen
import com.mifos.feature.individualCollectionSheet.individualCollectionSheetDetails.IndividualCollectionSheetDetailsScreen
import com.mifos.feature.individualCollectionSheet.newIndividualCollectionSheet.NewIndividualCollectionSheetScreen
import com.mifos.feature.individualCollectionSheet.paymentDetails.PaymentDetailsScreenRoute
import com.mifos.room.entities.collectionsheet.IndividualCollectionSheet
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

fun NavGraphBuilder.individualCollectionSheetNavGraph(
    navController: NavController,
    onBackPressed: () -> Unit,
) {
    navigation(
        route = "generate_collection_sheet",
        startDestination = CollectionSheetScreens.NewIndividualCollectionSheetScreen.route,
    ) {
        newIndividualCollectionSheetScreen(
            onBackPressed = onBackPressed,
            onDetail = { date, sheet ->
                navController.navigateToNewIndividualCollectionSheetDetailScreen(date, sheet)
            },
        )

        newIndividualCollectionSheetDetailScreen(
            onBackPressed = onBackPressed,
        )

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
private fun NavGraphBuilder.newIndividualCollectionSheetScreen(
    onBackPressed: () -> Unit,
    onDetail: (String, IndividualCollectionSheet) -> Unit,
) {
    composable(
        route = CollectionSheetScreens.NewIndividualCollectionSheetScreen.route,
    ) {
        NewIndividualCollectionSheetScreen(
            onDetail = onDetail,
        )
    }
}

private fun NavGraphBuilder.newIndividualCollectionSheetDetailScreen(
    onBackPressed: () -> Unit,
) {
    composable(
        route = CollectionSheetScreens.NewIndividualCollectionSheetDetailScreen.route,
        arguments = listOf(
            navArgument(name = Constants.INDIVIDUAL_SHEET_DATE) {
                type = NavType.StringType
            },
            navArgument(name = Constants.INDIVIDUAL_SHEET) {
                type = NavType.StringType
            },
        ),
    ) { backStackEntry ->
        val dateString = backStackEntry.arguments?.getString(Constants.INDIVIDUAL_SHEET_DATE) ?: ""
        val sheetJson = backStackEntry.arguments?.getString(Constants.INDIVIDUAL_SHEET) ?: ""

        val collectionSheet = Json.decodeFromString<IndividualCollectionSheet>(sheetJson)
        IndividualCollectionSheetDetailScreen(
            repaymentDate = dateString,
            collectionSheet = collectionSheet,
            onAddPayment = { loanItem ->
                // TODO: Implement payment functionality
                println("Add payment for loan: ${loanItem.loanId}")
            },
            onAddSavingsPayment = { savingsItem ->
                // TODO: Implement savings payment functionality
                println("Add savings payment for: ${savingsItem.clientName}")
            },
        )
    }
}

private fun NavGraphBuilder.individualCollectionSheetScreen(
    onBackPressed: () -> Unit,
    onDetail: (String, IndividualCollectionSheet) -> Unit,
) {
    composable(
        route = CollectionSheetScreens.IndividualCollectionSheetScreen.route,
    ) {
        IndividualCollectionSheetScreen(
            onBackPressed = onBackPressed,
            onDetail = onDetail,
        )
    }
}

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

fun NavGraphBuilder.generateCollectionSheetScreen(
    onBackPressed: () -> Unit,
) {
    composable(CollectionSheetScreens.GenerateCollectionSheetScreen.route) {
        GenerateCollectionSheetScreen(
            onBackPressed = onBackPressed,
        )
    }
}

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
fun NavController.navigateToNewIndividualCollectionSheetDetailScreen(
    date: String,
    sheet: IndividualCollectionSheet,
) {
    navigate(CollectionSheetScreens.NewIndividualCollectionSheetDetailScreen.argument(date, sheet))
}

fun NavController.navigateToIndividualCollectionSheetDetailScreen(sheet: IndividualCollectionSheet) {
    navigate(CollectionSheetScreens.IndividualCollectionSheetDetailScreen.argument(sheet))
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
