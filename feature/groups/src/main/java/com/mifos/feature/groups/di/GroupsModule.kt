/*
 * Copyright 2025 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.feature.groups.di

import com.mifos.feature.groups.createNewGroup.CreateNewGroupViewModel
import com.mifos.feature.groups.groupDetails.GroupDetailsViewModel
import com.mifos.feature.groups.groupList.GroupsListViewModel
import com.mifos.feature.groups.syncGroupDialog.SyncGroupsDialogViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val GroupsModule = module {
    viewModelOf(::CreateNewGroupViewModel)
    viewModelOf(::GroupDetailsViewModel)
    viewModelOf(::GroupsListViewModel)
    viewModelOf(::SyncGroupsDialogViewModel)
}
