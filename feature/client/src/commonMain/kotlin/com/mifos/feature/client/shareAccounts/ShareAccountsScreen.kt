package com.mifos.feature.client.shareAccounts

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.PagingData
import com.mifos.core.designsystem.component.MifosScaffold
import com.mifos.core.designsystem.theme.DesignToken
import com.mifos.core.model.objects.account.share.ShareAccounts
import com.mifos.core.ui.components.MifosEmptyCard
import com.mifos.core.ui.util.EventsEffect
import kotlinx.coroutines.flow.Flow
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun ShareAccountsScreenRoute(
    viewAccount : (Int) -> Unit,
    viewModel: ShareAccountsViewModel = koinViewModel(),
) {
    val state by viewModel.stateFlow.collectAsStateWithLifecycle()

    EventsEffect(viewModel.eventFlow) { event ->
        when (event) {
            is ShareAccountsEvent.ViewAccount -> viewAccount(event.accountId)
        }
    }

    ShareAccountsScreen(
        state = state,
        onAction = remember(viewModel) { { viewModel.trySendAction(it) } },
    )
}

@Composable
fun ShareAccountsScreen(
    state: ShareAccountsUiState,
    onAction: (ShareAccountsAction) -> Unit,
) {
    MifosScaffold(
        topBar = { Text("Share Accounts") },
    ) { paddingValues ->
        Column(
            modifier = Modifier.padding(paddingValues)
                .fillMaxSize()
                .padding(DesignToken.padding.large),
        ) {
            if (state.accounts.isNotEmpty()) {
                Text("Not empty")
            } else {
                MifosEmptyCard()
            }
        }
    }
}

@Composable
internal expect fun ShareAccountsScreenContent(
    pagingFlow : Flow<PagingData<ShareAccounts>>,
    onAction : (ShareAccountsAction) -> Unit,
    modifier : Modifier = Modifier,
)



