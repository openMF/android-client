package com.mifos.feature.settings.di

import org.koin.dsl.module
import com.mifos.feature.settings.settings.SettingsViewModel
import com.mifos.feature.settings.syncSurvey.SyncSurveysDialogViewModel
import com.mifos.feature.settings.updateServer.UpdateServerConfigViewModel
import org.koin.core.module.dsl.viewModelOf

val SettingsModule = module {
    viewModelOf(::SettingsViewModel)
    viewModelOf(::SyncSurveysDialogViewModel)
    viewModelOf(::UpdateServerConfigViewModel)
}