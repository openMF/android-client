/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.core.objects.survey

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.mifos.core.database.MifosDatabase
import com.mifos.core.model.MifosBaseModel
import kotlinx.parcelize.Parcelize

/**
 * Created by Nasim Banu on 27,January,2016.
 */
@Parcelize
@Entity("ResponseDatas")
data class ResponseDatas(
    @PrimaryKey
    var responseId: Int = 0,

    @ColumnInfo("questionId")
    @Transient
    var questionId: Int = 0,

    @ColumnInfo("text")
    var text: String? = null,

    @ColumnInfo("sequenceNo")
    var sequenceNo: Int = 0,

    @ColumnInfo("value")
    var value: Int = 0,
) : MifosBaseModel(), Parcelable
