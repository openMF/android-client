package com.mifos.repositories

import com.mifos.api.datamanager.DataManagerNote
import com.mifos.objects.noncore.Note
import rx.Observable
import javax.inject.Inject

/**
 * Created by Aditya Gupta on 08/08/23.
 */
class NoteRepositoryImp @Inject constructor(private val dataManagerNote: DataManagerNote) :
    NoteRepository {

    override fun getNotes(entityType: String?, entityId: Int): Observable<List<Note>> {
        return dataManagerNote.getNotes(entityType, entityId)
    }
}