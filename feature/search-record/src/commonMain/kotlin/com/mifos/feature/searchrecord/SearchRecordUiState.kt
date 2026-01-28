/*
 * Copyright 2025 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.feature.searchrecord

import com.mifos.core.model.objects.searchrecord.GenericSearchRecord
import org.jetbrains.compose.resources.StringResource

sealed interface SearchRecordUiState {
    data object Idle : SearchRecordUiState
    data object Loading : SearchRecordUiState
    data object EmptyQuery : SearchRecordUiState
    data object NoResults : SearchRecordUiState
    data class Success(val records: List<GenericSearchRecord> = emptyList()) : SearchRecordUiState
    data class Error(val message: String, val messageRes: StringResource? = null) : SearchRecordUiState
}
