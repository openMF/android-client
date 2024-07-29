/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.core.model

import com.google.gson.Gson
import com.raizlabs.android.dbflow.structure.BaseModel

/**
 * Created by Rajan Maurya on 23/06/16.
 */
open class MifosBaseModel : BaseModel() {
    override fun toString(): String {
        return Gson().toJson(this)
    }
}
