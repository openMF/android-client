/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.core.network.model

/**
 * Created by Tarun on 06-07-2017.
 */
data class RequestCollectionSheetPayload(
    var dateFormat: String = "dd MMMM yyyy",
    var locale: String = "en",
    var officeId: Int? = null,
    var staffId: Int? = null,
    var transactionDate: String = "",
)
