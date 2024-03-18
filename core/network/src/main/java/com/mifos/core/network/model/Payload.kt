/*
 * This project is licensed under the open source MPL V2.
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.core.network.model

//TODO Remove calendarId and TransactionDate from this Payload class;
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