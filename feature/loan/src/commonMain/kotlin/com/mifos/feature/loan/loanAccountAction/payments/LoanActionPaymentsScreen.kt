/*
 * Copyright 2026 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mifos-x-field-officer-app/blob/master/LICENSE.md
 */
package com.mifos.feature.loan.loanAccountAction.payments

import androidclient.feature.loan.generated.resources.Res
import androidclient.feature.loan.generated.resources.feature_loan_header_payments
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.mifos.core.designsystem.theme.DesignToken
import com.mifos.core.designsystem.theme.MifosTypography
import com.mifos.core.ui.components.MifosBreadcrumbNavBar
import com.mifos.core.ui.components.MifosRowCard
import com.mifos.core.ui.util.EventsEffect
import com.mifos.core.ui.util.TextUtil
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import template.core.base.designsystem.theme.KptTheme

@Composable
internal fun LoanPaymentsActionScreen(
    navController: NavController,
    onNavigateBack: () -> Unit,
    onGoodwillCreditClick: () -> Unit,
    onInterestPaymentWaiverClick: () -> Unit,
    onPaymentRefundClick: () -> Unit,
    onMerchantIssuedRefundClick: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: LoanPaymentsActionViewModel = koinViewModel(),
) {
    val state by viewModel.stateFlow.collectAsStateWithLifecycle()

    EventsEffect(viewModel.eventFlow) { event ->
        when (event) {
            LoanPaymentsActionEvent.NavigateBack -> onNavigateBack()
            LoanPaymentsActionEvent.NavigateToGoodwillCredit -> onGoodwillCreditClick()
            LoanPaymentsActionEvent.NavigateToInterestPaymentWaiver -> onInterestPaymentWaiverClick()
            LoanPaymentsActionEvent.NavigateToPaymentRefund -> onPaymentRefundClick()
            LoanPaymentsActionEvent.NavigateToMerchantIssuedRefund -> onMerchantIssuedRefundClick()
        }
    }

    Column(modifier = modifier.fillMaxSize()) {
        MifosBreadcrumbNavBar(navController)

        LoanPaymentsActionContent(
            state = state,
            onAction = remember(viewModel) { { viewModel.trySendAction(it) } },
        )
    }
}

@Composable
private fun LoanPaymentsActionContent(
    state: LoanPaymentsActionState,
    onAction: (LoanPaymentsAction) -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = KptTheme.spacing.md),
    ) {
        item {
            Text(
                text = stringResource(Res.string.feature_loan_header_payments),
                style = MifosTypography.labelMediumEmphasized,
            )

            Spacer(Modifier.height(DesignToken.padding.medium))
        }

        items(
            items = state.actions,
            key = { it.title },
        ) { item ->
            MifosRowCard(
                title = stringResource(item.title),
                imageVector = item.icon,
                leftValues = listOf(
                    TextUtil(
                        text = stringResource(item.subTitle),
                        style = MifosTypography.bodySmall,
                        color = KptTheme.colorScheme.secondary,
                    ),
                ),
                rightValues = emptyList(),
                modifier = Modifier
                    .clickable {
                        onAction(
                            LoanPaymentsAction.OnActionClickLoan(item),
                        )
                    }
                    .padding(vertical = DesignToken.padding.medium),
            )
        }

        item {
            Spacer(Modifier.height(KptTheme.spacing.md))
        }
    }
}
