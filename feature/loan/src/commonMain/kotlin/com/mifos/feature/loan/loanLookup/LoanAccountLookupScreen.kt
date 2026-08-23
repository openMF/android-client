/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mifos-x-field-officer-app/blob/master/LICENSE.md
 */
package com.mifos.feature.loan.loanLookup

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import kotlinx.serialization.Serializable
import kpt.core.base.designsystem.KptTheme
import kpt.feature.loan.generated.resources.Res
import kpt.feature.loan.generated.resources.feature_loan_lookup_account_label
import kpt.feature.loan.generated.resources.feature_loan_lookup_hint
import kpt.feature.loan.generated.resources.feature_loan_lookup_title
import kpt.feature.loan.generated.resources.feature_loan_lookup_view
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@Serializable
data object LoanAccountLookupRoute

fun NavController.navigateToLoanAccountLookup(navOptions: NavOptions? = null) =
    navigate(LoanAccountLookupRoute, navOptions)

/**
 * Loan-account lookup — the officer's entry point into the loan vertical from the home board.
 *
 * The loan screens are keyed by loan account number, and (until the client feature is wired,
 * offline-first-template-migration sub-plan 21) loans are not reachable through a client list.
 * This lightweight lookup lets the officer open any loan account's transactions directly by
 * number, and is the reachable entry the pilot (sub-plan 04 T4/T5) uses to exercise the
 * `loanTransaction` Store5 offline-render vertical on device.
 */
fun NavGraphBuilder.loanAccountLookupScreen(
    onViewTransactions: (Int) -> Unit,
) {
    composable<LoanAccountLookupRoute> {
        LoanAccountLookupScreen(onViewTransactions = onViewTransactions)
    }
}

@Composable
internal fun LoanAccountLookupScreen(
    onViewTransactions: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    var input by remember { mutableStateOf("") }
    val loanId = input.toIntOrNull()

    Scaffold(modifier = modifier) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Text(
                text = stringResource(Res.string.feature_loan_lookup_title),
                style = MaterialTheme.typography.titleLarge,
            )
            Text(
                text = stringResource(Res.string.feature_loan_lookup_hint),
                style = MaterialTheme.typography.bodyMedium,
            )
            OutlinedTextField(
                value = input,
                onValueChange = { new -> input = new.filter(Char::isDigit) },
                label = { Text(stringResource(Res.string.feature_loan_lookup_account_label)) },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth(),
            )
            Button(
                onClick = { loanId?.let(onViewTransactions) },
                enabled = loanId != null,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text(stringResource(Res.string.feature_loan_lookup_view))
            }
        }
    }
}

/**
 * Reference @Preview for the device-free CMP render tier (SCREENSHOT_TEST.md CMP-PRIMARY) —
 * auto-discovered by `CommonComposablePreviewScanner` and rendered off `desktopTest` via
 * `verifyRoborazziDesktop`. Locks the loan-account lookup (the officer home's entry into the
 * loan vertical) render.
 */
@Preview
@Composable
internal fun LoanAccountLookupScreenPreview() {
    KptTheme {
        LoanAccountLookupScreen(onViewTransactions = {})
    }
}
