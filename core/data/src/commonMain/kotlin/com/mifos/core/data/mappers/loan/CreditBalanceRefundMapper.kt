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

import com.mifos.core.model.objects.account.loan.LoanRefundDetails
import com.mifos.core.model.objects.loan.CreditBalanceRefundInput
import com.mifos.core.network.dto.loan.CreditBalanceRefundRequestDto
import com.mifos.room.entities.accounts.loans.LoanWithAssociationsEntity

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
 * Maps database entity to domain model for credit balance refund.
 */
fun LoanWithAssociationsEntity.toRefundDetails(): LoanRefundDetails = LoanRefundDetails(
    id = id,
    accountNo = accountNo,
    clientName = clientName,
    totalOverpaid = totalOverpaid,
    currencyCode = currency.code,
    decimalPlaces = currency.decimalPlaces,
)
