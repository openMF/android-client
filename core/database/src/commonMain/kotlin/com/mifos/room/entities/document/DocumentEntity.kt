/*
 * Copyright 2025 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mifos-x-field-officer-app/blob/master/LICENSE.md
 */
package com.mifos.room.entities.document

import androidx.room3.Entity
import kotlinx.serialization.Serializable

/**
 * Flattened Room row for a single document's METADATA (not the binary file).
 *
 * The Fineract network payload (`com.mifos.core.model.objects.noncoreobjects.Document`)
 * is already flat — every field is a scalar — so it maps 1:1 onto Room columns with no
 * nested objects to unpack. Following this fork's Store5 house style (see
 * `CheckerTaskEntity` / `LoanTransactionEntity`), the domain shape is persisted verbatim so
 * the cached list round-trips back into `Document` for the `DocumentListScreen` (which
 * renders `name`, `description`, and uses `id` for download/update/remove).
 *
 * ### Composite primary key — scoped by parent
 * A document list is fetched per PARENT via the REST path
 * `.../{entityType}/{entityId}/documents`, so the same `documents` table caches many parents'
 * lists side by side. The Fineract document `id` is unique per parent path but is NOT assumed
 * globally unique here, so the primary key is the composite
 * `(parentEntityType, parentEntityId, id)`. The leading `(parentEntityType, parentEntityId)`
 * PK prefix is exactly the filter used by [DocumentDao.pageFlow], so no extra index is
 * needed. The parent scoping columns are populated from the Store KEY (not the domain
 * object's own nullable `parentEntityType`), mirroring how `LoanTransactionEntity` takes its
 * `loanId` from the store key.
 */
@Entity(
    tableName = "documents",
    primaryKeys = ["parentEntityType", "parentEntityId", "id"],
)
@Serializable
data class DocumentEntity(
    val id: Int,

    /** Parent scoping column — the store key's `entityType` (e.g. "clients", "loans"). */
    val parentEntityType: String,

    /** Parent scoping column — the store key's `entityId`. */
    val parentEntityId: Int,

    val name: String? = null,

    val fileName: String? = null,

    val size: Long = 0,

    val type: String? = null,

    val description: String? = null,
)
