/*
 * Copyright 2025 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mifos-x-field-officer-app/blob/master/LICENSE.md
 */
package com.mifos.feature.settings.di

import com.mifos.feature.settings.settings.SettingsViewModel
import com.mifos.feature.settings.updateServer.UpdateServerConfigViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

// SyncSurveysDialogViewModel + SyncSurveysDialogRepositoryImp are quarantined until W20 (survey wave).
// SyncSurveysDialogRepositoryImp references DataManagerSurveys (deleted); restored via Store5 pattern at W20.

val SettingsModule = module {
    viewModelOf(::SettingsViewModel)
    viewModelOf(::UpdateServerConfigViewModel)
}
