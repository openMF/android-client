/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mifos-x-field-officer-app/blob/master/LICENSE.md
 */
@file:OptIn(ExperimentalFoundationApi::class)

package com.mifos.feature.individualCollectionSheet.individualCollectionSheetDetails

import androidclient.feature.collectionsheet.generated.resources.Res
import androidclient.feature.collectionsheet.generated.resources.feature_collection_sheet_failed_to_save_collection_sheet
import androidclient.feature.collectionsheet.generated.resources.feature_collection_sheet_ic_dp_placeholder
import androidclient.feature.collectionsheet.generated.resources.feature_collection_sheet_individual_collection_details
import androidclient.feature.collectionsheet.generated.resources.feature_collection_sheet_save_collection_sheet_success
import androidclient.feature.collectionsheet.generated.resources.feature_collection_sheet_total_charges
import androidclient.feature.collectionsheet.generated.resources.feature_collection_sheet_total_due
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color.Companion.LightGray
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mifos.core.designsystem.component.MifosScaffold
import com.mifos.core.designsystem.component.MifosSweetError
import com.mifos.core.designsystem.icon.MifosIcons
import com.mifos.core.designsystem.theme.DesignToken
import com.mifos.core.model.objects.account.loan.PaymentTypeOptions
import com.mifos.core.model.objects.collectionsheets.LoanAndClientName
import com.mifos.core.network.model.IndividualCollectionSheetPayload
import com.mifos.core.ui.components.MifosProgressIndicator
import com.mifos.core.ui.util.DevicePreview
import com.mifos.room.entities.collectionsheet.ClientCollectionSheet
import com.mifos.room.entities.collectionsheet.IndividualCollectionSheet
import com.mifos.room.entities.noncore.BulkRepaymentTransactions
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import template.core.base.designsystem.theme.KptTheme

@Composable
internal fun IndividualCollectionSheetDetailsScreen(
    onBackPressed: () -> Unit,
    submit: (Int, IndividualCollectionSheetPayload, List<String>, LoanAndClientName, List<PaymentTypeOptions>, Int) -> Unit,
    viewModel: IndividualCollectionSheetDetailsViewModel = koinViewModel(),
) {
    val state by viewModel.individualCollectionSheetDetailsUiState.collectAsStateWithLifecycle()
    val loansAndClientNames =
        viewModel.filterLoanAndClientNames(viewModel.sheet.clients ?: emptyList())

    IndividualCollectionSheetDetailsScreen(
        sheet = viewModel.sheet,
        loansAndClientNames = loansAndClientNames,
        state = state,
        onBackPressed = onBackPressed,
        onRetry = {},
        submit = submit,
        onSave = {
            viewModel.submitIndividualCollectionSheet(it)
        },
    )
}

