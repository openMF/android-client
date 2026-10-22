/*
 * Copyright 2026 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See See https://github.com/openMF/kmp-project-template/blob/main/LICENSE
 */
package kpt.core.data.document

import kotlinx.coroutines.CoroutineScope
import kpt.core.base.store.screen.ScreenDataStream
import kpt.core.model.objects.noncoreobjects.Document

interface DocumentRepository {
    /** Documents attached to any Fineract entity — clients, loans, groups all share this surface. */
    fun documentsStream(
        entityType: String,
        entityId: Int,
        scope: CoroutineScope,
    ): ScreenDataStream<List<Document>>

    /**
     * One document's bytes, or `null` when it cannot be fetched.
     *
     * A one-shot blob, NOT a [ScreenDataStream] — same reasoning as `ClientRepository.clientImage`:
     * the caller either has the file or does not, and there is no Loading/Empty/Error for a screen
     * to render around it.
     */
    suspend fun downloadDocument(entityType: String, entityId: Int, documentId: Int): ByteArray?
}
