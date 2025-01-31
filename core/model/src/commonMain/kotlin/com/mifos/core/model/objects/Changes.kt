/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.core.model.objects

import com.mifos.core.common.utils.Parcelable
import com.mifos.core.common.utils.Parcelize
import kotlinx.serialization.Serializable

@Parcelize
@Serializable
class Changes(
    var transactionDate: String? = null,

    var transactionAmount: String? = null,

    var locale: String? = null,

    var dateFormat: String? = null,

    var note: String? = null,

    var accountNumber: String? = null,

    var checkNumber: String? = null,

    var routingCode: String? = null,

    var receiptNumber: String? = null,

    var bankNumber: String? = null,
) : Parcelable
