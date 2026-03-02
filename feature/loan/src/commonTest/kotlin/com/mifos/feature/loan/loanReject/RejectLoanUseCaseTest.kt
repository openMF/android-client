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

import com.mifos.core.common.utils.DataState
import com.mifos.core.data.repository.LoanAccountRejectRepository
import com.mifos.core.domain.useCases.RejectLoanUseCase
import com.mifos.core.model.objects.account.loan.RejectLoanPayload
import com.mifos.core.model.objects.account.loan.RejectLoanResponse
import com.mifos.core.model.utils.DateConstants
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.toList
import kotlinx.io.IOException
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertTrue

/**
 * Unit tests for [RejectLoanUseCase].
 */
class RejectLoanUseCaseTest {

    @Test
    fun `emits Loading then Success when repository succeeds`() = kotlinx.coroutines.test.runTest {
        val repository = FakeLoanAccountRejectRepository().apply {
            flowFactory = { _, _ -> flowOf(RejectLoanResponse(loanId = 33)) }
        }
        val useCase = RejectLoanUseCase(repository)

        val emissions = useCase(33, payload()).toList()

        assertEquals(2, emissions.size)
        assertIs<DataState.Loading>(emissions[0])
        assertIs<DataState.Success<RejectLoanResponse>>(emissions[1])
    }

    @Test
    fun `emits Loading then Error when repository throws IOException`() = kotlinx.coroutines.test.runTest {
        val repository = FakeLoanAccountRejectRepository().apply {
            flowFactory = { _, _ ->
                flow {
                    throw IOException("Network error")
                }
            }
        }
        val useCase = RejectLoanUseCase(repository)

        val emissions = useCase(33, payload()).toList()

        assertEquals(2, emissions.size)
        assertIs<DataState.Loading>(emissions[0])
        val error = assertIs<DataState.Error<RejectLoanResponse>>(emissions[1])
        assertIs<IOException>(error.exception)
    }

    @Test
    fun `emits Loading then Error when repository throws HttpException`() = kotlinx.coroutines.test.runTest {
        val repository = FakeLoanAccountRejectRepository().apply {
            flowFactory = { _, _ ->
                flow {
                    throw TestHttpException("HTTP 400")
                }
            }
        }
        val useCase = RejectLoanUseCase(repository)

        val emissions = useCase(33, payload()).toList()

        assertEquals(2, emissions.size)
        assertIs<DataState.Loading>(emissions[0])
        val error = assertIs<DataState.Error<RejectLoanResponse>>(emissions[1])
        assertIs<TestHttpException>(error.exception)
    }

    @Test
    fun `delegates payload to repository correctly`() = kotlinx.coroutines.test.runTest {
        val repository = FakeLoanAccountRejectRepository()
        val useCase = RejectLoanUseCase(repository)
        val expectedPayload = payload(note = "declined")

        useCase(33, expectedPayload).toList()

        assertEquals(1, repository.callCount)
        assertEquals(33, repository.lastLoanId)
        assertEquals(expectedPayload, repository.lastPayload)
        assertTrue(repository.lastPayload?.note == "declined")
    }

    private fun payload(note: String? = null): RejectLoanPayload {
        return RejectLoanPayload(
            rejectedOnDate = "02 March 2026",
            note = note,
            locale = DateConstants.LOCALE,
            dateFormat = DateConstants.DATE_FORMAT,
        )
    }

    private class FakeLoanAccountRejectRepository : LoanAccountRejectRepository {
        var callCount: Int = 0
        var lastLoanId: Int? = null
        var lastPayload: RejectLoanPayload? = null
        var flowFactory: (Int, RejectLoanPayload) -> Flow<RejectLoanResponse> =
            { _, _ -> flowOf(RejectLoanResponse(loanId = 33)) }

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

    private class TestHttpException(
        message: String,
    ) : Exception(message)
}
