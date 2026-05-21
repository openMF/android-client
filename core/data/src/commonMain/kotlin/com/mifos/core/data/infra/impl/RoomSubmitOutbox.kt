/*
 * Copyright 2025 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mifos-x-field-officer-app/blob/master/LICENSE.md
 */
package com.mifos.core.data.infra.impl

import com.mifos.room.infra.dao.DraftDao
import com.mifos.room.infra.entity.DraftEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.KSerializer
import kotlinx.serialization.json.Json
import template.core.base.store.submit.SubmitOutbox
import template.core.base.store.submit.SubmitOutboxEntry
import template.core.base.store.submit.SubmitOutboxStatus

/**
 * Room-backed [SubmitOutbox] that persists form payloads across process death.
 *
 * Serializes payloads to JSON via [kotlinx.serialization] and stores them in the
 * `framework_submit_drafts` table inside [com.mifos.room.MifosDatabase].
 *
 * Wire one instance per form type in your DI module:
 * ```kotlin
 * single<SubmitOutbox<LoanApplicationPayload>> {
 *     RoomSubmitOutbox(
 *         dao = get<MifosDatabase>().draftDao,
 *         serializer = LoanApplicationPayload.serializer(),
 *     )
 * }
 * ```
 *
 * @param P Payload type. Must be annotated with `@Serializable`.
 * @param dao The Room DAO for the `framework_submit_drafts` table.
 * @param serializer Kotlinx serializer for [P]. Obtain via `P.serializer()`.
 */
class RoomSubmitOutbox<P>(
    private val dao: DraftDao,
    private val serializer: KSerializer<P>,
) : SubmitOutbox<P> {

    private val json = Json { ignoreUnknownKeys = true }

    override suspend fun save(formKey: String, payload: P): Long {
        val nowMs = currentTimeMillis()
        val existing = dao.getPendingByFormKey(formKey)
        if (existing != null) {
            dao.updatePayload(existing.id, json.encodeToString(serializer, payload), nowMs)
            return existing.id
        }
        val entity = DraftEntity(
            formKey = formKey,
            payloadJson = json.encodeToString(serializer, payload),
            status = SubmitOutboxStatus.PENDING.name,
            createdAtMs = nowMs,
            updatedAtMs = nowMs,
        )
        return dao.insert(entity)
    }

    override suspend fun getPending(formKey: String): SubmitOutboxEntry<P>? =
        dao.getPendingByFormKey(formKey)?.toEntry()

    override fun observePending(formKey: String): Flow<SubmitOutboxEntry<P>?> =
        dao.observePendingByFormKey(formKey).map { it?.toEntry() }

    override suspend fun getAllPending(): List<SubmitOutboxEntry<P>> =
        dao.getAllPending().mapNotNull { it.toEntry() }

    override suspend fun markRetrying(id: Long) =
        dao.markRetrying(id, currentTimeMillis())

    override suspend fun markSubmitted(id: Long) =
        dao.markSubmitted(id, currentTimeMillis())

    override suspend fun markFailed(id: Long, error: String?) =
        dao.markFailed(id, currentTimeMillis(), error)

    override suspend fun deleteByFormKey(formKey: String) =
        dao.deleteByFormKey(formKey)

    override suspend fun deleteAll() =
        dao.deleteAll()

    private fun DraftEntity.toEntry(): SubmitOutboxEntry<P>? = try {
        SubmitOutboxEntry(
            id = id,
            formKey = formKey,
            payload = json.decodeFromString(serializer, payloadJson),
            status = SubmitOutboxStatus.valueOf(status),
            createdAtMs = createdAtMs,
            errorMessage = errorMessage,
        )
    } catch (_: Throwable) {
        null
    }
}

private fun currentTimeMillis(): Long = kotlin.time.Clock.System.now().toEpochMilliseconds()
