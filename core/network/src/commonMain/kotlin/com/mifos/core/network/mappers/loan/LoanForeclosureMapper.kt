/*
 * Copyright 2026 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mifos-x-field-officer-app/blob/master/LICENSE.md
 */
package com.mifos.core.network.mappers.loan

import com.mifos.core.model.objects.account.loan.foreclosure.LoanForeclosureInput
import com.mifos.core.model.objects.account.loan.foreclosure.LoanForeclosureTemplate
import com.mifos.core.network.model.loan.LoanForeclosureRequestDto
import com.mifos.core.network.model.loan.LoanForeclosureTemplateDto

fun LoanForeclosureTemplateDto.toDomain(): LoanForeclosureTemplate =
    LoanForeclosureTemplate(
        loanId = loanId,
        amount = amount,
        principalPortion = principalPortion,
        interestPortion = interestPortion,
        feeChargesPortion = feeChargesPortion,
        penaltyChargesPortion = penaltyChargesPortion,
    )

fun LoanForeclosureInput.toDto(): LoanForeclosureRequestDto =
    LoanForeclosureRequestDto(
        transactionDate = transactionDate,
        note = note,
        dateFormat = dateFormat,
        locale = locale,
    )
