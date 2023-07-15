package com.mifos.objects.client

import com.mifos.objects.accounts.savings.Currency
import com.mifos.objects.accounts.savings.DepositType
import com.mifos.objects.accounts.savings.Status

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