@Composable
internal fun IndividualCollectionSheetDetailsScreen(
    sheet: IndividualCollectionSheet,
    loansAndClientNames: List<LoanAndClientName>,
    state: IndividualCollectionSheetDetailsUiState,
    onBackPressed: () -> Unit,
    onRetry: () -> Unit,
    submit: (Int, IndividualCollectionSheetPayload, List<String>, LoanAndClientName, List<PaymentTypeOptions>, Int) -> Unit,
    modifier: Modifier = Modifier,
    onSave: (IndividualCollectionSheetPayload) -> Unit,
) {
    val scope = rememberCoroutineScope()
    val snackbarHostState by remember { mutableStateOf(SnackbarHostState()) }
    var payload by rememberSaveable { mutableStateOf(IndividualCollectionSheetPayload()) }
    var showLoading by rememberSaveable { mutableStateOf(false) }
    var showError by rememberSaveable { mutableStateOf(false) }

    LaunchedEffect(key1 = loansAndClientNames) {
        payload = IndividualCollectionSheetPayload()
        for (loanAndClientName in loansAndClientNames) {
            val loanCollectionSheet = loanAndClientName.loan
            if (loanCollectionSheet != null) {
                payload.bulkRepaymentTransactions.add(
                    BulkRepaymentTransactions(
                        loanCollectionSheet.loanId,
                        loanCollectionSheet.totalDue +
                            loanCollectionSheet.chargesDue,
                    ),
                )
            }
        }
    }

    when (state) {
        is IndividualCollectionSheetDetailsUiState.Error -> {
            showLoading = false
            showError = true
        }

        is IndividualCollectionSheetDetailsUiState.Loading -> showLoading = true

        is IndividualCollectionSheetDetailsUiState.SavedSuccessfully -> {
            showLoading = false
            val message = stringResource(Res.string.feature_collection_sheet_save_collection_sheet_success)
            scope.launch {
                snackbarHostState.showSnackbar(message = message)
            }
        }

        IndividualCollectionSheetDetailsUiState.Empty -> Unit
    }

    MifosScaffold(
        modifier = modifier,
        onBackPressed = onBackPressed,
        title = stringResource(
            Res.string
                .feature_collection_sheet_individual_collection_details,
        ),
        actions = {
            IconButton(
                onClick = {
                    onSave(payload)
                },
            ) {
                Icon(
                    imageVector = MifosIcons.Save,
                    contentDescription = null,
                )
            }
        },
        snackbarHostState = snackbarHostState,
    ) { paddingValues ->
        Column(modifier = Modifier.padding(paddingValues)) {
            if (showLoading) {
                MifosProgressIndicator()
            } else if (showError) {
                MifosSweetError(
                    message = stringResource(
                        Res.string.feature_collection_sheet_failed_to_save_collection_sheet,
                    ),
                ) {
                    onRetry()
                }
            } else {
                LazyColumn {
                    sheet.clients?.toList()?.let {
                        itemsIndexed(it) { index, client ->
                            IndividualCollectionSheetItem(
                                client = client,
                                index = index,
                                onClick = {
                                    sheet.paymentTypeOptions?.let { paymentTypeOptions ->
                                        submit(
                                            index,
                                            payload,
                                            paymentTypeOptions.map { paymentTypeOption -> paymentTypeOption.name.toString() },
                                            loansAndClientNames[index],
                                            paymentTypeOptions.toList(),
                                            client.clientId,
                                        )
                                    }
                                },
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun IndividualCollectionSheetItem(
    client: ClientCollectionSheet,
    index: Int,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    OutlinedCard(
        modifier = modifier
            .padding(DesignToken.padding.dp6)
            .combinedClickable(
                onClick = {
                    onClick()
                },
                onLongClick = {},
            ),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    horizontal = KptTheme.spacing.md,
                    vertical = KptTheme.spacing.lg,
                ),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Image(
                modifier = Modifier
                    .size(DesignToken.sizes.topBarStandardHeight)
                    .clip(CircleShape)
                    .border(width = DesignToken.spacing.dp1, LightGray, shape = CircleShape),
                painter = painterResource(Res.drawable.feature_collection_sheet_ic_dp_placeholder),
                contentDescription = "collection sheet placeholder",
            )
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(start = KptTheme.spacing.md),
            ) {
                client.clientName?.let {
                    Text(
                        text = it,
                        style = KptTheme.typography.bodyLarge,

                    )
                }
                Row {
                    Text(
                        text = stringResource(Res.string.feature_collection_sheet_total_due),
                        style = KptTheme.typography.bodyMedium,
                    )
                    Spacer(modifier = Modifier.width(KptTheme.spacing.md))
                    Text(
                        text = client.loans?.get(index)?.totalDue.toString(),
                        style = KptTheme.typography.bodyMedium,
                    )
                }
                Row {
                    Text(
                        text = stringResource(Res.string.feature_collection_sheet_total_charges),
                        style = KptTheme.typography.bodyMedium,
                    )
                    Spacer(modifier = Modifier.width(KptTheme.spacing.md))
                    Text(
                        text = client.loans?.get(index)?.chargesDue.toString(),
                        style = KptTheme.typography.bodyMedium,
                    )
                }
                Text(
                    text = "${client.loans?.get(index)?.productShortName} (#${
                        client.loans?.get(
                            index,
                        )?.productShortName
                    })",
                    style = KptTheme.typography.bodyMedium,
                )
            }
            Icon(
                imageVector = MifosIcons.ArrowForward,
                contentDescription = null,
            )
        }
    }
}

@DevicePreview
@Composable
private fun IndividualCollectionSheetDetailsScreenEmptyPreview() {
    IndividualCollectionSheetDetailsScreen(
        sheet = IndividualCollectionSheet(),
        loansAndClientNames = emptyList(),
        state = IndividualCollectionSheetDetailsUiState.Empty,
        onBackPressed = {},
        onRetry = {},
        submit = { _, _, _, _, _, _ -> },
        onSave = {},
    )
}

@DevicePreview
@Composable
private fun IndividualCollectionSheetDetailsScreenErrorPreview() {
    IndividualCollectionSheetDetailsScreen(
        sheet = IndividualCollectionSheet(),
        loansAndClientNames = emptyList(),
        state = IndividualCollectionSheetDetailsUiState.Error(stringResource(Res.string.feature_collection_sheet_failed_to_save_collection_sheet)),
        onBackPressed = {},
        onRetry = {},
        submit = { _, _, _, _, _, _ -> },
        onSave = {},
    )
}

@DevicePreview
@Composable
private fun IndividualCollectionSheetDetailsScreenLoadingPreview() {
    IndividualCollectionSheetDetailsScreen(
        sheet = IndividualCollectionSheet(),
        loansAndClientNames = emptyList(),
        state = IndividualCollectionSheetDetailsUiState.Loading,
        onBackPressed = {},
        onRetry = {},
        submit = { _, _, _, _, _, _ -> },
        onSave = {},
    )
}

@DevicePreview
@Composable
private fun IndividualCollectionSheetDetailsScreenSuccessPreview() {
    IndividualCollectionSheetDetailsScreen(
        sheet = IndividualCollectionSheet(),
        loansAndClientNames = emptyList(),
        state = IndividualCollectionSheetDetailsUiState.SavedSuccessfully,
        onBackPressed = {},
        onRetry = {},
        submit = { _, _, _, _, _, _ -> },
        onSave = {},
    )
}
