/*
 * Copyright 2025 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mifos-x-field-officer-app/blob/master/LICENSE.md
 */
package com.mifos.room.infra.entity

import androidx.room3.Entity
import androidx.room3.PrimaryKey

/**
 * Durable row for a form payload that failed to reach the server.
 *
 * Written by [com.mifos.core.data.infra.impl.RoomSubmitOutbox] when a network error
 * occurs during form submission. The row survives process death so the user can
 * resume or retry later.
 *
 * @param id          Auto-generated surrogate key.
 * @param formKey     Consumer-defined identifier that groups drafts by screen/form type
 *                    (e.g. `"loan_application"`, `"client_registration"`). One pending
 *                    draft per formKey is the intended invariant — consumers enforce
 *                    uniqueness via [com.mifos.room.infra.dao.DraftDao.deleteByFormKey] before
 *                    inserting.
 * @param payloadJson Serialized form payload (JSON). The concrete type is opaque to the
 *                    framework; consumers encode/decode via kotlinx.serialization.
 * @param status      Lifecycle state — one of
 *                    [template.core.base.store.submit.SubmitOutboxStatus] values
 *                    serialized as `.name`.
 * @param createdAtMs Epoch millis when the draft was first saved.
 * @param updatedAtMs Epoch millis of the most recent status transition.
 * @param errorMessage Last failure reason for display in the resume UI (nullable).
 */
@Entity(
    indices = [],
    inheritSuperIndices = false,
    primaryKeys = [],
    foreignKeys = [],
    ignoredColumns = [],
    tableName = "framework_submit_drafts",
)
data class DraftEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val formKey: String,
    val payloadJson: String,
    val status: String,
    val createdAtMs: Long,
    val updatedAtMs: Long,
    val errorMessage: String? = null,
)
