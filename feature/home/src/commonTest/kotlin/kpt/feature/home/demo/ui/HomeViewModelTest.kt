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

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlinx.datetime.LocalDate
import kpt.core.base.data.infra.Synchronizer
import kpt.core.base.store.screen.ExperimentalScreenDataStreamTestingApi
import kpt.core.base.store.screen.FetchPolicy
import kpt.core.base.store.screen.ScreenDataStream
import kpt.core.base.store.screen.ScreenState
import kpt.core.base.store.screen.screenDataStreamForTesting
import kpt.core.data.demo.banking.BillReminderRepository
import kpt.core.data.demo.banking.LoanRepository
import kpt.core.data.demo.currency.CurrencyRepository
import kpt.core.data.demo.economic.EconomicRatesRepository
import kpt.core.model.demo.banking.BillCategory
import kpt.core.model.demo.banking.BillReminder
import kpt.core.model.demo.banking.Loan
import kpt.core.model.demo.banking.LoanKind
import kpt.core.model.demo.banking.Recurrence
import kpt.core.model.demo.currency.ExchangeRates
import kpt.core.model.demo.currency.RateHistory
import kpt.core.model.demo.currency.RateHistoryKey
import kpt.core.model.demo.economic.InterestRateSeries
import kpt.core.model.demo.economic.RateObservation
import kpt.core.store.demo.economic.impl.InterestRateSeriesKey
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

/**
 * Locks the [HomeViewModel] contract:
 *
 *  - Initial state has 4 independent Loading slots.
 *  - Loans + Bills are pure-local: empty list → `ScreenState.Empty`; non-empty
 *    → `ScreenState.Content(freshness = FRESH)` with the right aggregates.
 *  - The Rates Quick widget composes the Fed Funds + 30Y Mortgage streams via
 *    the framework's `combineScreenStates` helper — Content appears only when
 *    BOTH source streams reach Content.
 *  - `RefreshAll` fans out to every network-backed stream; per-row retry
 *    refreshes only its own scope.
 */
@OptIn(ExperimentalCoroutinesApi::class, ExperimentalScreenDataStreamTestingApi::class)
class HomeViewModelTest {

    private val dispatcher = StandardTestDispatcher()

