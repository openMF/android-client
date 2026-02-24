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

import com.mifos.core.common.utils.extractErrorMessage
import com.mifos.core.datastore.UserPreferencesRepository
import com.mifos.core.model.objects.clients.ChargeCreationResponse
import com.mifos.core.model.objects.clients.Page
import com.mifos.core.model.objects.payloads.ChargesPayload
import com.mifos.core.model.objects.template.client.ChargeTemplate
import com.mifos.core.network.BaseApiManager
import com.mifos.room.entities.client.ChargesEntity
import com.mifos.room.helper.ChargeDaoHelper
import io.ktor.client.statement.bodyAsText
import io.ktor.http.isSuccess
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.onEach
import kotlinx.serialization.json.Json

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
     * @param resourceId different types of resource ID like ClientId, SavingAccountId and etc.
     * @param offset   Offset From Which Position Charge List user want
     * @param limit    Maximum Limit of the Response Charge List Size
     * @return Page<Charge> Page of Charge in Which List Size is according to Limit and from
     * where position is Starting according to offset</Charge>>
     */
    @OptIn(ExperimentalCoroutinesApi::class)
    fun getListOfPagingCharges(
        resourceType: String,
        resourceId: Int,
        offset: Int,
        limit: Int,
    ): Flow<Page<ChargesEntity>> {
        return prefManager.userInfo.flatMapLatest { userData ->
            when (userData.userStatus) {
                false -> mBaseApiManager.chargeService.getListOfPagingCharges(
                    resourceType,
                    resourceId,
                    offset,
                    limit,
                )
                    .onEach { chargeDatabase.saveClientCharges(it, resourceId) }

                true -> {
                    if (offset == 0) {
                        chargeDatabase.readClientCharges(resourceId)
                    } else {
                        flowOf(Page())
                    }
                }
            }
        }
    }

    suspend fun getChargeTemplate(resourceType: String, resourceId: Int): ChargeTemplate {
        return mBaseApiManager.chargeService.getChargeTemplate(resourceType, resourceId)
    }

    suspend fun createCharges(
        resourceType: String,
        resourceId: Int,
        payload: ChargesPayload,
    ): ChargeCreationResponse {
        val response =
            mBaseApiManager.chargeService.createCharges(resourceType, resourceId, payload)

        if (!response.status.isSuccess()) {
            val errorMsg = extractErrorMessage(response)

            throw IllegalStateException(errorMsg)
        }

        return Json.decodeFromString<ChargeCreationResponse>(response.bodyAsText())
    }

    suspend fun deleteCharge(
        resourceId: Int,
        resourceType: String,
        chargeId: Int,
    ) {
        mBaseApiManager.chargeService.deleteCharge(
            resourceType = resourceType,
            resourceId = resourceId,
            chargeId = chargeId,
        )
    }

    suspend fun updateCharge(
        resourceId: Int,
        resourceType: String,
        chargeId: Int,
        payload: ChargesPayload,
    ) {
        mBaseApiManager.chargeService.updateCharge(
            resourceType = resourceType,
            resourceId = resourceId,
            chargeId = chargeId,
            payload = payload,
        )
    }

    fun getListOfClientCharges(
        resourceType: String,
        resourceId: Int,
    ): Flow<Page<ChargesEntity>> {
        return mBaseApiManager.chargeService.getListOfClientCharges(
            resourceType = resourceType,
            resourceId = resourceId,
        )
    }

    fun getListOfOtherAccountCharge(
        resourceType: String,
        resourceId: Int,
    ): Flow<List<ChargesEntity>> {
        return mBaseApiManager.chargeService.getListOfOtherAccountCharge(
            resourceType = resourceType,
            resourceId = resourceId,
        )
    }

    fun getCharge(
        resourceType: String,
        resourceId: Int,
        chargeId: Int,
    ): Flow<ChargesEntity> {
        return mBaseApiManager.chargeService.getCharge(
            resourceType = resourceType,
            resourceId = resourceId,
            chargeId = chargeId,
        )
    }
}
