/*
 * Copyright 2026 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See See https://github.com/openMF/kmp-project-template/blob/main/LICENSE
 */
package kpt.core.data.note.impl

import kpt.core.base.data.annotation.RepositoryBinding
import kpt.core.base.store.mutation.CommandSpec
import kpt.core.base.store.mutation.MutationGateway
import kpt.core.base.store.mutation.MutationPolicy
import kpt.core.base.store.mutation.MutationResult
import kpt.core.data.note.NoteCommandRepository
import kpt.core.data.note.NoteSubmitException
import kpt.core.model.note.NoteDraft
import kpt.core.network.mifos.note.api.NoteApi
import kpt.core.network.mifos.note.dto.NoteRequestDto

@RepositoryBinding(binds = NoteCommandRepository::class)
internal class NoteCommandRepositoryImpl(
    private val noteApi: NoteApi,
    private val gateway: MutationGateway,
) : NoteCommandRepository {

    override suspend fun addNewNote(
        resourceType: String,
        resourceId: Long,
        noteRequestDto: NoteRequestDto,
        idempotencyKey: String,
    ) = online {
        noteApi.addNewNote(
            resourceType = resourceType,
            resourceId = resourceId,
            noteRequestDto = noteRequestDto,
            idempotencyKey = idempotencyKey,
        )
    }

    override suspend fun deleteNote(resourceType: String, resourceId: Long, noteId: Long) =
        online { noteApi.deleteNote(resourceType = resourceType, resourceId = resourceId, noteId = noteId) }

    override suspend fun updateNote(resourceType: String, resourceId: Long, noteId: Long, noteRequestDto: NoteRequestDto) =
        online { noteApi.updateNote(resourceType = resourceType, resourceId = resourceId, noteId = noteId, noteRequestDto = noteRequestDto) }

    override suspend fun submitDraft(draft: NoteDraft) {
        // A REPLAY of a create may be re-sending something the server already accepted — the first
        // attempt's response simply never reached the client (timeout, process death, auth race).
        // `Idempotency-Key` is sent for this, but `apis.mifos.community` ignores it (measured
        // 2026-10-21, see NoteApi), so without a client-side check the replay writes a SECOND note.
        //
        // Only CREATES need this. An update and a delete name the note they act on, so replaying
        // either is naturally idempotent.
        if (draft.noteId == null && alreadyOnServer(draft)) return

        val dto = NoteRequestDto(note = draft.text)
        // Bound to a local: `noteId` is a nullable property of a class in another module, so the
        // compiler cannot smart-cast it inside the branch.
        val editing = draft.noteId
        val result = if (editing == null) {
            addNewNote(draft.resourceType, draft.resourceId, dto, draft.idempotencyKey)
        } else {
            updateNote(draft.resourceType, draft.resourceId, editing, dto)
        }

        // Unwrap to throw-to-fail. `OfflineSubmitSyncer` marks a draft SUBMITTED when the block
        // RETURNS, so swallowing a Blocked here would delete a note that never reached Fineract —
        // the exact data loss this draft exists to prevent.
        when (result) {
            is MutationResult.Applied -> Unit
            is MutationResult.Blocked -> throw NoteSubmitException.Offline(result.reason.toString())
            is MutationResult.Failed -> throw NoteSubmitException.Rejected(result.cause)
            is MutationResult.Conflicted ->
                throw NoteSubmitException.Rejected(IllegalStateException("conflict ${'$'}{result.conflictId}"))
        }
    }

    /**
     * Commands run [MutationPolicy.OnlineRequired]: they await the server and write nothing
     * offline, yielding `Blocked(OFFLINE)` rather than a local success the officer would act on.
     *
     * The call is closed over rather than threaded through [CommandSpec.payload]: the payload hook
     * exists for `localApply` / `rollback` / `conflictOf`, none of which apply to a write that never
     * lands locally. It also keeps the 28 endpoints whose body is nullable usable, which
     * `CommandSpec<P : Any>` would otherwise reject.
     */
    /**
     * Has this exact draft already landed?
     *
     * Matches on TEXT within the resource, which is the only identity a create has before the
     * server assigns one. Deliberately conservative in the one direction that matters: on any
     * doubt — the read fails, the list is unreachable — it returns false and the note is sent.
     * A duplicate note is recoverable by deleting one; a silently dropped note is not, and
     * dropping it would defeat the outbox this whole path exists to serve.
     *
     * The narrow false-positive is an officer deliberately writing the SAME text twice on one
     * client while the first is still queued. Judged acceptable against losing work: notes are
     * free-text observations, and an exact repeat of a queued one is far rarer than a replay.
     */
    private suspend fun alreadyOnServer(draft: NoteDraft): Boolean = runCatching {
        noteApi.retrieveListNotes(draft.resourceType, draft.resourceId)
            .any { it.note?.trim() == draft.text.trim() }
    }.getOrDefault(false)

    private suspend fun <R : Any> online(endpoint: suspend () -> R): MutationResult<R> =
        gateway.command(
            CommandSpec(payload = Unit, endpoint = { endpoint() }),
            policy = MutationPolicy.OnlineRequired,
        )
}
