/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.core.modelobjects.template.client

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

/**
 * Created by mayankjindal on 13/12/16.
 */
@Parcelize
data class ChargeTimeType(
    @SerializedName("id")
    val id: Int? = null,
    @SerializedName("code")
    val code: String? = null,
    @SerializedName("value")
    val value: String? = null,
) : Parcelable {
    override fun toString(): String {
        return "ChargeTimeType(id=$id, code='$code', value='$value')"
    }
}
