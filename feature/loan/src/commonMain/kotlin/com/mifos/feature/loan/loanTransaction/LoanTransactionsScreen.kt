/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.feature.loan.loanTransaction

import androidclient.feature.loan.generated.resources.Res
import androidclient.feature.loan.generated.resources.feature_loan_break_down
import androidclient.feature.loan.generated.resources.feature_loan_export
import androidclient.feature.loan.generated.resources.feature_loan_filters
import androidclient.feature.loan.generated.resources.feature_loan_hide_accruals
import androidclient.feature.loan.generated.resources.feature_loan_hide_reversed
import androidclient.feature.loan.generated.resources.feature_loan_id
import androidclient.feature.loan.generated.resources.feature_loan_loan_fees
import androidclient.feature.loan.generated.resources.feature_loan_loan_interest
import androidclient.feature.loan.generated.resources.feature_loan_loan_penalty
import androidclient.feature.loan.generated.resources.feature_loan_loan_transactions
import androidclient.feature.loan.generated.resources.feature_loan_no_transactions
import androidclient.feature.loan.generated.resources.feature_loan_office
import androidclient.feature.loan.generated.resources.feature_loan_principal
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mifos.core.common.utils.DateHelper
import com.mifos.core.designsystem.component.MifosScaffold
import com.mifos.core.designsystem.component.MifosSweetError
import com.mifos.core.designsystem.icon.MifosIcons
import com.mifos.core.designsystem.theme.DesignToken
import com.mifos.core.model.objects.account.loan.Transaction
import com.mifos.core.model.objects.account.loan.Type
import com.mifos.core.ui.components.MifosCheckBox
import com.mifos.core.ui.components.MifosEmptyUi
import com.mifos.core.ui.components.MifosProgressIndicator
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.jetbrains.compose.ui.tooling.preview.PreviewParameter
import org.jetbrains.compose.ui.tooling.preview.PreviewParameterProvider
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun LoanTransactionsScreen(
    navigateBack: () -> Unit,
    viewModel: LoanTransactionsViewModel = koinViewModel(),
) {
    val state by viewModel.stateFlow.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.eventFlow.collect { event ->
            when (event) {
                is LoanTransactionsEvent.NavigateBack -> navigateBack()
            }
        }
    }

    LoanTransactionsScreen(
        state = state,
        onAction = viewModel::trySendAction,
        onExportClick = { },
        navigateBack = navigateBack,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun LoanTransactionsScreen(
    state: LoanTransactionsState,
    onAction: (LoanTransactionsAction) -> Unit,
    onExportClick: () -> Unit,
    navigateBack: () -> Unit,
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val isFilterApplied = state.hideReversed || state.hideAccruals

    MifosScaffold(
        snackbarHostState = snackbarHostState,
        title = stringResource(Res.string.feature_loan_loan_transactions),
        onBackPressed = navigateBack,
        actions = {
            IconButton(onClick = { onAction(LoanTransactionsAction.ShowFilterSheet) }) {
                Icon(
                    imageVector = MifosIcons.Filter,
                    contentDescription = stringResource(Res.string.feature_loan_filters),
                    tint = if (isFilterApplied) {
                        MaterialTheme.colorScheme.primary
                    } else {
                        MaterialTheme.colorScheme.onSurface
                    },
                    modifier = Modifier
                        .size(DesignToken.sizes.iconMedium)
                        .offset(y = DesignToken.padding.extraExtraSmall),
                )
            }
            IconButton(onClick = onExportClick) {
                Icon(
                    imageVector = MifosIcons.Share,
                    contentDescription = stringResource(Res.string.feature_loan_export),
                    tint = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.size(DesignToken.sizes.iconMedium),
                )
            }
        },
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(it),
        ) {
            if (state.isTransactionsEmpty) {
                MifosEmptyUi(text = stringResource(Res.string.feature_loan_no_transactions))
            } else {
                LoanTransactionsContent(
                    transactions = state.transactions,
                )
            }

            LoanTransactionsDialogState(
                dialogState = state.dialogState,
                onAction = onAction,
            )
        }
    }

    FilterBottomSheet(
        showFilterBottomSheet = state.showFilterSheet,
        hideReversed = state.hideReversed,
        hideAccruals = state.hideAccruals,
        onHideReversedChange = { onAction(LoanTransactionsAction.SetHideReversed(it)) },
        onHideAccrualsChange = { onAction(LoanTransactionsAction.SetHideAccruals(it)) },
        onDismiss = { onAction(LoanTransactionsAction.DismissFilterSheet) },
    )
}

