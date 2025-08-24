package com.mifos.feature.client.clientStaff

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mifos.core.designsystem.component.MifosButton
import com.mifos.core.designsystem.component.MifosOutlinedButton
import com.mifos.core.designsystem.component.MifosScaffold
import com.mifos.core.designsystem.component.MifosTextButton
import com.mifos.core.designsystem.component.MifosTextFieldDropdown
import com.mifos.core.designsystem.icon.MifosIcons
import com.mifos.core.designsystem.theme.DesignToken
import com.mifos.core.designsystem.theme.MifosTypography
import com.mifos.core.ui.components.MifosErrorComponent
import com.mifos.core.ui.components.MifosProgressIndicator
import com.mifos.core.ui.components.MifosStatusDialog
import com.mifos.core.ui.components.ResultStatus
import com.mifos.core.ui.util.EventsEffect
import org.koin.compose.viewmodel.koinViewModel

@Composable
internal fun ClientStaffScreen(
    onNavigateBack: () -> Unit,
    onNavigateNext: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ClientStaffViewModel = koinViewModel(),
) {
    val state by viewModel.stateFlow.collectAsStateWithLifecycle()

    EventsEffect(viewModel.eventFlow) { event ->
        when (event) {
            ClientStaffEvent.NavigateBack -> onNavigateBack()
            ClientStaffEvent.NavigateNext -> onNavigateNext()
        }
    }

    ClientStaffScaffold(
        state = state,
        onAction = remember(viewModel) { { viewModel.trySendAction(it) } },
        modifier = modifier,
    )
}

@Composable
private fun ClientStaffScaffold(
    state: ClientStaffState,
    onAction: (ClientStaffAction) -> Unit,
    modifier: Modifier = Modifier,
) {
    MifosScaffold(
        title = "Client Staff",
        onBackPressed = { onAction(ClientStaffAction.NavigateBack) },
        modifier = modifier,
    ) { paddingValues ->
        ClientStaffDialogs(
            state = state,
            onAction = onAction
        )
        if (state.dialogState == null) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(
                         DesignToken.padding.large,
                    ),
            ) {
                if(state.staffOptions.isNotEmpty()){
                    Text(
                        text="Assign Staff",
                        style = MifosTypography.labelLargeEmphasized,
                    )
                    Spacer(Modifier.height(DesignToken.padding.largeIncreased))
                    MifosTextFieldDropdown(
                        value = state.staffOptions[state.currentSelectedIndex].displayName,
                        onValueChanged = {},
                        onOptionSelected = { index,value->
                            onAction(ClientStaffAction.OptionChanged(index))
                        } ,
                        options = state.staffOptions.map { it.displayName },
                        label = "Choose the service staff",
                        modifier = Modifier.fillMaxWidth(),
                    )
                    Spacer(Modifier.height(DesignToken.padding.largeIncreased))
                    Row(
                        modifier= Modifier.fillMaxWidth()
                    ) {
                        MifosOutlinedButton(
                            onClick = {
                                onAction(ClientStaffAction.NavigateBack)
                            },
                            leadingIcon = {
                                Icon(
                                    imageVector = MifosIcons.ChevronLeft,
                                    contentDescription = null,
                                    modifier = Modifier.size(DesignToken.sizes.iconAverage),
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            },
                            text = {
                                Text(
                                    text="Back",
                                    color = MaterialTheme.colorScheme.primary,
                                    style = MifosTypography.labelLarge
                                )
                            },
                            modifier=Modifier.weight(1f)
                        )
                        Spacer(Modifier.padding(DesignToken.padding.small))
                        MifosTextButton(
                            onClick = {
                                onAction(ClientStaffAction.OnSubmit)
                            },
                            leadingIcon = {
                                Icon(
                                    imageVector = MifosIcons.Check,
                                    contentDescription = null,
                                    modifier = Modifier.size(DesignToken.sizes.iconAverage),
                                )
                            },
                            text = {
                                Text(
                                    text="Submit",
                                    style = MifosTypography.labelLarge
                                )
                            },
                            modifier=Modifier.weight(1f)
                        )
                    }
                }
                else{
                    Text("Can't Assign a Staff to this client")
                }

            }
        }
    }
}

@Composable
private fun ClientStaffDialogs(
    state: ClientStaffState,
    onAction: (ClientStaffAction) -> Unit
) {
    when (state.dialogState) {
        is ClientStaffState.DialogState.Loading -> MifosProgressIndicator()

        is ClientStaffState.DialogState.Error -> {
            MifosErrorComponent(
                isNetworkConnected = state.networkConnection,
                message = state.dialogState.message,
                isRetryEnabled = true,
                onRetry = {
                    onAction(ClientStaffAction.OnRetry)
                },
            )
        }

        null -> Unit
        is ClientStaffState.DialogState.ShowStatusDialog -> {
            MifosStatusDialog(
                status = state.dialogState.status,
                btnText = "Continue",
                onConfirm = {
                    onAction(ClientStaffAction.OnNext)
                },
                successTitle = "Staff Assigned Successfully!!",
            successMessage= "Your request was completed successfully.",
            failureTitle= "Staff Assigning Failed!",
            failureMessage = state.dialogState.msg
            )
        }
    }
}
