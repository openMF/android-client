/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mifos-x-field-officer-app/blob/master/LICENSE.md
 */
package com.mifos.feature.search.ui

import androidclient.feature.search.generated.resources.Res
import androidclient.feature.search.generated.resources.feature_search_filter_options_clients_label
import androidclient.feature.search.generated.resources.feature_search_filter_options_clients_value
import androidclient.feature.search.generated.resources.feature_search_filter_options_groups_label
import androidclient.feature.search.generated.resources.feature_search_filter_options_groups_value
import androidclient.feature.search.generated.resources.feature_search_filter_options_loans_label
import androidclient.feature.search.generated.resources.feature_search_filter_options_loans_value
import androidclient.feature.search.generated.resources.feature_search_filter_options_savings_label
import androidclient.feature.search.generated.resources.feature_search_filter_options_savings_value
import template.core.base.store.screen.ScreenState
import com.mifos.core.model.objects.SearchedEntity
import org.jetbrains.compose.resources.StringResource

data class SearchScreenState(
    val searchText: String = "",
    val selectedFilter: FilterOption? = null,
    val exactMatch: Boolean? = null,
    val showEmptyError: Boolean = false,
    val resultState: ScreenState<List<SearchedEntity>> = ScreenState.Empty,
)

sealed interface SearchAction {
    data class UpdateSearchText(val searchText: String) : SearchAction
    data class UpdateSelectedFilter(val filter: FilterOption? = null) : SearchAction
    data object ClearSearchText : SearchAction
    data object UpdateExactMatch : SearchAction
    data object PerformSearch : SearchAction
}

sealed class FilterOption(val labelRes: StringResource, val valueRes: StringResource) {

    data object Clients : FilterOption(
        labelRes = Res.string.feature_search_filter_options_clients_label,
        valueRes = Res.string.feature_search_filter_options_clients_value,
    )

    data object Groups : FilterOption(
        labelRes = Res.string.feature_search_filter_options_groups_label,
        valueRes = Res.string.feature_search_filter_options_groups_value,
    )

    data object LoanAccounts : FilterOption(
        labelRes = Res.string.feature_search_filter_options_loans_label,
        valueRes = Res.string.feature_search_filter_options_loans_value,
    )

    data object SavingsAccounts : FilterOption(
        labelRes = Res.string.feature_search_filter_options_savings_label,
        valueRes = Res.string.feature_search_filter_options_savings_value,
    )

    companion object {
        val values = listOf(Clients, Groups, LoanAccounts, SavingsAccounts)
    }
}
