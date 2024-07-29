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

import com.mifos.core.data.repository.IndividualCollectionSheetDetailsRepository
import com.mifos.core.network.GenericResponse
import com.mifos.core.network.datamanager.DataManagerCollectionSheet
import com.mifos.core.network.model.IndividualCollectionSheetPayload
import javax.inject.Inject

/**
 * Created by Aditya Gupta on 10/08/23.
 */
class IndividualCollectionSheetDetailsRepositoryImp @Inject constructor(
    private val dataManagerCollection: DataManagerCollectionSheet,
) :
    IndividualCollectionSheetDetailsRepository {

    override suspend fun saveIndividualCollectionSheet(payload: IndividualCollectionSheetPayload): GenericResponse {
        return dataManagerCollection.saveIndividualCollectionSheet(payload)
    }
}
