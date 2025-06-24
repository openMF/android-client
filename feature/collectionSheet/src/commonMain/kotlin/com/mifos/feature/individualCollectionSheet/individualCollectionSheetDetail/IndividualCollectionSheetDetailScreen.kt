/*
 * Copyright 2025 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.feature.individualCollectionSheet.individualCollectionSheetDetail

import androidclient.feature.collectionsheet.generated.resources.Res
import androidclient.feature.collectionsheet.generated.resources.feature_collection_sheet_actions
import androidclient.feature.collectionsheet.generated.resources.feature_collection_sheet_add_payment
import androidclient.feature.collectionsheet.generated.resources.feature_collection_sheet_charges
import androidclient.feature.collectionsheet.generated.resources.feature_collection_sheet_client_name
import androidclient.feature.collectionsheet.generated.resources.feature_collection_sheet_deposit_account
import androidclient.feature.collectionsheet.generated.resources.feature_collection_sheet_due_collections
import androidclient.feature.collectionsheet.generated.resources.feature_collection_sheet_due_savings_collections
import androidclient.feature.collectionsheet.generated.resources.feature_collection_sheet_individual_collection_sheet
import androidclient.feature.collectionsheet.generated.resources.feature_collection_sheet_loan_account
import androidclient.feature.collectionsheet.generated.resources.feature_collection_sheet_product_name
import androidclient.feature.collectionsheet.generated.resources.feature_collection_sheet_savings_account
import androidclient.feature.collectionsheet.generated.resources.feature_collection_sheet_total_due
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mifos.core.designsystem.component.MifosButton
import com.mifos.core.designsystem.component.MifosCircularProgress
import com.mifos.core.designsystem.component.MifosScaffold
import com.mifos.room.entities.collectionsheet.IndividualCollectionSheet
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.viewmodel.koinViewModel
import kotlin.math.roundToInt

@Composable
internal fun IndividualCollectionSheetDetailScreen(
    repaymentDate: String,
    collectionSheet: IndividualCollectionSheet,
    viewmodel: IndividualCollectionSheetDetailViewModel = koinViewModel(),
    onAddPayment: (LoanCollectionItem) -> Unit = {},
    onAddSavingsPayment: (SavingsCollectionItem) -> Unit = {},
) {
    val state = viewmodel.individualCollectionSheetDetailUiState.collectAsStateWithLifecycle().value

    LaunchedEffect(collectionSheet) {
        println("DEBUG: LaunchedEffect triggered with collectionSheet")
        try {
            viewmodel.setCollectionSheetData(repaymentDate, collectionSheet)
            println("DEBUG: ViewModel data set successfully")
        } catch (e: Exception) {
            println("DEBUG: Error in LaunchedEffect: ${e.message}")
            println("DEBUG: Full error: ${e.stackTraceToString()}")
        }
    }

    IndividualCollectionSheetDetailScreen(
        state = state,
        onAddPayment = onAddPayment,
        onAddSavingsPayment = onAddSavingsPayment,
    )
}

@Composable
internal fun IndividualCollectionSheetDetailScreen(
    state: IndividualCollectionSheetDetailUiState,
    modifier: Modifier = Modifier,
    onAddPayment: (LoanCollectionItem) -> Unit = {},
    onAddSavingsPayment: (SavingsCollectionItem) -> Unit = {},
) {
    val snackbarHostState = remember { SnackbarHostState() }
    var selectedTabIndex by rememberSaveable { mutableIntStateOf(0) }

    LaunchedEffect(key1 = state.error) {
        state.error?.let { snackbarHostState.showSnackbar(it) }
    }

    MifosScaffold(
        modifier = modifier,
        snackbarHostState = snackbarHostState,
        topBar = {
            Text(
                modifier = Modifier.padding(16.dp),
                text = stringResource(Res.string.feature_collection_sheet_individual_collection_sheet),
                style = MaterialTheme.typography.titleLarge,
            )
        },
    ) { paddingValues ->
        if (state.isLoading) {
            MifosCircularProgress()
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
            ) {
                ParametersSection(
                    repaymentDate = state.repaymentDate,
                    modifier = Modifier.padding(16.dp),
                )

                val tabs = listOf(
                    stringResource(Res.string.feature_collection_sheet_due_collections),
                    stringResource(Res.string.feature_collection_sheet_due_savings_collections),
                )

                TabRow(
                    selectedTabIndex = selectedTabIndex,
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    tabs.forEachIndexed { index, title ->
                        Tab(
                            selected = selectedTabIndex == index,
                            onClick = { selectedTabIndex = index },
                            text = {
                                Text(
                                    text = title,
                                    style = MaterialTheme.typography.bodyMedium,
                                )
                            },
                        )
                    }
                }

                when (selectedTabIndex) {
                    0 -> {
                        state.collectionSheet?.let { collectionSheet ->
                            DueCollectionsContent(
                                collectionSheet = collectionSheet,
                                onAddPayment = onAddPayment,
                                modifier = Modifier.weight(1f),
                            )
                        }
                    }
                    1 -> {
                        state.collectionSheet?.let { collectionSheet ->
                            DueSavingsCollectionsContent(
                                collectionSheet = collectionSheet,
                                onAddSavingsPayment = onAddSavingsPayment,
                                modifier = Modifier.weight(1f),
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ParametersSection(
    repaymentDate: String,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
        ),
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
        ) {
            Text(
                text = "Parameters",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Repayment Date: $repaymentDate",
                style = MaterialTheme.typography.bodyMedium,
            )
        }
    }
}

@Composable
private fun DueCollectionsContent(
    collectionSheet: IndividualCollectionSheet,
    onAddPayment: (LoanCollectionItem) -> Unit,
    modifier: Modifier = Modifier,
) {
    val loanItems = remember(collectionSheet) {
        try {
            collectionSheet.clients?.flatMap { client ->
                println("DEBUG: Processing client: ${client.clientName}, loans count: ${client.loans?.size}")
                client.loans?.mapIndexed { index, loan ->
                    println("DEBUG: Processing loan $index: ${loan.loanId}")
                    val totalDue = (loan.chargesDue ?: 0.0) + (loan.feeDue ?: 0.0)
                    LoanCollectionItem(
                        loanId = loan.loanId ?: 0,
                        accountId = loan.accountId ?: "",
                        clientName = "${client.clientName}(${client.clientId})",
                        productName = "${loan.productShortName ?: ""}(${loan.loanId ?: 0})",
                        totalDue = totalDue,
                        charges = loan.chargesDue ?: 0.0,
                        principalDue = loan.principalDue ?: 0.0,
                        interestDue = loan.interestDue ?: 0.0,
                        feeDue = loan.feeDue ?: 0.0,
                        currencySymbol = loan.currency?.displaySymbol ?: "$",
                    )
                } ?: emptyList()
            } ?: emptyList()
        } catch (e: Exception) {
            println("DEBUG: Error processing loan items: ${e.message}")
            e.printStackTrace()
            emptyList()
        }
    }

    LazyColumn(
        modifier = modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        item {
            DueCollectionsHeader()
        }

        items(loanItems) { loanItem ->
            DueCollectionItemCard(
                loanItem = loanItem,
                onAddPayment = onAddPayment,
            )
        }

        if (loanItems.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = "No due collections found",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
        }
    }
}

@Composable
private fun DueCollectionsHeader() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(
                text = stringResource(Res.string.feature_collection_sheet_loan_account),
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.weight(1f),
            )
            Text(
                text = stringResource(Res.string.feature_collection_sheet_product_name),
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.Center,
            )
            Text(
                text = stringResource(Res.string.feature_collection_sheet_client_name),
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.Center,
            )
            Text(
                text = stringResource(Res.string.feature_collection_sheet_total_due),
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.Center,
            )
            Text(
                text = stringResource(Res.string.feature_collection_sheet_charges),
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.Center,
            )
            Text(
                text = stringResource(Res.string.feature_collection_sheet_actions),
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.Center,
            )
        }
    }
}

@Composable
private fun DueCollectionItemCard(
    loanItem: LoanCollectionItem,
    onAddPayment: (LoanCollectionItem) -> Unit,
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
        ),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = loanItem.accountId,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.weight(1f),
            )
            Text(
                text = loanItem.productName,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.Center,
            )
            Text(
                text = loanItem.clientName,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.Center,
            )
            Text(
                text = "${loanItem.currencySymbol}${loanItem.totalDue.roundToInt()}",
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.Center,
            )
            Text(
                text = "${loanItem.currencySymbol}${{loanItem.charges.roundToInt()}}",
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.Center,
            )
            MifosButton(
                onClick = { onAddPayment(loanItem) },
                modifier = Modifier.weight(1f),
            ) {
                Text(
                    text = stringResource(Res.string.feature_collection_sheet_add_payment),
                    style = MaterialTheme.typography.labelSmall,
                )
            }
        }
    }
}

@Composable
private fun DueSavingsCollectionsContent(
    collectionSheet: IndividualCollectionSheet,
    onAddSavingsPayment: (SavingsCollectionItem) -> Unit,
    modifier: Modifier = Modifier,
) {
    val savingsItems = remember(collectionSheet) {
        collectionSheet.clients?.flatMap { client ->
            client.savings?.map { savings ->
                SavingsCollectionItem(
                    clientName = "${client.clientName}(${client.clientId})",
                    depositAccountType = savings.depositAccountType ?: "",
                    savingsAccountId = "",
                    productName = "",
                    totalDue = 0.0,
                    currencySymbol = "$",
                )
            } ?: emptyList()
        } ?: emptyList()
    }

    LazyColumn(
        modifier = modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        item {
            DueSavingsCollectionsHeader()
        }

        items(savingsItems) { savingsItem ->
            DueSavingsCollectionItemCard(
                savingsItem = savingsItem,
                onAddSavingsPayment = onAddSavingsPayment,
            )
        }

        if (savingsItems.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = "No due savings collections found",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
        }
    }
}

@Composable
private fun DueSavingsCollectionsHeader() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(
                text = stringResource(Res.string.feature_collection_sheet_deposit_account),
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.weight(1f),
            )
            Text(
                text = stringResource(Res.string.feature_collection_sheet_savings_account),
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.Center,
            )
            Text(
                text = stringResource(Res.string.feature_collection_sheet_product_name),
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.Center,
            )
            Text(
                text = stringResource(Res.string.feature_collection_sheet_client_name),
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.Center,
            )
            Text(
                text = stringResource(Res.string.feature_collection_sheet_total_due),
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.Center,
            )
            Text(
                text = stringResource(Res.string.feature_collection_sheet_actions),
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.Center,
            )
        }
    }
}

@Composable
private fun DueSavingsCollectionItemCard(
    savingsItem: SavingsCollectionItem,
    onAddSavingsPayment: (SavingsCollectionItem) -> Unit,
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
        ),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = savingsItem.depositAccountType,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.weight(1f),
            )
            Text(
                text = "0",
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.Center,
            )
            Text(
                text = "0",
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.Center,
            )
            Text(
                text = savingsItem.clientName,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.Center,
            )
            Text(
                text = "${savingsItem.currencySymbol}${savingsItem.totalDue.roundToInt()}",
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.Center,
            )
            MifosButton(
                onClick = { onAddSavingsPayment(savingsItem) },
                modifier = Modifier.weight(1f),
            ) {
                Text(
                    text = stringResource(Res.string.feature_collection_sheet_add_payment),
                    style = MaterialTheme.typography.labelSmall,
                )
            }
        }
    }
}

@Preview
@Composable
private fun IndividualCollectionSheetDetailPreview() {
    IndividualCollectionSheetDetailScreen(
        state = IndividualCollectionSheetDetailUiState(
            repaymentDate = "26-06-2025",
            collectionSheet = IndividualCollectionSheet(),
        ),
    )
}
