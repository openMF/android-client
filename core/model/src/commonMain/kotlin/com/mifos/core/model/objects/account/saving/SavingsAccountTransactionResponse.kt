/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.core.model.objects.account.saving

import com.mifos.core.model.objects.Changes
import com.mifos.core.model.utils.Parcelable
import com.mifos.core.model.utils.Parcelize
import kotlinx.serialization.Serializable

/**
 * Created by ishankhanna on 12/06/14.
 */
@Serializable
@Parcelize
data class SavingsAccountTransactionResponse(
    var officeId: Int? = null,

    var clientId: Int? = null,

    var savingsId: Int? = null,

    var resourceId: Int? = null,

    var changes: Changes? = null,
) : Parcelable
