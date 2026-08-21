/*
 * Copyright 2026 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See See https://github.com/openMF/kmp-project-template/blob/main/LICENSE
 */
package kpt.feature.home.demo.ui

import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kpt.core.base.store.freshness.FreshnessBand
import kpt.core.base.store.freshness.FreshnessSignal
import kpt.core.base.store.screen.FetchPolicy
import kpt.core.base.store.screen.ScreenState
import kpt.core.base.store.screen.asLocalScreenStream
import kpt.core.base.store.screen.combineScreenStates
import kpt.core.base.store.screen.emptyIfContent
import kpt.core.base.store.screen.mapContent
import kpt.core.base.ui.viewmodel.BaseViewModel
import kpt.core.data.demo.banking.BillReminderRepository
import kpt.core.data.demo.banking.LoanRepository
import kpt.core.data.demo.currency.CurrencyRepository
import kpt.core.data.demo.economic.EconomicRatesRepository
import kpt.core.model.demo.banking.BillReminder
import kpt.core.model.demo.currency.ExchangeRates
import kpt.core.store.demo.economic.impl.InterestRateSeriesKey

/**
 * **Home dashboard ViewModel.**
 *
 * Composes four independent reactive sources into a single UI state with
 * per-widget loading/empty/error/content slots. Each widget's state evolves
 * independently — one card can be Loading while another shows Content; pull-
 * to-refresh fans out to the network-backed streams concurrently.
 *
 * Sources split by character:
 *  - [exchangeRateStream] and the two FRED rate streams ([fedFundsStream],
 *    [mortgageStream]) are network-backed `ScreenDataStream`s with their own
 *    Loading/Error/Content + freshness state. The rate streams feed a single
 *    [RatesQuickView] slot via [combine].
 *  - [LoanRepository.observeAll] and [BillReminderRepository.observeUpcoming]
 *    are purely local reactive `Flow`s — mapped here into `ScreenState.Empty` /
 *    `ScreenState.Content(freshness = FRESH)` (same projection used elsewhere
 *    in the toolkit for local-only sources).
 */
