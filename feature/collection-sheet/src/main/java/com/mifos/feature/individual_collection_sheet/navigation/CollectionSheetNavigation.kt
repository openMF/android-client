package com.mifos.feature.individual_collection_sheet.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.navigation
import com.mifos.core.common.utils.Constants
import com.mifos.core.network.model.IndividualCollectionSheetPayload
import com.mifos.core.objects.accounts.loan.PaymentTypeOptions
import com.mifos.core.objects.collectionsheet.IndividualCollectionSheet
import com.mifos.core.objects.collectionsheet.LoanAndClientName
import com.mifos.feature.individual_collection_sheet.generate_collection_sheet.GenerateCollectionSheetScreen
import com.mifos.feature.individual_collection_sheet.individual_collection_sheet.ui.IndividualCollectionSheetScreen
import com.mifos.feature.individual_collection_sheet.individual_collection_sheet_details.IndividualCollectionSheetDetailsScreen

/**
 * Created by Pronay Sarker on 20/08/2024 (4:06 PM)
 */
fun NavGraphBuilder.individualCollectionSheetNavGraph(
    navController: NavController,
    onBackPressed: () -> Unit,
    navigateToPaymentDetails: (Int, IndividualCollectionSheetPayload, List<String>, LoanAndClientName, List<PaymentTypeOptions>, Int) -> Unit
) {
    navigation(
        route = "generate_collection_sheet",
        startDestination = CollectionSheetScreens.IndividualCollectionSheetScreen.route
    ) {
        individualCollectionSheetScreen(
            onBackPressed = onBackPressed,
            onDetail = { _, sheet -> navController.navigateToIndividualCollectionSheetDetailScreen(sheet) }
        )

        individualCollectionSheetDetailScreen(
            onBackPressed = onBackPressed,
            submit = navigateToPaymentDetails
        )
    }
}

private fun NavGraphBuilder.individualCollectionSheetScreen(
    onBackPressed: () -> Unit,
    onDetail: (String, IndividualCollectionSheet) -> Unit
) {
    composable(
        route = CollectionSheetScreens.IndividualCollectionSheetScreen.route
    ) {
        IndividualCollectionSheetScreen(
            onBackPressed = onBackPressed,
            onDetail = onDetail
        )
    }
}

private fun NavGraphBuilder.individualCollectionSheetDetailScreen(
    onBackPressed: () -> Unit,
    submit: (Int, IndividualCollectionSheetPayload, List<String>, LoanAndClientName, List<PaymentTypeOptions>, Int) -> Unit
) {
    composable(
        route = CollectionSheetScreens.IndividualCollectionSheetDetailScreen.route,
        arguments = listOf(
            navArgument(name = Constants.INDIVIDUAL_SHEET, builder = { NavType.StringType })
        )
    ) {
        IndividualCollectionSheetDetailsScreen(
            onBackPressed = onBackPressed,
            submit = submit
        )
    }
}

fun NavGraphBuilder.generateCollectionSheetScreen(
    onBackPressed: () -> Unit
) {
    composable(CollectionSheetScreens.GenerateCollectionSheetScreen.route) {
        GenerateCollectionSheetScreen(
            onBackPressed = onBackPressed
        )
    }
}

fun NavController.navigateToIndividualCollectionSheetDetailScreen(sheet: IndividualCollectionSheet) {
    navigate(CollectionSheetScreens.IndividualCollectionSheetDetailScreen.argument(sheet))
}

fun NavController.navigateToIndividualCollectionSheet() {
    navigate(CollectionSheetScreens.IndividualCollectionSheetScreen.route)
}