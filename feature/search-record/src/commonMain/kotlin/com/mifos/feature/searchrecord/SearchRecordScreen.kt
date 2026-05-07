/*
 * Copyright 2025 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mifos-x-field-officer-app/blob/master/LICENSE.md
 */
package com.mifos.feature.searchrecord

import androidclient.feature.search_record.generated.resources.Res
import androidclient.feature.search_record.generated.resources.search_record_address_line_1
import androidclient.feature.search_record.generated.resources.search_record_address_line_2
import androidclient.feature.search_record.generated.resources.search_record_address_line_3
import androidclient.feature.search_record.generated.resources.search_record_city
import androidclient.feature.search_record.generated.resources.search_record_clear_icon_desc
import androidclient.feature.search_record.generated.resources.search_record_country
import androidclient.feature.search_record.generated.resources.search_record_empty_state
import androidclient.feature.search_record.generated.resources.search_record_error
import androidclient.feature.search_record.generated.resources.search_record_generic_searchLabel
import androidclient.feature.search_record.generated.resources.search_record_input_placeholder
import androidclient.feature.search_record.generated.resources.search_record_label_format
import androidclient.feature.search_record.generated.resources.search_record_no_results_description
import androidclient.feature.search_record.generated.resources.search_record_no_results_title
import androidclient.feature.search_record.generated.resources.search_record_postal_code
import androidclient.feature.search_record.generated.resources.search_record_province
import androidclient.feature.search_record.generated.resources.search_record_search_icon_desc
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
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
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mifos.core.common.utils.Constants
import com.mifos.core.designsystem.component.MifosScaffold
import com.mifos.core.designsystem.icon.MifosIcons
import com.mifos.core.designsystem.theme.DesignToken
import com.mifos.core.model.objects.searchrecord.GenericSearchRecord
import com.mifos.core.model.objects.searchrecord.RecordType
import com.mifos.core.ui.components.MifosActionsIdentifierListingComponent
import com.mifos.core.ui.components.MifosAddressCard
import com.mifos.core.ui.components.MifosAlertDialog
import com.mifos.core.ui.components.MifosProgressIndicator
import com.mifos.core.ui.util.EventsEffect
import com.mifos.core.ui.utils.getClientIdentifierStatus
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.jetbrains.compose.ui.tooling.preview.PreviewParameter
import org.jetbrains.compose.ui.tooling.preview.PreviewParameterProvider
import org.koin.compose.viewmodel.koinViewModel
import template.core.base.designsystem.theme.KptTheme

