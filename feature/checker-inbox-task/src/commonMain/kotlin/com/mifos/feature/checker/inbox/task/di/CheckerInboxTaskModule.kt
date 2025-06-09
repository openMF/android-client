/*
 * Copyright 2025 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.feature.checker.inbox.task.di

import com.mifos.feature.checker.inbox.task.checkerInbox.CheckerInboxViewModel
import com.mifos.feature.checker.inbox.task.checkerInboxDialog.CheckerInboxDialogViewmodel
import com.mifos.feature.checker.inbox.task.checkerInboxTasks.CheckerInboxTasksViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val CheckerInboxTaskModule = module {
    viewModelOf(::CheckerInboxViewModel)
    viewModelOf(::CheckerInboxTasksViewModel)
    viewModelOf(::CheckerInboxDialogViewmodel)
}
