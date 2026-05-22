/*
 * Copyright 2026 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mifos-x-field-officer-app/blob/master/LICENSE.md
 */
package com.mifos.feature.document.di

import com.mifos.feature.document.ui.DocumentDialogKind
import com.mifos.feature.document.ui.DocumentListViewModel
import com.mifos.feature.document.ui.UploadDocumentViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val DocumentModule = module {
    viewModelOf(::DocumentListViewModel)

    // Upload/Update dialog VM seeds itself from positional `parametersOf(...)`
    // at the call site (entityId / entityType / kind / documentId? / name /
    // description). The `documentId` slot is nullable to cover the Upload kind.
    factory { params ->
        UploadDocumentViewModel(
            repository = get(),
            entityId = params.get(),
            entityType = params.get(),
            kind = params.get<DocumentDialogKind>(),
            documentId = params.getOrNull(),
            initialName = params.get(),
            initialDescription = params.get(),
        )
    }
}
