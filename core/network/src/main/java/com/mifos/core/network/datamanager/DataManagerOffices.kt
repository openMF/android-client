/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.core.network.datamanager

import com.mifos.core.datastore.UserPreferencesRepository
import com.mifos.core.network.BaseApiManager
import com.mifos.core.network.mappers.offices.GetOfficeResponseMapper
import com.mifos.room.entities.organisation.OfficeEntity
import com.mifos.room.helper.OfficeDaoHelper
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow

/**
 * This DataManager is for Managing Offices API, In which Request is going to Server
 * and In Response, We are getting Offices API Observable Response using Retrofit2.
 * DataManagerOffices saving response in Database and response to Presenter as accordingly.
 *
 * Created by Rajan Maurya on 7/7/16.
 */
class DataManagerOffices(
    val mBaseApiManager: BaseApiManager,
    private val baseApiManager: org.mifos.core.apimanager.BaseApiManager,
    private val officeDaoHelper: OfficeDaoHelper,
    private val prefManager: UserPreferencesRepository,
) {
    /**
     * return all List of Offices from DatabaseHelperOffices
     */
    fun offices(): Flow<List<OfficeEntity>> {
        return flow {
            emit(
                baseApiManager.getOfficeApi().retrieveOffices(null, null, null).map(
                    GetOfficeResponseMapper::mapFromEntity,
                ),
            )
        }
    }

    val offices: Flow<List<OfficeEntity>>
        get() = prefManager.userInfo.flatMapLatest { userData ->
            when (userData.userStatus) {
                false -> flow {
                    baseApiManager.getOfficeApi().retrieveOffices(null, null, null)
                        .map { GetOfficeResponseMapper.mapFromEntity(it) }
                }

                true ->
                    /**
                     * return all List of Offices from DatabaseHelperOffices
                     */
                    /**
                     * return all List of Offices from DatabaseHelperOffices
                     */
                    officeDaoHelper.readAllOffices()
            }
        }
}
