/*
 * Copyright 2026 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See See https://github.com/openMF/kmp-project-template/blob/main/LICENSE
 */
package kpt.feature.home.demo

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.ReceiptLong
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.Compare
import androidx.compose.material.icons.filled.CurrencyExchange
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.Savings
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kpt.core.base.designsystem.component.AppCard
import kpt.core.base.store.freshness.FreshnessSignal
import kpt.core.base.store.screen.ScreenState
import kpt.core.base.ui.dashboard.DashboardProgressBar
import kpt.core.base.ui.dashboard.toDashboardProgressState
import kpt.core.base.ui.freshness.FreshnessIndicator
import kpt.core.base.ui.screen.ScreenContent
import kpt.core.common.formatDecimal
import kpt.core.common.formatGrouped
import kpt.core.designsystem.component.AmountDisplay
import kpt.core.designsystem.component.MoneyText
import kpt.core.designsystem.component.MoneyTone
import kpt.core.designsystem.component.RateBadge
import kpt.core.designsystem.component.RateDirection
import kpt.core.designsystem.component.SectionHeader
import kpt.core.designsystem.component.Urgency
import kpt.core.designsystem.component.UrgencyDot
import kpt.core.designsystem.theme.spacing
import kpt.core.model.demo.banking.BillReminder
import kpt.feature.home.demo.ui.HomeAction
import kpt.feature.home.demo.ui.HomeViewModel
import kpt.feature.home.demo.ui.LoansSummary
import kpt.feature.home.demo.ui.RatesQuickView
import kpt.feature.home.generated.resources.Res
import kpt.feature.home.generated.resources.screens_home_active_loans_plural
import kpt.feature.home.generated.resources.screens_home_active_loans_singular
import kpt.feature.home.generated.resources.screens_home_bills_card_title
import kpt.feature.home.generated.resources.screens_home_bills_day_value
import kpt.feature.home.generated.resources.screens_home_bills_empty
import kpt.feature.home.generated.resources.screens_home_bills_total_label
import kpt.feature.home.generated.resources.screens_home_exchange_card_title
import kpt.feature.home.generated.resources.screens_home_loan_tile_footer
import kpt.feature.home.generated.resources.screens_home_loans_carousel_title
import kpt.feature.home.generated.resources.screens_home_loans_label
import kpt.feature.home.generated.resources.screens_home_money_value
import kpt.feature.home.generated.resources.screens_home_money_zero
import kpt.feature.home.generated.resources.screens_home_monthly_emi_label
import kpt.feature.home.generated.resources.screens_home_no_loans_message
import kpt.feature.home.generated.resources.screens_home_outstanding_label
import kpt.feature.home.generated.resources.screens_home_rates_card_title
import kpt.feature.home.generated.resources.screens_home_rates_delta_flat
import kpt.feature.home.generated.resources.screens_home_rates_delta_mortgage
import kpt.feature.home.generated.resources.screens_home_rates_fed_funds_label
import kpt.feature.home.generated.resources.screens_home_rates_mortgage_30y_label
import kpt.feature.home.generated.resources.screens_home_rates_value_format
import kpt.feature.home.generated.resources.screens_home_remaining_label
import kpt.feature.home.generated.resources.screens_home_section_this_week
import kpt.feature.home.generated.resources.screens_home_section_tools
import kpt.feature.home.generated.resources.screens_home_section_tools_subtitle
import kpt.feature.home.generated.resources.screens_home_see_all
import kpt.feature.home.generated.resources.screens_home_tool_affordability_subtitle
import kpt.feature.home.generated.resources.screens_home_tool_affordability_title
import kpt.feature.home.generated.resources.screens_home_tool_amortization_subtitle
import kpt.feature.home.generated.resources.screens_home_tool_amortization_title
import kpt.feature.home.generated.resources.screens_home_tool_compare_subtitle
import kpt.feature.home.generated.resources.screens_home_tool_compare_title
import kpt.feature.home.generated.resources.screens_home_tool_crypto_subtitle
import kpt.feature.home.generated.resources.screens_home_tool_crypto_title
import kpt.feature.home.generated.resources.screens_home_tool_currency_subtitle
import kpt.feature.home.generated.resources.screens_home_tool_currency_title
import kpt.feature.home.generated.resources.screens_home_tool_emi_subtitle
import kpt.feature.home.generated.resources.screens_home_tool_emi_title
import kpt.feature.home.generated.resources.screens_home_tool_history_subtitle
import kpt.feature.home.generated.resources.screens_home_tool_history_title
import kpt.feature.home.generated.resources.screens_home_tool_macro_subtitle
import kpt.feature.home.generated.resources.screens_home_tool_macro_title
import kpt.feature.home.generated.resources.screens_home_tool_wizard_subtitle
import kpt.feature.home.generated.resources.screens_home_tool_wizard_title
import kpt.feature.home.generated.resources.screens_home_total_outstanding_label
import kpt.feature.home.generated.resources.screens_home_usd_label
import kpt.feature.home.ui.TestTags
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinNavViewModel as retainedKoinViewModel

