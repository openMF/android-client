/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.core.entity.survey

import android.os.Parcelable
import com.mifos.core.database.MifosDatabase
import com.mifos.core.model.MifosBaseModel
import com.mifos.room.entities.survey.ResponseDatas
import com.raizlabs.android.dbflow.annotation.Column
import com.raizlabs.android.dbflow.annotation.PrimaryKey
import com.raizlabs.android.dbflow.annotation.Table
import kotlinx.parcelize.Parcelize

/**
 * Created by Nasim Banu on 27,January,2016.
 */
@Parcelize
@Table(database = MifosDatabase::class)
data class QuestionDatas(
    @PrimaryKey
    var id: Int = 0,

    @Column
    @Transient
    var surveyId: Int = 0,

    @Column
    var componentKey: String? = null,

    @Column
    var key: String? = null,

    @Column
    var text: String? = null,

    @Column
    var description: String? = null,

    @Column
    var sequenceNo: Int = 0,

    var responseDatas: List<ResponseDatas> = ArrayList(),
) : MifosBaseModel(), Parcelable {

    var questionId: Int
        get() = id
        set(id) {
            this.id = id
        }
}
