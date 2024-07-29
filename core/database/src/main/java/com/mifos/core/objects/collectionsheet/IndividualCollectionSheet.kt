/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.core.objects.collectionsheet

import android.os.Parcelable
import com.mifos.core.objects.accounts.loan.PaymentTypeOptions
import kotlinx.parcelize.Parcelize

/**
 * Created by Tarun on 06-07-2017.
 */
@Parcelize
data class IndividualCollectionSheet(
    var dueDate: IntArray? = null,

    var clients: ArrayList<ClientCollectionSheet>? = null,

    var paymentTypeOptions: ArrayList<PaymentTypeOptions>? = null,
) : Parcelable
