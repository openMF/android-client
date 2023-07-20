/*
 * This project is licensed under the open source MPL V2.
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.objects.noncore

import android.os.Parcelable
import com.mifos.api.local.MifosBaseModel
import com.mifos.api.local.MifosDatabase
import com.raizlabs.android.dbflow.annotation.Column
import com.raizlabs.android.dbflow.annotation.ModelContainer
import com.raizlabs.android.dbflow.annotation.PrimaryKey
import com.raizlabs.android.dbflow.annotation.Table
import kotlinx.parcelize.Parcelize

/**
 * Created by ishankhanna on 16/06/14.
 */
@Parcelize
@Table(database = MifosDatabase::class)
@ModelContainer
data class DataTable(
    @Column
    var applicationTableName: String? = null,

    var columnHeaderData: List<ColumnHeader> = ArrayList(),

    @PrimaryKey
    var registeredTableName: String? = null
) : MifosBaseModel(), Parcelable