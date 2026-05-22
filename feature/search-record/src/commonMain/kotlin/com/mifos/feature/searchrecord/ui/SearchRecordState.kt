/*
 * Copyright 2026 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mifos-x-field-officer-app/blob/master/LICENSE.md
 */
package com.mifos.feature.searchrecord.ui

import androidclient.feature.search_record.generated.resources.Res
import androidclient.feature.search_record.generated.resources.search_record_address
import androidclient.feature.search_record.generated.resources.search_record_identifiers
import com.mifos.core.model.objects.searchrecord.GenericSearchRecord
import com.mifos.core.model.objects.searchrecord.RecordType
import org.jetbrains.compose.resources.StringResource
import template.core.base.store.screen.ScreenState

data class SearchRecordState(
    val recordType: RecordType = RecordType.ADDRESS,
    val searchQuery: String = "",
    val screenState: ScreenState<List<GenericSearchRecord>> = ScreenState.Empty,
) {
    val displayTitle: StringResource
        get() = when (recordType) {
            RecordType.ADDRESS -> Res.string.search_record_address
            RecordType.IDENTIFIER -> Res.string.search_record_identifiers
        }
}

sealed interface SearchRecordEvent {
    data object NavigateBack : SearchRecordEvent
    data class NavigateToRecord(val record: GenericSearchRecord) : SearchRecordEvent
}

sealed interface SearchRecordAction {
    data class SearchQueryChanged(val query: String) : SearchRecordAction
    data object ClearSearch : SearchRecordAction
    data object NavigateBack : SearchRecordAction
    data class SelectRecord(val record: GenericSearchRecord) : SearchRecordAction
}
