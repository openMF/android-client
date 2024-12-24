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

import androidx.room.Database
import androidx.room.RoomDatabase
import com.mifos.core.data.CenterPayload
import com.mifos.core.model.MifosBaseModel
import com.mifos.core.objects.PaymentTypeOption
import com.mifos.core.objects.Timeline
import com.mifos.core.objects.client.Client
import com.mifos.core.objects.client.ClientPayload
import kotlin.reflect.KClass


/**
 * Created by Rajan Maurya on 23/06/16.
 */

@Database(entities = [
    CenterPayload::class,
    Timeline::class,
    MifosBaseModel::class,
    CenterPayload::class,
    PaymentTypeOption::class,
    ClientPayload::class
    ],
    // Always Increase the Version Number
    version = 3, exportSchema = true,
)
abstract class MifosDatabase {
// TODO add all entities
}

