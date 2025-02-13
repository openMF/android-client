/*
 * Copyright 2025 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.room.entities.survey

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(tableName = "ResponseDatas")
data class ResponseDatas(
    @PrimaryKey
    val responseId: Int = 0,

    @Transient
    val questionId: Int = 0,

    val text: String? = null,

    val sequenceNo: Int = 0,

    val value: Int = 0,
) : Parcelable
