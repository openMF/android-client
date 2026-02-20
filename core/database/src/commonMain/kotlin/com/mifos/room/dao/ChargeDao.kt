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

import com.mifos.room.entities.client.ChargesEntity
import kotlinx.coroutines.flow.Flow
import template.core.base.database.Dao
import template.core.base.database.Insert
import template.core.base.database.OnConflictStrategy
import template.core.base.database.Query

/**
 * Created by Pronay Sarker on 14/02/2025 (3:32 PM)
 */
@Dao
interface ChargeDao {

    @Query("SELECT * FROM Charges where clientId = :clientId")
    fun getClientCharges(clientId: Int): Flow<List<ChargesEntity>>

    @Insert(entity = ChargesEntity::class, onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllCharges(charges: List<ChargesEntity>)
}