@Composable
private fun LoanTransactionsDialogState(
    dialogState: LoanTransactionsState.DialogState?,
    onAction: (LoanTransactionsAction) -> Unit,
) {
    when (dialogState) {
        is LoanTransactionsState.DialogState.Error -> {
            MifosSweetError(
                message = dialogState.message,
                onclick = { onAction(LoanTransactionsAction.Refresh) },
            )
        }
        LoanTransactionsState.DialogState.Loading -> {
            MifosProgressIndicator()
        }
        null -> Unit
    }
}

@Composable
private fun LoanTransactionsContent(
    transactions: List<Transaction>,
) {
    LazyColumn {
        items(transactions) { transaction ->
            LoanTransactionsItemRow(transaction = transaction)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FilterBottomSheet(
    showFilterBottomSheet: Boolean,
    hideReversed: Boolean,
    hideAccruals: Boolean,
    onHideReversedChange: (Boolean) -> Unit,
    onHideAccrualsChange: (Boolean) -> Unit,
    onDismiss: () -> Unit,
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    if (showFilterBottomSheet) {
        ModalBottomSheet(
            onDismissRequest = onDismiss,
            sheetState = sheetState,
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(DesignToken.padding.medium),
            ) {
                Text(
                    text = stringResource(Res.string.feature_loan_filters),
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(bottom = DesignToken.padding.medium),
                )

                MifosCheckBox(
                    text = stringResource(Res.string.feature_loan_hide_reversed),
                    checked = hideReversed,
                    onCheckChanged = onHideReversedChange,
                    modifier = Modifier.fillMaxWidth(),
                )

                Spacer(modifier = Modifier.height(DesignToken.spacing.small))

                MifosCheckBox(
                    text = stringResource(Res.string.feature_loan_hide_accruals),
                    checked = hideAccruals,
                    onCheckChanged = onHideAccrualsChange,
                    modifier = Modifier.fillMaxWidth(),
                )

                Spacer(modifier = Modifier.height(DesignToken.spacing.large))
            }
        }
    }
}

@Composable
private fun LoanTransactionsItemRow(transaction: Transaction) {
    val density = LocalDensity.current
    var showDetails by rememberSaveable {
        mutableStateOf(false)
    }
    Column {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            shape = RoundedCornerShape(0.dp),
            onClick = { showDetails = !showDetails },
            colors = CardDefaults.cardColors(
                containerColor = White,
            ),
        ) {
            Column(
                modifier = Modifier
                    .padding(horizontal = 8.dp, vertical = 10.dp)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.Center,
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Icon(
                        imageVector = if (!showDetails) MifosIcons.ArrowDown else MifosIcons.ArrowUp,
                        contentDescription = "",
                    )

                    Text(
                        modifier = Modifier
                            .weight(3f)
                            .padding(start = 8.dp),
                        text = DateHelper.getDateAsString(transaction.date),
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onBackground,
                        textAlign = TextAlign.Center,
                    )

                    Text(
                        modifier = Modifier
                            .weight(3.3f)
                            .padding(start = 8.dp),
                        text = transaction.type?.value ?: "",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onBackground,
                        textAlign = TextAlign.Center,
                    )

                    Text(
                        modifier = Modifier
                            .weight(2.7f)
                            .padding(start = 8.dp),
                        text = transaction.amount.toString(),
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onBackground,
                        textAlign = TextAlign.End,
                    )
                }
            }
        }

        AnimatedVisibility(
            visible = showDetails,
            enter = slideInVertically {
                with(density) { -40.dp.roundToPx() }
            } + expandVertically(
                expandFrom = Alignment.Top,
            ) + fadeIn(
                initialAlpha = 0.3f,
            ),
            exit = slideOutVertically() + shrinkVertically() + fadeOut(),
        ) {
            LoanTransactionsItemDetailsCard(transaction = transaction)
        }
        HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))
    }
}

