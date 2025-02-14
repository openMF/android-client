/*
 * Copyright 2025 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.room.helper

import com.mifos.room.dao.OfficeDao
import com.mifos.room.entities.organisation.OfficeEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class OfficeDaoHelper @Inject constructor(
    private val officeDao: OfficeDao,
) {

    suspend fun saveAllOffices(offices: List<OfficeEntity>) {
        val officeEntities = offices.map { it }
        officeDao.insertOffices(officeEntities)
    }

    fun readAllOffices(): Flow<List<OfficeEntity>> {
        return officeDao.getAllOffices()
            .map { entities -> entities.map { it } }
    }
}
