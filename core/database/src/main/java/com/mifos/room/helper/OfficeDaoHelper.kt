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

import com.mifos.core.model.objects.databaseobjects.office.Office
import com.mifos.room.dao.OfficeDao
import com.mifos.room.model.toOffice
import com.mifos.room.model.toOfficeEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class OfficeDaoHelper @Inject constructor(
    private val officeDao: OfficeDao,
) {

    suspend fun saveAllOffices(offices: List<Office>) {
        val officeEntities = offices.map { it.toOfficeEntity() }
        officeDao.insertOffices(officeEntities)
    }

    fun readAllOffices(): Flow<List<Office>> {
        return officeDao.getAllOffices()
            .map { entities -> entities.map { it.toOffice() } }
    }
}
