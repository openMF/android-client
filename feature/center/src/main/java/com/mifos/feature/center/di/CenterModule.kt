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