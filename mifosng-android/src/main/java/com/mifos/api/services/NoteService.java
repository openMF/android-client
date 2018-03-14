/*
 * This project is licensed under the open source MPL V2.
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.api.services;

import com.mifos.api.model.APIEndPoint;
import com.mifos.objects.noncore.Note;

import java.util.List;

import retrofit2.http.GET;
import retrofit2.http.Path;
import rx.Observable;

public interface NoteService {
    @GET("{entityType}/{entityId}/" + APIEndPoint.NOTES)
    Observable<List<Note>> getNotes(@Path("entityType") String entityType,
                                    @Path("entityId") int entityId);
    /**
     * @param entityType              - Type for which note is being fetched (Client or Group)
     * @param entityId                - Id of Entity
     */
}

