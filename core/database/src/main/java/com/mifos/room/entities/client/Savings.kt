/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.room.entities.client

import com.mifos.room.entities.accounts.savings.SavingAccountDepositTypeEntity

/**
 * Created by nellyk on 2/19/2016.
 */
data class Savings(
    val id: Int? = null,

    val accountNo: String? = null,

    val productId: Int? = null,

    val productName: String? = null,

    val status: ClientStatusEntity? = null,

    val currency: ClientChargeCurrencyEntity? = null,

    val accountBalance: Double? = null,

    val additionalProperties: Map<String, Any> = emptyMap(),

    val depositType: SavingAccountDepositTypeEntity? = null,
)
