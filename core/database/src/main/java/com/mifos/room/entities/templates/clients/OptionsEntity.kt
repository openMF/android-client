/*
 * Copyright 2025 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.room.entities.templates.clients

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(tableName = "ClientTemplateOptions")
data class OptionsEntity(
    val optionType: String? = null,

    @PrimaryKey
    val id: Int = 0,

    val name: String = "",

    val position: Int = 0,

    val description: String? = null,

    @SerializedName("isActive")
    val activeStatus: Boolean = false,
) : Parcelable
