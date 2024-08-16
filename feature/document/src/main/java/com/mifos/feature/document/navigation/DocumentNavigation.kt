package com.mifos.feature.document.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.mifos.core.common.utils.Constants
import com.mifos.feature.document.document_list.DocumentListScreen

/**
 * Created by Pronay Sarker on 17/08/2024 (4:00 AM)
 */
fun NavGraphBuilder.documentListScreen(
    onBackPressed: () -> Unit
) {
    composable(
        route = DocumentScreens.DocumentListScreen.route,
        arguments = listOf(
            navArgument(name = Constants.ENTITY_ID, builder = { type = NavType.IntType }),
            navArgument(name = Constants.ENTITY_TYPE, builder = { type = NavType.StringType })
        )
    ) {
        DocumentListScreen(
            onBackPressed = onBackPressed
        )
    }
}

fun NavController.navigateToDocumentListScreen(entityId : Int, entityType : String) {
    navigate(DocumentScreens.DocumentListScreen.argument(entityId, entityType))
}