@Composable
internal fun SearchRecordScreen(
    onBackClick: () -> Unit,
    onRecordSelected: (GenericSearchRecord) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: SearchRecordViewModel = koinViewModel(),
) {
    val state by viewModel.stateFlow.collectAsStateWithLifecycle()

    EventsEffect(viewModel.eventFlow) { event ->
        when (event) {
            SearchRecordEvent.NavigateBack -> onBackClick()
            is SearchRecordEvent.NavigateToRecord -> onRecordSelected(event.record)
        }
    }

    val searchLabel = stringResource(
        Res.string.search_record_label_format,
        state.displayTitle,
    )

    SearchRecordScreen(
        modifier = modifier,
        state = state,
        searchLabel = searchLabel,
        onSearchQueryChanged = { viewModel.trySendAction(SearchRecordAction.SearchQueryChanged(it)) },
        onClearSearch = { viewModel.trySendAction(SearchRecordAction.ClearSearch) },
        onBackClick = { viewModel.trySendAction(SearchRecordAction.NavigateBack) },
        onRecordSelected = { viewModel.trySendAction(SearchRecordAction.SelectRecord(it)) },
        onCloseDialog = { viewModel.trySendAction(SearchRecordAction.CloseDialog) },
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun SearchRecordScreen(
    state: SearchRecordState,
    modifier: Modifier = Modifier,
    searchLabel: String = stringResource(Res.string.search_record_generic_searchLabel),
    onSearchQueryChanged: (String) -> Unit = {},
    onClearSearch: () -> Unit = {},
    onBackClick: () -> Unit = {},
    onRecordSelected: (GenericSearchRecord) -> Unit = {},
    onCloseDialog: () -> Unit = {},
) {
    MifosScaffold(
        title = searchLabel,
        onBackPressed = onBackClick,
    ) { paddingValues ->
        Box(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues),
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                SearchRecordToolbar(
                    searchQuery = state.searchQuery,
                    onSearchQueryChanged = onSearchQueryChanged,
                    onClearSearch = onClearSearch,
                )

                when {
                    state.searchRecords.isNotEmpty() -> {
                        SearchRecordResultsList(
                            records = state.searchRecords,
                            onRecordSelected = onRecordSelected,
                        )
                    }
                    state.isNoResultsFound -> {
                        SearchRecordNoResultsState(state.searchQuery)
                    }
                    else -> {
                        SearchRecordEmptyState()
                    }
                }
            }

            when (val dialog = state.dialogState) {
                SearchRecordState.DialogState.Loading -> {
                    MifosProgressIndicator(
                        modifier = Modifier.align(Alignment.Center),
                    )
                }

                is SearchRecordState.DialogState.Error -> {
                    MifosAlertDialog(
                        dialogTitle = stringResource(Res.string.search_record_error),
                        dialogText = dialog.message,
                        onDismissRequest = onCloseDialog,
                        onConfirmation = onCloseDialog,
                    )
                }
                null -> { }
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
            .padding(KptTheme.spacing.md),
    ) {
        TextField(
            value = searchQuery,
            onValueChange = onSearchQueryChanged,
            modifier = Modifier
                .fillMaxWidth()
                .focusRequester(focusRequester),
            shape = DesignToken.shapes.extraLargeIncreased,
            colors = TextFieldDefaults.colors(
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
            ),
            leadingIcon = {
                Icon(
                    imageVector = MifosIcons.Search,
                    contentDescription = stringResource(Res.string.search_record_search_icon_desc),
                    tint = KptTheme.colorScheme.onSurfaceVariant,
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
            .padding(horizontal = KptTheme.spacing.md),
    ) {
        Spacer(modifier = Modifier.height(KptTheme.spacing.lg))

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
        ) {
            items(
                items = records,
                key = { record -> "${record.type}-${record.id}" },
            ) { record ->
                SearchRecordItem(
                    record = record,
                    onRecordSelected = onRecordSelected,
                )
                Spacer(Modifier.height(KptTheme.spacing.sm))
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
        record.type.equals(RecordType.ADDRESS.name, ignoreCase = true) -> {
            AddressRecordCard(record, onRecordSelected)
        }
        record.type.equals(RecordType.IDENTIFIER.name, ignoreCase = true) -> {
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
    Surface(
        onClick = { onRecordSelected(record) },
        modifier = Modifier.fillMaxWidth(),
        shape = KptTheme.shapes.medium,
        border = BorderStroke(
            DesignToken.spacing.dp1,
            KptTheme.colorScheme.secondaryContainer,
        ),
        color = Color.Transparent,
    ) {
        MifosAddressCard(
            title = record.name,
            addressList = mapOf(
                stringResource(Res.string.search_record_address_line_1) to (record.metadata[Constants.ADDRESS_LINE_1] ?: ""),
                stringResource(Res.string.search_record_address_line_2) to (record.metadata[Constants.ADDRESS_LINE_2] ?: ""),
                stringResource(Res.string.search_record_address_line_3) to (record.metadata[Constants.ADDRESS_LINE_3] ?: ""),
                stringResource(Res.string.search_record_city) to (record.metadata[Constants.CITY] ?: ""),
                stringResource(Res.string.search_record_province) to (record.metadata[Constants.STATE] ?: ""),
                stringResource(Res.string.search_record_country) to (record.metadata[Constants.COUNTRY] ?: ""),
                stringResource(Res.string.search_record_postal_code) to (record.metadata[Constants.POSTAL_CODE] ?: ""),
            ),
        )
    }
}

@Composable
private fun IdentifierRecordCard(
    record: GenericSearchRecord,
    onRecordSelected: (GenericSearchRecord) -> Unit,
) {
    val rawStatus = record.metadata[Constants.STATUS] ?: ""
    val statusObject = getClientIdentifierStatus(rawStatus)

    MifosActionsIdentifierListingComponent(
        type = record.name,
        id = record.id.toString(),
        key = record.metadata[Constants.DOCUMENT_KEY] ?: "",
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
    Card(
        modifier = Modifier
            .fillMaxWidth(),
        onClick = { onRecordSelected(record) },
    ) {
        Column(
            modifier = Modifier.padding(KptTheme.spacing.md),
        ) {
            Text(
                text = record.name,
                style = KptTheme.typography.titleMedium,
            )
            Spacer(modifier = Modifier.height(KptTheme.spacing.xs))
            Text(
                text = record.description,
                style = KptTheme.typography.bodySmall,
                color = KptTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@Composable
private fun SearchRecordEmptyState() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(KptTheme.spacing.xl),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = stringResource(Res.string.search_record_empty_state),
            style = KptTheme.typography.bodyMedium,
            color = KptTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@Composable
private fun SearchRecordNoResultsState(query: String) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(KptTheme.spacing.xl),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = stringResource(Res.string.search_record_no_results_title),
            style = KptTheme.typography.headlineSmall,
        )
        Spacer(modifier = Modifier.height(KptTheme.spacing.sm))
        Text(
            text = stringResource(Res.string.search_record_no_results_description, query),
            style = KptTheme.typography.bodyMedium,
            color = KptTheme.colorScheme.onSurfaceVariant,
        )
    }
}

private class SearchRecordPreviewProvider : PreviewParameterProvider<SearchRecordState> {
    override val values: Sequence<SearchRecordState>
        get() = sequenceOf(
            SearchRecordState(
                searchQuery = "Kartikey",
                displayTitle = "Client",
                searchRecords = listOf(
                    GenericSearchRecord(
                        id = 1,
                        name = "Home Address",
                        description = "Delhi, India",
                        type = RecordType.ADDRESS.name,
                        metadata = mapOf(
                            Constants.ADDRESS_LINE_1 to "123 Main",
                            Constants.CITY to "Delhi",
                            Constants.COUNTRY to "India",
                        ),
                    ),
                    GenericSearchRecord(
                        id = 2,
                        name = "Passport",
                        description = "Valid Document",
                        type = RecordType.IDENTIFIER.name,
                        metadata = mapOf(
                            Constants.DOCUMENT_KEY to "A12345678",
                            Constants.STATUS to "Active",
                        ),
                    ),
                ),
            ),
            SearchRecordState(
                displayTitle = "Client",
                dialogState = SearchRecordState.DialogState.Loading,
            ),
            SearchRecordState(
                displayTitle = "Client",
                searchQuery = "Unknown",
                isNoResultsFound = true,
            ),
            SearchRecordState(
                displayTitle = "Client",
                dialogState = SearchRecordState.DialogState.Error("Network Timeout"),
            ),
        )
}

@Composable
@Preview
private fun SearchRecordScreenPreview(
    @PreviewParameter(SearchRecordPreviewProvider::class) state: SearchRecordState,
) {
    MaterialTheme {
        SearchRecordScreen(
            state = state,
            onBackClick = {},
            onRecordSelected = {},
        )
    }
}
