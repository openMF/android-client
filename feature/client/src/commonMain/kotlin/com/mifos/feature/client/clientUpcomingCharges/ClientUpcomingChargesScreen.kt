package com.mifos.feature.client.clientUpcomingCharges

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.paging.PagingData
import com.mifos.core.designsystem.component.MifosScaffold
import com.mifos.core.designsystem.theme.DesignToken
import com.mifos.core.ui.components.MifosEmptyCard
import com.mifos.core.ui.util.EventsEffect
import com.mifos.room.entities.client.ChargesEntity
import kotlinx.coroutines.flow.Flow
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
            if (state.chargesFlow.isEmpty()) {
                MifosEmptyCard("")
            } else {
                ChargesListContent(
                    
                )
            }
        }
    }
}

@Composable
expect fun ChargesListContent(
    state: ClientUpcomingChargesState,
    onAction: (ClientUpcomingChargesAction) -> Unit,,
    refresh : () -> Unit
)
