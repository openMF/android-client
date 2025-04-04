/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.core.data.repositoryImp

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.mifos.core.data.pagingSource.ClientChargesPagingSource
import com.mifos.core.data.repository.ClientChargeRepository
import com.mifos.core.network.datamanager.DataManagerCharge
import com.mifos.room.entities.client.ChargesEntity
import kotlinx.coroutines.flow.Flow

/**
 * Created by Aditya Gupta on 08/08/23.
 */
class ClientChargeRepositoryImp(
    private val dataManagerCharge: DataManagerCharge,
) : ClientChargeRepository {

    override fun getClientCharges(
        clientId: Int,
    ): Flow<PagingData<ChargesEntity>> {
        return Pager(
            config = PagingConfig(
                pageSize = 10,
            ),
            pagingSourceFactory = {
                ClientChargesPagingSource(clientId, dataManagerCharge)
            },
        ).flow
    }
}
