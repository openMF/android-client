package com.mifos.core.data

/**
 * Created by nellyk on 2/15/2016.
 */
/*
 * This project is licensed under the open source MPL V2.
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
class ChargesPayload {
    var chargeId: Int? = null
    var clientId: Int? = null
    var loanId: Int? = null
    var amount: String? = null
    var locale: String? = null
    var dueDate: String? = null
    var dateFormat: String? = null
}