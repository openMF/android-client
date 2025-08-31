package com.mifos.feature.client.clientIdentitiesList

import androidclient.feature.client.generated.resources.Res
import androidclient.feature.client.generated.resources.add_icon
import androidclient.feature.client.generated.resources.client_identifiers_click_on_plus_button_to_add_an_item
import androidclient.feature.client.generated.resources.client_identifiers_error_text
import androidclient.feature.client.generated.resources.client_identifiers_identities_client_identifier_deletion_success
import androidclient.feature.client.generated.resources.client_identifiers_identities_success_text
import androidclient.feature.client.generated.resources.client_identifiers_not_available
import androidclient.feature.client.generated.resources.client_savings_item
import androidclient.feature.client.generated.resources.feature_client_error_not_connected_internet
import androidclient.feature.client.generated.resources.feature_client_identifiers
import androidclient.feature.client.generated.resources.search
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mifos.core.designsystem.component.LoadingDialogState
import com.mifos.core.designsystem.component.MifosCircularProgress
import com.mifos.core.designsystem.component.MifosLoadingDialog
import com.mifos.core.designsystem.component.MifosScaffold
import com.mifos.core.designsystem.theme.DesignToken
import com.mifos.core.designsystem.theme.MifosTypography
import com.mifos.core.ui.components.Actions
import com.mifos.core.ui.components.MifosActionsIdentifierListingComponent
import com.mifos.core.ui.components.MifosAlertDialog
import com.mifos.core.ui.components.MifosEmptyCard
import com.mifos.core.ui.components.Status
import com.mifos.core.ui.util.EventsEffect
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

@Composable
internal fun ClientIdentitiesListScreenRoute(
    addNewClientIdentity: (Int) -> Unit,
    viewModel: ClientIdentitiesListViewModel = koinViewModel(),
) {
    val state by viewModel.stateFlow.collectAsStateWithLifecycle()

    EventsEffect(viewModel.eventFlow) { event ->
        when (event) {
            is ClientIdentitiesListEvent.AddNewClientIdentity -> addNewClientIdentity(event.id)
            ClientIdentitiesListEvent.ViewDocument -> { }
        }
    }

    ClientIdentitiesListScreen(
        state = state,
        onAction = remember(viewModel) { { viewModel.trySendAction(it) } },
    )

    ClientIdentitiesDialog(
        state = state,
        onAction = remember(viewModel) { { viewModel.trySendAction(it) } },
    )
}

@Composable
internal fun ClientIdentitiesListScreen(
    state: ClientIdentitiesListState,
    onAction: (ClientIdentitiesListAction) -> Unit,
) {
    val emptyMessage = stringResource(Res.string.client_identifiers_not_available)

    MifosScaffold(
        onBackPressed = {},
        title = "Client Identities",
    ) { paddingValues ->
        Column(
            modifier = Modifier.fillMaxSize()
                .padding(paddingValues)
                .padding(DesignToken.padding.large),
        ) {
            ClientIdentifiersHeader(
                totalItem = state.clientIdentitiesList.size.toString(),
                onAction = onAction,
            )

            if (state.clientIdentitiesList.isEmpty()) {
                MifosEmptyCard(stringResource(Res.string.client_identifiers_click_on_plus_button_to_add_an_item))
            } else {
                LazyColumn {
                    item {
                        state.clientIdentitiesList.forEachIndexed { index, item ->
                            MifosActionsIdentifierListingComponent(
                                type = item.documentType?.name ?: emptyMessage,
                                id = if (item.id != null) item.id.toString() else emptyMessage,
                                key = item.documentKey ?: emptyMessage,
                                status = if (item.status != null) {
                                    if (item.status!!.lowercase().endsWith("active")) Status.Active
                                    if (item.status!!.lowercase()
                                            .endsWith("inactive")
                                    ) Status.InActive
                                    else Status.Pending
                                } else null,
                                description = item.description ?: emptyMessage,
                                // TODO check what is identifyDocuments, couldnot find in the api
                                identifyDocuments = item.documentType?.name ?: emptyMessage,
                                menuList = listOf(
                                    Actions.ViewDocument,
                                    Actions.DeleteDocument,
                                    Actions.UploadAgain,
                                ),
                                onActionClicked = { actions ->
                                    when (actions) {
                                        Actions.ViewDocument -> onAction.invoke(
                                            ClientIdentitiesListAction.ViewDocument,
                                        )

                                        Actions.UploadAgain -> onAction.invoke(
                                            ClientIdentitiesListAction.UploadAgain,
                                        )

                                        Actions.DeleteDocument -> onAction.invoke(
                                            ClientIdentitiesListAction.DeleteDocument(item.id ?: -1),
                                        )

                                        else -> {}
                                    }
                                },
                                onClick = {
                                    onAction.invoke(
                                        ClientIdentitiesListAction.ToggleShowMenu(
                                            index,
                                        ),
                                    )
                                },
                                isExpanded = (index == state.currentExpandedItem) && state.expandClientIdentity,
                            )
                        }
                    }
                }
            }
        }
    }

}

