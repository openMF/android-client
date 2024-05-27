package com.mifos.core.objects.client

/**
 * Created by nellyk on 2/15/2016.
 */
/*
 * This project is licensed under the open source MPL V2.
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
data class ChargesRename (
    var chargeId: Int? = null,

    var amount: String? = null,

    var locale: String? = null,

    var dueDate: String? = null
)