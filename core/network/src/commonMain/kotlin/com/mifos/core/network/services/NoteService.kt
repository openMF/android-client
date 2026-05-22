/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mifos-x-field-officer-app/blob/master/LICENSE.md
 */
package com.mifos.core.network.services

import com.mifos.core.network.dto.note.CreateNoteResponseDto
import com.mifos.core.network.dto.note.DeleteNoteResponseDto
import com.mifos.core.network.dto.note.NoteDto
import com.mifos.core.network.dto.note.NoteRequestDto
import com.mifos.core.network.dto.note.UpdateNoteResponseDto
import com.mifos.room.basemodel.APIEndPoint
import de.jensklingenberg.ktorfit.http.Body
import de.jensklingenberg.ktorfit.http.DELETE
import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.POST
import de.jensklingenberg.ktorfit.http.PUT
import de.jensklingenberg.ktorfit.http.Path
import kotlinx.coroutines.flow.Flow

/**
 * Migrated to per-resource [com.mifos.core.network.note.api.NoteApi] in Phase C
 * Wave 7 of the store5-adoption epic. New code MUST use `NoteApi` (suspend-only,
 * no Flow). This `NoteService` interface plus
 * [com.mifos.core.network.datamanager.DataManagerNote] remain only until
 * [com.mifos.core.network.BaseApiManager.noteService] has no live callers — at
 * which point all 3 (`NoteService`, `DataManagerNote`, `BaseApiManager.noteService`)
 * are deleted in Phase D cleanup.
 */
@Deprecated(
    message = "Use com.mifos.core.network.note.api.NoteApi (suspend-only, no Flow).",
    replaceWith = ReplaceWith(
        "NoteApi",
        "com.mifos.core.network.note.api.NoteApi",
    ),
    level = DeprecationLevel.WARNING,
)
interface NoteService {

    /**
     * Add a Resource Note
     * Adds a new note to a supported resource.  Example Requests:  clients/1/notes   groups/1/notes
     * Responses:
     *  - 200: OK
     *
     * @param resourceType resourceType, eg : Client, Loan, Group, Savings Account
     * @param resourceId resourceId, eg : ClientId, LoanId, GroupId, Savings AccountId
     * @param noteRequestDto
     * @return [CreateNoteResponseDto]
     */
    @POST("{resourceType}/{resourceId}/" + APIEndPoint.NOTES)
    suspend fun addNewNote(
        @Path("resourceType") resourceType: String,
        @Path("resourceId") resourceId: Long,
        @Body noteRequestDto: NoteRequestDto,
    ): CreateNoteResponseDto

    /**
     * Delete a Resource Note
     * Deletes a Resource Note
     * Responses:
     *  - 200: OK
     *
     * @param resourceType resourceType, eg : Client, Loan, Group, Savings Account
     * @param resourceId resourceId, eg : ClientId, LoanId, GroupId, Savings AccountId
     * @param noteId noteId
     * @return [DeleteNoteResponseDto]
     */
    @DELETE("{resourceType}/{resourceId}/" + APIEndPoint.NOTES + "/{noteId}")
    suspend fun deleteNote(
        @Path("resourceType") resourceType: String,
        @Path("resourceId") resourceId: Long,
        @Path("noteId") noteId: Long,
    ): DeleteNoteResponseDto

    /**
     * Retrieve a single Note
     * Responses:
     *  - 200: OK
     *
     * @param resourceType resourceType, eg : Client, Loan, Group, Savings Account
     * @param resourceId resourceId, eg : ClientId, LoanId, GroupId, Savings AccountId
     * @param noteId noteId
     * @return [Flow<NoteDto>]
     */
    @GET("{resourceType}/{resourceId}/" + APIEndPoint.NOTES + "/{noteId}")
    fun retrieveNote(
        @Path("resourceType") resourceType: String,
        @Path("resourceId") resourceId: Long,
        @Path("noteId") noteId: Long,
    ): Flow<NoteDto>

    /**
     * Retrieve List of notes
     * Note: Notes are returned in descending createOn order.
     * Responses:
     *  - 200: OK
     *
     * @param resourceType resourceType, eg : Client, Loan, Group, Savings Account
     * @param resourceId resourceId, eg : ClientId, LoanId, GroupId, Savings AccountId
     * @return [Flow<List<NoteDto>>]
     */
    @GET("{resourceType}/{resourceId}/" + APIEndPoint.NOTES)
    fun retrieveListNotes(
        @Path("resourceType") resourceType: String,
        @Path("resourceId") resourceId: Long,
    ): Flow<List<NoteDto>>

    /**
     * Update a Note
     * Responses:
     *  - 200: OK
     *
     * @param resourceType resourceType, eg : Client, Loan, Group, Savings Account
     * @param resourceId resourceId, eg : ClientId, LoanId, GroupId, Savings AccountId
     * @param noteId noteId
     * @param noteRequestDto
     * @return UpdateNoteResponseDto
     */
    @PUT("{resourceType}/{resourceId}/" + APIEndPoint.NOTES + "/{noteId}")
    suspend fun updateNote(
        @Path("resourceType") resourceType: String,
        @Path("resourceId") resourceId: Long,
        @Path("noteId") noteId: Long,
        @Body noteRequestDto: NoteRequestDto,
    ): UpdateNoteResponseDto
}
