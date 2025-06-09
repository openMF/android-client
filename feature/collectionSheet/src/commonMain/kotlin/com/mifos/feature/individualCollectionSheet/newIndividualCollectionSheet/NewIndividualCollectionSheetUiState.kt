/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.feature.individualCollectionSheet.newIndividualCollectionSheet

import com.mifos.room.entities.collectionsheet.IndividualCollectionSheet
import com.mifos.room.entities.organisation.OfficeEntity
import com.mifos.room.entities.organisation.StaffEntity

data class NewIndividualCollectionSheetUiState(

    val isLoading: Boolean = false,

    val error: String? = null,

    val officeList: List<OfficeEntity> = emptyList(),

    val staffList: List<StaffEntity> = emptyList(),

    val individualCollectionSheet: IndividualCollectionSheet? = null,
)
