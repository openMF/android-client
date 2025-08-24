package com.mifos.feature.client.clientTransfer

import androidclient.feature.client.generated.resources.Res
import androidclient.feature.client.generated.resources.dialog_continue
import androidx.compose.runtime.Composable
import com.mifos.core.ui.components.MifosErrorComponent
import com.mifos.core.ui.components.MifosProgressIndicator
import com.mifos.core.ui.components.MifosStatusDialog
import org.jetbrains.compose.resources.stringResource

@Composable
internal fun ClientTransferScreen(
    onNavigateBack: () -> Unit,
    onNavigateNext: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ClientTransferViewModel = koinViewModel(),
) {
    val state by viewModel.stateFlow.collectAsStateWithLifecycle()

    EventsEffect(viewModel.eventFlow) { event ->
        when (event) {
            ClientTransferEvent.NavigateBack -> onNavigateBack()
            ClientTransferEvent.NavigateNext -> onNavigateNext()
        }
    }

    ClientTransferScaffold(
        state = state,
        onAction = remember(viewModel) { { viewModel.trySendAction(it) } },
        modifier = modifier,
    )
}

@Composable
private fun ClientTransferScaffold(
    state: ClientTransferState,
    onAction: (ClientTransferAction) -> Unit,
    modifier: Modifier = Modifier,
) {
    MifosScaffold(
        title = stringResource(Res.string.title_client_transfer),
        onBackPressed = { onAction(ClientTransferAction.NavigateBack) },
        modifier = modifier,
    ) { paddingValues ->
        ClientTransferDialogs(state, onAction)

        if (state.dialogState == null) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(DesignToken.padding.large),
            ) {
                if (state.transferOptions.isNotEmpty()) {
                    Text(
                        text = stringResource(Res.string.label_transfer_to_office),
                        style = MifosTypography.labelLargeEmphasized,
                    )
                    Spacer(Modifier.height(DesignToken.padding.largeIncreased))

                    MifosTextFieldDropdown(
                        value = state.transferOptions[state.currentSelectedIndex].displayName,
                        onValueChanged = {},
                        onOptionSelected = { index, value ->
                            onAction(ClientTransferAction.OptionChanged(index))
                        },
                        options = state.transferOptions.map { it.displayName },
                        label = stringResource(Res.string.label_choose_office),
                        modifier = Modifier.fillMaxWidth(),
                    )

                    Spacer(Modifier.height(DesignToken.padding.largeIncreased))

                    MifosTextField(
                        value = state.note,
                        onValueChange = { onAction(ClientTransferAction.NoteChanged(it)) },
                        label = stringResource(Res.string.label_note),
                        modifier = Modifier.fillMaxWidth(),
                    )

                    Spacer(Modifier.height(DesignToken.padding.largeIncreased))

                    Row(modifier = Modifier.fillMaxWidth()) {
                        MifosOutlinedButton(
                            onClick = { onAction(ClientTransferAction.NavigateBack) },
                            text = { Text(stringResource(Res.string.btn_back)) },
                            modifier = Modifier.weight(1f),
                        )
                        Spacer(Modifier.padding(DesignToken.padding.small))
                        MifosTextButton(
                            onClick = { onAction(ClientTransferAction.OnSubmit) },
                            text = { Text(stringResource(Res.string.btn_submit)) },
                            modifier = Modifier.weight(1f),
                        )
                    }
                } else {
                    Text(stringResource(Res.string.msg_cannot_transfer_client))
                }
            }
        }
    }
}

@Composable
private fun ClientTransferDialogs(
    state: ClientTransferState,
    onAction: (ClientTransferAction) -> Unit,
) {
    when (state.dialogState) {
        is ClientTransferState.DialogState.Loading -> MifosProgressIndicator()
        is ClientTransferState.DialogState.Error -> {
            MifosErrorComponent(
                isNetworkConnected = state.networkConnection,
                message = state.dialogState.message,
                isRetryEnabled = true,
                onRetry = { onAction(ClientTransferAction.OnRetry) },
            )
        }
        is ClientTransferState.DialogState.ShowStatusDialog -> {
            MifosStatusDialog(
                status = state.dialogState.status,
                btnText = stringResource(Res.string.dialog_continue),
                onConfirm = { onAction(ClientTransferAction.OnNext) },
                successTitle = stringResource(Res.string.client_transfer_success_title),
                successMessage = stringResource(Res.string.client_transfer_success_message),
                failureTitle = stringResource(Res.string.client_transfer_failure_title),
                failureMessage = state.dialogState.msg,
            )
        }
        null -> Unit
    }
}
