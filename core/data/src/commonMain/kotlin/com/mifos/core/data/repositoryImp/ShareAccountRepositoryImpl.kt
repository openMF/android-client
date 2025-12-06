/*
 * Copyright 2025 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.core.data.repositoryImp

import com.mifos.core.common.utils.DataState
import com.mifos.core.common.utils.asDataStateFlow
import com.mifos.core.data.repository.ShareAccountRepository
import com.mifos.core.network.GenericResponse
import com.mifos.core.network.datamanager.DataManagerShare
import com.mifos.core.network.model.share.ShareAccountPayload
import com.mifos.core.network.model.share.ShareTemplate
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class ShareAccountRepositoryImpl(
    private val dataManagerShare: DataManagerShare,
) : ShareAccountRepository {

    override fun getShareTemplate(clientId: Int, productId: Int?): Flow<DataState<ShareTemplate>> {
        return dataManagerShare.getShareTemplate(clientId, productId).asDataStateFlow()
    }

    override suspend fun createShareAccount(
        shareAccountPayload: ShareAccountPayload,
    ): Flow<DataState<GenericResponse>> {
        return flow {
            emit(dataManagerShare.createShareAccount(shareAccountPayload))
        }.asDataStateFlow()
    }
}
