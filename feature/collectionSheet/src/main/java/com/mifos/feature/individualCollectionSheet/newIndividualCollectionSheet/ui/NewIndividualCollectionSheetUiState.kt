/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.feature.individualCollectionSheet.newIndividualCollectionSheet.ui

import com.mifos.core.objects.collectionsheet.IndividualCollectionSheet
import com.mifos.core.objects.organisation.Office
import com.mifos.core.objects.organisation.Staff

/**
 * Created by Aditya Gupta on 10/08/23.
 */
data class NewIndividualCollectionSheetUiState(

    val isLoading: Boolean = false,

    val error: String? = null,

    val officeList: List<Office> = emptyList(),

    val staffList: List<Staff> = emptyList(),

    val individualCollectionSheet: IndividualCollectionSheet? = null,
)