@Composable
private fun ClientIdentifiersHeader(
    totalItem: String,
    onAction: (ClientIdentitiesListAction) -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
    ) {
        Column {
            Text(
                text = stringResource(Res.string.feature_client_identifiers),
                style = MifosTypography.titleMedium,
            )

            Text(
                text = totalItem + " " + stringResource(Res.string.client_savings_item),
                style = MifosTypography.labelMedium,
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        IconButton(
            onClick = { onAction.invoke(ClientIdentitiesListAction.ToggleSearch) },
        ) {
            Icon(
                painter = painterResource(Res.drawable.search),
                contentDescription = null,
            )
        }

        Spacer(modifier = Modifier.height(DesignToken.padding.largeIncreased))

        IconButton(
            onClick = { onAction.invoke(ClientIdentitiesListAction.AddNewClientIdentity) },
        ) {
            Icon(
                painter = painterResource(Res.drawable.add_icon),
                contentDescription = null,
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ClientIdentitiesDialog(
    state: ClientIdentitiesListState,
    onAction: (ClientIdentitiesListAction) -> Unit,
) {
    when (state.dialogState) {
        is ClientIdentitiesListState.DialogState.Error -> {
            MifosAlertDialog(
                dialogTitle = stringResource(Res.string.client_identifiers_error_text),
                dialogText = state.dialogState.message,
                onDismissRequest = { onAction.invoke(ClientIdentitiesListAction.CloseDialog) },
                onConfirmation = { onAction.invoke(ClientIdentitiesListAction.CloseDialog) },
            )
        }

        ClientIdentitiesListState.DialogState.Loading -> MifosLoadingDialog(LoadingDialogState.Shown)

        is ClientIdentitiesListState.DialogState.DeletedSuccessfully -> {
            MifosAlertDialog(
                dialogTitle = stringResource(Res.string.client_identifiers_identities_success_text),
                dialogText = stringResource(Res.string.client_identifiers_identities_client_identifier_deletion_success)
                        + " " + state.dialogState.id,
                onDismissRequest = { onAction.invoke(ClientIdentitiesListAction.CloseDialog) },
                onConfirmation = { onAction.invoke(ClientIdentitiesListAction.CloseDialog) },
            )
        }

        null -> {}

        ClientIdentitiesListState.DialogState.NoInternet -> {
            MifosAlertDialog(
                dialogTitle = stringResource(Res.string.client_identifiers_error_text),
                dialogText = stringResource(Res.string.feature_client_error_not_connected_internet),
                onDismissRequest = { onAction.invoke(ClientIdentitiesListAction.CloseDialog) },
                onConfirmation = { onAction.invoke(ClientIdentitiesListAction.Refresh) },
                confirmationText = "Retry"
            )
        }
    }
}
