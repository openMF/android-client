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
 * Created by ishankhanna on 23/06/14.
 */
@Parcelize
data class LoanApprovalRequest(
    var locale: String = "en",

    var dateFormat: String = "dd MM yyyy",

    var approvedOnDate: String? = null,

    var note: String? = null,
) : Parcelable
