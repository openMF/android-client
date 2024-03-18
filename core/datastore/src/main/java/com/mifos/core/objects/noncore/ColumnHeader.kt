/*
 * This project is licensed under the open source MPL V2.
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.core.objects.noncore

import android.os.Parcelable
import com.mifos.core.database.MifosDatabase
import com.mifos.core.model.MifosBaseModel
import com.raizlabs.android.dbflow.annotation.Column
import com.raizlabs.android.dbflow.annotation.ModelContainer
import com.raizlabs.android.dbflow.annotation.PrimaryKey
import com.raizlabs.android.dbflow.annotation.Table
import kotlinx.parcelize.Parcelize

/**
 * Created by ishankhanna on 16/06/14.
 */
@Parcelize
@Table(database = MifosDatabase::class, useBooleanGetterSetters = false)
@ModelContainer
data class ColumnHeader(
    @PrimaryKey(autoincrement = true)
    var id: Int? = null,

    /**
     * columnCode will only be returned if columnDisplayType = "CODELOOKUP"
     * and null otherwise
     */
    var columnCode: String? = null,

    @Column
    var columnDisplayType: String? = null,

    @Column
    var columnLength: Int? = null,

    @Column
    var dataTableColumnName: String? = null,

    @Column
    var columnType: String? = null,

    @Column
    var columnNullable: Boolean? = null,

    @Column
    var columnPrimaryKey: Boolean? = null,

    @Column
    var registeredTableName: String? = null,

    /**
     * columnValues are actually Code Values that are either created by
     * system or defined manually by users
     */
    var columnValues: List<ColumnValue> = ArrayList()
) : MifosBaseModel(), Parcelable