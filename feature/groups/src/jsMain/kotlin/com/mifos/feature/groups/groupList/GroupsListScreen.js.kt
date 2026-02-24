/*
 * Copyright 2026 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.feature.groups.groupList

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable

@Composable
internal actual fun GroupsListRoute(
    paddingValues: PaddingValues,
    onAddGroupClick: () -> Unit,
    onGroupClick: (groupId: Int) -> Unit,
    viewModel: GroupsListViewModel,
) {
}
