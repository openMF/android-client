/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.core.database

import com.raizlabs.android.dbflow.annotation.Database

/**
 * Created by Rajan Maurya on 23/06/16.
 */
@Database(name = MifosDatabase.NAME, version = MifosDatabase.VERSION, foreignKeysSupported = true)
object MifosDatabase {
    // database name will be Mifos.db
    const val NAME = "Mifos"

    // Always Increase the Version Number
    const val VERSION = 2
}
