package com.mifos.feature.groups.di

import org.koin.dsl.module
import com.mifos.feature.groups.createNewGroup.CreateNewGroupViewModel
import com.mifos.feature.groups.groupDetails.GroupDetailsViewModel
import com.mifos.feature.groups.groupList.GroupsListViewModel
import com.mifos.feature.groups.syncGroupDialog.SyncGroupsDialogViewModel
import org.koin.core.module.dsl.viewModelOf

val GroupsModule = module {
    viewModelOf(::CreateNewGroupViewModel)
    viewModelOf(::GroupDetailsViewModel)
    viewModelOf(::GroupsListViewModel)
    viewModelOf(::SyncGroupsDialogViewModel)
}