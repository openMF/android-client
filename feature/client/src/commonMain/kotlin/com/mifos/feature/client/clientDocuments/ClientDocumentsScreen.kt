/*
 * Copyright 2025 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.feature.client.clientDocuments

import androidclient.feature.client.generated.resources.Res
import androidclient.feature.client.generated.resources.client_identifiers_click_on_plus_button_to_add_an_item
import androidclient.feature.client.generated.resources.client_profile_documents_title
import androidclient.feature.client.generated.resources.client_savings_item
import androidclient.feature.client.generated.resources.confirm_text
import androidclient.feature.client.generated.resources.delete_dialog_title
import androidclient.feature.client.generated.resources.dismiss_text
import androidclient.feature.client.generated.resources.document_delete_dialog_message
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.mifos.core.designsystem.component.MifosScaffold
import com.mifos.core.designsystem.icon.MifosIcons
import com.mifos.core.designsystem.theme.DesignToken
import com.mifos.core.designsystem.theme.MifosTypography
import com.mifos.core.ui.components.Actions
import com.mifos.core.ui.components.MifosActionsClientDocumentListingComponent
import com.mifos.core.ui.components.MifosBreadcrumbNavBar
import com.mifos.core.ui.components.MifosEmptyCard
import com.mifos.core.ui.components.MifosErrorComponent
import com.mifos.core.ui.components.MifosProgressIndicator
import com.mifos.core.ui.components.MifosSearchBar
import com.mifos.core.ui.util.EventsEffect
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

@Composable
internal fun ClientDocumentScreen(
    navController: NavController,
    viewModel: ClientDocumentsViewModel = koinViewModel(),
    onViewDocument: () -> Unit,
    onNavigateToAddDocument: () -> Unit,
    onNavigateBack: () -> Unit,
) {
    val state by viewModel.stateFlow.collectAsStateWithLifecycle()

    EventsEffect(viewModel.eventFlow) { events ->
        when (events) {
            ClientDocumentsEvents.OnNavigateBack -> onNavigateBack()
            ClientDocumentsEvents.OnViewDocument -> onViewDocument()
            ClientDocumentsEvents.OnAddDocument -> onNavigateToAddDocument()
        }
    }

    ClientDocumentsScaffold(
        navController,
        state,
        onAction = remember(viewModel) { { viewModel.trySendAction(it) } },
    )
}

@Composable
private fun ClientDocumentDialog(
    state: ClientDocumentsScreenState,
    onAction: (ClientDocumentsActions) -> Unit,
    modifier: Modifier = Modifier,
) {
    when (state.dialogState) {
        ClientDocumentsScreenState.DialogState.Loading -> {
            MifosProgressIndicator()
        }
        is ClientDocumentsScreenState.DialogState.Error -> {
            MifosErrorComponent(
                modifier = modifier,
                isNetworkConnected = state.isNetworkConnected,
                message = state.dialogState.message,
                onRetry = {
                    onAction(ClientDocumentsActions.Refresh)
                },
            )
        }
        is ClientDocumentsScreenState.DialogState.ConfirmDocumentDeletion -> {
            AlertDialog(
                title = { Text(stringResource(Res.string.delete_dialog_title)) },
                text = {
                    Text(
                        text = stringResource(
                            Res.string.document_delete_dialog_message,
                        ) + " " + state.dialogState.documentName,
                    )
                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            onAction(
                                ClientDocumentsActions.ConfirmDeleteDocument(state.dialogState.documentId),
                            )
                        },
                    ) {
                        Text(stringResource(Res.string.confirm_text))
                    }
                },
                dismissButton = {
                    TextButton(
                        onClick = { onAction(ClientDocumentsActions.CloseDialog) },
                    ) {
                        Text(stringResource(Res.string.dismiss_text))
                    }
                },
                onDismissRequest = { onAction(ClientDocumentsActions.CloseDialog) },
            )
        }
        null -> {}
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ClientDocumentsScaffold(
    navController: NavController,
    state: ClientDocumentsScreenState,
    onAction: (ClientDocumentsActions) -> Unit,
    modifier: Modifier = Modifier,
) {
    MifosScaffold(
        modifier = modifier
            .background(MaterialTheme.colorScheme.onPrimary),
        onBackPressed = {
            onAction(ClientDocumentsActions.NavigateBack)
        },
        title = "",
    ) { paddingValues ->

        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize(),
        ) {
            MifosBreadcrumbNavBar(navController)
            PullToRefreshBox(
                isRefreshing = state.pullDownRefresh,
                onRefresh = {
                    onAction(ClientDocumentsActions.Refresh)
                },
            ) {
                if (state.dialogState != null) {
                    ClientDocumentDialog(
                        state,
                        onAction = onAction,
                    )
                } else {
                    Column(
                        Modifier.fillMaxSize()
                            .padding(
                                horizontal = DesignToken.padding.large,
                            ),
                    ) {
                        ClientDocumentsHeader(
                            totalItem = state.clientDocuments.size.toString(),
                            onToggleSearch = {
                                onAction(ClientDocumentsActions.ToggleSearch)
                            },
                            onAddDocument = {
                                onAction(ClientDocumentsActions.AddDocument)
                            },
                        )

                        if (state.isSearchBarActive) {
                            MifosSearchBar(
                                query = state.searchText,
                                onQueryChange = {
                                    onAction(ClientDocumentsActions.UpdateSearchQuery(it))
                                },
                                onSearchClick = {
                                    onAction(ClientDocumentsActions.SearchDocument)
                                },
                                onBackClick = {
                                    onAction(ClientDocumentsActions.ToggleSearch)
                                },
                            )
                        }

                        Spacer(modifier = Modifier.height(DesignToken.padding.largeIncreasedExtra))

                        if (state.clientDocuments.isEmpty()) {
                            MifosEmptyCard(msg = stringResource(Res.string.client_identifiers_click_on_plus_button_to_add_an_item))
                        } else {
                            var selectedDocumentID by remember {
                                mutableStateOf(-1)
                            }
                            var isAlreadyExpanded by remember {
                                mutableStateOf(false)
                            }
                            LazyColumn {
                                items(state.clientDocuments) { clientDocument ->
                                    MifosActionsClientDocumentListingComponent(
                                        documentName = clientDocument.name ?: "",
                                        documentDescription = clientDocument.description ?: "",
                                        fileName = clientDocument.fileName ?: "",
                                        isExpanded = (selectedDocumentID == clientDocument.id) &&
                                            isAlreadyExpanded,
                                        onClick = {
                                            if (selectedDocumentID == clientDocument.id) {
                                                isAlreadyExpanded = false
                                                selectedDocumentID = -1
                                            } else {
                                                selectedDocumentID = clientDocument.id
                                                isAlreadyExpanded = true
                                            }
                                        },
                                        menuList = listOf(
                                            Actions.ViewDocument(),
                                            Actions.DeleteDocument(),
                                        ),
                                    ) { actions ->
                                        when (actions) {
                                            is Actions.DeleteDocument -> {
                                                onAction(
                                                    ClientDocumentsActions.DeleteDocument(
                                                        documentName = clientDocument.fileName ?: "",
                                                        documentId = clientDocument.id,
                                                    ),
                                                )
                                            }
                                            is Actions.ViewDocument -> {
                                                onAction(
                                                    ClientDocumentsActions.ViewDocument(
                                                        documentId = clientDocument.id,
                                                    ),
                                                )
                                            }
                                            else -> null
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(DesignToken.spacing.small))
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ClientDocumentsHeader(
    totalItem: String,
    onAddDocument: () -> Unit,
    modifier: Modifier = Modifier,
    onToggleSearch: () -> Unit,
) {
    Row(
        modifier = modifier.fillMaxWidth()
            .wrapContentHeight(),
    ) {
        Column {
            Text(
                text = stringResource(Res.string.client_profile_documents_title),
                style = MifosTypography.titleMedium,
            )

            Text(
                text = totalItem + " " + stringResource(Res.string.client_savings_item),
                style = MifosTypography.labelMedium,
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        Icon(
            imageVector = MifosIcons.Search,
            contentDescription = null,
            modifier = Modifier.clickable {
                onToggleSearch.invoke()
            },
        )

        Spacer(modifier = Modifier.width(DesignToken.spacing.largeIncreased))

        Icon(
            imageVector = MifosIcons.Add,
            contentDescription = null,
            modifier = Modifier.clickable {
                onAddDocument.invoke()
            },
        )
    }
}
