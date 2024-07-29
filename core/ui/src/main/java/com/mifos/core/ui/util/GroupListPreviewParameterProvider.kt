/*
 * Copyright 2024 Mifos Initiative
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.mifos.core.ui.util

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.paging.LoadState
import androidx.paging.LoadStates
import androidx.paging.PagingData
import com.mifos.core.objects.group.Group
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
