/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mifos-x-field-officer-app/blob/master/LICENSE.md
 */
package com.mifos.core.domain.useCases

import com.mifos.core.common.utils.DataState
import com.mifos.core.data.repository.DocumentListRepository
import com.mifos.core.model.objects.noncoreobjects.Document
import kotlinx.coroutines.flow.Flow

/**
 * @deprecated Pure-delegator use case. Wave 9 (Phase C of store5-adoption) replaces this with
 * direct `DocumentRepository.getDocuments(...)` calls from the feature ViewModel, wrapped in
 * try/catch → `ScreenState<List<Document>>` per RULE-STORE5-FETCH-001. Retained ONLY because
 * `feature/client` still consumes it; will be deleted in the Wave 9 → feature/client follow-up.
 */
@Deprecated(
    message = "Inject DocumentRepository directly and call getDocuments(...) inside the VM.",
    replaceWith = ReplaceWith(
        "DocumentRepository.getDocuments(entityType, entityId)",
        "com.mifos.core.data.document.DocumentRepository",
    ),
)
class GetDocumentsListUseCase(
    private val repository: DocumentListRepository,
) {
    operator fun invoke(
        entityType: String,
        entityId: Int,
    ): Flow<DataState<List<Document>>> =
        repository.getDocumentsList(entityType, entityId)
}
