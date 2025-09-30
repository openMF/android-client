/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
@file:OptIn(ExperimentalMaterial3Api::class)

package com.mifos.feature.client.clientCharges

import androidclient.feature.client.generated.resources.Res
import androidclient.feature.client.generated.resources.client_apply_new_applications_title
import androidclient.feature.client.generated.resources.feature_client_charge_amount
import androidclient.feature.client.generated.resources.feature_client_charge_created_successfully
import androidclient.feature.client.generated.resources.feature_client_charge_id
import androidclient.feature.client.generated.resources.feature_client_charge_name
import androidclient.feature.client.generated.resources.feature_client_charges
import androidclient.feature.client.generated.resources.feature_client_due_date
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.PullToRefreshState
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.paging.PagingData
import com.mifos.core.common.utils.CurrencyFormatter
import com.mifos.core.common.utils.DateHelper
import com.mifos.core.designsystem.component.MifosScaffold
import com.mifos.core.designsystem.component.MifosSweetError
import com.mifos.core.designsystem.icon.MifosIcons
import com.mifos.core.designsystem.theme.DesignToken
import com.mifos.core.designsystem.theme.MifosTypography
import com.mifos.core.ui.components.MifosBreadcrumbNavBar
import com.mifos.core.ui.components.MifosProgressIndicator
import com.mifos.core.ui.util.EventsEffect
import com.mifos.feature.client.clientChargeDialog.ChargeDialogScreen
import com.mifos.room.entities.client.ChargesEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.getString
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun ClientChargesScreen(
    navigateBack: () -> Unit,
    navController: NavController,
    modifier: Modifier = Modifier,
    viewModel: ClientChargesViewModel = koinViewModel(),
) {
    val state by viewModel.stateFlow.collectAsStateWithLifecycle()

    EventsEffect(viewModel.eventFlow) { event ->
        when (event) {
            ClientChargesEvent.NavigateBack -> navigateBack()
        }
    }

    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    val pullRefreshState = rememberPullToRefreshState()

    // Show success snackbar when charge is created
    LaunchedEffect(state.showSuccessSnackbar) {
        if (state.showSuccessSnackbar) {
            scope.launch {
                snackbarHostState.showSnackbar(
                    message = getString(Res.string.feature_client_charge_created_successfully),
                )
            }
            viewModel.trySendAction(ClientChargesAction.CloseDialog)
        }
    }

    ClientChargesScaffold(
        state = state,
        navController = navController,
        onAction = remember { viewModel::trySendAction },
        snackbarHostState = snackbarHostState,
        pullRefreshState = pullRefreshState
    )
}

@Composable
fun ClientChargesScaffold(
    state: ClientChargesState,
    navController: NavController,
    modifier: Modifier = Modifier,
    onAction: (ClientChargesAction) -> Unit,
    snackbarHostState: SnackbarHostState,
    pullRefreshState: PullToRefreshState
) {
    // Show charge dialog based on state
    if (state.showChargeDialog) {
        ChargeDialogScreen(
            state = state.chargeDialogUiState,
            onDismiss = { onAction(ClientChargesAction.CloseDialog) },
            onCreateCharge = { payload -> onAction(ClientChargesAction.CreateCharge(payload)) },
            onRetry = { onAction(ClientChargesAction.LoadChargeTemplate) },
            onChargeCreated = {
                onAction(ClientChargesAction.OnRetry)
            },
        )
    }

    MifosScaffold(
        title = stringResource(Res.string.feature_client_charges),
        onBackPressed = { onAction(ClientChargesAction.NavigateBack) },
        actions = {
            IconButton(
                onClick = { onAction(ClientChargesAction.LoadChargeTemplate) },
            ) {
                Icon(imageVector = MifosIcons.Add, contentDescription = null)
            }
        },
        snackbarHostState = snackbarHostState,
    ) { paddingValues ->
        Column(
            modifier = Modifier.fillMaxSize().padding(paddingValues),
        ) {
            MifosBreadcrumbNavBar(navController)
            PullToRefreshBox(
                state = pullRefreshState,
                onRefresh = { onAction(ClientChargesAction.Refresh) },
                isRefreshing = state.isRefreshing,
            ) {
                when {
                    state.isLoading -> MifosProgressIndicator()
                    state.error != null -> {
                        MifosSweetError(
                            message = state.error!!,
//                            onRetry = { onAction(ClientChargesAction.OnRetry) }
                        )
                    }
                    else -> ShowChargesContent(state, onAction)
                }
            }
        }
    }
}

