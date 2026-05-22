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
import androidclient.feature.search.generated.resources.feature_search_no_search_result_found
import androidx.annotation.VisibleForTesting
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import template.core.base.store.screen.DataFreshness
import template.core.base.store.screen.ScreenState
import com.mifos.core.designsystem.component.MifosScaffold
import com.mifos.core.designsystem.icon.MifosIcons
import com.mifos.core.model.objects.SearchedEntity
import com.mifos.core.ui.components.FabButton
import com.mifos.core.ui.components.FabButtonState
import com.mifos.core.ui.components.FabType
import com.mifos.core.ui.components.MifosEmptyUi
import com.mifos.core.ui.components.MultiFloatingActionButton
import com.mifos.core.ui.util.DevicePreview
import com.mifos.feature.search.components.SearchBox
import com.mifos.feature.search.components.SearchResultsList
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import template.core.base.designsystem.theme.KptTheme
import template.core.base.ui.screen.ScreenContent

@Composable
fun SearchScreen(
    onFabClick: (FabType) -> Unit,
    onSearchOptionClick: (SearchedEntity) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: SearchViewModel = koinViewModel(),
) {
    val state by viewModel.stateFlow.collectAsStateWithLifecycle()

    SearchScreenContent(
        modifier = modifier,
        state = state,
        onAction = viewModel::trySendAction,
        onFabClick = onFabClick,
        onResultItemClick = onSearchOptionClick,
    )
}

@VisibleForTesting
@Composable
internal fun SearchScreenContent(
    state: SearchScreenState,
    onAction: (SearchAction) -> Unit,
    onFabClick: (FabType) -> Unit,
    onResultItemClick: (SearchedEntity) -> Unit,
    modifier: Modifier = Modifier,
) {
    val snackbarHostState = remember { SnackbarHostState() }
    var fabButtonState by remember {
        mutableStateOf<FabButtonState>(FabButtonState.Collapsed)
    }

    MifosScaffold(
        modifier = modifier,
        floatingActionButton = {
            MultiFloatingActionButton(
                fabButtons = listOf(
                    FabButton.VectorFab(
                        type = FabType.CLIENT,
                        iconRes = MifosIcons.Person,
                    ),
                    FabButton.VectorFab(
                        type = FabType.CENTER,
                        iconRes = MifosIcons.buildingIcon,
                    ),
                    FabButton.VectorFab(
                        type = FabType.GROUP,
                        iconRes = MifosIcons.People,
                    ),
                ),
                fabButtonState = fabButtonState,
                onFabButtonStateChange = { fabButtonState = it },
                onFabClick = onFabClick,
            )
        },
        snackbarHostState = snackbarHostState,
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.Start,
        ) {
            SearchBox(
                modifier = Modifier
                    .padding(horizontal = KptTheme.spacing.md),
                state = state,
                onEvent = onAction,
            )

            ScreenContent(
                state = state.resultState,
                onRetry = { onAction(SearchAction.PerformSearch) },
                showFreshnessIndicator = false,
                empty = {
                    if (state.searchText.isNotEmpty()) {
                        MifosEmptyUi(
                            text = stringResource(Res.string.feature_search_no_search_result_found),
                        )
                    } else {
                        Box(modifier = Modifier.fillMaxSize())
                    }
                },
            ) { results, _ ->
                SearchResultsList(
                    results = results,
                    onResultItemClick = onResultItemClick,
                )
            }
        }
    }
}

@DevicePreview
@Composable
private fun SearchScreenContentLoadingPreview() {
    SearchScreenContent(
        modifier = Modifier,
        state = SearchScreenState(
            searchText = "smith",
            resultState = ScreenState.Loading,
        ),
        onAction = {},
        onFabClick = {},
        onResultItemClick = {},
    )
}

@DevicePreview
@Composable
private fun SearchScreenContentInitialEmptyPreview() {
    SearchScreenContent(
        modifier = Modifier,
        state = SearchScreenState(),
        onAction = {},
        onFabClick = {},
        onResultItemClick = {},
    )
}

@DevicePreview
@Composable
private fun SearchScreenContentNoResultsPreview() {
    SearchScreenContent(
        modifier = Modifier,
        state = SearchScreenState(
            searchText = "yyy",
            selectedFilter = FilterOption.Groups,
            resultState = ScreenState.Empty,
        ),
        onAction = {},
        onFabClick = {},
        onResultItemClick = {},
    )
}

@DevicePreview
@Composable
private fun SearchScreenContentContentPreview() {
    SearchScreenContent(
        modifier = Modifier,
        state = SearchScreenState(
            searchText = "smith",
            resultState = ScreenState.Content(
                data = listOf(
                    SearchedEntity(
                        entityId = 1,
                        entityAccountNo = "ACC-001",
                        entityName = "John Smith",
                        entityType = "client",
                        parentId = 0,
                        parentName = null,
                        entityStatus = null,
                    ),
                    SearchedEntity(
                        entityId = 2,
                        entityAccountNo = "ACC-002",
                        entityName = "Smith Group",
                        entityType = "group",
                        parentId = 0,
                        parentName = null,
                        entityStatus = null,
                    ),
                ),
                freshness = DataFreshness.FRESH,
            ),
        ),
        onAction = {},
        onFabClick = {},
        onResultItemClick = {},
    )
}

@DevicePreview
@Composable
private fun SearchScreenContentErrorPreview() {
    SearchScreenContent(
        modifier = Modifier,
        state = SearchScreenState(
            searchText = "smith",
            resultState = ScreenState.Error(
                error = RuntimeException("Network unavailable"),
            ),
        ),
        onAction = {},
        onFabClick = {},
        onResultItemClick = {},
    )
}
