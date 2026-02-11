/*
 * Copyright 2025 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.feature.searchrecord.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.mifos.core.model.objects.searchrecord.GenericSearchRecord
import com.mifos.core.model.objects.searchrecord.RecordType
import com.mifos.feature.searchrecord.SearchRecordScreen
import kotlinx.serialization.Serializable

@Serializable
data class SearchRecordRoute(
    val type: RecordType,
)

fun NavGraphBuilder.searchRecordNavigation(
    onBackClick: () -> Unit,
    onRecordSelected: (GenericSearchRecord) -> Unit,
) {
    composable<SearchRecordRoute> {
        SearchRecordScreen(
            onBackClick = onBackClick,
            onRecordSelected = onRecordSelected,
        )
    }
}

fun NavController.navigateToSearchRecord(type: RecordType) {
    navigate(SearchRecordRoute(type = type))
}
