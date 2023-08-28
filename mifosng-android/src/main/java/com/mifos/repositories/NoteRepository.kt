package com.mifos.repositories

import com.mifos.objects.noncore.Note
import org.apache.fineract.client.models.GetResourceTypeResourceIdNotesResponse
import rx.Observable

/**
 * Created by Aditya Gupta on 08/08/23.
 */
interface NoteRepository {

    fun getNotes(entityType: String?, entityId: Int): Observable<List<GetResourceTypeResourceIdNotesResponse>>

}