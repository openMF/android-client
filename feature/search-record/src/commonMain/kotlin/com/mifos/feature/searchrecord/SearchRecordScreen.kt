/*
 * Copyright 2025 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.feature.searchrecord

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mifos.core.designsystem.component.MifosScaffold
import com.mifos.core.designsystem.icon.MifosIcons
import com.mifos.core.designsystem.theme.DesignToken
import com.mifos.core.model.objects.searchrecord.GenericSearchRecord
import com.mifos.core.ui.components.MifosActionsIdentifierListingComponent
import com.mifos.core.ui.components.MifosAddressCard
import com.mifos.core.ui.components.MifosProgressIndicator
import com.mifos.core.ui.utils.getClientIdentifierStatus
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

@Composable
internal fun SearchRecordScreen(
    onBackClick: () -> Unit,
    onRecordSelected: (GenericSearchRecord) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: SearchRecordViewModel = koinViewModel(),
) {
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    SearchRecordScreen(
        modifier = modifier,
        searchQuery = searchQuery,
        searchLabel = viewModel.searchLabel,
        uiState = uiState,
        onSearchQueryChanged = viewModel::onSearchQueryChanged,
        onClearSearch = viewModel::clearSearch,
        onBackClick = onBackClick,
        onRecordSelected = onRecordSelected,
    )
}

@Composable
internal fun SearchRecordScreen(
    modifier: Modifier = Modifier,
    searchQuery: String = "",
    searchLabel: String = stringResource(Res.string.search_record_generic_searchLabel),
    uiState: SearchRecordUiState = SearchRecordUiState.Idle,
    onSearchQueryChanged: (String) -> Unit = {},
    onClearSearch: () -> Unit = {},
    onBackClick: () -> Unit = {},
    onRecordSelected: (GenericSearchRecord) -> Unit = {},
) {
    MifosScaffold(
        title = searchLabel,
        onBackPressed = onBackClick,
    ) { paddingValues ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues),
        ) {
            SearchRecordToolbar(
                searchQuery = searchQuery,
                onSearchQueryChanged = onSearchQueryChanged,
                onClearSearch = onClearSearch,
            )

            when (uiState) {
                SearchRecordUiState.Idle -> SearchRecordEmptyState()
                SearchRecordUiState.Loading -> MifosProgressIndicator()
                SearchRecordUiState.EmptyQuery -> SearchRecordEmptyState()
                SearchRecordUiState.NoResults -> SearchRecordNoResultsState(searchQuery)
                is SearchRecordUiState.Success -> SearchRecordResultsList(
                    records = uiState.records,
                    onRecordSelected = onRecordSelected,
                )
                is SearchRecordUiState.Error -> SearchRecordErrorState(uiState.message)
            }
        }
    }
}

@Composable
private fun SearchRecordToolbar(
    searchQuery: String,
    onSearchQueryChanged: (String) -> Unit,
    onClearSearch: () -> Unit,
) {
    val focusRequester = remember { FocusRequester() }
    val keyboardController = LocalSoftwareKeyboardController.current

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
    ) {
        TextField(
            value = searchQuery,
            onValueChange = onSearchQueryChanged,
            modifier = Modifier
                .fillMaxWidth()
                .focusRequester(focusRequester),
            shape = RoundedCornerShape(32.dp),
            colors = TextFieldDefaults.colors(
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
            ),
            leadingIcon = {
                Icon(
                    imageVector = MifosIcons.Search,
                    contentDescription = stringResource(Res.string.search_record_search_icon_desc),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            },
            trailingIcon = {
                if (searchQuery.isNotEmpty()) {
                    IconButton(onClick = onClearSearch) {
                        Icon(
                            imageVector = MifosIcons.Close,
                            contentDescription = stringResource(Res.string.search_record_clear_icon_desc),
                        )
                    }
                }
            },
            placeholder = { Text(stringResource(Res.string.search_record_input_placeholder)) },
            singleLine = true,
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
            keyboardActions = KeyboardActions(
                onSearch = { keyboardController?.hide() },
            ),
        )
    }

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }
}

@Composable
private fun SearchRecordResultsList(
    records: List<GenericSearchRecord>,
    onRecordSelected: (GenericSearchRecord) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = DesignToken.padding.large),
    ) {
        Spacer(modifier = Modifier.height(DesignToken.padding.largeIncreasedExtra))

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
        ) {
            items(records, key = { it.id }) { record ->
                SearchRecordItem(
                    record = record,
                    onRecordSelected = onRecordSelected,
                )
                Spacer(Modifier.height(DesignToken.spacing.small))
            }
        }
    }
}

@Composable
private fun SearchRecordItem(
    record: GenericSearchRecord,
    onRecordSelected: (GenericSearchRecord) -> Unit,
) {
    when {
        record.type.equals(stringResource(Res.string.search_record_address), ignoreCase = true) -> {
            AddressRecordCard(record, onRecordSelected)
        }
        record.type.equals(stringResource(Res.string.search_record_identifiers), ignoreCase = true) -> {
            IdentifierRecordCard(record, onRecordSelected)
        }
        else -> {
            GenericRecordCard(record, onRecordSelected)
        }
    }
}

@Composable
private fun AddressRecordCard(
    record: GenericSearchRecord,
    onRecordSelected: (GenericSearchRecord) -> Unit,
) {
    androidx.compose.material3.Surface(
        onClick = { onRecordSelected(record) },
        modifier = Modifier.fillMaxWidth(),
        shape = DesignToken.shapes.medium,
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            MaterialTheme.colorScheme.secondaryContainer,
        ),
        color = Color.Transparent,
    ) {
        MifosAddressCard(
            title = record.name,
            addressList = mapOf(
                "Address Line 1" to (record.metadata["addressLine1"] ?: ""),
                "Address Line 2" to (record.metadata["addressLine2"] ?: ""),
                "Address Line 3" to (record.metadata["addressLine3"] ?: ""),
                "City" to (record.metadata["city"] ?: ""),
                "Province" to (record.metadata["state"] ?: ""),
                "Country" to (record.metadata["country"] ?: ""),
                "Postal Code" to (record.metadata["postalCode"] ?: ""),
            ),
        )
    }
}

@Composable
private fun IdentifierRecordCard(
    record: GenericSearchRecord,
    onRecordSelected: (GenericSearchRecord) -> Unit,
) {
    val rawStatus = record.metadata["status"] ?: ""
    val statusObject = getClientIdentifierStatus(rawStatus)

    MifosActionsIdentifierListingComponent(
        type = record.name,
        id = record.id.toString(),
        key = record.metadata["documentKey"] ?: "",
        status = statusObject,
        description = record.description,
        identifyDocuments = record.name,
        isExpanded = false,
        menuList = emptyList(),
        onActionClicked = {},
        onClick = { onRecordSelected(record) },
    )
}

@Composable
private fun GenericRecordCard(
    record: GenericSearchRecord,
    onRecordSelected: (GenericSearchRecord) -> Unit,
) {
    androidx.compose.material3.Card(
        modifier = Modifier
            .fillMaxWidth(),
        onClick = { onRecordSelected(record) },
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
        ) {
            Text(
                text = record.name,
                style = MaterialTheme.typography.titleMedium,
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = record.description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@Composable
private fun SearchRecordEmptyState() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = stringResource(Res.string.search_record_empty_state),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@Composable
private fun SearchRecordNoResultsState(query: String) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = stringResource(Res.string.search_record_no_results_title),
            style = MaterialTheme.typography.headlineSmall,
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = stringResource(Res.string.search_record_no_results_description, query),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@Composable
private fun SearchRecordErrorState(message: String) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = stringResource(Res.string.search_record_error),
            style = MaterialTheme.typography.headlineSmall,
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = message,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.error,
        )
    }
}
