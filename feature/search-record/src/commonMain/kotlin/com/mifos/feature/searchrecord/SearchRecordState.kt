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

data class SearchRecordState(
    val searchQuery: String = "",
    val displayTitle: String = "",
    val searchRecords: List<GenericSearchRecord> = emptyList(),
    val dialogState: DialogState? = null,
    val isNoResultsFound: Boolean = false,
) {
    sealed interface DialogState {
        data object Loading : DialogState
        data class Error(val message: String, val messageRes: StringResource? = null) : DialogState
    }
}
