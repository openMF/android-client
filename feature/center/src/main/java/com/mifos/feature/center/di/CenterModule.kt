/*
 * Copyright 2025 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.feature.center.di

import com.mifos.feature.center.centerDetails.CenterDetailsViewModel
import com.mifos.feature.center.centerGroupList.GroupListViewModel
import com.mifos.feature.center.centerList.ui.CenterListViewModel
import com.mifos.feature.center.createCenter.CreateNewCenterViewModel
import com.mifos.feature.center.syncCentersDialog.SyncCentersDialogViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val CenterModule = module {
    viewModelOf(::CenterDetailsViewModel)
    viewModelOf(::GroupListViewModel)
    viewModelOf(::CenterListViewModel)
    viewModelOf(::CreateNewCenterViewModel)
    viewModelOf(::SyncCentersDialogViewModel)
}
