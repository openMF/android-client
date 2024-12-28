/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
@file:OptIn(ExperimentalFoundationApi::class)

package com.mifos.feature.individualCollectionSheet.individualCollectionSheetDetails

import android.widget.Toast
import androidx.compose.foundation.ExperimentalFoundationApi
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ChevronRight
import androidx.compose.material3.CardDefaults
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
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.mifos.core.designsystem.component.MifosCircularProgress
import com.mifos.core.designsystem.component.MifosScaffold
import com.mifos.core.designsystem.component.MifosSweetError
import com.mifos.core.designsystem.icon.MifosIcons
import com.mifos.core.designsystem.theme.Black
import com.mifos.core.designsystem.theme.DarkGray
import com.mifos.core.designsystem.theme.LightGray
import com.mifos.core.designsystem.theme.White
import com.mifos.core.model.BulkRepaymentTransactions
import com.mifos.core.`object`.account.loan.PaymentTypeOptions
import com.mifos.core.`object`.collectionsheets.LoanAndClientName
import com.mifos.core.network.model.IndividualCollectionSheetPayload
import com.mifos.core.objects.collectionsheet.ClientCollectionSheet
import com.mifos.core.objects.collectionsheet.IndividualCollectionSheet
import com.mifos.feature.collection_sheet.R

@Composable
internal fun IndividualCollectionSheetDetailsScreen(
    onBackPressed: () -> Unit,
    submit: (Int, IndividualCollectionSheetPayload, List<String>, LoanAndClientName, List<PaymentTypeOptions>, Int) -> Unit,
    viewModel: IndividualCollectionSheetDetailsViewModel = hiltViewModel(),
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
            Toast.makeText(
                LocalContext.current,
                stringResource(id = R.string.feature_collection_sheet_save_collection_sheet_success),
                Toast.LENGTH_SHORT,
            ).show()
        }

        IndividualCollectionSheetDetailsUiState.Empty -> Unit
    }

    MifosScaffold(
        modifier = modifier,
        icon = MifosIcons.arrowBack,
        onBackPressed = onBackPressed,
        title = stringResource(id = R.string.feature_collection_sheet_individual_collection_details),
        actions = {
            IconButton(
                onClick = {
                    onSave(payload)
                },
            ) {
                Icon(
                    imageVector = MifosIcons.save,
                    contentDescription = null,
                )
            }
        },
        snackbarHostState = snackbarHostState,
    ) { paddingValues ->
        Column(modifier = Modifier.padding(paddingValues)) {
            if (showLoading) {
                MifosCircularProgress()
            } else if (showError) {
                MifosSweetError(
                    message = stringResource(
                        id = R.string.feature_collection_sheet_failed_to_save_collection_sheet,
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
            .padding(6.dp)
            .combinedClickable(
                onClick = {
                    onClick()
                },
                onLongClick = {},
            ),
        colors = CardDefaults.cardColors(White),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    start = 16.dp,
                    end = 16.dp,
                    top = 24.dp,
                    bottom = 24.dp,
                ),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            AsyncImage(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .border(width = 1.dp, LightGray, shape = CircleShape),
                model = R.drawable.feature_collection_sheet_ic_dp_placeholder,
                contentDescription = null,
            )
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 16.dp),
            ) {
                client.clientName?.let {
                    Text(
                        text = it,
                        style = TextStyle(
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Normal,
                            fontStyle = FontStyle.Normal,
                            color = Black,
                        ),
                    )
                }
                Row {
                    Text(
                        text = stringResource(id = R.string.feature_collection_sheet_total_due),
                        style = TextStyle(
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Normal,
                            fontStyle = FontStyle.Normal,
                            color = Black,
                        ),
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Text(
                        text = client.loans?.get(index)?.totalDue.toString(),
                        style = TextStyle(
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Normal,
                            fontStyle = FontStyle.Normal,
                            color = DarkGray,
                        ),
                    )
                }
                Row {
                    Text(
                        text = stringResource(id = R.string.feature_collection_sheet_total_charges),
                        style = TextStyle(
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Normal,
                            fontStyle = FontStyle.Normal,
                            color = Black,
                        ),
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Text(
                        text = client.loans?.get(index)?.chargesDue.toString(),
                        style = TextStyle(
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Normal,
                            fontStyle = FontStyle.Normal,
                            color = DarkGray,
                        ),
                    )
                }
                Text(
                    text = "${client.loans?.get(index)?.productShortName} (#${
                        client.loans?.get(
                            index,
                        )?.productShortName
                    })",
                    style = TextStyle(
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Normal,
                        fontStyle = FontStyle.Normal,
                        color = Black,
                    ),
                )
            }
            Icon(
                imageVector = Icons.Rounded.ChevronRight,
                contentDescription = null,
            )
        }
    }
}

class IndividualCollectionSheetDetailsUiStateProvider :
    PreviewParameterProvider<IndividualCollectionSheetDetailsUiState> {

    override val values: Sequence<IndividualCollectionSheetDetailsUiState>
        get() = sequenceOf(
            IndividualCollectionSheetDetailsUiState.Error(R.string.feature_collection_sheet_failed_to_save_collection_sheet),
            IndividualCollectionSheetDetailsUiState.Loading,
            IndividualCollectionSheetDetailsUiState.SavedSuccessfully,
        )
}

@Preview(showBackground = true)
@Composable
private fun IndividualCollectionSheetDetailsScreenPreview(
    @PreviewParameter(IndividualCollectionSheetDetailsUiStateProvider::class)
    state: IndividualCollectionSheetDetailsUiState,
) {
    IndividualCollectionSheetDetailsScreen(
        sheet = IndividualCollectionSheet(),
        loansAndClientNames = emptyList(),
        state = state,
        onBackPressed = {},
        onRetry = {},
        submit = { _, _, _, _, _, _ -> },
        onSave = {},
    )
}
