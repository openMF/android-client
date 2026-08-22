/*
 * Copyright 2025 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See See https://github.com/openMF/kmp-project-template/blob/main/LICENSE
 */
package kpt.feature.home.di

import kpt.feature.home.ui.HomeViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

/**
 * Koin module for the field-officer home tab. Registers the nav-scoped [HomeViewModel] that
 * backs the home board ([kpt.feature.home.HomeDashboard]).
 */
val HomeModule = module {
    viewModelOf(::HomeViewModel)
}