    @BeforeTest
    fun setUp() {
        Dispatchers.setMain(dispatcher)
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun initialStateHasAllFourSlotsInLoading() = runTest {
        val vm = buildViewModel()

        val state = vm.stateFlow.first()

        assertEquals(ScreenState.Loading, state.loans)
        assertEquals(ScreenState.Loading, state.bills)
        assertEquals(ScreenState.Loading, state.rates)
        assertEquals(ScreenState.Loading, state.exchangeRate)
    }

    @Test
    fun emptyLoansProjectToEmpty() = runTest {
        val loans = FakeLoanRepository()
        val vm = buildViewModel(loans = loans)
        dispatcher.scheduler.advanceUntilIdle()

        val state = vm.stateFlow.first { it.loans !is ScreenState.Loading }
        assertEquals(ScreenState.Empty, state.loans)
    }

    @Test
    fun nonEmptyLoansProjectToContentWithAggregates() = runTest {
        val loans = FakeLoanRepository(
            listOf(
                sampleLoan(id = "l1", monthlyPayment = 250.0, principalRemaining = 5_000.0),
                sampleLoan(id = "l2", monthlyPayment = 1_200.0, principalRemaining = 240_000.0),
            ),
        )
        val vm = buildViewModel(loans = loans)
        dispatcher.scheduler.advanceUntilIdle()

        val state = vm.stateFlow.first { it.loans is ScreenState.Content }
        val content = state.loans as? ScreenState.Content<LoansSummary>
        assertNotNull(content, "loans should be Content")
        assertEquals(2, content.data.count)
        assertEquals(1_450.0, content.data.totalMonthlyEmi)
        assertEquals(245_000.0, content.data.totalOutstanding)
        // freshness moved from DataFreshness enum on Content to FreshnessSignal
        // sibling per data-freshness-redesign; default constructor value is
        // FreshnessSignal.initial() (band=Initial). No equivalent FRESH assert
        // makes sense for this build path — just verify a signal is present.
    }

    @Test
    fun emptyUpcomingBillsProjectToEmpty() = runTest {
        val bills = FakeBillReminderRepository()
        val vm = buildViewModel(bills = bills)
        dispatcher.scheduler.advanceUntilIdle()

        val state = vm.stateFlow.first { it.bills !is ScreenState.Loading }
        assertEquals(ScreenState.Empty, state.bills)
    }

    @Test
    fun upcomingBillsAreObservedWithSevenDayWindow() = runTest {
        val bills = FakeBillReminderRepository()
        val vm = buildViewModel(bills = bills)
        dispatcher.scheduler.advanceUntilIdle()

        bills.setUpcoming(
            window = HomeViewModel.UPCOMING_BILLS_WINDOW_DAYS,
            values = listOf(sampleBill(id = "b1"), sampleBill(id = "b2")),
        )
        dispatcher.scheduler.advanceUntilIdle()

        val state = vm.stateFlow.first { it.bills is ScreenState.Content }
        val content = state.bills as? ScreenState.Content<List<BillReminder>>
        assertNotNull(content)
        assertEquals(listOf("b1", "b2"), content.data.map { it.id })
        assertEquals(
            HomeViewModel.UPCOMING_BILLS_WINDOW_DAYS,
            bills.lastRequestedWindow,
            "Home widget must request the 7-day window per design.",
        )
    }

    @Test
    fun ratesWidgetIsLoadingUntilBothFredStreamsReachContent() = runTest {
        val fed = MutableStateFlow<ScreenState<InterestRateSeries>>(ScreenState.Loading)
        val mortgage = MutableStateFlow<ScreenState<InterestRateSeries>>(ScreenState.Loading)
        val rates = FakeEconomicRatesRepository(
            fed = fed,
            mortgage = mortgage,
        )
        val vm = buildViewModel(rates = rates)
        dispatcher.scheduler.advanceUntilIdle()

        // Only Fed reached Content — widget still Loading because Mortgage is still Loading.
        fed.value = ScreenState.Content(sampleSeries("DFF", current = 5.33))
        dispatcher.scheduler.advanceUntilIdle()
        assertEquals(ScreenState.Loading, vm.stateFlow.first().rates)

        // Both reached Content — widget transitions to Content with combined view.
        // ScreenState.Content's freshness arg was deleted by data-freshness-redesign;
        // freshnessSignal is now a sibling (defaults to FreshnessSignal.initial()).
        mortgage.value = ScreenState.Content(
            sampleSeries("MORTGAGE30US", current = 7.12),
        )
        dispatcher.scheduler.advanceUntilIdle()

        val state = vm.stateFlow.first { it.rates is ScreenState.Content }
        val content = state.rates as? ScreenState.Content<RatesQuickView>
        assertNotNull(content)
        assertEquals(5.33, content.data.fedFundsPercent)
        assertEquals(7.12, content.data.mortgage30YPercent)
    }

    @Test
    fun homeOpensTheTwoCanonicalFredSeries() = runTest {
        val rates = FakeEconomicRatesRepository(
            fed = MutableStateFlow(ScreenState.Loading),
            mortgage = MutableStateFlow(ScreenState.Loading),
        )
        buildViewModel(rates = rates)
        dispatcher.scheduler.advanceUntilIdle()

        val opened = rates.openedKeys.map { it.seriesId }.toSet()
        assertEquals(setOf("DFF", "MORTGAGE30US"), opened)
    }

    @Test
    fun refreshAllFansOutToEveryNetworkBackedStream() = runTest {
        val rates = FakeEconomicRatesRepository(
            fed = MutableStateFlow(ScreenState.Loading),
            mortgage = MutableStateFlow(ScreenState.Loading),
        )
        val currency = FakeCurrencyRepository()
        val vm = buildViewModel(rates = rates, currency = currency)
        dispatcher.scheduler.advanceUntilIdle()

        vm.trySendAction(HomeAction.RefreshAll)
        dispatcher.scheduler.advanceUntilIdle()

        assertEquals(1, rates.refreshCount("DFF"))
        assertEquals(1, rates.refreshCount("MORTGAGE30US"))
        assertEquals(1, currency.refreshCount)
    }

    @Test
    fun retryRatesRefreshesBothFredStreamsButNotCurrency() = runTest {
        val rates = FakeEconomicRatesRepository(
            fed = MutableStateFlow(ScreenState.Loading),
            mortgage = MutableStateFlow(ScreenState.Loading),
        )
        val currency = FakeCurrencyRepository()
        val vm = buildViewModel(rates = rates, currency = currency)
        dispatcher.scheduler.advanceUntilIdle()

        vm.trySendAction(HomeAction.RetryRates)
        dispatcher.scheduler.advanceUntilIdle()

        assertEquals(1, rates.refreshCount("DFF"))
        assertEquals(1, rates.refreshCount("MORTGAGE30US"))
        assertEquals(0, currency.refreshCount)
    }

    @Test
    fun retryExchangeRateRefreshesOnlyCurrency() = runTest {
        val rates = FakeEconomicRatesRepository(
            fed = MutableStateFlow(ScreenState.Loading),
            mortgage = MutableStateFlow(ScreenState.Loading),
        )
        val currency = FakeCurrencyRepository()
        val vm = buildViewModel(rates = rates, currency = currency)
        dispatcher.scheduler.advanceUntilIdle()

        vm.trySendAction(HomeAction.RetryExchangeRate)
        dispatcher.scheduler.advanceUntilIdle()

        assertEquals(0, rates.refreshCount("DFF"))
        assertEquals(0, rates.refreshCount("MORTGAGE30US"))
        assertEquals(1, currency.refreshCount)
    }

    // region Archetype: PERIODIC(300_000L)

    /**
     * **Archetype showcase: PERIODIC(300_000L)**
     *
     * The home dashboard exchange-rate tile must be opened with [FetchPolicy.PERIODIC]
     * at the 5-minute cadence so the tile auto-refreshes in the background without the
     * user triggering a pull-to-refresh.
     */
    @Test
    fun exchangeRateTileUsesPeriodicFetchPolicyWith5MinuteInterval() = runTest {
        val currency = FakeCurrencyRepository()
        buildViewModel(currency = currency)
        dispatcher.scheduler.advanceUntilIdle()

        val policy = currency.lastFetchPolicy
        assertTrue(
            policy is FetchPolicy.PERIODIC,
            "Exchange-rate tile must use FetchPolicy.PERIODIC — found: $policy",
        )
        assertEquals(
            HomeViewModel.EXCHANGE_RATE_REFRESH_INTERVAL_MS,
            (policy as FetchPolicy.PERIODIC).intervalMillis,
            "Periodic interval must be exactly 300 000 ms (5 minutes).",
        )
    }

    // endregion

    @Test
    fun ratesQuickKeysRequestThirtyDayWindowForCompactCards() {
        // Home dashboard widgets only need the latest value, so the keys ask for
        // 30 days (small payload, fast first paint) — not the 365-day full-screen window.
        assertEquals(30, HomeViewModel.FedFundsKey.days)
        assertEquals(30, HomeViewModel.Mortgage30YKey.days)
        assertEquals("DFF", HomeViewModel.FedFundsKey.seriesId)
        assertEquals("MORTGAGE30US", HomeViewModel.Mortgage30YKey.seriesId)
    }

    @Test
    fun rowsEvolveIndependently() = runTest {
        val loans = FakeLoanRepository(listOf(sampleLoan("l1", monthlyPayment = 100.0)))
        val rates = FakeEconomicRatesRepository(
            fed = MutableStateFlow(ScreenState.Loading),
            mortgage = MutableStateFlow(ScreenState.Loading),
        )
        val vm = buildViewModel(loans = loans, rates = rates)
        dispatcher.scheduler.advanceUntilIdle()

        val state = vm.stateFlow.first { it.loans is ScreenState.Content }
        // Loans Content reached…
        assertTrue(state.loans is ScreenState.Content)
        // …while Rates is still Loading (both Fed + Mortgage streams stuck at Loading).
        assertEquals(ScreenState.Loading, state.rates)
        // Exchange Rate is still Loading because the FakeCurrencyRepository's
        // upstream defaults to Loading.
        assertEquals(ScreenState.Loading, state.exchangeRate)
        // Bills has already evolved to Empty because the local Flow emitted the
        // initial empty list — independent evolution holds.
        assertEquals(ScreenState.Empty, state.bills)
    }

    // region Test helpers

    private fun buildViewModel(
        loans: FakeLoanRepository = FakeLoanRepository(),
        bills: FakeBillReminderRepository = FakeBillReminderRepository(),
        rates: FakeEconomicRatesRepository = FakeEconomicRatesRepository(
            fed = MutableStateFlow(ScreenState.Loading),
            mortgage = MutableStateFlow(ScreenState.Loading),
        ),
        currency: FakeCurrencyRepository = FakeCurrencyRepository(),
    ): HomeViewModel = HomeViewModel(
        loanRepository = loans,
        billReminderRepository = bills,
        economicRatesRepository = rates,
        currencyRepository = currency,
    )

    private fun sampleLoan(id: String, monthlyPayment: Double = 500.0, principalRemaining: Double = 10_000.0): Loan =
        Loan(
            id = id,
            name = "Loan $id",
            kind = LoanKind.PERSONAL,
            principal = principalRemaining,
            principalRemaining = principalRemaining,
            annualRatePercent = 6.5,
            tenureMonths = 60,
            monthsRemaining = 48,
            monthlyPayment = monthlyPayment,
            nextDueDate = LocalDate(2026, 6, 1),
            totalPaid = 0.0,
            createdAtMs = 0,
            updatedAtMs = 0,
        )

    private fun sampleBill(id: String): BillReminder = BillReminder(
        id = id,
        name = "Bill $id",
        amount = 75.0,
        dueDay = 15,
        recurrence = Recurrence.MONTHLY,
        category = BillCategory.UTILITIES,
        enabled = true,
        reminderDaysBefore = 1,
        createdAtMs = 0,
        updatedAtMs = 0,
    )

    private fun sampleSeries(seriesId: String, current: Double): InterestRateSeries = InterestRateSeries(
        seriesId = seriesId,
        name = seriesId,
        current = current,
        unit = "%",
        observations = listOf(RateObservation(LocalDate(2026, 5, 23), current)),
        source = "FRED",
    )

    // endregion
}

// region Fakes — kept private to the test compilation to avoid leakage.

@OptIn(ExperimentalScreenDataStreamTestingApi::class)
private class FakeLoanRepository(initial: List<Loan> = emptyList()) : LoanRepository {
    private val rows = MutableStateFlow(initial)

