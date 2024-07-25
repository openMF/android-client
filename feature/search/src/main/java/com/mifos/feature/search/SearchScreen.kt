package com.mifos.feature.search

import androidx.annotation.VisibleForTesting
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mifos.core.designsystem.component.MifosScaffold
import com.mifos.core.objects.SearchedEntity
import com.mifos.core.ui.components.FabButton
import com.mifos.core.ui.components.FabButtonState
import com.mifos.core.ui.components.FabType
import com.mifos.core.ui.components.MultiFloatingActionButton
import com.mifos.core.ui.util.DevicePreviews
import com.mifos.core.ui.util.SearchResultPreviewParameter
import com.mifos.feature.search.components.SearchBox
import com.mifos.feature.search.components.SearchScreenResult

@Composable
fun SearchScreenRoute(
    modifier: Modifier = Modifier,
    onFabClick: (FabType) -> Unit,
    onSearchOptionClick: (SearchedEntity) -> Unit,
    viewModel: SearchViewModel = hiltViewModel(),
) {
    val state by viewModel.state
    val searchResultState by viewModel.searchResult.collectAsStateWithLifecycle()

    SearchScreenContent(
        modifier = modifier,
        state = state,
        searchResultState = searchResultState,
        onEvent = viewModel::onEvent,
        onFabClick = onFabClick,
        onResultItemClick = onSearchOptionClick,
    )
}

@VisibleForTesting
@Composable
internal fun SearchScreenContent(
    modifier: Modifier = Modifier,
    state: SearchScreenState,
    searchResultState: SearchResultState,
    onEvent: (SearchScreenEvent) -> Unit,
    onFabClick: (FabType) -> Unit,
    onResultItemClick: (SearchedEntity) -> Unit,
) {
    val snackbarHostState = remember { SnackbarHostState() }
    var fabButtonState by remember { mutableStateOf<FabButtonState>(FabButtonState.Collapsed) }

    MifosScaffold(
        modifier = modifier,
        floatingActionButton = {
            MultiFloatingActionButton(
                fabButtons = listOf(
                    FabButton(
                        fabType = FabType.CLIENT,
                        iconRes = com.mifos.core.ui.R.drawable.core_ui_ic_person_black_24dp
                    ),
                    FabButton(
                        fabType = FabType.CENTER,
                        iconRes = com.mifos.core.ui.R.drawable.core_ui_ic_centers_24dp
                    ),
                    FabButton(
                        fabType = FabType.GROUP,
                        iconRes = com.mifos.core.ui.R.drawable.core_ui_ic_group_black_24dp
                    )
                ),
                fabButtonState = fabButtonState,
                onFabButtonStateChange = {
                    fabButtonState = it
                },
                onFabClick = onFabClick
            )
        },
        snackbarHostState = snackbarHostState
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(it),
            horizontalAlignment = Alignment.Start,
        ) {
            SearchBox(
                modifier = Modifier
                    .padding(horizontal = 16.dp),
                state = state,
                onEvent = onEvent
            )

            SearchScreenResult(
                modifier = Modifier.weight(2f),
                searchResultState = searchResultState,
                onResultItemClick = onResultItemClick
            )
        }
    }
}

// Previews
@DevicePreviews
@Composable
private fun SearchScreenContentEmptyStateAndLoadingPreview() {
    SearchScreenContent(
        modifier = Modifier,
        state = SearchScreenState(),
        searchResultState = SearchResultState.Loading,
        onEvent = {},
        onFabClick = {},
        onResultItemClick = {}
    )
}

@DevicePreviews
@Composable
private fun SearchScreenContentInitialEmptyStatePreview() {
    SearchScreenContent(
        modifier = Modifier,
        state = SearchScreenState(),
        searchResultState = SearchResultState.Empty(),
        onEvent = {},
        onFabClick = {},
        onResultItemClick = {}
    )
}

@DevicePreviews
@Composable
private fun SearchScreenContentEmptyResultPreview() {
    SearchScreenContent(
        modifier = Modifier,
        state = SearchScreenState(
            searchText = "yyy",
            selectedFilter = FilterOption.Groups
        ),
        searchResultState = SearchResultState.Empty(false),
        onEvent = {},
        onFabClick = {},
        onResultItemClick = {}
    )
}

@DevicePreviews
@Composable
private fun SearchScreenContentErrorPreview() {
    SearchScreenContent(
        modifier = Modifier,
        state = SearchScreenState(),
        searchResultState = SearchResultState.Error("Something went wrong!"),
        onEvent = {},
        onFabClick = {},
        onResultItemClick = {}
    )
}

@DevicePreviews
@Composable
private fun SearchScreenContentSuccessPreview(
    @PreviewParameter(SearchResultPreviewParameter::class)
    results: List<SearchedEntity>
) {
    SearchScreenContent(
        modifier = Modifier,
        state = SearchScreenState(
            searchText = "center",
        ),
        searchResultState = SearchResultState.Success(results),
        onEvent = {},
        onFabClick = {},
        onResultItemClick = {}
    )
}