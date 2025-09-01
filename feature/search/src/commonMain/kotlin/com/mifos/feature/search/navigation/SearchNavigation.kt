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

import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.mifos.core.common.utils.Constants
import com.mifos.core.ui.components.FabType
import com.mifos.feature.search.SearchScreen
import kotlinx.serialization.Serializable

@Serializable
data object SearchScreenRoute

fun NavGraphBuilder.searchNavGraph(
    onCreateClient: () -> Unit,
    onCreateCenter: () -> Unit,
    onCreateGroup: () -> Unit,
    onClient: (Int) -> Unit,
    onCenter: (Int) -> Unit,
    onGroup: (Int) -> Unit,
    onSavings: (Int) -> Unit,
    onLoan: (Int) -> Unit,
) {
    composable<SearchScreenRoute> {
        SearchScreen(
            modifier = Modifier,
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

fun NavController.navigateToSearchScreen() {
    this.navigate(SearchScreenRoute)
}