@Composable
private fun ShowChargesContent(
    state: ClientChargesState,
    onAction: (ClientChargesAction) -> Unit,
) {
    Column {
        Text(
            text = stringResource(Res.string.feature_client_charges),
            style = MifosTypography.labelLargeEmphasized,
        )
        Spacer(modifier = Modifier.height(DesignToken.spacing.medium))

        when (val charges = state.chargesFlow) {
            is Flow<*> -> {
                ClientChargeContent(
                    pagingFlow = charges as Flow<PagingData<ChargesEntity>>,
                    onRetry = { onAction(ClientChargesAction.OnRetry) },
                )
            }

            null -> MifosProgressIndicator()
            else -> {
                MifosProgressIndicator()
            }
        }
    }
}

@Composable
fun ChargesItems(charges: ChargesEntity) {
    val currencyCode = charges.currency?.code ?: ""

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        shape = RoundedCornerShape(0.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    ) {
        Spacer(modifier = Modifier.height(8.dp))
        MifosCenterDetailsText(
            stringResource(Res.string.feature_client_charge_id),
            charges.chargeId.toString(),
        )
        MifosCenterDetailsText(
            stringResource(Res.string.feature_client_charge_name),
            charges.name ?: "",
        )
        MifosCenterDetailsText(
            stringResource(Res.string.feature_client_charge_amount),
            CurrencyFormatter.format(
                charges.amount,
                currencyCode,
                2,
            ),
        )
        MifosCenterDetailsText(
            stringResource(Res.string.feature_client_due_date),
            DateHelper.getDateAsString(charges.dueDate ?: emptyList()),
        )
        Spacer(modifier = Modifier.height(8.dp))
    }
}

@Composable
private fun MifosCenterDetailsText(field: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            modifier = Modifier
                .weight(1f)
                .padding(start = 16.dp),
            text = field,
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Start,
        )
        Text(
            modifier = Modifier.weight(1f),
            text = value,
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Start,
        )
    }
}

@Composable
expect fun ClientChargeContent(
    pagingFlow: Flow<PagingData<ChargesEntity>>,
    onRetry: () -> Unit,
)

