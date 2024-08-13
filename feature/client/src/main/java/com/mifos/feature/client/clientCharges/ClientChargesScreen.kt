@file:OptIn(ExperimentalMaterialApi::class)

package com.mifos.feature.client.clientCharges

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.LoadState
import androidx.paging.PagingData
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.mifos.core.designsystem.component.MifosCircularProgress
import com.mifos.core.designsystem.component.MifosPagingAppendProgress
import com.mifos.core.designsystem.component.MifosScaffold
import com.mifos.core.designsystem.component.MifosSweetError
import com.mifos.core.designsystem.icon.MifosIcons
import com.mifos.core.designsystem.theme.Black
import com.mifos.core.designsystem.theme.BlueSecondary
import com.mifos.core.designsystem.theme.DarkGray
import com.mifos.core.objects.client.Charges
import com.mifos.feature.client.R
import com.mifos.feature.client.client_charge_dialog.ChargeDialogScreen
import kotlinx.coroutines.flow.flowOf

@Composable
fun ClientChargesScreen(
    onBackPressed: () -> Unit
) {

    val viewModel: ClientChargesViewModel = hiltViewModel()
    val clientId by viewModel.clientId.collectAsStateWithLifecycle()
    val clientChargeUiState by viewModel.clientChargesUiState.collectAsStateWithLifecycle()
    val refreshState by viewModel.isRefreshing.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.loadCharges(clientId)
    }

    ClientChargesScreen(
        clientId = clientId,
        state = clientChargeUiState,
        onBackPressed = onBackPressed,
        onRetry = { viewModel.loadCharges(clientId) },
        onRefresh = { viewModel.refreshCenterList(clientId) },
        refreshState = refreshState,
        onChargeCreated = {
            viewModel.loadCharges(clientId)
        }
    )

}

@Composable
fun ClientChargesScreen(
    clientId: Int,
    state: ClientChargeUiState,
    onBackPressed: () -> Unit,
    onRetry: () -> Unit,
    onRefresh: () -> Unit,
    refreshState: Boolean,
    onChargeCreated: () -> Unit
) {

    val snackbarHostState = remember { SnackbarHostState() }
    val pullRefreshState = rememberPullRefreshState(
        refreshing = refreshState,
        onRefresh = onRefresh
    )
    var showClientChargeDialog by rememberSaveable { mutableStateOf(false) }

    if (showClientChargeDialog) {
        ChargeDialogScreen(
            clientId = clientId,
            onDismiss = {
                showClientChargeDialog = false
            },
            onCreated = {
                onChargeCreated()
                showClientChargeDialog = false
            }
        )
    }

    MifosScaffold(
        icon = MifosIcons.arrowBack,
        title = stringResource(id = R.string.feature_client_charges),
        onBackPressed = onBackPressed,
        actions = {
            IconButton(onClick = { showClientChargeDialog = true }) {
                Icon(imageVector = MifosIcons.Add, contentDescription = null)
            }
        },
        snackbarHostState = snackbarHostState
    ) { paddingValues ->
        Column(modifier = Modifier.padding(paddingValues)) {
            Box(modifier = Modifier.pullRefresh(pullRefreshState)) {
                when (state) {
                    is ClientChargeUiState.ChargesList -> ClientChargeContent(
                        chargesPage = state.chargesPage.collectAsLazyPagingItems(),
                        onRetry = onRetry
                    )

                    is ClientChargeUiState.Error -> MifosSweetError(message = stringResource(id = state.message)) {
                        onRetry()
                    }

                    is ClientChargeUiState.Loading -> MifosCircularProgress()
                }
                PullRefreshIndicator(
                    refreshing = refreshState,
                    state = pullRefreshState,
                    modifier = Modifier.align(Alignment.TopCenter)
                )
            }
        }
    }
}

@Composable
fun ClientChargeContent(
    chargesPage: LazyPagingItems<Charges>,
    onRetry: () -> Unit
) {

    when (chargesPage.loadState.refresh) {
        is LoadState.Error -> {
            MifosSweetError(message = stringResource(id = R.string.feature_client_failed_to_load_client_charges)) {
                onRetry()
            }
        }

        is LoadState.Loading -> MifosCircularProgress()

        is LoadState.NotLoading -> Unit
    }


    LazyColumn {
        items(chargesPage.itemCount) { index ->
            chargesPage[index]?.let { ChargesItems(it) }
        }

        when (chargesPage.loadState.append) {
            is LoadState.Error -> {

            }

            is LoadState.Loading -> {
                item {
                    MifosPagingAppendProgress()
                }
            }

            is LoadState.NotLoading -> Unit
        }
        when (chargesPage.loadState.append.endOfPaginationReached) {
            true -> {
                item {
                    Text(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(6.dp),
                        text = stringResource(id = R.string.feature_client_no_more_charges_available),
                        style = TextStyle(
                            fontSize = 14.sp
                        ),
                        color = DarkGray,
                        textAlign = TextAlign.Center
                    )
                }
            }

            false -> Unit
        }
    }
}


@Composable
fun ChargesItems(charges: Charges) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        shape = RoundedCornerShape(0.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(
            containerColor = BlueSecondary
        )
    ) {
        Spacer(modifier = Modifier.height(8.dp))
        MifosCenterDetailsText(
            stringResource(id = R.string.feature_client_client_id),
            charges.chargeId.toString()
        )
        MifosCenterDetailsText(
            stringResource(id = R.string.feature_client_charge_name),
            charges.name ?: ""
        )
        MifosCenterDetailsText(
            stringResource(id = R.string.feature_client_charge_amount),
            charges.amount.toString()
        )
        MifosCenterDetailsText(
            stringResource(id = R.string.feature_client_due_date),
            charges.formattedDueDate
        )
        Spacer(modifier = Modifier.height(8.dp))
    }
}

@Composable
fun MifosCenterDetailsText(field: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            modifier = Modifier
                .weight(1f)
                .padding(start = 16.dp),
            text = field,
            style = TextStyle(
                fontSize = 16.sp,
                fontWeight = FontWeight.Normal,
                fontStyle = FontStyle.Normal
            ),
            color = Black,
            textAlign = TextAlign.Start
        )
        Text(
            modifier = Modifier.weight(1f),
            text = value,
            style = TextStyle(
                fontSize = 16.sp,
                fontWeight = FontWeight.Normal,
                fontStyle = FontStyle.Normal
            ),
            color = DarkGray,
            textAlign = TextAlign.Start
        )
    }
}

class ClientChargesScreenUiStateProvider : PreviewParameterProvider<ClientChargeUiState> {

    override val values: Sequence<ClientChargeUiState>
        get() = sequenceOf(
            ClientChargeUiState.Loading,
            ClientChargeUiState.Error(R.string.feature_client_failed_to_load_client_charges),
            ClientChargeUiState.ChargesList(flowOf(PagingData.from(sampleClientCharge)))
        )

}

@Preview(showBackground = true)
@Composable
private fun ClientChargesScreenPreview(
    @PreviewParameter(ClientChargesScreenUiStateProvider::class) state: ClientChargeUiState
) {
    ClientChargesScreen(
        clientId = 1,
        state = state,
        onBackPressed = {},
        onRetry = {},
        onRefresh = {},
        refreshState = false,
        onChargeCreated = {}
    )
}

val sampleClientCharge = List(10) {
    Charges(name = "charge $it", amount = it.toDouble())
}