/*
 * Copyright 2026 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mifos-x-field-officer-app/blob/master/LICENSE.md
 */
package com.mifos.core.network.note.api

import com.mifos.core.network.APIEndPoint
import com.mifos.core.network.note.dto.CreateNoteResponseDto
import com.mifos.core.network.note.dto.DeleteNoteResponseDto
import com.mifos.core.network.note.dto.NoteDto
import com.mifos.core.network.note.dto.NoteRequestDto
import com.mifos.core.network.note.dto.UpdateNoteResponseDto
import de.jensklingenberg.ktorfit.http.Body
import de.jensklingenberg.ktorfit.http.DELETE
import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.POST
import de.jensklingenberg.ktorfit.http.PUT
import de.jensklingenberg.ktorfit.http.Path

/**
 * Fineract `{resourceType}/{resourceId}/notes` endpoints. Per-feature API surface
 * (Phase B per-resource layout). Modern suspend-only contract — `retrieveListNotes`
 * and `retrieveNote` now return `List<NoteDto>` / `NoteDto` directly instead of
 * `Flow<...>` (legacy `NoteService` shape).
 *
 * Errors throw (`HttpException` / transport exceptions); callers handle via
 * `SubmitHandler.submit { ... }` for mutations or explicit try/catch wrapped to
 * `ScreenState<T>` for reads. No `runCatching` per RULE-NO-RUN-CATCHING-001.
 */
interface NoteApi {

    /**
     * Add a Resource Note. Example requests: `clients/1/notes`, `groups/1/notes`.
     *
     * @param resourceType e.g. `clients`, `loans`, `groups`, `savingsaccounts`.
     * @param resourceId The owning resource id (clientId, loanId, etc.).
     * @param noteRequestDto The note body.
     */
    @POST("{resourceType}/{resourceId}/" + APIEndPoint.NOTES)
    suspend fun addNote(
        @Path("resourceType") resourceType: String,
        @Path("resourceId") resourceId: Long,
        @Body noteRequestDto: NoteRequestDto,
    ): CreateNoteResponseDto

    /**
     * Delete a Resource Note.
     */
    @DELETE("{resourceType}/{resourceId}/" + APIEndPoint.NOTES + "/{noteId}")
    suspend fun deleteNote(
        @Path("resourceType") resourceType: String,
        @Path("resourceId") resourceId: Long,
        @Path("noteId") noteId: Long,
    ): DeleteNoteResponseDto

    /**
     * Retrieve a single Note.
     */
    @GET("{resourceType}/{resourceId}/" + APIEndPoint.NOTES + "/{noteId}")
    suspend fun retrieveNote(
        @Path("resourceType") resourceType: String,
        @Path("resourceId") resourceId: Long,
        @Path("noteId") noteId: Long,
    ): NoteDto

    /**
     * Retrieve list of notes for a resource. Notes are returned in descending `createOn` order.
     */
    @GET("{resourceType}/{resourceId}/" + APIEndPoint.NOTES)
    suspend fun retrieveListNotes(
        @Path("resourceType") resourceType: String,
        @Path("resourceId") resourceId: Long,
    ): List<NoteDto>

    /**
     * Update a Note.
     */
    @PUT("{resourceType}/{resourceId}/" + APIEndPoint.NOTES + "/{noteId}")
    suspend fun updateNote(
        @Path("resourceType") resourceType: String,
        @Path("resourceId") resourceId: Long,
        @Path("noteId") noteId: Long,
        @Body noteRequestDto: NoteRequestDto,
    ): UpdateNoteResponseDto
}
