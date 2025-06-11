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

import androidclient.feature.search.generated.resources.Res
import androidclient.feature.search.generated.resources.feature_search_no_search_result_found
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.mifos.core.designsystem.component.MifosCircularProgress
import com.mifos.core.model.objects.SearchedEntity
import com.mifos.core.ui.components.MifosEmptyUi
import com.mifos.core.ui.util.DevicePreview
import com.mifos.feature.search.SearchResultState
import org.jetbrains.compose.resources.stringResource

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
                            text = stringResource(Res.string.feature_search_no_search_result_found),
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
    val entityType = searchedEntity.entityType ?: "?"
    val color = getMaterialColor(entityType)

    ListItem(
        headlineContent = {
            Text(text = searchedEntity.description)
        },
        leadingContent = {
            ColoredAvatar(
                initial = entityType.firstOrNull() ?: '?',
                backgroundColor = color,
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

@Composable
fun ColoredAvatar(initial: Char, backgroundColor: Color, modifier: Modifier = Modifier) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .size(50.dp)
            .clip(CircleShape)
            .background(backgroundColor),
    ) {
        Text(
            text = initial.uppercaseChar().toString(),
            color = Color.White,
            fontWeight = FontWeight.Bold,
            style = MaterialTheme.typography.titleLarge,
        )
    }
}

fun getMaterialColor(input: String): Color {
    val hash = input.hashCode()
    val red = (hash shr 16 and 0xFF)
    val green = (hash shr 8 and 0xFF)
    val blue = (hash and 0xFF)
    return Color(red, green, blue)
}

@DevicePreview
@Composable
private fun SearchScreenResultLoadingPreview() {
    SearchScreenResult(
        searchResultState = SearchResultState.Loading,
        onResultItemClick = {},
    )
}

@DevicePreview
@Composable
private fun SearchScreenResultInitialEmptyPreview() {
    SearchScreenResult(
        searchResultState = SearchResultState.Empty(),
        onResultItemClick = {},
    )
}

@DevicePreview
@Composable
private fun SearchScreenResultEmptyPreview() {
    SearchScreenResult(
        searchResultState = SearchResultState.Empty(true),
        onResultItemClick = {},
    )
}

@DevicePreview
@Composable
private fun SearchScreenResultErrorPreview() {
    SearchScreenResult(
        searchResultState = SearchResultState.Error("Unable to fetch data from server"),
        onResultItemClick = {},
    )
}

@DevicePreview
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
