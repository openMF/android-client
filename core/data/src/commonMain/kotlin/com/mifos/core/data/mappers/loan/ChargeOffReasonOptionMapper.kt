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

import com.mifos.core.model.objects.account.loan.ChargeOffReasonOption
import com.mifos.core.network.dto.loans.ChargeOffReasonOptionDto

fun ChargeOffReasonOptionDto.toModel(): ChargeOffReasonOption = ChargeOffReasonOption(
    id = id,
    name = name,
    position = position,
    description = description,
    active = active,
    mandatory = mandatory,
)
