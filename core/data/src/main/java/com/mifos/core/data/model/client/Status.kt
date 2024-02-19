/*
 * This project is licensed under the open source MPL V2.
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.core.data.model.client

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 * This Model is the common for Client and Group. So we can use it in both client and group
 * database.
 * Created by ishankhanna on 09/02/14.
 */
@Parcelize
data class Status(

    var id: Int = 0,

    var code: String? = null,

    var value: String? = null,
) : Parcelable {


    companion object {
        private val STATUS_ACTIVE = "Active"

        fun isActive(value: String): Boolean {
            return value == STATUS_ACTIVE
        }
    }
}