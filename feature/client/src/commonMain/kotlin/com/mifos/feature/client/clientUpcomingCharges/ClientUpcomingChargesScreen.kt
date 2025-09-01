package com.mifos.feature.client.clientUpcomingCharges

import androidclient.feature.client.generated.resources.Res
import androidclient.feature.client.generated.resources.feature_client_error_not_connected_internet
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.paging.PagingData
import co.touchlab.kermit.Logger
import com.mifos.core.designsystem.component.MifosCircularProgress
import com.mifos.core.designsystem.component.MifosScaffold
import com.mifos.core.designsystem.component.MifosSweetError
import com.mifos.core.designsystem.icon.MifosIcons
import com.mifos.core.designsystem.theme.DesignToken
import com.mifos.core.ui.components.MifosAlertDialog
import com.mifos.core.ui.components.MifosEmptyCard
import com.mifos.core.ui.components.MifosIcon
import com.mifos.core.ui.util.EventsEffect
import com.mifos.feature.client.clientUpcomingCharges.ClientUpcomingChargesAction
import com.mifos.room.entities.client.ChargesEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.count
import kotlinx.coroutines.flow.first
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun ClientUpcomingChargesScreenRoute(
    payOutstandingAmount: () -> Unit,
    viewModel: ClientUpcomingChargesViewmodel = koinViewModel(),
) {
    val state by viewModel.stateFlow.collectAsStateWithLifecycle()

    EventsEffect(viewModel.eventFlow) { event ->
        when (event) {
            ClientUpcomingChargesEvent.PayOutstandingAmound -> payOutstandingAmount()
        }
    }

    ClientUpcomingChargesScreen(
        state = state,
        onAction = remember(viewModel) { { viewModel.trySendAction(it) } },
    )

    ClientUpcomingChargesDialog(
        state = state,
        onAction = remember(viewModel) { { viewModel.trySendAction(it) } },
    )
}

@Composable
fun ClientUpcomingChargesScreen(
    state: ClientUpcomingChargesState,
    onAction: (ClientUpcomingChargesAction) -> Unit,
) {
    MifosScaffold(
        title = "Client Upcoming Charges",
        onBackPressed = {},
    ) { padding ->
        Column(
            modifier = Modifier.fillMaxSize()
                .padding(padding)
                .padding(DesignToken.padding.large),
        ) {
            if (state.chargesFlow == null) {
                MifosEmptyCard()
            } else {
                ChargesListContent(
                    state = state,
                    charges = state.chargesFlow,
                    onAction = onAction,
                    refresh = {
                        onAction(ClientUpcomingChargesAction.OnRefresh)
                    }
                )
            }
        }
    }
}

@Composable
private fun ClientUpcomingChargesDialog(
    state: ClientUpcomingChargesState,
    onAction: (ClientUpcomingChargesAction) -> Unit
) {
    when (state.dialogState) {
        is ClientUpcomingChargesState.DialogState.Error -> {
            MifosSweetError(
                message = state.dialogState.message,
                onclick = { ClientUpcomingChargesAction.OnRefresh }
            )
        }

        ClientUpcomingChargesState.DialogState.Loading -> MifosCircularProgress()

        null -> {}

    }
}

@Composable
expect fun ChargesListContent(
    charges: Flow<PagingData<ChargesEntity>>,
    state: ClientUpcomingChargesState,
    onAction: (ClientUpcomingChargesAction) -> Unit,
    refresh : () -> Unit
)
