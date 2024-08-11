package com.mifos.feature.individual_collection_sheet.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.mifos.core.objects.collectionsheet.IndividualCollectionSheet
import com.mifos.feature.individual_collection_sheet.individual_collection_sheet.ui.IndividualCollectionSheetScreen

/**
 * Created by Pronay Sarker on 10/08/2024 (7:24 AM)
 */
const val INDIVIDUAL_COLLECTION_SHEET_SCREEN_ROUTE = "individual_collection_sheet_route"

fun NavController.navigateToIndividualCollectionSheet() {
    this.navigate(INDIVIDUAL_COLLECTION_SHEET_SCREEN_ROUTE)
}

fun NavGraphBuilder.individualCollectionSheetScreen(
    onBackClicked: () -> Unit,
    onDetail: (String, IndividualCollectionSheet) -> Unit,
){
    composable(INDIVIDUAL_COLLECTION_SHEET_SCREEN_ROUTE) {
        IndividualCollectionSheetScreen(
            onBackPressed = {},
            onDetail = { _, _ -> }
        )
    }
}