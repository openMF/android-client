/*
 * Copyright 2025 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.feature.settings.di

import com.mifos.feature.settings.settings.SettingsViewModel
import com.mifos.feature.settings.syncSurvey.SyncSurveysDialogRepository
import com.mifos.feature.settings.syncSurvey.SyncSurveysDialogRepositoryImp
import com.mifos.feature.settings.syncSurvey.SyncSurveysDialogViewModel
import com.mifos.feature.settings.updateServer.UpdateServerConfigViewModel
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.bind
import org.koin.dsl.module

val SettingsModule = module {
    viewModelOf(::SettingsViewModel)
    viewModelOf(::SyncSurveysDialogViewModel)
    viewModelOf(::UpdateServerConfigViewModel)

    singleOf(::SyncSurveysDialogRepositoryImp) bind SyncSurveysDialogRepository::class
}
