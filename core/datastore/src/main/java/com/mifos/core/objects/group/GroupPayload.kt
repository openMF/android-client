/*
 * This project is licensed under the open source MPL V2.
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.core.objects.group

import android.os.Parcelable
import com.mifos.core.database.MifosDatabase
import com.mifos.core.model.MifosBaseModel
import com.raizlabs.android.dbflow.annotation.Column
import com.raizlabs.android.dbflow.annotation.ModelContainer
import com.raizlabs.android.dbflow.annotation.PrimaryKey
import com.raizlabs.android.dbflow.annotation.Table
import kotlinx.parcelize.Parcelize

/**
 * Created by nellyk on 1/22/2016.
 */
@Parcelize
@Table(database = MifosDatabase::class, useBooleanGetterSetters = false)
@ModelContainer
data class GroupPayload(
    @PrimaryKey(autoincrement = true)
    @Transient
    var id: Int = 0,

    @Column
    @Transient
    var errorMessage: String? = null,

    @Column
    var officeId: Int = 0,

    @Column
    var active: Boolean = false,

    @Column
    var activationDate: String? = null,

    @Column
    var submittedOnDate: String? = null,

    @Column
    var externalId: String? = null,

    @Column
    var name: String? = null,

    @Column
    var locale: String? = null,

    @Column
    var dateFormat: String? = null
) : MifosBaseModel(), Parcelable