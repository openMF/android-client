package com.mifos.feature.checkerInboxTask.di

import com.mifos.feature.checkerInboxTask.checkerInbox.CheckerInboxViewModel
import com.mifos.feature.checkerInboxTask.checkerInboxTasks.CheckerInboxTasksViewModel
import com.mifos.feature.checkerInboxTask.checkerInboxDialog.CheckerInboxDialogViewmodel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val CheckerInboxTaskModule = module {
    viewModelOf(::CheckerInboxViewModel)
    viewModelOf(::CheckerInboxTasksViewModel)
    viewModelOf(::CheckerInboxDialogViewmodel)
}