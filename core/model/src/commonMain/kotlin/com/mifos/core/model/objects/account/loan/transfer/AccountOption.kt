/*
 * Copyright 2026 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mifos-x-field-officer-app/blob/master/LICENSE.md
 */
package com.mifos.core.model.objects.account.loan.transfer

import com.mifos.core.model.objects.template.loan.Currency
import com.mifos.core.model.utils.Parcelable
import com.mifos.core.model.utils.Parcelize
import kotlinx.serialization.Serializable

/**
 * Data class representing an account option for transfers
 */
@Parcelize
@Serializable
data class AccountOption(
    val id: Int? = null,
    val accountNo: String? = null,
    val clientId: Int? = null,
    val clientName: String? = null,
    val productId: Int? = null,
    val productName: String? = null,
    val fieldOfficerId: Int? = null,
    val currency: Currency? = null,
    val currencyCodeFromCurrency: String? = null,
) : Parcelable
