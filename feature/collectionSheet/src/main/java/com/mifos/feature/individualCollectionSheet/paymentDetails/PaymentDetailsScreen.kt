/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.feature.individualCollectionSheet.paymentDetails

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import coil.request.ImageResult
import com.mifos.core.designsystem.component.MifosOutlinedTextField
import com.mifos.core.designsystem.component.MifosTextFieldDropdown
import com.mifos.core.designsystem.theme.BluePrimary
import com.mifos.core.designsystem.theme.BluePrimaryDark
import com.mifos.core.model.BulkRepaymentTransactions
import com.mifos.core.network.model.IndividualCollectionSheetPayload
import com.mifos.core.objects.accounts.loan.PaymentTypeOptions
import com.mifos.core.objects.collectionsheet.LoanAndClientName
import com.mifos.feature.collection_sheet.R

/**
 * Created by Pronay Sarker on 24/08/2024 (4:20 PM)
 */
@Composable
internal fun PaymentDetailsScreenRoute(
    viewModel: PaymentDetailsViewModel = hiltViewModel(),
) {
    PaymentsDetailsScreen(
        clientId = viewModel.clientId,
        position = viewModel.position,
        payload = viewModel.individualCollectionSheetPayload,
        loanAndClientNameItem = viewModel.loanAndClientName,
        paymentTypeOptionList = viewModel.paymentTypeOptionsName,
        paymentTypeOptions = viewModel.paymentTypeOptions,
        getClientImage = { viewModel.getClientImageUrl(it) },
    )
}