// public (not internal): rendered as the default home body by cmp-navigation's fork-owned
// BackboneRegistry.homeBody seam (epic pure-white-label-store5-network, T7). Lives in the
// fork-owned `demo/` package; customizer --clean deletes it together with the seam's default.
@Composable
fun HomeDashboard(
    onNavigateToLoans: () -> Unit,
    onNavigateToBills: () -> Unit,
    onNavigateToRates: () -> Unit,
    onNavigateToExchangeRates: () -> Unit,
    onNavigateToRateHistory: () -> Unit,
    onNavigateToMacro: () -> Unit,
    onNavigateToEmi: () -> Unit,
    onNavigateToAffordability: () -> Unit,
    onNavigateToAmortization: () -> Unit,
    onNavigateToLoanComparison: () -> Unit,
    onNavigateToLoanCalcWizard: () -> Unit,
    onNavigateToCrypto: () -> Unit,
    viewModel: HomeViewModel = retainedKoinViewModel(),
) {
    val state by viewModel.stateFlow.collectAsStateWithLifecycle()
    val exchangeFreshness by viewModel.exchangeFreshness.collectAsStateWithLifecycle()
    val ratesFreshness by viewModel.ratesFreshness.collectAsStateWithLifecycle()
    val sp = MaterialTheme.spacing

    // `rememberScrollState()` internally uses `rememberSaveable` with
    // `ScrollState.Saver`, so the dashboard scroll position survives tab-switch
    // (Navigation `saveState`/`restoreState`) and config-change (Android's
    // saved-instance-state Bundle). No per-screen retention code.
    val scrollState = rememberScrollState()
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = sp.lg)
            .verticalScroll(scrollState)
            .testTag(TestTags.Home.DASHBOARD_SCROLL),
        verticalArrangement = Arrangement.spacedBy(sp.md),
    ) {
        Spacer(Modifier.height(sp.xs))

        // ── Aggregate load/refresh progress ──────────────────────────────
        // Same core-base dashboard archetype the Country Macro screen showcases:
        // fold the four independent per-card ScreenStates (loans / bills / rates /
        // exchange) into one "X of Y loaded" strip. DashboardProgressBar hides itself
        // once every card is Content (or on an empty dashboard), so it only surfaces
        // while cards are still resolving or during a pull-to-refresh fan-out — the
        // per-card independence + retry below is untouched.
        DashboardProgressBar(
            state = listOf(
                state.loans,
                state.bills,
                state.rates,
                state.exchangeRate,
            ).toDashboardProgressState(),
        )

        // ── Hero: at-a-glance financial snapshot ─────────────────────────
        HeroSnapshot(state.loans)

        // ── Loans carousel — scroll through every active loan ────────────
        LoansCarousel(state.loans, onLoanClick = onNavigateToLoans)

        // ── Quick stats grid (Bills + Rates) ─────────────────────────────
        SectionHeader(title = stringResource(Res.string.screens_home_section_this_week))

        BillsQuickCard(
            state = state.bills,
            onSeeAll = onNavigateToBills,
        )

        RatesQuickCard(
            state = state.rates,
            freshness = ratesFreshness,
            onRetry = { viewModel.trySendAction(HomeAction.RetryRates) },
            onSeeAll = onNavigateToRates,
        )

        ExchangeRateCard(
            state = state.exchangeRate,
            freshness = exchangeFreshness,
            onRetry = { viewModel.trySendAction(HomeAction.RetryExchangeRate) },
            onSeeAll = onNavigateToExchangeRates,
        )

        // ── Tools grid ───────────────────────────────────────────────────
        SectionHeader(
            title = stringResource(Res.string.screens_home_section_tools),
            supporting = stringResource(Res.string.screens_home_section_tools_subtitle),
        )
        FeatureRow(
            title = stringResource(Res.string.screens_home_tool_emi_title),
            subtitle = stringResource(Res.string.screens_home_tool_emi_subtitle),
            icon = Icons.Default.Calculate,
            onClick = onNavigateToEmi,
        )
        FeatureRow(
            title = stringResource(Res.string.screens_home_tool_affordability_title),
            subtitle = stringResource(Res.string.screens_home_tool_affordability_subtitle),
            icon = Icons.Default.Savings,
            onClick = onNavigateToAffordability,
        )
        FeatureRow(
            title = stringResource(Res.string.screens_home_tool_amortization_title),
            subtitle = stringResource(Res.string.screens_home_tool_amortization_subtitle),
            icon = Icons.AutoMirrored.Default.TrendingUp,
            onClick = onNavigateToAmortization,
        )
        FeatureRow(
            title = stringResource(Res.string.screens_home_tool_compare_title),
            subtitle = stringResource(Res.string.screens_home_tool_compare_subtitle),
            icon = Icons.Default.Compare,
            onClick = onNavigateToLoanComparison,
        )
        FeatureRow(
            title = stringResource(Res.string.screens_home_tool_wizard_title),
            subtitle = stringResource(Res.string.screens_home_tool_wizard_subtitle),
            icon = Icons.Default.Tune,
            onClick = onNavigateToLoanCalcWizard,
        )
        FeatureRow(
            title = stringResource(Res.string.screens_home_tool_currency_title),
            subtitle = stringResource(Res.string.screens_home_tool_currency_subtitle),
            icon = Icons.Default.CurrencyExchange,
            onClick = onNavigateToExchangeRates,
        )
        FeatureRow(
            title = stringResource(Res.string.screens_home_tool_history_title),
            subtitle = stringResource(Res.string.screens_home_tool_history_subtitle),
            icon = Icons.AutoMirrored.Default.ReceiptLong,
            onClick = onNavigateToRateHistory,
        )
        FeatureRow(
            title = stringResource(Res.string.screens_home_tool_macro_title),
            subtitle = stringResource(Res.string.screens_home_tool_macro_subtitle),
            icon = Icons.Default.Public,
            onClick = onNavigateToMacro,
        )
        FeatureRow(
            title = stringResource(Res.string.screens_home_tool_crypto_title),
            subtitle = stringResource(Res.string.screens_home_tool_crypto_subtitle),
            icon = Icons.AutoMirrored.Default.TrendingUp,
            onClick = onNavigateToCrypto,
        )

        Spacer(Modifier.height(sp.lg))
    }
}