class HomeViewModel(
    private val loanRepository: LoanRepository,
    private val billReminderRepository: BillReminderRepository,
    private val economicRatesRepository: EconomicRatesRepository,
    private val currencyRepository: CurrencyRepository,
) : BaseViewModel<HomeUiState, Nothing, HomeAction>(HomeUiState()) {

    /**
     * **Archetype showcase: PERIODIC(300_000L)**
     *
     * The home dashboard exchange-rate tile is an ambient surface — the user expects
     * it to stay fresh without manually pulling to refresh. By passing
     * [FetchPolicy.PERIODIC] with a 5-minute cadence the framework's [ScreenDataStream]
     * fires a background refresh every 5 minutes automatically, through the same
     * debounce + lastContent preservation pipeline as a user-tap refresh.
     */
    private val exchangeRateStream = currencyRepository.exchangeRatesStream(
        baseCurrency = "USD",
        scope = viewModelScope,
        fetchPolicy = FetchPolicy.PERIODIC(intervalMillis = EXCHANGE_RATE_REFRESH_INTERVAL_MS),
    )

    private val fedFundsStream = economicRatesRepository.interestRateSeriesStream(
        key = FedFundsKey,
        scope = viewModelScope,
    )

    private val mortgageStream = economicRatesRepository.interestRateSeriesStream(
        key = Mortgage30YKey,
        scope = viewModelScope,
    )

    /**
     * Per-card freshness for the Exchange Rate widget — drives the [FreshnessIndicator]
     * info icon next to the card title. Pure time-based staleness; network connectivity
     * is rendered separately by the global `ConnectivityBanner`.
     */
    val exchangeFreshness: StateFlow<FreshnessSignal> = exchangeRateStream.freshness
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(stopTimeoutMillis = STATE_TIMEOUT_MS),
            initialValue = FreshnessSignal.initial(),
        )

    /**
     * Per-card freshness for the Rates Quick widget — combines the worse of the two
     * underlying FRED series (Fed Funds + 30Y Mortgage) so the indicator surfaces the
     * weakest signal across the two streams.
     */
    val ratesFreshness: StateFlow<FreshnessSignal> = combine(
        fedFundsStream.freshness,
        mortgageStream.freshness,
    ) { fed, mortgage ->
        if (mortgage.band.severity() > fed.band.severity()) mortgage else fed
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(stopTimeoutMillis = STATE_TIMEOUT_MS),
        initialValue = FreshnessSignal.initial(),
    )

    init {
        // Exchange Rate widget: direct ScreenState<ExchangeRates>.
        exchangeRateStream.state
            .onEach { state ->
                updateState { copy(exchangeRate = state) }
            }
            .launchIn(viewModelScope)

        // Rates quick view: zip Fed Funds + 30Y Mortgage into a single widget
        // state via `combineScreenStates` — the framework helper applies the
        // canonical priority: NoNetwork > Loading > Unauthenticated > Error >
        // Empty > Content. The resulting Content's freshness is the worst of
        // the two source freshnesses.
        combineScreenStates(
            a = fedFundsStream.state,
            b = mortgageStream.state,
        ) { fed, mortgage ->
            RatesQuickView(
                fedFundsPercent = fed.current,
                mortgage30YPercent = mortgage.current,
            )
        }
            .onEach { state -> updateState { copy(rates = state) } }
            .launchIn(viewModelScope)

        // Loans summary widget: consume the repository's ScreenDataStream (empty portfolio → Empty
        // via the Store) and project the list into a LoansSummary via mapContent.
        loanRepository.loansStream(viewModelScope).state
            .mapContent { loans, _ ->
                LoansSummary(
                    count = loans.size,
                    totalMonthlyEmi = loans.sumOf { it.monthlyPayment },
                    totalOutstanding = loans.sumOf { it.principalRemaining },
                    loans = loans,
                )
            }
            .onEach { state -> updateState { copy(loans = state) } }
            .launchIn(viewModelScope)

        // Upcoming bills (next 7 days) — a FILTERED local projection (a distinct DAO query, not the
        // bills Store's full-list read), so it lifts via asLocalScreenStream + emptyIfContent.
        billReminderRepository.observeUpcoming(maxDays = UPCOMING_BILLS_WINDOW_DAYS)
            .asLocalScreenStream()
            .emptyIfContent { it.isEmpty() }
            .onEach { state -> updateState { copy(bills = state) } }
            .launchIn(viewModelScope)
    }

    override fun handleAction(action: HomeAction) = when (action) {
        HomeAction.RefreshAll -> {
            exchangeRateStream.refresh()
            fedFundsStream.refresh()
            mortgageStream.refresh()
            // Loans + Bills are local reactive Flows — nothing to refresh.
        }
        HomeAction.RetryExchangeRate -> exchangeRateStream.retry()
        HomeAction.RetryRates -> {
            fedFundsStream.retry()
            mortgageStream.retry()
        }
    }

    /**
     * Returns a "worse-of-two" ordering on [FreshnessBand] — used by the rates-quick
     * combiner to surface the weaker of two source signals. Order: Initial < Fresh <
     * Stale < VeryStale (higher = worse).
     */
    private fun FreshnessBand.severity(): Int = when (this) {
        FreshnessBand.Initial -> 0
        FreshnessBand.Fresh -> 1
        FreshnessBand.Stale -> 2
        FreshnessBand.VeryStale -> 3
    }

    companion object {
        /** WhileSubscribed timeout for the per-card freshness StateFlows. */
        private const val STATE_TIMEOUT_MS: Long = 5_000L

        /** Lookahead window for the "Upcoming Bills" widget. */
        const val UPCOMING_BILLS_WINDOW_DAYS: Int = 7

        /**
         * **Archetype: PERIODIC** — background refresh cadence for the exchange-rate tile.
         *
         * 5 minutes (300 000 ms). FX rates are volatile; refreshing every 5 minutes keeps
         * the dashboard tile reasonably fresh without hammering the Frankfurter API. The
         * ticker fires through [ScreenDataStream]'s internal refresh pipeline so last-known
         * content is preserved across intervals and no Loading flicker occurs.
         */
        const val EXCHANGE_RATE_REFRESH_INTERVAL_MS: Long = 300_000L

        /** Effective Federal Funds Rate — the overnight bank-to-bank lending rate. */
        val FedFundsKey: InterestRateSeriesKey = InterestRateSeriesKey(
            seriesId = "DFF",
            name = "Federal Funds Rate",
            unit = "%",
            days = 30,
        )

        /** 30-Year Fixed Mortgage Average — Freddie Mac weekly survey. */
        val Mortgage30YKey: InterestRateSeriesKey = InterestRateSeriesKey(
            seriesId = "MORTGAGE30US",
            name = "30-Year Mortgage",
            unit = "%",
            days = 30,
        )
    }
}

/**
 * Aggregate state for the home dashboard.
 *
 * Each slot is an independent [ScreenState] so the screen can render per-card
 * Loading / Empty / Error / Content states.
 */
data class HomeUiState(
    val loans: ScreenState<LoansSummary> = ScreenState.Loading,
    val bills: ScreenState<List<BillReminder>> = ScreenState.Loading,
    val rates: ScreenState<RatesQuickView> = ScreenState.Loading,
    val exchangeRate: ScreenState<ExchangeRates> = ScreenState.Loading,
)

/**
 * Compact projection of the user's loan portfolio for the home dashboard.
 *
 * Only the dashboard's summary surface needs these aggregates; the full list
 * lives on the Loans screen.
 *
 * @property count Number of tracked loans.
 * @property totalMonthlyEmi Sum of every loan's monthly EMI.
 * @property totalOutstanding Sum of every loan's remaining principal.
 */
data class LoansSummary(
    val count: Int,
    val totalMonthlyEmi: Double,
    val totalOutstanding: Double,
    /**
     * The actual loans, sorted soonest-due first. The home dashboard hero renders these as a
     * horizontally-scrollable carousel below the totals so users can flip through every loan
     * without leaving the dashboard.
     */
    val loans: List<kpt.core.model.demo.banking.Loan> = emptyList(),
)

/**
 * Compact projection of the two headline rate series for the home dashboard.
 *
 * @property fedFundsPercent Latest Effective Federal Funds Rate, in percent.
 * @property mortgage30YPercent Latest 30-Year Fixed Mortgage Average, in percent.
 */
data class RatesQuickView(
    val fedFundsPercent: Double,
    val mortgage30YPercent: Double,
)

sealed interface HomeAction {
    /** Pull-to-refresh — fans out to every network-backed stream. */
    data object RefreshAll : HomeAction

    /** Retry just the Exchange Rate widget (after an error). */
    data object RetryExchangeRate : HomeAction

    /** Retry the Rates Quick widget (refreshes both FRED-backed streams). */
    data object RetryRates : HomeAction
}
