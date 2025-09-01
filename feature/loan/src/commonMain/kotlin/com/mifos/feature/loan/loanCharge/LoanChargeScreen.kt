/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.feature.loan.loanCharge

import androidclient.feature.loan.generated.resources.Res
import androidclient.feature.loan.generated.resources.feature_loan_charge_amount
import androidclient.feature.loan.generated.resources.feature_loan_charge_due_date
import androidclient.feature.loan.generated.resources.feature_loan_charge_name
import androidclient.feature.loan.generated.resources.feature_loan_client_id
import androidclient.feature.loan.generated.resources.feature_loan_failed_to_load_loan_charges
import androidclient.feature.loan.generated.resources.feature_loan_loan_charges
import androidclient.feature.loan.generated.resources.feature_loan_no_loan_charges
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color.Companion.Black
import androidx.compose.ui.graphics.Color.Companion.DarkGray
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mifos.core.designsystem.component.MifosCircularProgress
import com.mifos.core.designsystem.component.MifosScaffold
import com.mifos.core.designsystem.component.MifosSweetError
import com.mifos.core.designsystem.icon.MifosIcons
import com.mifos.core.ui.components.MifosEmptyUi
import com.mifos.feature.loan.loanChargeDialog.LoanChargeDialogScreen
import com.mifos.room.entities.client.ChargesEntity
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.jetbrains.compose.ui.tooling.preview.PreviewParameter
import org.jetbrains.compose.ui.tooling.preview.PreviewParameterProvider
import org.koin.compose.viewmodel.koinViewModel

@Composable
internal fun LoanChargeScreen(
    onBackPressed: () -> Unit,
    viewModel: LoanChargeViewModel = koinViewModel(),
) {
    val state by viewModel.loanChargeUiState.collectAsStateWithLifecycle()
    val refreshState by viewModel.isRefreshing.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.loadLoanChargesList()
    }

    LoanChargeScreen(
        loanAccountNumber = viewModel.loanAccountNumber,
        state = state,
        onBackPressed = onBackPressed,
        onChargeCreated = {
            viewModel.loadLoanChargesList()
        },
        onRetry = {
            viewModel.loadLoanChargesList()
        },
        refreshState = refreshState,
        onRefresh = {
            viewModel.refreshLoanChargeList()
        },
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun LoanChargeScreen(
    loanAccountNumber: Int,
    state: LoanChargeUiState,
    onBackPressed: () -> Unit,
    onChargeCreated: () -> Unit,
    onRetry: () -> Unit,
    refreshState: Boolean,
    onRefresh: () -> Unit,
) {
    val snackbarHostState by remember { mutableStateOf(SnackbarHostState()) }
    val pullRefreshState = rememberPullToRefreshState()
    var showLoanChargeDialog by rememberSaveable { mutableStateOf(false) }

    if (showLoanChargeDialog) {
        LoanChargeDialogScreen(
            loanId = loanAccountNumber,
            onSuccess = {
                onChargeCreated()
                showLoanChargeDialog = false
            },
            onDismiss = { showLoanChargeDialog = false },
        )
    }

    MifosScaffold(
        title = stringResource(Res.string.feature_loan_loan_charges),
        onBackPressed = onBackPressed,
        actions = {
            IconButton(onClick = {
                showLoanChargeDialog = true
            }) {
                Icon(imageVector = MifosIcons.Add, contentDescription = null)
            }
        },
        snackbarHostState = snackbarHostState,
    ) { paddingValues ->
        Column(modifier = Modifier.padding(paddingValues)) {
            PullToRefreshBox(
                state = pullRefreshState,
                onRefresh = onRefresh,
                isRefreshing = refreshState,
            ) {
                when (state) {
                    is LoanChargeUiState.Error -> MifosSweetError(message = stringResource(state.message)) {
                        onRetry()
                    }

                    is LoanChargeUiState.Loading -> MifosCircularProgress()

                    is LoanChargeUiState.LoanChargesList -> {
                        if (state.loanCharges.isEmpty()) {
                            MifosEmptyUi(
                                text = stringResource(Res.string.feature_loan_no_loan_charges),
                                icon = MifosIcons.FileTask,
                            )
                        } else {
                            LoanChargesContent(loanCharges = state.loanCharges)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun LoanChargesContent(
    loanCharges: List<ChargesEntity>,
) {
    LazyColumn {
        items(loanCharges) { charges ->
            LoanChargeItem(
                charges = charges,
            )
        }
    }
}

@Composable
private fun LoanChargeItem(
    charges: ChargesEntity,
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        shape = RoundedCornerShape(0.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    ) {
        Spacer(modifier = Modifier.height(8.dp))
        MifosLoanChargeDetailsText(
            stringResource(Res.string.feature_loan_client_id),
            charges.chargeId.toString(),
        )
        MifosLoanChargeDetailsText(
            stringResource(Res.string.feature_loan_charge_name),
            charges.name ?: "",
        )
        MifosLoanChargeDetailsText(
            stringResource(Res.string.feature_loan_charge_amount),
            charges.amount.toString(),
        )
        MifosLoanChargeDetailsText(
            stringResource(Res.string.feature_loan_charge_due_date),
            charges.formattedDueDate,
        )
        Spacer(modifier = Modifier.height(8.dp))
    }
}

@Composable
private fun MifosLoanChargeDetailsText(field: String, value: String) {
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
            style = TextStyle(
                fontSize = 16.sp,
                fontWeight = FontWeight.Normal,
                fontStyle = FontStyle.Normal,
            ),
            color = Black,
            textAlign = TextAlign.Start,
        )
        Text(
            modifier = Modifier.weight(1f),
            text = value,
            style = TextStyle(
                fontSize = 16.sp,
                fontWeight = FontWeight.Normal,
                fontStyle = FontStyle.Normal,
            ),
            color = DarkGray,
            textAlign = TextAlign.Start,
        )
    }
}

private class LoanChargeUiStateProvider : PreviewParameterProvider<LoanChargeUiState> {

    override val values: Sequence<LoanChargeUiState>
        get() = sequenceOf(
            LoanChargeUiState.Loading,
            LoanChargeUiState.Error(Res.string.feature_loan_failed_to_load_loan_charges),
            LoanChargeUiState.LoanChargesList(sampleLoanChargeList),
        )
}

@Preview
@Composable
private fun LoanChargeScreenPreview(
    @PreviewParameter(LoanChargeUiStateProvider::class) state: LoanChargeUiState,
) {
    LoanChargeScreen(
        loanAccountNumber = 1,
        state = state,
        onChargeCreated = {},
        onBackPressed = {},
        onRetry = {},
        refreshState = false,
        onRefresh = {},
    )
}

val sampleLoanChargeList = List(10) {
    ChargesEntity(name = "name $it", chargeId = it, amount = it.toDouble())
}
