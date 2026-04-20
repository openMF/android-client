/*
 * Copyright 2025 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mifos-x-field-officer-app/blob/master/LICENSE.md
 */
package com.mifos.room.dao

import com.mifos.room.entities.organisation.OfficeEntity
import kotlinx.coroutines.flow.Flow
import template.core.base.database.Dao
import template.core.base.database.Insert
import template.core.base.database.OnConflictStrategy
import template.core.base.database.Query

@Dao
interface OfficeDao {

    @Insert(entity = OfficeEntity::class, onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOffices(officeEntity: List<OfficeEntity>)

    @Query("SELECT * FROM Office")
    fun getAllOffices(): Flow<List<OfficeEntity>>
}
