/*
 * Copyright 2025 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mifos-x-field-officer-app/blob/master/LICENSE.md
 */
package com.mifos.room.entities.checkerinbox

import androidx.room3.Entity
import androidx.room3.PrimaryKey
import kotlinx.serialization.Serializable

/**
 * Flattened Room row for a single checker-inbox task.
 *
 * The Fineract network payload (`com.mifos.core.model.objects.checkerinboxtask.CheckerTask`)
 * is already flat — every field is a scalar (`String`/`Long`/`Int`) — so it maps 1:1 onto
 * Room columns with no nested objects to unpack. Following this fork's Store5 house style
 * (see `LoanTransactionEntity`), the domain shape is persisted verbatim so the cached list
 * round-trips back into `CheckerTask` for the `checkerInbox` screen (which renders `id`,
 * `actionName`, `entityName`, `processingResult`, `maker`, `madeOnDate` via `getDate()`,
 * and filters on `resourceId`).
 *
 * `id` is the globally-unique Fineract audit/task id and is the primary key. The checker
 * task list is a single global collection (no server-side scoping params are used today),
 * so the store is keyed by `Unit` and the table is a flat full-list cache refreshed
 * wholesale on each fetch.
 */
@Entity(
    tableName = "checker_tasks",
)
@Serializable
data class CheckerTaskEntity(
    @PrimaryKey
    val id: Int,

    val madeOnDate: Long,

    val processingResult: String,

    val maker: String,

    val actionName: String,

    val entityName: String,

    val resourceId: String,
)
