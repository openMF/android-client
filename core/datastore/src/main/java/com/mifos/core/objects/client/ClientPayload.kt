package com.mifos.core.objects.client

import android.os.Parcelable
import com.mifos.core.database.MifosDatabase
import com.mifos.core.model.MifosBaseModel
import com.mifos.core.objects.noncore.DataTablePayload
import com.raizlabs.android.dbflow.annotation.Column
import com.raizlabs.android.dbflow.annotation.ModelContainer
import com.raizlabs.android.dbflow.annotation.PrimaryKey
import com.raizlabs.android.dbflow.annotation.Table
import kotlinx.parcelize.Parcelize

/*
 * This project is licensed under the open source MPL V2.
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */ /**
 * Created by ADMIN on 16-Jun-15.
 */
@Parcelize
@Table(database = MifosDatabase::class, useBooleanGetterSetters = false)
@ModelContainer
data class ClientPayload(
    @PrimaryKey(autoincrement = true)
    @Transient
    var id: Int? = null,

    @Column
    @Transient
    var clientCreationTime: Long? = null,

    @Column
    @Transient
    var errorMessage: String? = null,

    @Column
    var firstname: String? = null,

    @Column
    var lastname: String? = null,

    @Column
    var middlename: String? = null,

    @Column
    var officeId: Int? = null,

    @Column
    var staffId: Int? = null,

    @Column
    var genderId: Int? = null,

    @Column
    var active: Boolean? = null,

    @Column
    var activationDate: String? = null,

    @Column
    var submittedOnDate: String? = null,

    @Column
    var dateOfBirth: String? = null,

    @Column
    var mobileNo: String? = null,

    @Column
    var externalId: String? = null,

    @Column
    var clientTypeId: Int? = null,

    @Column
    var clientClassificationId: Int? = null,

    var address: List<Address>? = ArrayList(),

    @Column
    var dateFormat: String? = "dd MMMM YYYY",

    @Column
    var locale: String? = "en",

    var datatables: List<DataTablePayload>? = null
) : MifosBaseModel(), Parcelable