/**
 * Hero card — the dashboard's first impression. Renders the user's outstanding-loans
 * snapshot on a primary-gradient surface so it pops above the rest of the page.
 */
@Composable
private fun HeroSnapshot(state: ScreenState<LoansSummary>) {
    kpt.core.base.designsystem.component.HeroCard {
        ScreenContent(
            state = state,
            onRetry = {},
            modifier = Modifier.fillMaxWidth(),
            empty = {
                Column {
                    AmountDisplay(
                        amountText = stringResource(Res.string.screens_home_money_zero),
                        label = stringResource(Res.string.screens_home_outstanding_label),
                    )
                    Spacer(Modifier.height(MaterialTheme.spacing.sm))
                    Text(
                        text = stringResource(Res.string.screens_home_no_loans_message),
                        style = MaterialTheme.typography.bodyMedium,
                    )
                }
            },
        ) { summary, _ ->
            Column(verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.md)) {
                AmountDisplay(
                    amountText = stringResource(
                        Res.string.screens_home_money_value,
                        summary.totalOutstanding.formatGrouped(2),
                    ),
                    label = stringResource(Res.string.screens_home_total_outstanding_label),
                    supporting = {
                        Text(
                            text = stringResource(
                                if (summary.count == 1) {
                                    Res.string.screens_home_active_loans_singular
                                } else {
                                    Res.string.screens_home_active_loans_plural
                                },
                                summary.count,
                            ),
                            style = MaterialTheme.typography.bodyMedium,
                        )
                    },
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    HeroStat(
                        label = stringResource(Res.string.screens_home_monthly_emi_label),
                        value = stringResource(
                            Res.string.screens_home_money_value,
                            summary.totalMonthlyEmi.formatGrouped(2),
                        ),
                    )
                    HeroStat(
                        label = stringResource(Res.string.screens_home_loans_label),
                        value = summary.count.toString(),
                    )
                }
            }
        }
    }
}

