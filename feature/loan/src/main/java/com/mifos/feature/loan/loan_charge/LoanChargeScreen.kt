@file:OptIn(ExperimentalMaterialApi::class)

package com.mifos.feature.loan.loan_charge

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import com.mifos.core.designsystem.component.MifosCircularProgress
import com.mifos.core.designsystem.component.MifosScaffold
import com.mifos.core.designsystem.component.MifosSweetError
import com.mifos.core.designsystem.icon.MifosIcons
import com.mifos.core.designsystem.theme.Black
import com.mifos.core.designsystem.theme.BlueSecondary
import com.mifos.core.designsystem.theme.DarkGray
import com.mifos.core.objects.client.Charges
import com.mifos.core.ui.components.MifosEmptyUi
import com.mifos.feature.loan.R
import com.mifos.feature.loan.loan_charge_dialog.LoanChargeDialogScreen

@Composable
fun LoanChargeScreen(onBackPressed: () -> Unit) {

    val viewModel: LoanChargeViewModel = hiltViewModel()
    val state by viewModel.loanChargeUiState.collectAsStateWithLifecycle()
    val refreshState by viewModel.isRefreshing.collectAsStateWithLifecycle()
    val loanAccountNumber by viewModel.loanAccountNumber.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.loadLoanChargesList(loanAccountNumber)
    }

    LoanChargeScreen(
        loanAccountNumber = loanAccountNumber,
        state = state,
        onBackPressed = onBackPressed,
        onChargeCreated = {
            viewModel.loadLoanChargesList(loanAccountNumber)
        },
        onRetry = {
            viewModel.loadLoanChargesList(loanAccountNumber)
        },
        refreshState = refreshState,
        onRefresh = {
            viewModel.refreshLoanChargeList(loanAccountNumber)
        }
    )

}

@Composable
fun LoanChargeScreen(
    loanAccountNumber: Int,
    state: LoanChargeUiState,
    onBackPressed: () -> Unit,
    onChargeCreated: () -> Unit,
    onRetry: () -> Unit,
    refreshState: Boolean,
    onRefresh: () -> Unit,
) {
    val snackbarHostState by remember { mutableStateOf(SnackbarHostState()) }
    val pullRefreshState = rememberPullRefreshState(
        refreshing = refreshState,
        onRefresh = onRefresh
    )
    var showLoanChargeDialog by rememberSaveable { mutableStateOf(false) }

    if (showLoanChargeDialog) {
        LoanChargeDialogScreen(
            loanId = loanAccountNumber,
            onSuccess = {
                onChargeCreated()
                showLoanChargeDialog = false
            },
            onDismiss = { showLoanChargeDialog = false }
        )
    }

    MifosScaffold(
        icon = MifosIcons.arrowBack,
        title = stringResource(id = R.string.feature_loan_loan_charges),
        onBackPressed = onBackPressed,
        actions = {
            IconButton(onClick = {
                showLoanChargeDialog = true
            }) {
                Icon(imageVector = MifosIcons.Add, contentDescription = null)
            }
        },
        snackbarHostState = snackbarHostState
    ) { paddingValues ->
        Column(modifier = Modifier.padding(paddingValues)) {
            Box(modifier = Modifier.pullRefresh(pullRefreshState)) {
                when (state) {
                    is LoanChargeUiState.Error -> MifosSweetError(message = stringResource(id = state.message)) {
                        onRetry()
                    }

                    is LoanChargeUiState.Loading -> MifosCircularProgress()

                    is LoanChargeUiState.LoanChargesList -> {
                        if (state.loanCharges.isEmpty()) {
                            MifosEmptyUi(
                                text = stringResource(id = R.string.feature_loan_no_loan_charges),
                                icon = MifosIcons.fileTask
                            )
                        } else {
                            LoanChargesContent(loanCharges = state.loanCharges)
                        }
                    }
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
fun LoanChargesContent(
    loanCharges: List<Charges>
) {
    LazyColumn {
        items(loanCharges) { charges ->
            LoanChargeItem(
                charges = charges
            )
        }
    }
}

@Composable
fun LoanChargeItem(
    charges: Charges
) {
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
        MifosLoanChargeDetailsText(
            stringResource(id = R.string.feature_loan_client_id),
            charges.chargeId.toString()
        )
        MifosLoanChargeDetailsText(
            stringResource(id = R.string.feature_loan_charge_name),
            charges.name ?: ""
        )
        MifosLoanChargeDetailsText(
            stringResource(id = R.string.feature_loan_charge_amount),
            charges.amount.toString()
        )
        MifosLoanChargeDetailsText(
            stringResource(id = R.string.feature_loan_charge_due_date),
            charges.formattedDueDate
        )
        Spacer(modifier = Modifier.height(8.dp))
    }
}

@Composable
fun MifosLoanChargeDetailsText(field: String, value: String) {
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

class LoanChargeUiStateProvider : PreviewParameterProvider<LoanChargeUiState> {

    override val values: Sequence<LoanChargeUiState>
        get() = sequenceOf(
            LoanChargeUiState.Loading,
            LoanChargeUiState.Error(R.string.feature_loan_failed_to_load_loan_charges),
            LoanChargeUiState.LoanChargesList(sampleLoanChargeList)
        )
}


@Preview(showBackground = true)
@Composable
private fun LoanChargeScreenPreview(
    @PreviewParameter(LoanChargeUiStateProvider::class) state: LoanChargeUiState
) {
    LoanChargeScreen(
        loanAccountNumber = 1,
        state = state,
        onChargeCreated = {},
        onBackPressed = {},
        onRetry = {},
        refreshState = false,
        onRefresh = {}
    )
}

val sampleLoanChargeList = List(10) {
    Charges(name = "name $it", chargeId = it, amount = it.toDouble())
}