/*
 * This project is licensed under the open source MPL V2.
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.core.objects.client

import android.os.Parcelable
import com.mifos.core.database.MifosDatabase
import com.mifos.core.model.MifosBaseModel
import com.raizlabs.android.dbflow.annotation.Column
import com.raizlabs.android.dbflow.annotation.PrimaryKey
import com.raizlabs.android.dbflow.annotation.Table
import kotlinx.parcelize.Parcelize

/**
 * This Model is the common for Client and Group. So we can use it in both client and group
 * database.
 * Created by ishankhanna on 09/02/14.
 */
@Parcelize
@Table(database = MifosDatabase::class)
data class Status(
    @PrimaryKey
    var id: Int = 0,

    @Column
    var code: String? = null,

    @Column
    var value: String? = null,
) : MifosBaseModel(), Parcelable {


    companion object {
        private val STATUS_ACTIVE = "Active"

        fun isActive(value: String): Boolean {
            return value == STATUS_ACTIVE
        }
    }
}