@Composable
private fun LoanTransactionsItemDetailsCard(
    transaction: Transaction,
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFe7eb9a),
        ),
        elevation = CardDefaults.cardElevation(2.dp),
        shape = RoundedCornerShape(0.dp),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Text(
                    text = stringResource(Res.string.feature_loan_id),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onBackground,
                )

                Text(
                    text = transaction.id.toString(),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onBackground,
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Text(
                    text = stringResource(Res.string.feature_loan_office),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onBackground,
                )

                Text(
                    text = transaction.officeName.toString(),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onBackground,
                )
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp, bottom = 8.dp),
            ) {
                Text(
                    modifier = Modifier.align(Alignment.Center),
                    text = stringResource(Res.string.feature_loan_break_down),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onBackground,
                )
            }

            HorizontalDivider(color = Color.Black)

            Spacer(modifier = Modifier.height(8.dp))

            Box(
                modifier = Modifier
                    .background(Color(0xFFdea164)),
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(4.dp),
                ) {
                    Text(
                        modifier = Modifier.weight(2.5f),
                        text = stringResource(Res.string.feature_loan_principal),
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground,
                        textAlign = TextAlign.Start,
                    )

                    Text(
                        modifier = Modifier.weight(2.5f),
                        text = stringResource(Res.string.feature_loan_loan_interest),
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground,
                        textAlign = TextAlign.Center,
                    )

                    Text(
                        modifier = Modifier.weight(2.5f),
                        text = stringResource(Res.string.feature_loan_loan_fees),
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground,
                        textAlign = TextAlign.Center,
                    )

                    Text(
                        modifier = Modifier.weight(2.5f),
                        text = stringResource(Res.string.feature_loan_loan_penalty),
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground,
                        textAlign = TextAlign.End,
                    )
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(4.dp),
            ) {
                Text(
                    modifier = Modifier.weight(2.5f),
                    text = transaction.principalPortion.toString(),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onBackground,
                    textAlign = TextAlign.Start,
                )

                Text(
                    modifier = Modifier
                        .weight(2.5f)
                        .padding(horizontal = 4.dp),
                    text = transaction.interestPortion.toString(),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onBackground,
                    textAlign = TextAlign.Center,
                )

                Text(
                    modifier = Modifier.weight(2.5f),
                    text = transaction.feeChargesPortion.toString(),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onBackground,
                    textAlign = TextAlign.Center,
                )

                Text(
                    modifier = Modifier
                        .weight(2.5f)
                        .padding(start = 4.dp),
                    text = transaction.penaltyChargesPortion.toString(),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onBackground,
                    textAlign = TextAlign.End,
                )
            }
        }
    }
}

private class LoanTransactionsPreviewProvider : PreviewParameterProvider<LoanTransactionsState> {
    val transaction =
        Transaction(
            id = 23,
            officeName = "Main office",
            date = listOf(2024, 6, 1),
            principalPortion = 121.2,
            penaltyChargesPortion = 32323.232,
            overpaymentPortion = 23232.23,
            feeChargesPortion = 323.3,
            interestPortion = 232.3,
            type = Type(
                value = "Repayment",
            ),
        )

    override val values: Sequence<LoanTransactionsState>
        get() = sequenceOf(
            LoanTransactionsState(
                dialogState = LoanTransactionsState.DialogState.Error("Something went wrong"),
            ),
            LoanTransactionsState(
                dialogState = LoanTransactionsState.DialogState.Loading,
            ),
            LoanTransactionsState(
                transactions = List(10) { transaction },
                dialogState = null,
            ),
        )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
@Preview
private fun PreviewLoanTransactions(
    @PreviewParameter(LoanTransactionsPreviewProvider::class) loanTransactionsState: LoanTransactionsState,
) {
    LoanTransactionsScreen(
        state = loanTransactionsState,
        onAction = {},
        onExportClick = {},
        navigateBack = {},
    )
}
