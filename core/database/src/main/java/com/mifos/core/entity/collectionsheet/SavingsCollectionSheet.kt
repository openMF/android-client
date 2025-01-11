/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.core.entity.collectionsheet

import android.os.Parcelable
import com.mifos.core.entity.accounts.savings.Currency
import kotlinx.parcelize.Parcelize

/**
 * Created by Tarun on 31-07-17.
 */
@Parcelize
data class SavingsCollectionSheet(
    // The accountId is of String type only. It's not a mistake.
    var accountId: String? = null,

    var accountStatusId: Int = 0,

    var currency: Currency? = null,

    var depositAccountType: String? = null,

    var dueAmount: Int = 0,

    var productId: Int = 0,

    var productName: String? = null,

    var savingsId: Int = 0,
) : Parcelable
