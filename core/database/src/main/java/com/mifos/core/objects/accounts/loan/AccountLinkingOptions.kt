/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.core.objects.accounts.loan

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 * Created by Tarun on 12/16/2016.
 */
@Parcelize
data class AccountLinkingOptions(
    var accountNo: String? = null,

    var clientId: Int? = null,

    var clientName: String? = null,

    var currency: Currency? = null,

    var fieldOfficerId: Int? = null,

    var id: Int? = null,

    var productId: Int? = null,

    var productName: String? = null,
) : Parcelable