@Composable
internal fun PaymentsDetailsScreen(
    clientId: Int,
    position: Int,
    payload: IndividualCollectionSheetPayload,
    loanAndClientNameItem: LoanAndClientName,
    paymentTypeOptionList: List<String>,
    paymentTypeOptions: List<PaymentTypeOptions>,
    modifier: Modifier = Modifier,
    getClientImage: (Int) -> ImageResult?,
) {
    val loanCollectionSheetItem = loanAndClientNameItem?.loan
    val scrollState = rememberScrollState()

    val bulkRepaymentTransactions by rememberSaveable { mutableStateOf(BulkRepaymentTransactions()) }
    var totalDues: String by rememberSaveable {
        mutableStateOf(
            loanCollectionSheetItem?.totalDue?.toString() ?: "0.0",
        )
    }
    var paymentType by rememberSaveable { mutableStateOf("") }
    val totalCharges by rememberSaveable { mutableStateOf("") }
    var accountNumber by rememberSaveable { mutableStateOf("") }
    var chequeNumber by rememberSaveable { mutableStateOf("") }
    var routingCode by rememberSaveable { mutableStateOf("") }
    var receiptNumber by rememberSaveable { mutableStateOf("") }
    var bankNumber by rememberSaveable { mutableStateOf("") }

    var showAdditionalDetails by rememberSaveable { mutableStateOf(true) }
    var noPaymentVisibility by rememberSaveable { mutableStateOf(true) }

    fun onSaveAdditionalItem(transaction: BulkRepaymentTransactions, position: Int) {
        payload!!.bulkRepaymentTransactions[position] = transaction
    }

    fun onShowSheetMandatoryItem(transaction: BulkRepaymentTransactions, position: Int) {
        payload!!.bulkRepaymentTransactions[position] = transaction
    }

    fun cancelAdditional() { // done
        val charge1: Double =
            if (totalCharges.isNotEmpty()) totalCharges.toDoubleOrNull() ?: 0.0 else 0.0
        val charge2: Double = if (totalDues.isNotEmpty()) totalDues.toDoubleOrNull() ?: 0.0 else 0.0

        bulkRepaymentTransactions.loanId = loanAndClientNameItem?.loan!!.loanId
        bulkRepaymentTransactions.transactionAmount = charge1 + charge2
        showAdditionalDetails = false
        bulkRepaymentTransactions.paymentTypeId = null
        bulkRepaymentTransactions.accountNumber = null
        bulkRepaymentTransactions.checkNumber = null
        bulkRepaymentTransactions.routingCode = null
        bulkRepaymentTransactions.receiptNumber = null
        bulkRepaymentTransactions.bankNumber = null
        onSaveAdditionalItem(bulkRepaymentTransactions, position)
    }

    fun saveAdditional() {
        var isAnyDetailNull = false

        bulkRepaymentTransactions.loanId = loanAndClientNameItem?.loan!!.loanId
        val charge1: Double =
            if (totalCharges.isNotEmpty()) totalCharges.toDoubleOrNull() ?: 0.0 else 0.0
        val charge2: Double = if (totalDues.isNotEmpty()) totalDues.toDoubleOrNull() ?: 0.0 else 0.0

        bulkRepaymentTransactions.transactionAmount = (charge1 + charge2)

        if (accountNumber.isNotEmpty()) {
            bulkRepaymentTransactions.accountNumber = accountNumber
        } else {
            isAnyDetailNull = true
        }

        if (chequeNumber.isNotEmpty()) {
            bulkRepaymentTransactions.checkNumber = chequeNumber
        } else {
            isAnyDetailNull = true
        }

        if (routingCode.isNotEmpty()) {
            bulkRepaymentTransactions.routingCode = routingCode
        } else {
            isAnyDetailNull = true
        }

        if (receiptNumber.isNotEmpty()) {
            bulkRepaymentTransactions.receiptNumber = receiptNumber
        } else {
            isAnyDetailNull = true
        }

        if (bankNumber.isNotEmpty()) {
            bulkRepaymentTransactions.bankNumber = bankNumber
        } else {
            isAnyDetailNull = true
        }

        if (!isAnyDetailNull) {
            noPaymentVisibility = false
        }
        onSaveAdditionalItem(bulkRepaymentTransactions, position)
        showAdditionalDetails = false
    }

    LaunchedEffect(key1 = Unit) {
        val defaultBulkRepaymentTransaction = BulkRepaymentTransactions()
        if (loanCollectionSheetItem != null) {
            defaultBulkRepaymentTransaction.loanId = loanCollectionSheetItem.loanId
            defaultBulkRepaymentTransaction.transactionAmount =
                loanCollectionSheetItem.chargesDue + loanCollectionSheetItem.totalDue
        }

        onShowSheetMandatoryItem(defaultBulkRepaymentTransaction, position)
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState),
    ) {
        OutlinedCard(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color.White,
            ),
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,

            ) {
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(end = 16.dp),
                ) {
                    Text(
                        text = loanAndClientNameItem?.clientName ?: "This is Tv name",
                        style = TextStyle(
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                        ),
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "${loanCollectionSheetItem?.productShortName} (#${loanCollectionSheetItem?.accountId})",
                        color = Color.DarkGray.copy(alpha = .7f),
                        style = TextStyle(
                            fontWeight = FontWeight.Bold,
                        ),
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    MifosOutlinedTextField(
                        modifier = Modifier.fillMaxWidth(),
                        value = totalDues,
                        onValueChange = { totalDues = it },
                        label = stringResource(id = R.string.feature_collection_sheet_total_due),
                        error = null,
                        keyboardType = KeyboardType.Number,
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = stringResource(id = R.string.feature_collection_sheet_total_charges) + " : " + loanCollectionSheetItem?.chargesDue,
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Bold,
                    )
                }
                Column(
                    modifier = Modifier
                        .weight(.3f)
                        .fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    AsyncImage(
                        modifier = Modifier.size(60.dp),
                        model = getClientImage(clientId)
                            ?: R.drawable.feature_collection_sheet_ic_dp_placeholder,
                        contentDescription = null,
                        contentScale = ContentScale.FillBounds,
                    )
                }
            }
        }

        Button(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .height(50.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = if (isSystemInDarkTheme()) BluePrimaryDark else BluePrimary,
            ),
            onClick = {
                showAdditionalDetails = !showAdditionalDetails
            },
        ) {
            Text(text = stringResource(id = R.string.feature_collection_sheet_add_payment_detail))
        }

        if (noPaymentVisibility) {
            Text(
                text = stringResource(id = R.string.feature_collection_sheet_no_payment_added),
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(vertical = 16.dp),
                color = Color.Gray,
            )
        }

        if (showAdditionalDetails) {
            OutlinedCard(
                modifier = Modifier.padding(horizontal = 16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White,
                ),
            ) {
                Column(
                    modifier = Modifier.padding(vertical = 16.dp),
                ) {
                    MifosTextFieldDropdown(
                        label = R.string.feature_collection_sheet_payment_type,
                        value = paymentType,
                        onValueChanged = { paymentType = it },
                        onOptionSelected = { index, value ->
                            paymentType = value
                            bulkRepaymentTransactions.paymentTypeId = paymentTypeOptions!![index].id
                        },
                        options = paymentTypeOptionList ?: emptyList(),
                        readOnly = true,
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    MifosOutlinedTextField(
                        value = accountNumber,
                        onValueChange = { accountNumber = it },
                        label = stringResource(id = R.string.feature_collection_sheet_account_number),
                        error = null,
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    MifosOutlinedTextField(
                        value = chequeNumber,
                        onValueChange = { chequeNumber = it },
                        label = stringResource(id = R.string.feature_collection_sheet_cheque_number),
                        error = null,
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    MifosOutlinedTextField(
                        value = routingCode,
                        onValueChange = { routingCode = it },
                        label = stringResource(id = R.string.feature_collection_sheet_routing_code),
                        error = null,
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    MifosOutlinedTextField(
                        value = receiptNumber,
                        onValueChange = { receiptNumber = it },
                        label = stringResource(id = R.string.feature_collection_sheet_receipt_number),
                        error = null,
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    MifosOutlinedTextField(
                        value = bankNumber,
                        onValueChange = { bankNumber = it },
                        label = stringResource(id = R.string.feature_collection_sheet_bank_number),
                        error = null,
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    ButtonRow(cancelAdditional = { cancelAdditional() }) {
                        saveAdditional()
                    }
                }
            }
        }
    }
}

@Composable
private fun ButtonRow(
    cancelAdditional: () -> Unit,
    modifier: Modifier = Modifier,
    saveAdditional: () -> Unit,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Button(
            modifier = Modifier.height(50.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = if (isSystemInDarkTheme()) BluePrimaryDark else BluePrimary,
            ),
            onClick = { cancelAdditional() },
        ) {
            Text(text = stringResource(id = R.string.feature_collection_sheet_cancel))
        }

        Button(
            modifier = Modifier.height(50.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = if (isSystemInDarkTheme()) BluePrimaryDark else BluePrimary,
            ),
            onClick = { saveAdditional() },
        ) {
            Text(text = stringResource(id = R.string.feature_collection_sheet_save))
        }
    }
}

@Composable
@Preview(showBackground = true)
private fun PreviewPaymentDetails(modifier: Modifier = Modifier) {
    PaymentsDetailsScreen(
        modifier = modifier,
        clientId = 0,
        position = 0,
        payload = IndividualCollectionSheetPayload(),
        loanAndClientNameItem = LoanAndClientName(id = 2, loan = null, clientName = ""),
        paymentTypeOptionList = emptyList(),
        paymentTypeOptions = emptyList(),
        getClientImage = { null },
    )
}
