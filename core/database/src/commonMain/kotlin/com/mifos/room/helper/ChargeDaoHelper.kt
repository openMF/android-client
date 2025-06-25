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

import com.mifos.core.common.network.Dispatcher
import com.mifos.core.common.network.MifosDispatchers
import com.mifos.core.model.objects.clients.Page
import com.mifos.room.dao.ChargeDao
import com.mifos.room.entities.client.ChargesEntity
import com.mifos.room.entities.client.ClientDateEntity
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map

/**
 * Created by Pronay Sarker on 14/02/2025 (3:36 PM)
 */
class ChargeDaoHelper(
    private val chargeDao: ChargeDao,
    @Dispatcher(MifosDispatchers.IO)
    private val ioDispatcher: CoroutineDispatcher,
) {
    /**
     * This Method save the All Client Charges in Database and save the Charge Due date in the
     * ClientDate as reference with Charge Id.
     *
     * @param chargesPage
     * @param clientId
     * @return null
     */
    suspend fun saveClientCharges(
        chargesPage: Page<ChargesEntity>,
        clientId: Int,
    ) {
        val updatedCharges = chargesPage.pageItems.map { charges ->
            val dateParts = charges.dueDate.orEmpty()

            val clientDate = if (dateParts.size == 3) {
                charges.id.toLong().let { chargeId ->
                    ClientDateEntity(
                        0,
                        chargeId,
                        dateParts[2],
                        dateParts[1],
                        dateParts[0],
                    )
                }
            } else {
                null
            }

            charges.copy(clientId = clientId, chargeDueDate = clientDate)
        }

        chargeDao.insertAllCharges(updatedCharges)
    }

    /**
     * This method Retrieve the Charges from Charges_Table and set the Charges Due date after
     * loading the Charge due date from the ChargeDate_table as reference with charge Id.
     *
     * @param clientId Client ID
     * @return Page of Charges
     */
    fun readClientCharges(clientId: Int): Flow<Page<ChargesEntity>> {
        return chargeDao.getClientCharges(clientId)
            .map { chargesList ->
                Page<ChargesEntity>().apply {
                    pageItems = chargesList.map { charge ->
                        charge.copy(dueDate = charge.dueDate)
                    }
                }
            }
            .flowOn(ioDispatcher)
    }
}
