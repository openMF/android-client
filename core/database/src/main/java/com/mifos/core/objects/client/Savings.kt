/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.core.objects.client

import com.mifos.core.objects.accounts.savings.DepositType

/**
 * Created by nellyk on 2/19/2016.
 */
data class Savings(
    var id: Int? = null,

    var accountNo: String? = null,

    var productId: Int? = null,

    var productName: String? = null,

    var status: Status? = null,

    var currency: Currency? = null,

    var accountBalance: Double? = null,

    var additionalProperties: Map<String, Any> = HashMap(),

    var depositType: DepositType? = null,
)
