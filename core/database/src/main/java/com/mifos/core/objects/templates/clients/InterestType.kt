/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.core.objects.templates.clients

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.mifos.core.model.MifosBaseModel
import kotlinx.parcelize.Parcelize

/**
 * Created by Rajan Maurya on 06/07/16.
 */
@Entity("ClientTemplateInterest")
@Parcelize
data class InterestType(
    @PrimaryKey
    var id: Int = 0,

    @ColumnInfo("code")
    var code: String = "",

    @ColumnInfo("value")
    var value: String = "",
) : MifosBaseModel(), Parcelable
