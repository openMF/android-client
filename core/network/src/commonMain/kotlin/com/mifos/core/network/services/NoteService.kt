/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.core.network.services

import com.mifos.core.model.objects.notes.Note
import com.mifos.core.model.objects.payloads.NotesPayload
import com.mifos.core.network.GenericResponse
import com.mifos.room.basemodel.APIEndPoint
import de.jensklingenberg.ktorfit.http.Body
import de.jensklingenberg.ktorfit.http.DELETE
import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.POST
import de.jensklingenberg.ktorfit.http.PUT
import de.jensklingenberg.ktorfit.http.Path
import kotlinx.coroutines.flow.Flow

interface NoteService {

    /**
     * Add a Resource Note
     * Adds a new note to a supported resource.  Example Requests:  clients/1/notes   groups/1/notes
     * Responses:
     *  - 200: OK
     *
     * @param resourceType resourceType, eg : Client, Loan, Group, Savings Account
     * @param resourceId resourceId, eg : ClientId, LoanId, GroupId, Savings AccountId
     * @param notesPayload
     * @return [GenericResponse]
     */
    @POST("{resourceType}/{resourceId}/" + APIEndPoint.NOTES)
    suspend fun addNewNote(
        @Path("resourceType") resourceType: String,
        @Path("resourceId") resourceId: Long,
        @Body notesPayload: NotesPayload,
    ): GenericResponse

    /**
     * Delete a Resource Note
     * Deletes a Resource Note
     * Responses:
     *  - 200: OK
     *
     * @param resourceType resourceType, eg : Client, Loan, Group, Savings Account
     * @param resourceId resourceId, eg : ClientId, LoanId, GroupId, Savings AccountId
     * @param noteId noteId
     * @return [GenericResponse]
     */
    @DELETE("{resourceType}/{resourceId}/" + APIEndPoint.NOTES + "/{noteId}")
    suspend fun deleteNote(
        @Path("resourceType") resourceType: String,
        @Path("resourceId") resourceId: Long,
        @Path("noteId") noteId: Long,
    ): GenericResponse

    /**
     * Retrieve a single Note
     * Responses:
     *  - 200: OK
     *
     * @param resourceType resourceType, eg : Client, Loan, Group, Savings Account
     * @param resourceId resourceId, eg : ClientId, LoanId, GroupId, Savings AccountId
     * @param noteId noteId
     * @return [Note]
     */
    @GET("{resourceType}/{resourceId}/" + APIEndPoint.NOTES + "/{noteId}")
    fun retrieveNote(
        @Path("resourceType") resourceType: String,
        @Path("resourceId") resourceId: Long,
        @Path("noteId") noteId: Long,
    ): Flow<Note>

    /**
     * Retrieve List of notes
     * Note: Notes are returned in descending createOn order.
     * Responses:
     *  - 200: OK
     *
     * @param resourceType resourceType, eg : Client, Loan, Group, Savings Account
     * @param resourceId resourceId, eg : ClientId, LoanId, GroupId, Savings AccountId
     * @return [List<Note>]
     */
    @GET("{resourceType}/{resourceId}/" + APIEndPoint.NOTES)
    fun retrieveListNotes(
        @Path("resourceType") resourceType: String,
        @Path("resourceId") resourceId: Long,
    ): Flow<List<Note>>

    /**
     * Update a Note
     * Responses:
     *  - 200: OK
     *
     * @param resourceType resourceType, eg : Client, Loan, Group, Savings Account
     * @param resourceId resourceId, eg : ClientId, LoanId, GroupId, Savings AccountId
     * @param noteId noteId
     * @param notesPayload
     */
    @PUT("{resourceType}/{resourceId}/" + APIEndPoint.NOTES + "/{noteId}")
    suspend fun updateNote(
        @Path("resourceType") resourceType: String,
        @Path("resourceId") resourceId: Long,
        @Path("noteId") noteId: Long,
        @Body notesPayload: NotesPayload,
    ): GenericResponse
}
