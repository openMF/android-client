/*
 * This project is licensed under the open source MPL V2.
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.core.objects.survey

import android.os.Parcelable
import com.mifos.core.database.MifosDatabase
import com.mifos.core.model.MifosBaseModel
import com.raizlabs.android.dbflow.annotation.Column
import com.raizlabs.android.dbflow.annotation.PrimaryKey
import com.raizlabs.android.dbflow.annotation.Table
import kotlinx.parcelize.Parcelize

/**
 * Created by Rajan Maurya on 28/3/16.
 */
@Parcelize
@Table(database = MifosDatabase::class)
data class ComponentDatas(
    @PrimaryKey
    var id: Int? = null,

    @Column
    var key: String? = null,

    @Column
    var text: String? = null,

    @Column
    var description: String? = null,

    @Column
    var sequenceNo: Int = 0
) : MifosBaseModel(), Parcelable