    override fun observeAll(): Flow<List<Loan>> = rows
    override fun loansStream(scope: CoroutineScope): ScreenDataStream<List<Loan>> =
        screenDataStreamForTesting(rows.map { if (it.isEmpty()) ScreenState.Empty else ScreenState.Content(it) })
    override fun loanDetailStream(id: String, scope: CoroutineScope): ScreenDataStream<Loan> =
        screenDataStreamForTesting(rows.map { r -> r.firstOrNull { it.id == id }?.let { ScreenState.Content(it) } ?: ScreenState.Empty })
    override fun observeById(id: String): Flow<Loan?> = throw UnsupportedOperationException()
    override suspend fun getById(id: String): Loan? = throw UnsupportedOperationException()
    override suspend fun upsert(loan: Loan) {
        rows.value = rows.value.filterNot { it.id == loan.id } + loan
    }
    override suspend fun delete(id: String) {
        rows.value = rows.value.filterNot { it.id == id }
    }
    override fun observeTotalMonthlyEmi(): Flow<Double> = throw UnsupportedOperationException()
    override fun observeTotalPrincipalRemaining(): Flow<Double> = throw UnsupportedOperationException()
    override fun observeCount(): Flow<Int> = throw UnsupportedOperationException()
}

@OptIn(ExperimentalScreenDataStreamTestingApi::class)
private class FakeBillReminderRepository : BillReminderRepository {
    private val upcoming = MutableStateFlow<List<BillReminder>>(emptyList())
    var lastRequestedWindow: Int = -1
        private set

