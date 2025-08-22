/*
 * Copyright 2025 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.feature.savings.savingsAccountTransactionReceipt

import androidclient.feature.savings.generated.resources.Res
import androidclient.feature.savings.generated.resources.feature_savings_transaction_view_receipt
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.mifos.core.designsystem.component.MifosCircularProgress
import com.mifos.core.designsystem.component.MifosSweetError
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

/**
 * Created by Arin Yadav on 20/08/2025
 */
@Composable
fun SavingsAccountTransactionReceiptScreen(
    transactionId: Int,
    viewModel: SavingsAccountTransactionReceiptViewModel = koinViewModel(),
) {
    val uiState by viewModel.savingsAccountTransactionReceiptUiState.collectAsState()

    when (val uiState = uiState) {
        is SavingsAccountTransactionReceiptUiState.Idle -> {
            Button(
                contentPadding = PaddingValues(),
                onClick = { viewModel.loadReceipt(transactionId) },
                content = {
                    Text(
                        modifier = Modifier
                            .padding(horizontal = 20.dp),
                        text = stringResource(Res.string.feature_savings_transaction_view_receipt),
                        style = MaterialTheme.typography.bodyLarge,
                    )
                },
            )
        }

        is SavingsAccountTransactionReceiptUiState.ShowError -> {
            MifosSweetError(
                message = uiState.message,
                onclick = { viewModel.loadReceipt(transactionId) },
            )
        }

        is SavingsAccountTransactionReceiptUiState.ShowProgressbar -> {
            MifosCircularProgress()
        }

        is SavingsAccountTransactionReceiptUiState.ShowReceipt -> {
            PdfViewer(uiState.receipt)
            viewModel.makeIdle()
        }
    }
}

@Composable
expect fun PdfViewer(pdfBytes: ByteArray)
