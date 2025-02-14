/*
 * Copyright 2025 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.room.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.mifos.room.entities.client.Charges
import kotlinx.coroutines.flow.Flow

/**
 * Created by Pronay Sarker on 14/02/2025 (3:32 PM)
 */
@Dao
interface ChargeDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCharges(charge: List<Charges>)

    @Query("SELECT * FROM Charges where clientId = :clientId")
    fun getClientCharges(clientId: Int): Flow<List<Charges>>
}
