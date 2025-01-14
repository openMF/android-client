/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.core.model.objects.template.client

import com.mifos.core.common.utils.Parcelable
import com.mifos.core.common.utils.Parcelize

/**
 * Created by mayankjindal on 13/12/16.
 */
@Parcelize
data class ChargeAppliesTo(
    val id: Int,
    val code: String,
    val value: String,
) : Parcelable {
    override fun toString(): String {
        return "ChargeAppliesTo(id=$id, code=$code, value=$value)"
    }
}
