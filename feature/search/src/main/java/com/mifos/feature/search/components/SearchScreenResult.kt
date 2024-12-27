/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.feature.search.components

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import com.amulyakhare.textdrawable.TextDrawable
import com.amulyakhare.textdrawable.util.ColorGenerator
import com.google.accompanist.drawablepainter.rememberDrawablePainter
import com.mifos.core.designsystem.component.MifosCircularProgress
import com.mifos.core.modelobjects.SearchedEntity
import com.mifos.core.ui.components.MifosEmptyUi
import com.mifos.core.ui.util.DevicePreviews
import com.mifos.core.ui.util.SearchResultPreviewParameter
import com.mifos.feature.search.R
import com.mifos.feature.search.SearchResultState

@Composable
internal fun SearchScreenResult(
    searchResultState: SearchResultState,
    onResultItemClick: (SearchedEntity) -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .fillMaxSize(),
    ) {
        Crossfade(
            targetState = searchResultState,
            label = "SearchResult",
        ) { state ->
            when (state) {
                is SearchResultState.Loading -> {
                    MifosCircularProgress(contentDesc = "searchResultIndicator")
                }

                is SearchResultState.Empty -> {
                    if (!state.initial) {
                        MifosEmptyUi(
                            text = stringResource(R.string.feature_search_no_search_result_found),
                        )
                    }
                }

                is SearchResultState.Error -> {
                    MifosEmptyUi(
                        text = state.message,
                        modifier = Modifier.align(Alignment.Center),
                    )
                }

                is SearchResultState.Success -> {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize(),
                        contentPadding = PaddingValues(vertical = 8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        items(
                            items = state.results,
                            key = {
                                "${it.entityType}-${it.entityId}-${it.parentId}"
                            },
                        ) { searchedEntity ->
                            SearchResult(
                                searchedEntity = searchedEntity,
                                onSearchOptionClick = onResultItemClick,
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
internal fun SearchResult(
    searchedEntity: SearchedEntity,
    onSearchOptionClick: (SearchedEntity) -> Unit,
    modifier: Modifier = Modifier,
) {
    val color = ColorGenerator.MATERIAL.getColor(searchedEntity.entityType)
    val drawable =
        TextDrawable.builder().round().build(searchedEntity.entityType?.get(0).toString(), color)

    ListItem(
        headlineContent = {
            Text(text = searchedEntity.description)
        },
        leadingContent = {
            Image(
                modifier = Modifier
                    .width(50.dp)
                    .height(50.dp),
                contentDescription = null,
                painter = rememberDrawablePainter(drawable = drawable),
            )
        },
        colors = ListItemDefaults.colors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerLowest,
        ),
        modifier = modifier
            .fillMaxWidth()
            .clickable {
                onSearchOptionClick(searchedEntity)
            },
    )
}

@DevicePreviews
@Composable
private fun SearchScreenResultLoadingPreview() {
    SearchScreenResult(
        searchResultState = SearchResultState.Loading,
        onResultItemClick = {},
    )
}

@DevicePreviews
@Composable
private fun SearchScreenResultInitialEmptyPreview() {
    SearchScreenResult(
        searchResultState = SearchResultState.Empty(),
        onResultItemClick = {},
    )
}

@DevicePreviews
@Composable
private fun SearchScreenResultEmptyPreview() {
    SearchScreenResult(
        searchResultState = SearchResultState.Empty(true),
        onResultItemClick = {},
    )
}

@DevicePreviews
@Composable
private fun SearchScreenResultErrorPreview() {
    SearchScreenResult(
        searchResultState = SearchResultState.Error("Unable to fetch data from server"),
        onResultItemClick = {},
    )
}

@DevicePreviews
@Composable
private fun SearchScreenResultSuccessPreview(
    @PreviewParameter(SearchResultPreviewParameter::class)
    results: List<SearchedEntity>,
) {
    SearchScreenResult(
        searchResultState = SearchResultState.Success(results),
        onResultItemClick = {},
    )
}

@DevicePreviews
@Composable
private fun SearchResultPreview() {
    SearchResult(
        searchedEntity = SearchedEntity(
            entityId = 5683,
            entityAccountNo = "122233",
            entityName = "Center 2",
            entityType = "center",
            parentId = 3296,
            parentName = "center",
            entityStatus = null,
        ),
        onSearchOptionClick = {},
    )
}
