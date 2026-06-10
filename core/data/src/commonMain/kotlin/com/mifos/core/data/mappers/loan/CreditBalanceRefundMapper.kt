/*
 * Copyright 2025 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mifos-x-field-officer-app/blob/master/LICENSE.md
 */
package com.mifos.core.data.mappers.loan

import com.mifos.core.model.objects.account.loan.creditBalanceRefund.CreditBalanceRefundInput
import com.mifos.core.model.objects.account.loan.creditBalanceRefund.CreditBalanceRefundResponse
import com.mifos.core.model.objects.account.loan.creditBalanceRefund.LoanRefundDetails
import com.mifos.core.network.dto.loan.CreditBalanceRefundRequestDto
import com.mifos.core.network.dto.loan.CreditBalanceRefundResponseDto
import com.mifos.room.entities.accounts.loans.LoanRefundDetailsEntity

/**
 * Maps domain input model to network DTO for credit balance refund requests.
 */
fun CreditBalanceRefundInput.toDto(): CreditBalanceRefundRequestDto = CreditBalanceRefundRequestDto(
    transactionDate = transactionDate,
    transactionAmount = transactionAmount,
    dateFormat = dateFormat,
    locale = locale,
    externalId = externalId,
    note = note,
)

/**
 * Maps Room entity to domain model.
 * Used in repository when reading from database (offline or online).
 */
fun LoanRefundDetailsEntity.toDomain(): LoanRefundDetails = LoanRefundDetails(
    id = loanId,
    accountNo = accountNo,
    clientName = clientName,
    totalOverpaid = totalOverpaid,
    transactionDate = defaultTransactionDate,
    currencyCode = currencyCode,
    decimalPlaces = decimalPlaces,
)

/**
 * Maps network DTO response to domain model.
 */
fun CreditBalanceRefundResponseDto.toDomain(): CreditBalanceRefundResponse {
    return CreditBalanceRefundResponse(
        clientId = clientId,
        transactionId = resourceId,
    )
}
