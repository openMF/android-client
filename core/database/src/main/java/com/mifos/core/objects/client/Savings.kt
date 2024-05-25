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