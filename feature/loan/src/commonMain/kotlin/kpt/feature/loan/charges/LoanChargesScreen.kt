/*
 * Copyright 2026 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See See https://github.com/openMF/kmp-project-template/blob/main/LICENSE
 */
package kpt.feature.loan.charges

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
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.style.TextAlign
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kpt.core.base.designsystem.theme.KptTheme
import kpt.core.base.store.screen.ScreenState
import kpt.core.base.ui.effects.EventsEffect
import kpt.core.base.ui.screen.RefreshableScreenContent
import kpt.core.designsystem.component.AppScaffold
import kpt.core.designsystem.icon.AppIcons
import kpt.core.designsystem.theme.DesignToken
import kpt.core.ui.components.AppEmptyUi
import kpt.feature.loan.generated.resources.Res
import kpt.feature.loan.generated.resources.feature_loan_charge_amount
import kpt.feature.loan.generated.resources.feature_loan_charge_due_date
import kpt.feature.loan.generated.resources.feature_loan_charge_id
import kpt.feature.loan.generated.resources.feature_loan_charge_name
import kpt.feature.loan.generated.resources.feature_loan_loan_charges
import kpt.feature.loan.generated.resources.feature_loan_no_loan_charges
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

/**
 * Carried over from `6b66e8a43:feature/loan/loanCharge/LoanChargeScreen.kt` — one card per charge
 * with its four labelled rows, and the add-charge action in the top bar.
 *
 * ## §6a adaptations
 *
 * The three-case `LoanChargeUiState` becomes [ScreenState] through [ScreenContent], which also
 * removes the hand-rolled `PullToRefreshBox` + `isRefreshing` pair — the stream reports its own
 * refreshing signal, and the original's flag could never be observed true anyway (see the
 * ViewModel).
 *
 * The add-charge "+" is `null`-gated, and `LoanNavigation` now supplies it — the form landed in
 * S4h as its own destination ([kpt.feature.loan.chargeform.LoanChargeFormScreen]), where the
 * original composed it as a bottom sheet inside THIS screen behind a boolean this screen owned.
 * The list needs no callback to learn a charge was added: its stream re-reads when the officer
 * comes back.
 *
 * ## Two hardcoded colours and a hardcoded font size are NOT carried over
 *
 * The detail rows used `Color.Black` for the label and `Color.DarkGray` for the value, plus
 * `TextStyle(fontSize = 16.sp)` built inline. On the dark scheme that is near-black on near-black,
 * so the charge amounts would be unreadable — the same render failure as the repayment header
 * (`repayment-header-hardcoded-colours`), and
 * RULE-IMPL-DESIGN-TOKEN-CONSUMPTION-001 blocks shipping it. Mapped to `onSurface` /
 * `onSurfaceVariant` and `bodyLarge`.
 */
@Composable
internal fun LoanChargesScreen(
    onBackPressed: () -> Unit,
    modifier: Modifier = Modifier,
    onAddCharge: ((Int) -> Unit)? = null,
    viewModel: LoanChargesViewModel = koinViewModel(),
) {
    val charges by viewModel.charges.collectAsStateWithLifecycle()

    EventsEffect(viewModel.eventFlow) { event ->
        when (event) {
            LoanChargesEvent.NavigateBack -> onBackPressed()
            is LoanChargesEvent.AddCharge -> onAddCharge?.invoke(event.loanId)
        }
    }

    LoanChargesContent(
        charges = charges,
        onAction = remember(viewModel) { { viewModel.trySendAction(it) } },
        canAddCharge = onAddCharge != null,
        modifier = modifier,
    )
}

