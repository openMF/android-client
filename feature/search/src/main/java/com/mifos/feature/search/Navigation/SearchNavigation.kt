package com.mifos.feature.search.Navigation

import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.mifos.core.ui.components.FabType
import com.mifos.feature.search.SearchScreenRoute

/**
 * Created by Pronay Sarker on 10/08/2024 (6:35 AM)
 */
const val SEARCH_SCREEN_ROUTE = "search_screen"

fun NavController.navigateToSearchScreen() {
    this.navigate(SEARCH_SCREEN_ROUTE)
}

fun NavGraphBuilder.searchScreen(
    modifier: Modifier = Modifier,
    centerListScreen: () -> Unit,
    groupListScreen: () -> Unit,
    clientListScreen: () -> Unit,
) {
    composable(SEARCH_SCREEN_ROUTE) {
        SearchScreenRoute(
            modifier = Modifier,
            onFabClick = { fabOptions ->
                when(fabOptions){
                    FabType.CLIENT -> TODO()
                    FabType.CENTER -> TODO()
                    FabType.GROUP -> TODO()
                }
            },
            onSearchOptionClick = {

            },
        )
    }
}