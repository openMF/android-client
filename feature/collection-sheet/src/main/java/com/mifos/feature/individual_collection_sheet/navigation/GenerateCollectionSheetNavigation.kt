package com.mifos.feature.individual_collection_sheet.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.mifos.feature.individual_collection_sheet.generate_collection_sheet.GenerateCollectionSheetScreen

/**
 * Created by Pronay Sarker on 10/08/2024 (7:30 AM)
 */
const val GENERATE_COLLECTION_SHEET_SCREEN_ROUTE = "generate_collection_sheet_route"

fun NavController.navigateToGenerateCollectionSheet() {
    this.navigate(GENERATE_COLLECTION_SHEET_SCREEN_ROUTE)
}

fun NavGraphBuilder.generateCollectionSheetScreen(
    onBackPressed: () -> Unit
){
    composable(GENERATE_COLLECTION_SHEET_SCREEN_ROUTE) {
        GenerateCollectionSheetScreen (
            onBackPressed = onBackPressed
        )
    }
}