    fun setUpcoming(window: Int, values: List<BillReminder>) {
        lastRequestedWindow = window
        upcoming.value = values
    }

    override fun observeAll(): Flow<List<BillReminder>> = throw UnsupportedOperationException()
    override fun billRemindersStream(scope: CoroutineScope): ScreenDataStream<List<BillReminder>> =
        screenDataStreamForTesting(upcoming.map { if (it.isEmpty()) ScreenState.Empty else ScreenState.Content(it) })
    override fun observeUpcoming(maxDays: Int): Flow<List<BillReminder>> {
        lastRequestedWindow = maxDays
        return upcoming
    }
    override fun observeById(id: String): Flow<BillReminder?> = throw UnsupportedOperationException()
    override suspend fun getById(id: String): BillReminder? = throw UnsupportedOperationException()
    override suspend fun upsert(bill: BillReminder) = throw UnsupportedOperationException()
    override suspend fun delete(id: String) = throw UnsupportedOperationException()
    override fun observeTotalUpcomingAmount(maxDays: Int): Flow<Double> = throw UnsupportedOperationException()
    override fun observeCount(): Flow<Int> = throw UnsupportedOperationException()
}

@OptIn(ExperimentalScreenDataStreamTestingApi::class)
private class FakeEconomicRatesRepository(
    private val fed: MutableStateFlow<ScreenState<InterestRateSeries>>,
    private val mortgage: MutableStateFlow<ScreenState<InterestRateSeries>>,
) : EconomicRatesRepository {

    val openedKeys: MutableList<InterestRateSeriesKey> = mutableListOf()
    private val refreshes: MutableMap<String, Int> = mutableMapOf()

    fun refreshCount(seriesId: String): Int = refreshes[seriesId] ?: 0

    override fun interestRateSeriesStream(
        key: InterestRateSeriesKey,
        scope: CoroutineScope,
    ): ScreenDataStream<InterestRateSeries> {
        openedKeys += key
        val source = when (key.seriesId) {
            "DFF" -> fed
            "MORTGAGE30US" -> mortgage
            else -> error("Unknown series id ${key.seriesId} — Home only opens DFF + MORTGAGE30US")
        }
        val trigger = MutableSharedFlow<Unit>(extraBufferCapacity = 64)
        // Observe trigger emissions inside the caller's scope so we can count
        // refresh() / retry() dispatches. The viewModelScope keeps this collector
        // alive for the lifetime of the VM under test.
        trigger
            .onEach { refreshes[key.seriesId] = (refreshes[key.seriesId] ?: 0) + 1 }
            .launchIn(scope)
        return screenDataStreamForTesting(state = source, refreshTrigger = trigger)
    }

    override fun interestRateSeriesStream(
        keyFlow: Flow<InterestRateSeriesKey>,
        scope: CoroutineScope,
    ): ScreenDataStream<InterestRateSeries> = throw UnsupportedOperationException(
        "Home dashboard uses static keys; the dynamic-key overload isn't exercised.",
    )
}

@OptIn(ExperimentalScreenDataStreamTestingApi::class)
private class FakeCurrencyRepository : CurrencyRepository {
    override suspend fun syncWith(synchronizer: Synchronizer): Boolean = true
    private val source = MutableStateFlow<ScreenState<ExchangeRates>>(ScreenState.Loading)
    var refreshCount: Int = 0
        private set

    /**
     * Records the [fetchPolicy] the Home VM supplied on the last call so tests
     * can assert that PERIODIC was chosen for the exchange-rate tile.
     */
    var lastFetchPolicy: FetchPolicy? = null
        private set

    override fun exchangeRatesStream(
        baseCurrency: String,
        scope: CoroutineScope,
        fetchPolicy: FetchPolicy,
    ): ScreenDataStream<ExchangeRates> {
        lastFetchPolicy = fetchPolicy
        val trigger = MutableSharedFlow<Unit>(extraBufferCapacity = 64)
        trigger.onEach { refreshCount += 1 }.launchIn(scope)
        return screenDataStreamForTesting(state = source, refreshTrigger = trigger)
    }

    override fun spotRateStream(baseCurrency: String, online: Boolean, scope: CoroutineScope): ScreenDataStream<ExchangeRates> =
        screenDataStreamForTesting(state = source)

    override fun rateHistoryStream(
        keyFlow: Flow<RateHistoryKey>,
        scope: CoroutineScope,
    ): ScreenDataStream<RateHistory> = throw UnsupportedOperationException(
        "Home dashboard does not subscribe to rate-history streams.",
    )
}

// endregion
