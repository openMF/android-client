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
import com.google.gson.Gson
import com.mifos.core.common.utils.Constants
import com.mifos.core.network.model.IndividualCollectionSheetPayload
import com.mifos.core.modelobjects.account.loan.PaymentTypeOptions
import com.mifos.core.objects.collectionsheet.IndividualCollectionSheet
import com.mifos.core.modelobjects.collectionsheets.LoanAndClientName
import com.mifos.feature.individualCollectionSheet.generateCollectionSheet.GenerateCollectionSheetScreen
import com.mifos.feature.individualCollectionSheet.individualCollectionSheet.ui.IndividualCollectionSheetScreen
import com.mifos.feature.individualCollectionSheet.individualCollectionSheetDetails.IndividualCollectionSheetDetailsScreen
import com.mifos.feature.individualCollectionSheet.paymentDetails.PaymentDetailsScreenRoute

/**
 * Created by Pronay Sarker on 20/08/2024 (4:06 PM)
 */
fun NavGraphBuilder.individualCollectionSheetNavGraph(
    navController: NavController,
    onBackPressed: () -> Unit,
) {
    navigation(
        route = "generate_collection_sheet",
        startDestination = CollectionSheetScreens.IndividualCollectionSheetScreen.route,
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
            navArgument(name = Constants.ADAPTER_POSITION, builder = { NavType.IntType }),
            navArgument(name = Constants.CLIENT_ID, builder = { NavType.IntType }),
            navArgument(name = Constants.PAYLOAD, builder = { NavType.StringType }),
            navArgument(name = Constants.PAYMENT_LIST, builder = { NavType.StringType }),
            navArgument(name = Constants.LOAN_AND_CLIENT, builder = { NavType.StringType }),
            navArgument(name = Constants.PAYMENT_OPTIONS, builder = { NavType.StringType }),
        ),
    ) {
        PaymentDetailsScreenRoute()
    }
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
    val payloadInGsonString = Gson().toJson(payload)
    val paymentTypeOptionNameInGsonString = Gson().toJson(paymentTypeOptionsName)
    val loansAndClientNameInGsonString = Gson().toJson(loansAndClientName)
    val paymentTypeOptionsInGsonString = Gson().toJson(paymentTypeOptions)

    navigate(
        CollectionSheetScreens.PaymentDetailsScreen.argument(
            position,
            payloadInGsonString,
            paymentTypeOptionNameInGsonString,
            loansAndClientNameInGsonString,
            paymentTypeOptionsInGsonString,
            clientId,
        ),
    )
}
