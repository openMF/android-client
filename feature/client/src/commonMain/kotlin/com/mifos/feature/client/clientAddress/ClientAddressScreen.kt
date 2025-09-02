package com.mifos.feature.client.clientAddress

import androidclient.feature.client.generated.resources.Res
import androidclient.feature.client.generated.resources.client_collateral_failure_title
import androidclient.feature.client.generated.resources.client_collateral_success_message
import androidclient.feature.client.generated.resources.client_collateral_success_title
import androidclient.feature.client.generated.resources.client_savings_item
import androidclient.feature.client.generated.resources.dialog_continue
import androidclient.feature.client.generated.resources.feature_client_address
import androidclient.feature.client.generated.resources.search
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mifos.core.designsystem.component.MifosCircularProgress
import com.mifos.core.designsystem.component.MifosScaffold
import com.mifos.core.designsystem.icon.MifosIcons
import com.mifos.core.designsystem.theme.AppColors
import com.mifos.core.designsystem.theme.DesignToken
import com.mifos.core.designsystem.theme.MifosTypography
import com.mifos.core.ui.components.MifosStatusDialog
import com.mifos.core.ui.util.EventsEffect
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

@Composable
internal fun ClientAddressScreen(
    onNavigateBack: () -> Unit,
    onNavigateNext: (Int) -> Unit,
    navigateToAddAddressForm: (Int) -> Unit,
    viewModel: ClientAddressViewModel = koinViewModel()
) {
    val state by viewModel.stateFlow.collectAsStateWithLifecycle()

    EventsEffect(viewModel.eventFlow) { event ->
        when (event) {
            ClientAddressEvent.NavigateBack -> onNavigateBack.invoke()
            ClientAddressEvent.NavigateNext -> onNavigateNext(state.id)
            ClientAddressEvent.ShowAddressForm -> navigateToAddAddressForm(state.id)
        }
    }

    ClientAddressScaffold(
        state = state,
        onAction = { viewModel.trySendAction(it) }
    )
}

@Composable
fun ClientAddressDialogs(
    state: ClientAddressState,
    onAction: (ClientAddressAction) -> Unit
) {
    when(state.dialogState) {
        is ClientAddressState.DialogState.Loading -> {
            MifosCircularProgress()
        }
//        is ClientAddressState.DialogState.ShowStatusDialog -> {
//            MifosStatusDialog(
//                status = state.dialogState.status,
//                btnText = stringResource(Res.string.dialog_continue),
//                onConfirm = { onAction(ClientAddressAction.OnNext) },
//                successTitle = stringResource(Res.string.client_collateral_success_title),
//                successMessage = stringResource(Res.string.client_collateral_success_message),
//                failureTitle = stringResource(Res.string.client_collateral_failure_title),
//                failureMessage = state.dialogState.msg,
//                modifier = Modifier.fillMaxSize(),
//            )
//        }
        is ClientAddressState.DialogState.Error -> {

        }

        else -> Unit
    }
}

@Composable
private fun ClientAddressScaffold(
    state: ClientAddressState,
    onAction: (ClientAddressAction) -> Unit
) {
    val scrollState = rememberScrollState()
    val keyboardController = LocalSoftwareKeyboardController.current
    MifosScaffold(
        title = "Client Address",
        onBackPressed = { onAction(ClientAddressAction.NavigateBack) },
    ) { paddingValues ->
        Column(
            modifier = Modifier.padding(paddingValues)
                .fillMaxSize()
                .padding(
                    start = DesignToken.padding.large,
                    end = DesignToken.padding.large,
                    top = DesignToken.padding.large,
                )
                .pointerInput(Unit) {
                    detectTapGestures(
                        onTap = {
                            keyboardController?.hide()
                        },
                    )
                }
                .verticalScroll(state = scrollState),
        ) {
            if(state.dialogState == null) {
                ClientAddressHeader(
                    totalItem = "7",
                    onAction = onAction
                )
                Spacer(modifier = Modifier.height(DesignToken.padding.large))
                if(state.address.isEmpty()) {
                    EmptyAddressCard()
                } else {
                    //Lazy Columns of Address Items
                }
            } else {
                ClientAddressDialogs(
                    state = state,
                    onAction = onAction
                )
            }
        }
    }
}


@Composable
fun ClientAddressHeader(
    totalItem: String,
    onAction: (ClientAddressAction) -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
    ) {
        Column {
            Text(
                text = stringResource(Res.string.feature_client_address),
                style = MifosTypography.titleMedium,
            )

            Text(
                text = totalItem + " " + stringResource(Res.string.client_savings_item),
                style = MifosTypography.labelMedium,
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        IconButton(
            onClick = { },
        ) {
            // add a cross icon when its active, talk with design team
            Icon(
                painter = painterResource(Res.drawable.search),
                contentDescription = null,
            )
        }

        IconButton(
            onClick = { onAction(ClientAddressAction.ShowAddressForm) },
        ) {
            Icon(
                imageVector = MifosIcons.Add,
                contentDescription = null,
            )
        }
    }
}

@Composable
private fun EmptyAddressCard() {
    OutlinedCard(
        modifier = Modifier.fillMaxWidth(),
        border = BorderStroke(
            width = 1.dp,
            color = AppColors.cardBorders,
        ),
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
        ) {
            Text(
                text = "No Item Found",
                style = MifosTypography.titleSmallEmphasized,
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Click on '+' Button to add an item. ",
                style = MifosTypography.bodySmall,
            )
        }
    }
}

