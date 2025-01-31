/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.core.model.objects.payloads

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
