/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.core.ui.util

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.paging.LoadState
import androidx.paging.LoadStates
import androidx.paging.PagingData
import com.mifos.core.entity.group.Group
import com.mifos.core.testing.repository.sampleGroups
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

class GroupListSuccessPreviewParameterProvider : PreviewParameterProvider<Flow<PagingData<Group>>> {
    override val values: Sequence<Flow<PagingData<Group>>>
        get() = sequenceOf(
            flowOf(
                PagingData.from(
                    data = sampleGroups,
                    sourceLoadStates = LoadStates(
                        refresh = LoadState.NotLoading(false),
                        append = LoadState.NotLoading(false),
                        prepend = LoadState.NotLoading(false),
                    ),
                ),
            ),
        )
}

class GroupListEmptyPreviewParameterProvider : PreviewParameterProvider<Flow<PagingData<Group>>> {
    override val values: Sequence<Flow<PagingData<Group>>>
        get() = sequenceOf(
            flowOf(
                PagingData.empty(
                    sourceLoadStates = LoadStates(
                        refresh = LoadState.NotLoading(false),
                        append = LoadState.NotLoading(false),
                        prepend = LoadState.NotLoading(false),
                    ),
                ),
            ),
        )
}

class GroupListLoadingPreviewParameterProvider : PreviewParameterProvider<Flow<PagingData<Group>>> {
    override val values: Sequence<Flow<PagingData<Group>>>
        get() = sequenceOf(
            flowOf(
                PagingData.empty(
                    sourceLoadStates = LoadStates(
                        refresh = LoadState.Loading,
                        append = LoadState.Loading,
                        prepend = LoadState.Loading,
                    ),
                ),
            ),
        )
}

class GroupListErrorPreviewParameterProvider : PreviewParameterProvider<Flow<PagingData<Group>>> {
    override val values: Sequence<Flow<PagingData<Group>>>
        get() = sequenceOf(
            flowOf(
                PagingData.empty(
                    sourceLoadStates = LoadStates(
                        refresh = LoadState.Error(Throwable("Unable to fetch data from server")),
                        append = LoadState.Loading,
                        prepend = LoadState.NotLoading(false),
                    ),
                ),
            ),
        )
}

class GroupListItemPreviewParameterProvider : PreviewParameterProvider<Group> {
    override val values: Sequence<Group>
        get() = sequenceOf(sampleGroups[1])
}
