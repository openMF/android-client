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

// TODO Remove calendarId and TransactionDate from this Payload class;
open class Payload {
    var dateFormat = "dd MMMM YYYY"
    var locale = "en"
    var calendarId: Long = 0
    var transactionDate: String? = null
    override fun toString(): String {
        return "{" +
            "dateFormat='" + dateFormat + '\'' +
            ", locale='" + locale + '\'' +
            ", calendarId=" + calendarId +
            ", transactionDate='" + transactionDate + '\'' +
            '}'
    }
}
