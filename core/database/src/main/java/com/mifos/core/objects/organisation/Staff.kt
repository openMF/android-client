/*
 * This project is licensed under the open source MPL V2.
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.core.objects.organisation

import android.os.Parcelable
import com.mifos.core.database.MifosDatabase
import com.mifos.core.model.MifosBaseModel
import com.raizlabs.android.dbflow.annotation.Column
import com.raizlabs.android.dbflow.annotation.ModelContainer
import com.raizlabs.android.dbflow.annotation.PrimaryKey
import com.raizlabs.android.dbflow.annotation.Table
import kotlinx.parcelize.Parcelize

/**
 * Created by ishankhanna on 14/07/14.
 */
@Parcelize
@Table(database = MifosDatabase::class)
@ModelContainer
data class Staff(
    @PrimaryKey
    var id: Int? = null,

    @Column
    var firstname: String? = null,

    @Column
    var lastname: String? = null,

    @Column
    var mobileNo: String? = null,

    @Column
    var displayName: String? = null,

    @Column
    var officeId: Int? = null,

    @Column
    var officeName: String? = null,

    @Column
    var isLoanOfficer: Boolean? = null,

    @Column
    var isActive: Boolean? = null
) : MifosBaseModel(), Parcelable