///*
// * Copyright 2024 Mifos Initiative
// *
// * This Source Code Form is subject to the terms of the Mozilla Public
// * License, v. 2.0. If a copy of the MPL was not distributed with this
// * file, You can obtain one at https://mozilla.org/MPL/2.0/.
// *
// * See https://github.com/openMF/android-client/blob/master/LICENSE.md
// */
//@file:OptIn(ExperimentalMaterial3Api::class)
//
//package com.mifos.feature.client.clientCharges
//
//import androidclient.feature.client.generated.resources.Res
//import androidclient.feature.client.generated.resources.feature_client_charge_amount
//import androidclient.feature.client.generated.resources.feature_client_charge_created_successfully
//import androidclient.feature.client.generated.resources.feature_client_charge_id
//import androidclient.feature.client.generated.resources.feature_client_charge_name
//import androidclient.feature.client.generated.resources.feature_client_charges
//import androidclient.feature.client.generated.resources.feature_client_due_date
//import androidclient.feature.client.generated.resources.feature_client_failed_to_load_client_charges
//import androidx.compose.foundation.layout.Column
//import androidx.compose.foundation.layout.Row
//import androidx.compose.foundation.layout.Spacer
//import androidx.compose.foundation.layout.fillMaxWidth
//import androidx.compose.foundation.layout.height
//import androidx.compose.foundation.layout.padding
//import androidx.compose.foundation.shape.RoundedCornerShape
//import androidx.compose.material3.Card
//import androidx.compose.material3.CardDefaults
//import androidx.compose.material3.ExperimentalMaterial3Api
//import androidx.compose.material3.Icon
//import androidx.compose.material3.IconButton
//import androidx.compose.material3.MaterialTheme
//import androidx.compose.material3.SnackbarHostState
//import androidx.compose.material3.Text
//import androidx.compose.material3.pulltorefresh.PullToRefreshBox
//import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
//import androidx.compose.runtime.Composable
//import androidx.compose.runtime.getValue
//import androidx.compose.runtime.mutableStateOf
//import androidx.compose.runtime.remember
//import androidx.compose.runtime.rememberCoroutineScope
//import androidx.compose.runtime.saveable.rememberSaveable
//import androidx.compose.runtime.setValue
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.text.style.TextAlign
//import androidx.compose.ui.unit.dp
//import androidx.lifecycle.compose.collectAsStateWithLifecycle
//import androidx.paging.PagingData
//import com.mifos.core.common.utils.CurrencyFormatter
//import com.mifos.core.common.utils.DateHelper
//import com.mifos.core.designsystem.component.MifosScaffold
//import com.mifos.core.designsystem.component.MifosSweetError
//import com.mifos.core.designsystem.icon.MifosIcons
//import com.mifos.core.model.objects.payloads.ChargesPayload
//import com.mifos.core.ui.components.MifosProgressIndicator
//import com.mifos.core.ui.util.DevicePreview
//import com.mifos.feature.client.clientChargeDialog.ChargeDialogScreen
//import com.mifos.feature.client.clientChargeDialog.ChargeDialogUiState
//import com.mifos.room.entities.client.ChargesEntity
//import kotlinx.coroutines.flow.Flow
//import kotlinx.coroutines.flow.flowOf
//import kotlinx.coroutines.launch
//import org.jetbrains.compose.resources.getString
//import org.jetbrains.compose.resources.stringResource
//import org.jetbrains.compose.ui.tooling.preview.PreviewParameter
//import org.jetbrains.compose.ui.tooling.preview.PreviewParameterProvider
//import org.koin.compose.viewmodel.koinViewModel
//
//@Composable
//fun ClientChargesScreen(
//    onBackPressed: () -> Unit,
//    viewModel: ClientChargesViewModel = koinViewModel(),
//) {
//    val clientChargeUiState by viewModel.clientChargesUiState.collectAsStateWithLifecycle()
//    val refreshState by viewModel.isRefreshing.collectAsStateWithLifecycle()
//    val chargeDialogUiState by viewModel.chargeDialogUiState.collectAsStateWithLifecycle()
//
//    ClientChargesScreen(
//        state = clientChargeUiState,
//        dialogState = chargeDialogUiState,
//        onShowDialog = viewModel::loadChargeTemplate,
//        onCreateCharge = { payload -> viewModel.createCharge(payload) },
//        onChargeCreated = viewModel::loadCharges,
//        onBackPressed = onBackPressed,
//        onRetry = viewModel::loadCharges,
//        onRefresh = viewModel::refreshChargesList,
//        refreshState = refreshState,
//    )
//}
//
//@Composable
//fun ClientChargesScreen(
//    state: ClientChargeUiState,
//    dialogState: ChargeDialogUiState,
//    onShowDialog: () -> Unit,
//    onCreateCharge: (ChargesPayload) -> Unit,
//    onChargeCreated: () -> Unit,
//    onBackPressed: () -> Unit,
//    onRetry: () -> Unit,
//    onRefresh: () -> Unit,
//    refreshState: Boolean,
//) {
//    val scope = rememberCoroutineScope()
//    val snackbarHostState = remember { SnackbarHostState() }
//    val pullRefreshState = rememberPullToRefreshState()
//    var showClientChargeDialog by rememberSaveable { mutableStateOf(false) }
//    var showChargeCreatedSuccess by rememberSaveable { mutableStateOf(false) }
//
//    if (showClientChargeDialog) {
//        ChargeDialogScreen(
//            state = dialogState,
//            onDismiss = {
//                showClientChargeDialog = false
//            },
//            onCreateCharge = onCreateCharge,
//            onRetry = onRetry,
//            onChargeCreated = {
//                onChargeCreated()
//                showClientChargeDialog = false
//                showChargeCreatedSuccess = true
//            },
//        )
//    }
//
//    MifosScaffold(
//        title = stringResource(Res.string.feature_client_charges),
//        onBackPressed = onBackPressed,
//        actions = {
//            IconButton(
//                onClick = {
//                    onShowDialog()
//                    showClientChargeDialog = true
//                },
//            ) {
//                Icon(imageVector = MifosIcons.Add, contentDescription = null)
//            }
//        },
//        snackbarHostState = snackbarHostState,
//    ) { paddingValues ->
//        Column(modifier = Modifier.padding(paddingValues)) {
//            PullToRefreshBox(
//                state = pullRefreshState,
//                onRefresh = onRefresh,
//                isRefreshing = refreshState,
//            ) {
//                when (state) {
//                    is ClientChargeUiState.ChargesList -> ClientChargeContent(
//                        pagingFlow = state.chargesPage,
//                        onRetry = onRetry,
//                    )
//
//                    is ClientChargeUiState.Error ->
//                        MifosSweetError(
//                            message = stringResource(state.message),
//                        ) {
//                            onRetry()
//                        }
//
//                    is ClientChargeUiState.Loading -> MifosProgressIndicator()
//                }
//            }
//        }
//    }
//
//    if (showChargeCreatedSuccess) {
//        scope.launch {
//            snackbarHostState.showSnackbar(
//                message = getString(Res.string.feature_client_charge_created_successfully),
//            )
//        }
//        showChargeCreatedSuccess = false
//    }
//}
//
//@Composable
//expect fun ClientChargeContent(
//    pagingFlow: Flow<PagingData<ChargesEntity>>,
//    onRetry: () -> Unit,
//)
//
//@Composable
//fun ChargesItems(charges: ChargesEntity) {
//    val currencyCode = charges.currency?.code ?: ""
//
//    Card(
//        modifier = Modifier
//            .fillMaxWidth()
//            .padding(8.dp),
//        shape = RoundedCornerShape(0.dp),
//        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
//    ) {
//        Spacer(modifier = Modifier.height(8.dp))
//        MifosCenterDetailsText(
//            stringResource(Res.string.feature_client_charge_id),
//            charges.chargeId.toString(),
//        )
//        MifosCenterDetailsText(
//            stringResource(Res.string.feature_client_charge_name),
//            charges.name ?: "",
//        )
//        MifosCenterDetailsText(
//            stringResource(Res.string.feature_client_charge_amount),
//            CurrencyFormatter.format(
//                charges.amount,
//                currencyCode,
//                2,
//            ),
//        )
//        MifosCenterDetailsText(
//            stringResource(Res.string.feature_client_due_date),
//            DateHelper.getDateAsString(charges.dueDate ?: emptyList()),
//        )
//        Spacer(modifier = Modifier.height(8.dp))
//    }
//}
//
//@Composable
//private fun MifosCenterDetailsText(field: String, value: String) {
//    Row(
//        modifier = Modifier
//            .fillMaxWidth(),
//        verticalAlignment = Alignment.CenterVertically,
//    ) {
//        Text(
//            modifier = Modifier
//                .weight(1f)
//                .padding(start = 16.dp),
//            text = field,
//            style = MaterialTheme.typography.bodyLarge,
//            textAlign = TextAlign.Start,
//        )
//        Text(
//            modifier = Modifier.weight(1f),
//            text = value,
//            style = MaterialTheme.typography.bodyLarge,
//            textAlign = TextAlign.Start,
//        )
//    }
//}
//
//private class ClientChargesScreenUiStateProvider : PreviewParameterProvider<ClientChargeUiState> {
//
//    override val values: Sequence<ClientChargeUiState>
//        get() = sequenceOf(
//            ClientChargeUiState.Loading,
//            ClientChargeUiState.Error(Res.string.feature_client_failed_to_load_client_charges),
//            ClientChargeUiState.ChargesList(flowOf(PagingData.from(sampleClientCharge))),
//        )
//}
//
//@DevicePreview
//@Composable
//private fun ClientChargesScreenPreview(
//    @PreviewParameter(ClientChargesScreenUiStateProvider::class) state: ClientChargeUiState,
//) {
//    ClientChargesScreen(
//        state = state,
//        dialogState = ChargeDialogUiState.Loading,
//        onShowDialog = {},
//        onCreateCharge = {},
//        onChargeCreated = {},
//        onBackPressed = {},
//        onRetry = {},
//        onRefresh = {},
//        refreshState = false,
//    )
//}
//
//val sampleClientCharge = List(10) {
//    ChargesEntity(name = "charge $it", amount = it.toDouble())
//}
