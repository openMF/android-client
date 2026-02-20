/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.core.model.objects.collectionsheets

import com.mifos.core.common.utils.ApiDateFormatter
import com.mifos.core.model.utils.Parcelable
import com.mifos.core.model.utils.Parcelize

/**
 * Created by Tarun on 25-07-2017.
 */
@Parcelize
data class CollectionSheetRequestPayload(
    var calendarId: Int? = null,

    var dateFormat: String = ApiDateFormatter.DATE_FORMAT,

    var locale: String = ApiDateFormatter.LOCALE,

    var transactionDate: String? = null,
) : Parcelable
