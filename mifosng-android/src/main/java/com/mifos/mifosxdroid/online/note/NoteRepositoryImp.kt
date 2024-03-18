package com.mifos.mifosxdroid.online.note

import com.mifos.core.network.datamanager.DataManagerNote
import com.mifos.core.objects.noncore.Note
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