/** Stateless half — rendered in tests and previews without Koin. */
@Composable
internal fun LoanChargesContent(
    charges: ScreenState<List<LoanChargeRow>>,
    onAction: (LoanChargesAction) -> Unit,
    modifier: Modifier = Modifier,
    canAddCharge: Boolean = false,
) {
    AppScaffold(
        title = stringResource(Res.string.feature_loan_loan_charges),
        onBackPressed = { onAction(LoanChargesAction.NavigateBack) },
        modifier = modifier.testTag(LoanChargesTestTags.SCREEN),
        actions = {
            if (canAddCharge) {
                IconButton(
                    onClick = { onAction(LoanChargesAction.OnAddCharge) },
                    modifier = Modifier.testTag(LoanChargesTestTags.ADD),
                ) {
                    Icon(imageVector = AppIcons.Add, contentDescription = null)
                }
            }
        },
    ) { padding ->
        // `RefreshableScreenContent`: the same renderer inside a `PullToRefreshBox`, with the M3
        // indicator driven off the stream's own `freshnessSignal.isRefreshing` rather than a
        // boolean this screen would have to own. The original hand-rolled that boolean and set it
        // true -> false around a call that only LAUNCHED a coroutine, so its spinner could never
        // be observed true (`LoanChargeViewModel.refreshLoanChargeList`, `6b66e8a43`).
        //
        // Usable here only since the `empty` slot became forwardable — the wrapper used to pass
        // the inner `ScreenContent` nothing but `state`/`onRetry`/`content`, so a screen with a
        // branded empty state had to choose between it and the pull gesture
        // (openMF/kmp-project-template#310).
        RefreshableScreenContent(
            state = charges,
            onRefresh = { onAction(LoanChargesAction.OnRefresh) },
            modifier = Modifier.fillMaxSize().padding(padding).testTag(LoanChargesTestTags.CONTENT),
            empty = {
                AppEmptyUi(
                    text = stringResource(Res.string.feature_loan_no_loan_charges),
                    icon = AppIcons.FileTask,
                    modifier = Modifier.testTag(LoanChargesTestTags.EMPTY),
                )
            },
        ) { rows, _ ->
            LazyColumn(modifier = Modifier.testTag(LoanChargesTestTags.LIST)) {
                items(rows, key = { it.id }) { charge -> ChargeCard(charge) }
            }
        }
    }
}

@Composable
private fun ChargeCard(charge: LoanChargeRow) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(KptTheme.spacing.sm)
            .testTag(LoanChargesTestTags.row(charge.id)),
        shape = DesignToken.shapes.none,
        elevation = CardDefaults.cardElevation(defaultElevation = DesignToken.elevation.dp2),
    ) {
        Spacer(Modifier.height(KptTheme.spacing.sm))
        DetailRow(stringResource(Res.string.feature_loan_charge_id), charge.chargeId)
        DetailRow(stringResource(Res.string.feature_loan_charge_name), charge.name)
        DetailRow(stringResource(Res.string.feature_loan_charge_amount), charge.amount)
        DetailRow(stringResource(Res.string.feature_loan_charge_due_date), charge.dueDate)
        Spacer(Modifier.height(KptTheme.spacing.sm))
    }
}

/**
 * A label-left / value-right row. Theme colours and a typography token, not the original's
 * `Color.Black` / `Color.DarkGray` and inline `16.sp` — see this file's header.
 */
@Composable
private fun DetailRow(field: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            modifier = Modifier.weight(1f).padding(start = KptTheme.spacing.md),
            text = field,
            style = KptTheme.typography.bodyLarge,
            color = KptTheme.colorScheme.onSurface,
            textAlign = TextAlign.Start,
        )
        Text(
            modifier = Modifier.weight(1f),
            text = value,
            style = KptTheme.typography.bodyLarge,
            color = KptTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Start,
        )
    }
}

/** Stable handles for UI tests and Maestro flows — see RULE-KMP-COMPOSE-UITEST-001. */
internal object LoanChargesTestTags {
    const val SCREEN = "loanCharges:screen"
    const val CONTENT = "loanCharges:content"
    const val LIST = "loanCharges:list"
    const val EMPTY = "loanCharges:empty"
    const val ADD = "loanCharges:add"
    fun row(id: Int): String = "loanCharges:row:$id"
}
