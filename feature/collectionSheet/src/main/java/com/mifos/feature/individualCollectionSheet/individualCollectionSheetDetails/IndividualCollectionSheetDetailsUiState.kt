/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.feature.individualCollectionSheet.individualCollectionSheetDetails

/**
 * Created by Aditya Gupta on 10/08/23.
 */
sealed class IndividualCollectionSheetDetailsUiState {

    data object Empty : IndividualCollectionSheetDetailsUiState()

    data object Loading : IndividualCollectionSheetDetailsUiState()

    data class Error(val message: Int) : IndividualCollectionSheetDetailsUiState()

    data object SavedSuccessfully : IndividualCollectionSheetDetailsUiState()
}
