/*
 * Copyright 2026 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mifos-x-field-officer-app/blob/master/LICENSE.md
 */
package com.mifos.core.data.note.store

import kotlinx.serialization.Serializable

/**
 * Composite key for `NoteListStore` — identifies one notes list by its owning
 * resource (client / group / loan / savings). One Store5 cache row per pair.
 */
@Serializable
data class NoteListKey(
    val resourceType: String,
    val resourceId: Long,
)

/** Stable cache key for `FetchedAtRepository` indexing. */
fun NoteListKey.cacheKey(): String = "note:list:$resourceType:$resourceId"
