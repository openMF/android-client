/*
 * Copyright 2026 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mifos-x-field-officer-app/blob/master/LICENSE.md
 */
package com.mifos.room.note.entity

import template.core.base.database.Entity
import template.core.base.database.PrimaryKey

/**
 * Room cache row for a single note, keyed by `<resourceType>:<resourceId>:<noteId>`.
 *
 * Backs `core/data/note/store/NoteListStore` — Store5 reads/writes this table as
 * the SourceOfTruth so notes lists survive offline + replay on reconnect per
 * RULE-STORE5-FETCH-001.
 */
@Entity(
    indices = [],
    inheritSuperIndices = false,
    primaryKeys = [],
    foreignKeys = [],
    ignoredColumns = [],
    tableName = "note_cache",
)
data class NoteCacheEntity(
    @PrimaryKey(autoGenerate = false) val cacheKey: String,
    val resourceType: String,
    val resourceId: Long,
    val noteId: Long,
    val note: String,
    val clientId: Long? = null,
    val createdById: Long? = null,
    val createdByUsername: String? = null,
    val createdOn: String? = null,
    val updatedById: Long? = null,
    val updatedByUsername: String? = null,
    val updatedOn: String? = null,
) {
    companion object {
        fun key(resourceType: String, resourceId: Long, noteId: Long): String =
            "$resourceType:$resourceId:$noteId"
    }
}
