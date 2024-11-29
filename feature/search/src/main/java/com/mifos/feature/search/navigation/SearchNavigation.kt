/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.feature.search.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.mifos.core.common.utils.Constants
import com.mifos.core.objects.SearchedEntity
import com.mifos.core.ui.components.FabType
import com.mifos.feature.search.SearchScreenRoute

fun NavGraphBuilder.searchNavGraph(
    paddingValues: PaddingValues,
    onCreateClient: () -> Unit,
    onCreateCenter: () -> Unit,
    onCreateGroup: () -> Unit,
    onClient: (Int) -> Unit,
    onCenter: (Int) -> Unit,
    onGroup: (Int) -> Unit,
    onSavings: (Int) -> Unit,
    onLoan: (Int) -> Unit,
) {
    navigation(
        startDestination = SearchScreens.SearchScreen.route,
        route = SearchScreens.SearchScreenRoute.route,
    ) {
        searchRoute(
            modifier = Modifier.padding(paddingValues),
            onFabClick = {
                when (it) {
                    FabType.CLIENT -> {
                        onCreateClient()
                    }

                    FabType.CENTER -> {
                        onCreateCenter()
                    }

                    FabType.GROUP -> {
                        onCreateGroup()
                    }
                }
            },
            onSearchOptionClick = { searchedEntity ->
                when (searchedEntity.entityType) {
                    Constants.SEARCH_ENTITY_LOAN -> {
                        onLoan(searchedEntity.entityId)
                    }

                    Constants.SEARCH_ENTITY_CLIENT -> {
                        onClient(searchedEntity.entityId)
                    }

                    Constants.SEARCH_ENTITY_GROUP -> {
                        onGroup(searchedEntity.entityId)
                    }

                    Constants.SEARCH_ENTITY_SAVING -> {
                        onSavings(searchedEntity.entityId)
                    }

                    Constants.SEARCH_ENTITY_CENTER -> {
                        onCenter(searchedEntity.entityId)
                    }
                }
            },
        )
    }
}

fun NavGraphBuilder.searchRoute(
    modifier: Modifier,
    onFabClick: (FabType) -> Unit,
    onSearchOptionClick: (SearchedEntity) -> Unit,
) {
    composable(
        route = SearchScreens.SearchScreen.route,
    ) {
        SearchScreenRoute(
            modifier = modifier,
            onFabClick = onFabClick,
            onSearchOptionClick = onSearchOptionClick,
        )
    }
}
