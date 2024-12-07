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
import com.google.gson.annotations.Expose
import kotlinx.parcelize.Parcelize

@Parcelize
data class LoanRepaymentResponse(
    @Expose
    var officeId: Int? = null,

    @Expose
    var clientId: Int? = null,

    @Expose
    var loanId: Int? = null,

    @Expose
    var resourceId: Int? = null,

    @Expose
    var changes: com.mifos.core.objects.Changes? = null,
) : Parcelable
