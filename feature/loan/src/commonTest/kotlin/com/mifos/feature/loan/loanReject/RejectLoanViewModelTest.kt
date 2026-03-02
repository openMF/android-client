/*
 * Copyright 2026 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.feature.loan.loanReject

import androidx.lifecycle.SavedStateHandle
import com.mifos.core.data.repository.LoanAccountRejectRepository
import com.mifos.core.domain.useCases.RejectLoanUseCase
import com.mifos.core.model.objects.account.loan.RejectLoanPayload
import com.mifos.core.model.objects.account.loan.RejectLoanResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlinx.datetime.LocalDate
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

/**
 * Unit tests for [RejectLoanViewModel].
 */
class RejectLoanViewModelTest {

    private val dispatcher = StandardTestDispatcher()
    private val fixedToday = LocalDate.parse("2026-03-02")
    private val fixedTomorrow = LocalDate.parse("2026-03-03")
    private val futureDateError = "Rejected date cannot be in the future"
    private val unknownError = "Unknown error"

    private lateinit var repository: FakeLoanAccountRejectRepository
    private lateinit var viewModel: RejectLoanViewModel

    @BeforeTest
    fun setUp() {
        Dispatchers.setMain(dispatcher)
        repository = FakeLoanAccountRejectRepository()
        viewModel = createViewModel()
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `rejectedOnDateError populated when date is after today`() = runTest(dispatcher) {
        viewModel.processIntent(RejectLoanViewIntent.RejectedOnDateChanged(fixedTomorrow))
        viewModel.processIntent(RejectLoanViewIntent.SubmitClicked)
        advanceUntilIdle()

        assertEquals(futureDateError, viewModel.state.value.rejectedOnDateError)
    }

    @Test
    fun `rejectedOnDateError not populated when date is today boundary is valid`() = runTest(dispatcher) {
        viewModel.processIntent(RejectLoanViewIntent.SubmitClicked)
        advanceUntilIdle()

        assertNull(viewModel.state.value.rejectedOnDateError)
    }

    @Test
    fun `API not called when validation fails`() = runTest(dispatcher) {
        viewModel.processIntent(RejectLoanViewIntent.RejectedOnDateChanged(fixedTomorrow))
        viewModel.processIntent(RejectLoanViewIntent.SubmitClicked)
        advanceUntilIdle()

        assertEquals(0, repository.callCount)
    }

    @Test
    fun `isLoading becomes true then false on successful submission`() = runTest(dispatcher) {
        repository.flowFactory = { _, _ ->
            flow {
                delay(100)
                emit(RejectLoanResponse(loanId = 11))
            }
        }

        viewModel.processIntent(RejectLoanViewIntent.SubmitClicked)
        runCurrent()
        assertTrue(viewModel.state.value.isLoading)

        advanceTimeBy(100)
        advanceUntilIdle()
        assertFalse(viewModel.state.value.isLoading)
    }

    @Test
    fun `isSuccess becomes true on successful submission`() = runTest(dispatcher) {
        viewModel.processIntent(RejectLoanViewIntent.SubmitClicked)
        advanceUntilIdle()

        assertTrue(viewModel.state.value.isSuccess)
    }

    @Test
    fun `submissionError populated on API failure`() = runTest(dispatcher) {
        repository.flowFactory = { _, _ ->
            flow {
                throw IllegalStateException("Loan rejection failed")
            }
        }

        viewModel.processIntent(RejectLoanViewIntent.SubmitClicked)
        advanceUntilIdle()

        assertEquals("Loan rejection failed", viewModel.state.value.submissionError)
    }

    @Test
    fun `isLoading becomes false on API failure`() = runTest(dispatcher) {
        repository.flowFactory = { _, _ ->
            flow {
                throw IllegalStateException("Loan rejection failed")
            }
        }

        viewModel.processIntent(RejectLoanViewIntent.SubmitClicked)
        advanceUntilIdle()

        assertFalse(viewModel.state.value.isLoading)
    }

    @Test
    fun `showDiscardDialog becomes true on CancelClicked with dirty form`() = runTest(dispatcher) {
        viewModel.processIntent(RejectLoanViewIntent.NoteChanged("Needs manager review"))
        viewModel.processIntent(RejectLoanViewIntent.CancelClicked)

        assertTrue(viewModel.state.value.showDiscardDialog)
    }

    @Test
    fun `popBackStack triggered on CancelClicked with clean form`() = runTest(dispatcher) {
        viewModel.processIntent(RejectLoanViewIntent.CancelClicked)

        assertTrue(viewModel.state.value.shouldNavigateBack)
        assertFalse(viewModel.state.value.showDiscardDialog)
    }

    @Test
    fun `NavigationHandled intent resets shouldNavigateBack to false`() = runTest(dispatcher) {
        viewModel.processIntent(RejectLoanViewIntent.CancelClicked)
        assertTrue(viewModel.state.value.shouldNavigateBack)

        viewModel.processIntent(RejectLoanViewIntent.NavigationHandled)

        assertFalse(viewModel.state.value.shouldNavigateBack)
    }

    @Test
    fun `DismissError intent clears submissionError`() = runTest(dispatcher) {
        repository.flowFactory = { _, _ ->
            flow {
                throw IllegalStateException("Loan rejection failed")
            }
        }
        viewModel.processIntent(RejectLoanViewIntent.SubmitClicked)
        advanceUntilIdle()
        assertEquals("Loan rejection failed", viewModel.state.value.submissionError)

        viewModel.processIntent(RejectLoanViewIntent.DismissError)

        assertNull(viewModel.state.value.submissionError)
    }

    @Test
    fun `rejectedOnDateError cleared when RejectedOnDateChanged intent is processed`() = runTest(dispatcher) {
        viewModel.processIntent(RejectLoanViewIntent.RejectedOnDateChanged(fixedTomorrow))
        viewModel.processIntent(RejectLoanViewIntent.SubmitClicked)
        advanceUntilIdle()
        assertEquals(futureDateError, viewModel.state.value.rejectedOnDateError)

        viewModel.processIntent(RejectLoanViewIntent.RejectedOnDateChanged(fixedToday))

        assertNull(viewModel.state.value.rejectedOnDateError)
    }

    private fun createViewModel(): RejectLoanViewModel {
        val savedStateHandle = SavedStateHandle(mapOf("loanId" to 11))
        return RejectLoanViewModel(
            rejectLoanUseCase = RejectLoanUseCase(repository),
            savedStateHandle = savedStateHandle,
            currentDateProvider = { fixedToday },
            futureDateErrorProvider = { futureDateError },
            unknownErrorProvider = { unknownError },
        )
    }

    private class FakeLoanAccountRejectRepository : LoanAccountRejectRepository {
        var callCount: Int = 0
        var lastLoanId: Int? = null
        var lastPayload: RejectLoanPayload? = null
        var flowFactory: (Int, RejectLoanPayload) -> Flow<RejectLoanResponse> =
            { _, _ -> flowOf(RejectLoanResponse(loanId = 11)) }

        override fun rejectLoan(
            loanId: Int,
            rejectLoanPayload: RejectLoanPayload,
        ): Flow<RejectLoanResponse> {
            callCount += 1
            lastLoanId = loanId
            lastPayload = rejectLoanPayload
            return flowFactory(loanId, rejectLoanPayload)
        }
    }
}
