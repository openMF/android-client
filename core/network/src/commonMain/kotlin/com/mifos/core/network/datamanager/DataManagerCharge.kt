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
import com.mifos.core.model.objects.clients.Page
import com.mifos.core.network.BaseApiManager
import com.mifos.room.entities.client.ChargesEntity
import com.mifos.room.helper.ChargeDaoHelper
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.onEach

/**
 * This DataManager is for Managing Charge API, In which Request is going to Server
 * and In Response, We are getting Charge API Observable Response using Retrofit2.
 * DataManagerCharge saving response in Database and response to Presenter as accordingly.
 *
 *
 * Created by Rajan Maurya on 4/7/16.
 */
class DataManagerCharge(
    val mBaseApiManager: BaseApiManager,
    val chargeDatabase: ChargeDaoHelper,
    private val prefManager: UserPreferencesRepository,
) {
    /**
     * This Method Request the Charge API at
     * https://demo.openmf.org/fineract-provider/api/v1/clients/{clientId}/charges
     * and in response get the of the Charge Page that contains Charges list.
     *
     * @param clientId Client Id
     * @param offset   Offset From Which Position Charge List user want
     * @param limit    Maximum Limit of the Response Charge List Size
     * @return Page<Charge> Page of Charge in Which List Size is according to Limit and from
     * where position is Starting according to offset</Charge>>
     */
    @OptIn(ExperimentalCoroutinesApi::class)
    fun getClientCharges(
        clientId: Int,
        offset: Int,
        limit: Int,
    ): Flow<Page<ChargesEntity>> {
        return prefManager.userInfo.flatMapLatest { userData ->
            when (userData.userStatus) {
                false -> mBaseApiManager.chargeService.getListOfCharges(clientId, offset, limit)
                    .onEach { chargeDatabase.saveClientCharges(it, clientId) }

                true -> {
                    if (offset == 0) {
                        chargeDatabase.readClientCharges(clientId)
                    } else {
                        flowOf(Page())
                    }
                }
            }
        }
    }
}
