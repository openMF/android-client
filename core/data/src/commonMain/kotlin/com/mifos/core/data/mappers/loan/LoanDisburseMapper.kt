/*
 * Copyright 2026 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mifos-x-field-officer-app/blob/master/LICENSE.md
 */
package com.mifos.core.data.mappers.loan

import com.mifos.core.data.mappers.toModel
import com.mifos.core.model.objects.account.loan.loanDisburse.LoanDisburseInput
import com.mifos.core.model.objects.account.loan.loanDisburse.LoanDisburseTemplate
import com.mifos.core.model.objects.account.loan.loanWithAssociations.Currency
import com.mifos.core.network.dto.loans.disburse.LoanDisburseRequestDto
import com.mifos.core.network.dto.loans.template.LoanDisburseTemplateDto

fun LoanDisburseInput.toDto(): LoanDisburseRequestDto = LoanDisburseRequestDto(
    actualDisbursementDate = actualDisbursementDate,
    transactionAmount = transactionAmount,
    paymentTypeId = paymentTypeId,
    note = note,
    externalId = externalId,
    accountNumber = accountNumber,
    checkNumber = checkNumber,
    routingCode = routingCode,
    receiptNumber = receiptNumber,
    bankNumber = bankNumber,
    locale = locale,
    dateFormat = dateFormat,
)

fun LoanDisburseTemplateDto.toModel(): LoanDisburseTemplate = LoanDisburseTemplate(
    netDisbursalAmount = netDisbursalAmount,
    date = date,
    currency = currency?.toModel() ?: Currency(),
    paymentTypeOptions = paymentTypeOptions.map { it.toModel() },
)