@Composable
private fun HeroStat(label: String, value: String) {
    Column {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Medium),
        )
        Text(
            text = value,
            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.SemiBold),
        )
    }
}

/**
 * Horizontally-scrollable carousel of per-loan tiles. Renders nothing when the loans state
 * isn't [ScreenState.Content] OR when the user has no loans yet — the [HeroSnapshot] empty-state
 * already prompts them to add one. Tapping any tile navigates to the full Personal Loans screen.
 */
@Composable
private fun LoansCarousel(state: ScreenState<LoansSummary>, onLoanClick: () -> Unit) {
    val content = state as? ScreenState.Content ?: return
    val loans = content.data.loans
    if (loans.isEmpty()) return

    val sp = MaterialTheme.spacing
    Column(verticalArrangement = Arrangement.spacedBy(sp.sm)) {
        Text(
            text = stringResource(Res.string.screens_home_loans_carousel_title, loans.size),
            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold),
            color = MaterialTheme.colorScheme.onBackground,
        )
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(sp.md),
            contentPadding = PaddingValues(end = sp.md),
        ) {
            items(items = loans, key = { it.id }) { loan ->
                LoanCarouselTile(loan = loan, onClick = onLoanClick)
            }
        }
    }
}

/** Compact per-loan tile used by [LoansCarousel]. Sized to fit 2–2.5 cards on a phone screen. */
@Composable
private fun LoanCarouselTile(loan: kpt.core.model.demo.banking.Loan, onClick: () -> Unit) {
    val sp = MaterialTheme.spacing
    val progress = if (loan.principal > 0) {
        (1.0 - (loan.principalRemaining / loan.principal)).toFloat().coerceIn(0f, 1f)
    } else {
        0f
    }
    AppCard(
        modifier = Modifier
            .width(220.dp)
            .clickable(onClick = onClick),
        accentColor = loanKindAccent(loan.kind),
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(sp.xs)) {
            Text(
                text = loan.name,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                maxLines = 1,
                overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis,
            )
            Text(
                text = stringResource(Res.string.screens_home_remaining_label),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            kpt.core.designsystem.component.MoneyText(
                text = stringResource(
                    Res.string.screens_home_money_value,
                    loan.principalRemaining.formatGrouped(2),
                ),
                tone = kpt.core.designsystem.component.MoneyTone.Negative,
                style = MaterialTheme.typography.titleLarge,
            )
            Spacer(Modifier.height(sp.xs))
            androidx.compose.material3.LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp),
                color = MaterialTheme.colorScheme.secondary,
                trackColor = MaterialTheme.colorScheme.surfaceContainerHighest,
            )
            Text(
                text = stringResource(
                    Res.string.screens_home_loan_tile_footer,
                    loan.monthlyPayment.formatGrouped(2),
                    (progress * 100).toInt(),
                ),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

/**
 * Maps each [kpt.core.model.demo.banking.LoanKind] to a distinct accent stripe colour so
 * users can scan a list of loans and identify mortgage vs. auto vs. personal vs. business
 * vs. student at a glance — same vocabulary as [PersonalLoansListScreen]'s LoanRowCard.
 */
@Composable
private fun loanKindAccent(kind: kpt.core.model.demo.banking.LoanKind): androidx.compose.ui.graphics.Color? = when (kind) {
    kpt.core.model.demo.banking.LoanKind.MORTGAGE -> MaterialTheme.colorScheme.secondary
    kpt.core.model.demo.banking.LoanKind.BUSINESS -> MaterialTheme.colorScheme.secondary
    kpt.core.model.demo.banking.LoanKind.AUTO -> MaterialTheme.colorScheme.tertiary
    kpt.core.model.demo.banking.LoanKind.STUDENT -> MaterialTheme.colorScheme.tertiary
    kpt.core.model.demo.banking.LoanKind.PERSONAL -> MaterialTheme.colorScheme.primary
    kpt.core.model.demo.banking.LoanKind.OTHER -> null
}

@Composable
private fun BillsQuickCard(state: ScreenState<List<BillReminder>>, onSeeAll: () -> Unit) {
    SectionCard(
        title = stringResource(Res.string.screens_home_bills_card_title),
        icon = Icons.Default.NotificationsActive,
        onSeeAll = onSeeAll,
    ) {
        val emptyMessage = stringResource(Res.string.screens_home_bills_empty)
        ScreenContent(
            state = state,
            onRetry = {},
            modifier = Modifier.fillMaxWidth(),
            empty = { WidgetEmpty(emptyMessage) },
        ) { bills, _ ->
            Column(verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.sm)) {
                val total = bills.sumOf { it.amount }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = stringResource(Res.string.screens_home_bills_total_label),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    MoneyText(
                        text = stringResource(
                            Res.string.screens_home_money_value,
                            total.formatGrouped(2),
                        ),
                        tone = MoneyTone.Negative,
                        style = MaterialTheme.typography.titleMedium,
                    )
                }
                bills.take(BILLS_PREVIEW_LIMIT).forEach { bill ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.sm),
                    ) {
                        UrgencyDot(urgency = urgencyForDay(bill.dueDay))
                        Text(
                            text = bill.name,
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.weight(1f),
                        )
                        Text(
                            text = stringResource(Res.string.screens_home_bills_day_value, bill.dueDay),
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                        MoneyText(
                            text = stringResource(
                                Res.string.screens_home_money_value,
                                bill.amount.formatGrouped(2),
                            ),
                            tone = MoneyTone.Negative,
                            style = MaterialTheme.typography.bodyMedium,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun RatesQuickCard(
    state: ScreenState<RatesQuickView>,
    freshness: FreshnessSignal,
    onRetry: () -> Unit,
    onSeeAll: () -> Unit,
) {
    SectionCard(
        title = stringResource(Res.string.screens_home_rates_card_title),
        icon = Icons.AutoMirrored.Default.TrendingUp,
        onSeeAll = onSeeAll,
        trailing = {
            FreshnessIndicator(
                signal = freshness,
                onRefresh = onRetry,
            )
        },
    ) {
        ScreenContent(
            state = state,
            onRetry = onRetry,
            modifier = Modifier.fillMaxWidth(),
        ) { rates, _ ->
            Column(verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.sm)) {
                RateRow(
                    label = stringResource(Res.string.screens_home_rates_fed_funds_label),
                    value = stringResource(
                        Res.string.screens_home_rates_value_format,
                        rates.fedFundsPercent.formatDecimal(2),
                    ),
                    direction = RateDirection.Flat,
                    delta = stringResource(Res.string.screens_home_rates_delta_flat),
                )
                RateRow(
                    label = stringResource(Res.string.screens_home_rates_mortgage_30y_label),
                    value = stringResource(
                        Res.string.screens_home_rates_value_format,
                        rates.mortgage30YPercent.formatDecimal(2),
                    ),
                    direction = RateDirection.Up,
                    delta = stringResource(Res.string.screens_home_rates_delta_mortgage),
                )
            }
        }
    }
}

@Composable
private fun ExchangeRateCard(
    state: ScreenState<kpt.core.model.demo.currency.ExchangeRates>,
    freshness: FreshnessSignal,
    onRetry: () -> Unit,
    onSeeAll: () -> Unit,
) {
    SectionCard(
        title = stringResource(Res.string.screens_home_exchange_card_title),
        icon = Icons.Default.CurrencyExchange,
        onSeeAll = onSeeAll,
        trailing = {
            FreshnessIndicator(
                signal = freshness,
                onRefresh = onRetry,
            )
        },
    ) {
        ScreenContent(
            state = state,
            onRetry = onRetry,
            modifier = Modifier.fillMaxWidth(),
        ) { rates, _ ->
            val keyRates = listOf("EUR", "GBP", "INR")
            Column(verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.sm)) {
                keyRates.forEach { code ->
                    val value = rates.rates[code] ?: return@forEach
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        // "USD → $code" rendered as Row(Text, Icon, Text). The
                        // raw Unicode arrow (U+2192) is not in the project's
                        // Outfit font and renders as tofu on wasmJs (Skia does
                        // not fall back to system fonts for missing glyphs the
                        // way browser HTML text does). Compose Icon is a vector
                        // path, theme-tinted, font-independent.
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.xs),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Text(
                                text = stringResource(Res.string.screens_home_usd_label),
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(14.dp),
                            )
                            Text(
                                text = code,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        }
                        Text(
                            text = value.formatGrouped(4),
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.SemiBold,
                            ),
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun SectionCard(
    title: String,
    icon: ImageVector,
    onSeeAll: () -> Unit,
    trailing: (@Composable () -> Unit)? = null,
    content: @Composable () -> Unit,
) {
    AppCard {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.sm),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primaryContainer),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onPrimaryContainer,
                        modifier = Modifier.size(18.dp),
                    )
                }
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                )
                trailing?.invoke()
            }
            Text(
                text = stringResource(Res.string.screens_home_see_all),
                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Medium),
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.clickable(onClick = onSeeAll),
            )
        }
        Spacer(Modifier.height(MaterialTheme.spacing.md))
        content()
    }
}

@Composable
private fun RateRow(label: String, value: String, direction: RateDirection, delta: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.weight(1f),
        )
        Text(
            text = value,
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
        )
        Spacer(Modifier.size(MaterialTheme.spacing.sm))
        RateBadge(delta = delta, direction = direction)
    }
}

@Composable
private fun WidgetEmpty(message: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = message,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@Composable
private fun FeatureRow(title: String, subtitle: String, icon: ImageVector, onClick: () -> Unit) {
    AppCard(
        modifier = Modifier.clickable(onClick = onClick),
        contentPadding = PaddingValues(MaterialTheme.spacing.lg),
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.lg),
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.secondaryContainer),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSecondaryContainer,
                    modifier = Modifier.size(22.dp),
                )
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}

/** Heuristic mapping bill due-day to an urgency tier for the [UrgencyDot]. */
private fun urgencyForDay(dueDay: Int): Urgency = when {
    dueDay <= 1 -> Urgency.Today
    dueDay <= 3 -> Urgency.Upcoming
    dueDay <= 7 -> Urgency.Upcoming
    else -> Urgency.Distant
}

private const val BILLS_PREVIEW_LIMIT = 3
