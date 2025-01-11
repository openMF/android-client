/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.core.objects.account.saving

import com.google.gson.annotations.Expose
import com.mifos.core.objects.Changes

/**
 * Created by ishankhanna on 12/06/14.
 */
data class SavingsAccountTransactionResponse(
    @Expose
    var officeId: Int? = null,

    @Expose
    var clientId: Int? = null,

    @Expose
    var savingsId: Int? = null,

    @Expose
    var resourceId: Int? = null,

    @Expose
    var changes: Changes? = null,
)
