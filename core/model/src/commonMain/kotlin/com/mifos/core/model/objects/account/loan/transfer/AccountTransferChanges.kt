/*
 * Copyright 2026 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.core.model.objects.account.loan.transfer

import com.mifos.core.model.utils.Parcelable
import com.mifos.core.model.utils.Parcelize
import kotlinx.serialization.Serializable

@Parcelize
@Serializable
data class AccountTransferChanges(
    val fromOfficeId: Int? = null,
    val fromClientId: Int? = null,
    val fromAccountId: Int? = null,
    val fromAccountType: Int? = null,
    val toOfficeId: Int? = null,
    val toClientId: Int? = null,
    val toAccountId: Int? = null,
    val toAccountType: Int? = null,
    val transferDate: String? = null,
    val transferAmount: Double? = null,
    val transferDescription: String? = null,
    val currencyCode: String? = null,
    val locale: String? = null,
    val dateFormat: String? = null,
) : Parcelable
