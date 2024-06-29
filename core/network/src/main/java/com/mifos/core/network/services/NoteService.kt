/*
 * This project is licensed under the open source MPL V2.
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.core.network.services

import android.database.Observable
import com.mifos.core.model.APIEndPoint
import com.mifos.core.objects.noncore.Note
import kotlinx.coroutines.flow.StateFlow
import retrofit2.http.GET
import retrofit2.http.Path

interface NoteService {

    @GET("{entityType}/{entityId}/" + APIEndPoint.NOTES)
    fun getNotes(
        @Path("entityType") entityType: String?,
        @Path("entityId") entityId: Int
    ): List<Note>
    /**
     * @param entityType              - Type for which note is being fetched (Client or Group)
     * @param entityId                - Id of Entity
     */
}