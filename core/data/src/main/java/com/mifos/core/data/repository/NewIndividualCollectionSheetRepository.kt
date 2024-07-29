/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.core.data.repository

import com.mifos.core.network.model.RequestCollectionSheetPayload
import com.mifos.core.objects.collectionsheet.IndividualCollectionSheet
import com.mifos.core.objects.organisation.Office
import com.mifos.core.objects.organisation.Staff

/**
 * Created by Aditya Gupta on 10/08/23.
 */
interface NewIndividualCollectionSheetRepository {

    suspend fun getIndividualCollectionSheet(
        payload: RequestCollectionSheetPayload?,
    ): IndividualCollectionSheet

    suspend fun offices(): List<Office>

    suspend fun getStaffInOffice(officeId: Int): List<Staff>
}
