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
import com.mifos.core.common.utils.Page
import com.mifos.room.dao.CenterDao
import com.mifos.room.entities.accounts.CenterAccounts
import com.mifos.room.entities.center.CenterPayloadEntity
import com.mifos.room.entities.group.CenterDateEntity
import com.mifos.room.entities.group.CenterEntity
import com.mifos.room.entities.group.CenterWithAssociations
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map

/**
 * Created by Pronay Sarker on 03/02/2025 (7:32 PM)
 */
class CenterDaoHelper(
    private val centerDatabase: CenterDao,
    @Dispatcher(MifosDispatchers.IO)
    private val ioDispatcher: CoroutineDispatcher,
) {
    /**
     * Reading All Centers from table of Center and return the CenterList
     *
     * @return List Of Centers
     */
    // (todo from old code)
    //  TODO Implement Observable Transaction to load Center List
    fun readAllCenters(): Flow<Page<CenterEntity>> {
        return centerDatabase.readAllCenters().map { centers ->
            Page<CenterEntity>().apply { pageItems = centers }
        }.flowOn(ioDispatcher)
    }

    suspend fun saveCenterPayload(centerPayload: CenterPayloadEntity?) {
        centerPayload?.let {
            centerDatabase.saveCenterPayload(centerPayload)
        }
    }

    fun readAllCenterPayload(): Flow<List<CenterPayloadEntity>> {
        return centerDatabase.readAllCenterPayload()
            .flowOn(ioDispatcher)
    }

    /**
     * This Method Fetch the Groups that are attached to the Center.
     * @param centerId Center Id
     * @return CenterWithAssociations
     */
    fun getCenterAssociateGroups(centerId: Int): Flow<CenterWithAssociations> {
        return flow {
            val groups = centerDatabase.getCenterAssociateGroups(centerId).first()
            val centerWithAssociations = CenterWithAssociations()
            centerWithAssociations.groupMembers = groups
            emit(centerWithAssociations)
        }.flowOn(ioDispatcher)
    }

    /**
     * This Method Saving the Single Center in the Database
     *
     * @param center
     * @return Observable.just(Center)
     */
    suspend fun saveCenter(center: CenterEntity) {
        var updatedCenter: CenterEntity = center

        if (center.activationDate.isNotEmpty()) {
            val centerDate = center.id?.let {
                center.activationDate[0]?.let { it1 ->
                    center.activationDate[1]?.let { it2 ->
                        center.activationDate[2]?.let { it3 ->
                            CenterDateEntity(
                                it.toLong(),
                                0,
                                it1,
                                it2,
                                it3,
                            )
                        }
                    }
                }
            }
            updatedCenter = center.copy(centerDate = centerDate)
        }
        centerDatabase.saveCenter(updatedCenter)
    }

    /**
     * This Method for deleting the center payload from the Database according to Id and
     * again fetch the center List from the Database CenterPayload_Table
     * @param id is Id of the Center Payload in which reference center was saved into Database
     * @return List<CenterPayload></CenterPayload>>
     */
    // todo recheck logic
    fun deleteAndUpdateCenterPayloads(id: Int): Flow<List<CenterPayloadEntity>> {
        return flow {
            centerDatabase.deleteCenterPayloadById(id)
            emitAll(centerDatabase.readAllCenterPayload())
        }.flowOn(ioDispatcher)
    }

    suspend fun updateDatabaseCenterPayload(centerPayload: CenterPayloadEntity) {
        centerDatabase.updateCenterPayload(centerPayload)
    }

    /**
     * This Method  write the CenterAccounts in tho DB. According to Schema Defined in Model
     *
     * @param centerAccounts Model of List of LoanAccount and SavingAccount
     * @param centerId       Center Id
     * @return CenterAccounts
     */
    suspend fun saveCenterAccounts(
        centerAccounts: CenterAccounts,
        centerId: Int,
    ) {
        val loanAccounts = centerAccounts.loanAccounts
        val savingsAccounts = centerAccounts.savingsAccounts
        val memberLoanAccounts = centerAccounts.memberLoanAccounts

        for (loanAccount in loanAccounts) {
            val updatedLoanAccount = loanAccount.copy(centerId = centerId.toLong())
            centerDatabase.saveLoanAccount(updatedLoanAccount)
        }
        for (savingsAccount in savingsAccounts) {
            centerDatabase.saveSavingsAccount(
                savingsAccount.copy(
                    centerId = centerId.toLong(),
                ),
            )
        }
        for (memberLoanAccount in memberLoanAccounts) {
            val updatedLoanAccount = memberLoanAccount.copy(centerId = centerId.toLong())
            centerDatabase.saveMemberLoanAccount(updatedLoanAccount)
        }
    }
}
