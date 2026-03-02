/*
 * Copyright 2026 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.core.model.objects.template.recurring.approval

import com.mifos.core.model.utils.DateConstants
import kotlinx.serialization.EncodeDefault
import kotlinx.serialization.Serializable

@Serializable
data class RecurringDepositApproval(
    @EncodeDefault(EncodeDefault.Mode.ALWAYS)
    var locale: String = DateConstants.LOCALE,

    @EncodeDefault(EncodeDefault.Mode.ALWAYS)
    var dateFormat: String = DateConstants.DATE_FORMAT,

    var approvedOnDate: String? = null,

    var note: String? = null,
)
