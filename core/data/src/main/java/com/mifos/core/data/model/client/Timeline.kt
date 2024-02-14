/*
 * This project is licensed under the open source MPL V2.
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.core.data.model.client

import android.os.Parcelable
import com.mifos.core.data.model.database.MifosBaseModel
import kotlinx.parcelize.Parcelize

/**
 * Created by ishankhanna on 09/02/14.
 */
@Parcelize
class Timeline(
    var submittedOnDate: MutableList<Int> = ArrayList(),

    var submittedByUsername: String? = null,

    var submittedByFirstname: String? = null,

    var submittedByLastname: String? = null,

    var activatedOnDate: MutableList<Int> = ArrayList(),

    var activatedByUsername: String? = null,

    var activatedByFirstname: String? = null,

    var activatedByLastname: String? = null,

    var closedOnDate: MutableList<Int> = ArrayList(),

    var closedByUsername: String? = null,

    var closedByFirstname: String? = null,

    var closedByLastname: String? = null
) : MifosBaseModel(), Parcelable