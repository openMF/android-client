/*
 * This project is licensed under the open source MPL V2.
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.objects

import com.google.gson.annotations.Expose

class Changes {
    @Expose
    var transactionDate: String? = null

    @Expose
    var transactionAmount: String? = null

    @Expose
    var locale: String? = null

    @Expose
    var dateFormat: String? = null

    @Expose
    var note: String? = null

    @Expose
    var accountNumber: String? = null

    @Expose
    var checkNumber: String? = null

    @Expose
    var routingCode: String? = null

    @Expose
    var receiptNumber: String? = null

    @Expose
    var bankNumber: String? = null
}