/*
 * Copyright 2025 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.core.network.mappers.clients

import com.mifos.core.network.model.ChargesResponse
import com.mifos.room.entities.client.ChargesEntity

class ChargesMapper {
    fun ChargesResponse.toEntity(): ChargesEntity {
        return ChargesEntity(
            id = 0,
            clientId = this.clientId,
            loanId = this.loanId,
            chargeId = this.chargeId,
            name = this.name,
            chargeTimeTypeId = this.chargeTimeType?.id,
            chargeDueDateClientId = this.chargeDueDate?.clientId,
            chargeCalculationTypeId = this.chargeCalculationType?.id,
            currencyId = this.currency?.id,
            dueDate = this.dueDate,
            amount = this.amount,
            amountPaid = this.amountPaid,
            amountWaived = this.amountWaived,
            amountWrittenOff = this.amountWrittenOff,
            amountOutstanding = this.amountOutstanding,
            penalty = this.penalty,
            active = this.active,
            paid = this.paid,
            waived = this.waived,
        )
    }
}
