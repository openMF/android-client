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
import androidclient.feature.client.generated.resources.feature_share_account_back
import androidclient.feature.client.generated.resources.feature_share_account_detail_date_cancel
import androidclient.feature.client.generated.resources.feature_share_account_detail_date_select
import androidclient.feature.client.generated.resources.feature_share_account_detail_external_id
import androidclient.feature.client.generated.resources.feature_share_account_detail_product_name
import androidclient.feature.client.generated.resources.feature_share_account_detail_submission_date
import androidclient.feature.client.generated.resources.feature_share_account_details
import androidclient.feature.client.generated.resources.feature_share_account_next
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
import com.mifos.core.common.utils.DateHelper
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
fun DetailsPage(
    state: ShareAccountState,
    onAction: (ShareAccountAction) -> Unit,
    modifier: Modifier = Modifier,
) {
    val submissionDatePickerState = rememberDatePickerState(
        initialSelectedDateMillis = Clock.System.now().toEpochMilliseconds(),
        selectableDates = object : SelectableDates {
            override fun isSelectableDate(utcTimeMillis: Long): Boolean {
                return utcTimeMillis <= Clock.System.now().toEpochMilliseconds()
            }
        },
    )

    if (state.showSubmissionDatePicker) {
        DatePickerDialog(
            onDismissRequest = {
                onAction(ShareAccountAction.OnOpenSubmissionDatePicker(state = false))
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        onAction(ShareAccountAction.OnOpenSubmissionDatePicker(state = false))
                        submissionDatePickerState.selectedDateMillis?.let {
                            onAction(
                                ShareAccountAction.OnSubmissionDateChange(
                                    DateHelper.getDateAsStringFromLong(it),
                                ),
                            )
                        }
                    },
                ) { Text(stringResource(Res.string.feature_share_account_detail_date_select)) }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        onAction(ShareAccountAction.OnOpenSubmissionDatePicker(state = false))
                    },
                ) { Text(stringResource(Res.string.feature_share_account_detail_date_cancel)) }
            },
        ) {
            DatePicker(state = submissionDatePickerState)
        }
    }

    Column(modifier = Modifier.fillMaxSize().padding(bottom = DesignToken.padding.large)) {
        Column(
            modifier = modifier.weight(1f).verticalScroll(rememberScrollState()),
        ) {
            Text(
                text = stringResource(Res.string.feature_share_account_details),
                style = MifosTypography.labelLargeEmphasized,
            )
            Spacer(Modifier.height(DesignToken.padding.large))

            MifosTextFieldDropdown(
                value = if (state.shareProductIndex == null) {
                    ""
                } else {
                    state.productOption[state.shareProductIndex].name
                },
                onValueChanged = {},
                onOptionSelected = { index, value ->
                    onAction(ShareAccountAction.OnShareProductChange(index))
                },
                options = state.productOption.map {
                    it.name
                },
                label = stringResource(Res.string.feature_share_account_detail_product_name),
                errorMessage = state.shareProductError?.let { stringResource(it) },
            )
            MifosDatePickerTextField(
                value = state.submissionDate,
                label = stringResource(Res.string.feature_share_account_detail_submission_date),
                openDatePicker = {
                    onAction(ShareAccountAction.OnOpenSubmissionDatePicker(true))
                },
            )
            Spacer(Modifier.height(DesignToken.padding.large))
            MifosOutlinedTextField(
                value = state.externalId ?: "",
                onValueChange = {
                    onAction(ShareAccountAction.OnExternalIdChange(it))
                },
                label = stringResource(Res.string.feature_share_account_detail_external_id),
            )
            Spacer(Modifier.height(DesignToken.padding.large))
        }
        MifosTwoButtonRow(
            firstBtnText = stringResource(Res.string.feature_share_account_back),
            secondBtnText = stringResource(Res.string.feature_share_account_next),
            onFirstBtnClick = {
                onAction(ShareAccountAction.NavigateBack)
            },
            onSecondBtnClick = {
                onAction(ShareAccountAction.OnDetailNext)
            },
            modifier = Modifier.padding(top = DesignToken.padding.small),
        )
    }
}
