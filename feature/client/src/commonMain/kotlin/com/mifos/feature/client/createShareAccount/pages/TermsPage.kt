/*
 * Copyright 2025 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.feature.client.createShareAccount.pages

import androidclient.feature.client.generated.resources.Res
import androidclient.feature.client.generated.resources.share_account_back
import androidclient.feature.client.generated.resources.share_account_detail_date_cancel
import androidclient.feature.client.generated.resources.share_account_detail_date_select
import androidclient.feature.client.generated.resources.share_account_next
import androidclient.feature.client.generated.resources.share_account_terms
import androidclient.feature.client.generated.resources.share_account_terms_allow_dividends
import androidclient.feature.client.generated.resources.share_account_terms_application_date
import androidclient.feature.client.generated.resources.share_account_terms_currency
import androidclient.feature.client.generated.resources.share_account_terms_current_price
import androidclient.feature.client.generated.resources.share_account_terms_default_savings_account
import androidclient.feature.client.generated.resources.share_account_terms_frequency
import androidclient.feature.client.generated.resources.share_account_terms_lock_in_period
import androidclient.feature.client.generated.resources.share_account_terms_total_shares
import androidclient.feature.client.generated.resources.share_account_terms_type
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SelectableDates
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.mifos.core.designsystem.component.MifosDatePickerTextField
import com.mifos.core.designsystem.component.MifosOutlinedTextField
import com.mifos.core.designsystem.component.MifosTextFieldDropdown
import com.mifos.core.designsystem.theme.DesignToken
import com.mifos.core.designsystem.theme.MifosTypography
import com.mifos.core.ui.components.MifosTwoButtonRow
import com.mifos.feature.client.createShareAccount.ShareAccountAction
import com.mifos.feature.client.createShareAccount.ShareAccountState
import org.jetbrains.compose.resources.stringResource
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalMaterial3Api::class, ExperimentalTime::class)
@Composable
fun TermsPage(
    state: ShareAccountState,
    onAction: (ShareAccountAction) -> Unit,
    modifier: Modifier = Modifier,
) {
    val applicationDatePickerState = rememberDatePickerState(
        initialSelectedDateMillis = Clock.System.now().toEpochMilliseconds(),
        selectableDates = object : SelectableDates {
            override fun isSelectableDate(utcTimeMillis: Long): Boolean {
                return utcTimeMillis >= Clock.System.now().toEpochMilliseconds().minus(86_400_000L)
            }
        },
    )

    if (state.showApplicationDatePicker) {
        DatePickerDialog(
            onDismissRequest = {
//                onAction(ShareAccountAction.OnOpenDatePicker(state = false))
            },
            confirmButton = {
                TextButton(
                    onClick = {
//                        onAction(ShareAccountAction.OnOpenDatePicker(state = false))
                        applicationDatePickerState.selectedDateMillis?.let {
//                            onAction(
//                                ShareAccountAction.OnDateChange(
//                                    DateHelper.getDateAsStringFromLong(it),
//                                ),
//                            )
                        }
                    },
                ) { Text(stringResource(Res.string.share_account_detail_date_select)) }
            },
            dismissButton = {
                TextButton(
                    onClick = {
//                        onAction(ShareAccountAction.OnOpenDatePicker(state = false))
                    },
                ) { Text(stringResource(Res.string.share_account_detail_date_cancel)) }
            },
        ) {
            DatePicker(state = applicationDatePickerState)
        }
    }

    Column(modifier = Modifier.fillMaxSize().padding(bottom = DesignToken.padding.large)) {
        Column(
            modifier = modifier.weight(1f).verticalScroll(rememberScrollState()),
        ) {
            Text(
                text = stringResource(Res.string.share_account_terms),
                style = MifosTypography.labelLargeEmphasized,
            )
            Spacer(Modifier.height(DesignToken.padding.large))

            MifosTextFieldDropdown(
                value = state.currency.orEmpty(),
                onValueChanged = {},
                onOptionSelected = { index, value ->
//                    onAction(ShareAccountAction.OnShareProductChange(index))
                },
                options = state.productOption.map {
                    it.name
                },
                label = stringResource(Res.string.share_account_terms_currency),
                errorMessage = state.shareProductError?.let { stringResource(it) },
            )
            MifosOutlinedTextField(
                value = state.currentPrice.toString(),
                onValueChange = {
//                    onAction(ShareAccountAction.OnExternalIdChange(it))
                },
                label = stringResource(Res.string.share_account_terms_current_price),
            )
            Spacer(Modifier.height(DesignToken.padding.large))
            MifosOutlinedTextField(
                value = state.totalShares.toString(),
                onValueChange = {
//                    onAction(ShareAccountAction.OnExternalIdChange(it))
                },
                label = stringResource(Res.string.share_account_terms_total_shares),
            )
            Spacer(Modifier.height(DesignToken.padding.large))
            MifosTextFieldDropdown(
                value = if (state.savingsAccountIdx == null) {
                    ""
                } else {
                    state.productOption[state.savingsAccountIdx].name
                },
                onValueChanged = {},
                onOptionSelected = { index, value ->
//                    onAction(ShareAccountAction.OnShareProductChange(index))
                },
                options = state.productOption.map {
                    it.name
                },
                label = stringResource(Res.string.share_account_terms_default_savings_account),
                errorMessage = state.savingsAccountError?.let { stringResource(it) },
            )
            MifosDatePickerTextField(
                value = state.applicationDate,
                label = stringResource(Res.string.share_account_terms_application_date),
                openDatePicker = {
//                    onAction(ShareAccountAction.OnOpenDatePicker(true))
                },
            )
            Text(
                text = stringResource(Res.string.share_account_terms_allow_dividends),
                style = MifosTypography.labelLargeEmphasized,
            )
            Spacer(Modifier.height(DesignToken.padding.large))
            MifosOutlinedTextField(
                value = state.minActivePeriodFreq.toString(),
                onValueChange = {
//                    onAction(ShareAccountAction.OnExternalIdChange(it))
                },
                label = stringResource(Res.string.share_account_terms_frequency),
            )
            Spacer(Modifier.height(DesignToken.padding.large))
            MifosTextFieldDropdown(
                value = if (state.minActivePeriodFreqTypeIdx == null) {
                    ""
                } else {
                    state.productOption[state.minActivePeriodFreqTypeIdx].name
                },
                onValueChanged = {},
                onOptionSelected = { index, value ->
//                    onAction(ShareAccountAction.OnShareProductChange(index))
                },
                options = state.productOption.map {
                    it.name
                },
                label = stringResource(Res.string.share_account_terms_type),
                errorMessage = state.minActivePeriodFreqTypeError?.let { stringResource(it) },
            )

            Text(
                text = stringResource(Res.string.share_account_terms_lock_in_period),
                style = MifosTypography.labelLargeEmphasized,
            )
            Spacer(Modifier.height(DesignToken.padding.large))

            MifosOutlinedTextField(
                value = state.lockInPeriodFreq.toString(),
                onValueChange = {
//                    onAction(ShareAccountAction.OnExternalIdChange(it))
                },
                label = stringResource(Res.string.share_account_terms_frequency),
            )
            Spacer(Modifier.height(DesignToken.padding.large))

            MifosTextFieldDropdown(
                value = if (state.minActivePeriodFreqTypeIdx == null) {
                    ""
                } else {
                    state.productOption[state.minActivePeriodFreqTypeIdx].name
                },
                onValueChanged = {},
                onOptionSelected = { index, value ->
//                    onAction(ShareAccountAction.OnShareProductChange(index))
                },
                options = state.productOption.map {
                    it.name
                },
                label = stringResource(Res.string.share_account_terms_type),
                errorMessage = state.lockInPeriodFreqTypeError?.let { stringResource(it) },
            )

        }
        MifosTwoButtonRow(
            firstBtnText = stringResource(Res.string.share_account_back),
            secondBtnText = stringResource(Res.string.share_account_next),
            onFirstBtnClick = {
                onAction(ShareAccountAction.NavigateBack)
            },
            onSecondBtnClick = {
                onAction(ShareAccountAction.NextStep)
            },
            modifier = Modifier.padding(top = DesignToken.padding.small),
        )
    }
}
